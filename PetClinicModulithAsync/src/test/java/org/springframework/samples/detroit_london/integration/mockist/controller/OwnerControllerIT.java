package org.springframework.samples.detroit_london.integration.mockist.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class OwnerControllerIT {

	@Autowired
	MockMvc mvc;

	@SpyBean(value = OwnerManagement.class, reset = MockReset.BEFORE)
	OwnerManagement service;

	@Test
	void get_newOwnerForm_renders_template_no_calls() throws Exception {
		mvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));

		verifyNoInteractions(service);
	}

	@Test
	void post_newOwner_duplicateName_checks_duplicate_and_stays_on_form() throws Exception {
		mvc.perform(post("/owners/new").param("firstName", "George")
			.param("lastName", "Franklin")
			.param("address", "110 W. Liberty St.")
			.param("city", "Madison")
			.param("telephone", "6085551023"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));

		verify(service).findByName("George", "Franklin");
		verify(service, never()).save(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void post_newOwner_success_calls_service_and_redirects() throws Exception {
		mvc.perform(post("/owners/new").param("firstName", "Alice")
			.param("lastName", "Johnson")
			.param("address", "Addr")
			.param("city", "City")
			.param("telephone", "999"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/owners/*"));

		InOrder in = inOrder(service);
		in.verify(service).findByName("Alice", "Johnson");
		in.verify(service).save(argThat(o -> "Alice".equals(o.getFirstName()) && "Johnson".equals(o.getLastName())));

		verifyNoMoreInteractions(service);
	}

	@Test
	void get_findOwnersForm_renders_no_calls() throws Exception {
		mvc.perform(get("/owners/find")).andExpect(status().isOk()).andExpect(view().name("owners/findOwners"));

		verifyNoInteractions(service);
	}

	@Test
	void get_processFindOwners_multiple_matches_calls_findByLastName_and_lists() throws Exception {
		mvc.perform(get("/owners").param("page", "1").param("lastName", "Da"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));

		verify(service).findByLastName(eq("Da"), any(Pageable.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	void get_processFindOwners_single_match_redirects_and_calls_service() throws Exception {
		mvc.perform(get("/owners").param("page", "1").param("lastName", "Franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));

		verify(service).findByLastName(eq("Franklin"), any(Pageable.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	void get_editOwnerForm_calls_findById_and_renders_form() throws Exception {
		mvc.perform(get("/owners/6/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));

		verify(service, atLeast(2)).findById(6);
		verifyNoMoreInteractions(service);
	}

	@Test
	void post_editOwner_calls_findPetByOwner_then_save_and_redirects() throws Exception {
		mvc.perform(post("/owners/6/edit").param("firstName", "Jean")
			.param("lastName", "Coleman")
			.param("address", "New Address")
			.param("city", "New City")
			.param("telephone", "6085552654"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/6"));

		InOrder in = inOrder(service);
		in.verify(service).findById(6);
		in.verify(service).findPetByOwner(6);
		in.verify(service)
			.save(argThat(
					o -> o.getId().equals(6) && "Jean".equals(o.getFirstName()) && "Coleman".equals(o.getLastName())
							&& "New Address".equals(o.getAddress()) && "New City".equals(o.getCity())));

		verifyNoMoreInteractions(service);
	}

	@Test
	void get_ownerDetails_calls_findById_and_renders_details() throws Exception {
		mvc.perform(get("/owners/6")).andExpect(status().isOk()).andExpect(view().name("owners/ownerDetails"));

		verify(service, atLeast(2)).findById(6);
		verifyNoMoreInteractions(service);
	}

}
