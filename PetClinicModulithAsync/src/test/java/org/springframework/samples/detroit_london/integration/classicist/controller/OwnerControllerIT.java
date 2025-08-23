package org.springframework.samples.detroit_london.integration.classicist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class OwnerControllerIT {

	@Autowired
	MockMvc mvc;

	@Autowired
	OwnerManagement service; // only for post-verification of DB state

	@Test
	void get_newOwnerForm_renders_template() throws Exception {
		mvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"))
			.andExpect(model().attributeExists("owner"));
	}

	@Test
	void post_newOwner_duplicateName_stays_on_form_with_errors() throws Exception {
		// Seed has George Franklin
		mvc.perform(post("/owners/new").param("firstName", "George")
			.param("lastName", "Franklin")
			.param("address", "110 W. Liberty St.")
			.param("city", "Madison")
			.param("telephone", "6085551023"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"))
			.andExpect(model().attributeHasFieldErrors("owner", "firstName", "lastName"));
	}

	@Test
	void post_newOwner_success_redirects_to_show() throws Exception {
		mvc.perform(post("/owners/new").param("firstName", "Alice")
			.param("lastName", "Johnson")
			.param("address", "Addr")
			.param("city", "City")
			.param("telephone", "999"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/owners/*"));
	}

	@Test
	void get_findOwnersForm_renders() throws Exception {
		mvc.perform(get("/owners/find")).andExpect(status().isOk()).andExpect(view().name("owners/findOwners"));
	}

	@Test
	void get_processFindOwners_multiple_matches_lists_page() throws Exception {
		// “Da” matches Betty Davis(id 2) and Harold Davis(id 4)
		mvc.perform(get("/owners").param("page", "1").param("lastName", "Da"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attributeExists("listOwners", "totalItems", "totalPages", "currentPage"));
	}

	@Test
	void get_processFindOwners_single_match_redirects_to_owner() throws Exception {
		// “Franklin” → George Franklin (id 1)
		mvc.perform(get("/owners").param("page", "1").param("lastName", "Franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void get_editOwnerForm_loads_owner_and_renders_template() throws Exception {
		mvc.perform(get("/owners/6/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"))
			.andExpect(model().attributeExists("owner"));
	}

	@Test
	void post_editOwner_success_updates_values_and_redirects() throws Exception {
		// change Jean Coleman’s address/city
		mvc.perform(post("/owners/6/edit").param("firstName", "Jean")
			.param("lastName", "Coleman")
			.param("address", "New Address")
			.param("city", "New City")
			.param("telephone", "6085552654"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/6"));

		Owner updated = service.findById(6);
		assertThat(updated.getAddress()).isEqualTo("New Address");
		assertThat(updated.getCity()).isEqualTo("New City");
	}

	@Test
	void get_ownerDetails_renders_details() throws Exception {
		mvc.perform(get("/owners/6"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownerDetails"))
			.andExpect(model().attributeExists("owner"));
	}

}
