package org.springframework.samples.detroit_london.unitary.mockist.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Owner.OwnerExternalAPI;
import org.springframework.samples.Owner.controller.OwnerController;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerBuilder;
import org.springframework.samples.detroit_london.fake.data.OwnerPetTestData;
import org.springframework.samples.detroit_london.fake.data.OwnerTestData;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTests {

	@Mock
	OwnerExternalAPI api;

	@InjectMocks
	OwnerController controller;

	@Test
	void initCreationForm_returns_form_with_empty_owner() {
		var model = new java.util.HashMap<String, Object>();

		String view = controller.initCreationForm(model);

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		assertThat(model).containsKey("owner");
		verifyNoInteractions(api);
	}

	@Test
    void processCreationForm_duplicate_names_adds_field_errors_and_stays_on_form() {
        when(api.findByName("Tiago", "Brito")).thenReturn(Optional.of(OwnerBuilder.anOwner().withId(1).build()));
        Owner input = OwnerBuilder.anOwner().build();
        BindingResult br = new BeanPropertyBindingResult(input, "owner");

        String view = controller.processCreationForm(input, br, new RedirectAttributesModelMap(), new ModelMap());

        assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
        assertThat(br.hasFieldErrors("firstName")).isTrue();
        assertThat(br.hasFieldErrors("lastName")).isTrue();
        verify(api).findByName("Tiago", "Brito");
        verifyNoMoreInteractions(api);
    }

	@Test
	void processCreationForm_with_binding_errors_stays_on_form() {
		Owner input = OwnerBuilder.anOwner().build();
		BindingResult br = new BeanPropertyBindingResult(input, "owner");
		br.rejectValue("lastName", "invalid");

		when(api.findByName("Tiago", "Brito")).thenReturn(Optional.empty());

		String view = controller.processCreationForm(input, br, new RedirectAttributesModelMap(), new ModelMap());

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		verify(api).findByName("Tiago", "Brito");
		verify(api, never()).save(any());
		verifyNoMoreInteractions(api);
	}

	@Test
    void processCreationForm_success_redirects_to_details() {
        when(api.findByName("Tiago","Brito")).thenReturn(Optional.empty());
        when(api.save(any(Owner.class))).thenReturn(42);

        Owner input = OwnerBuilder.anOwner().build();
        BindingResult br = new BeanPropertyBindingResult(input, "owner");
        var redirect = new RedirectAttributesModelMap();

        String view = controller.processCreationForm(input, br, redirect, new ModelMap());

        assertThat(view).isEqualTo("redirect:/owners/42");
        verify(api).findByName("Tiago","Brito");
        verify(api).save(argThat(o -> o.getFirstName().equals("Tiago") && o.getLastName().equals("Brito")));
        verifyNoMoreInteractions(api);
    }

	@Test
	void initFindForm_returns_findOwners_view() {
		String view = controller.initFindForm();
		assertThat(view).isEqualTo("owners/findOwners");
		verifyNoInteractions(api);
	}

	@Test
    void processFindForm_no_results_adds_error_and_returns_findOwners() {
        when(api.findByLastName(eq("Nobody"), any())).thenReturn(Page.empty());

        Model model = new ExtendedModelMap();
        Owner probe = new Owner(); probe.setLastName("Nobody");
        BindingResult br = new BeanPropertyBindingResult(probe, "owner");

        String view = controller.processFindForm(1, probe, br, model);

        assertThat(view).isEqualTo("owners/findOwners");
        assertThat(br.hasFieldErrors("lastName")).isTrue();
        verify(api).findByLastName(eq("Nobody"), any());
        verifyNoMoreInteractions(api);
    }

	@Test
	void processFindForm_single_result_redirects_to_details() {
		Owner single = OwnerBuilder.anOwner().withId(7).build();
		when(api.findByLastName(eq("Br"), any())).thenReturn(new PageImpl<>(List.of(single), PageRequest.of(0, 5), 1));

		Model model = new ExtendedModelMap();
		Owner probe = new Owner();
		probe.setLastName("Br");
		BindingResult br = new BeanPropertyBindingResult(probe, "owner");

		String view = controller.processFindForm(1, probe, br, model);

		assertThat(view).isEqualTo("redirect:/owners/7");
		verify(api).findByLastName(eq("Br"), any());
		verifyNoMoreInteractions(api);
	}

	@Test
	void processFindForm_multiple_results_returns_list_with_pagination_model() {
		Owner o1 = OwnerTestData.JANE_SMITH;
		Owner o2 = OwnerTestData.ANTHONY_SMITH;
		when(api.findByLastName(eq("Sm"), any())).thenReturn(new PageImpl<>(List.of(o1, o2), PageRequest.of(0, 5), 2));

		Model model = new ExtendedModelMap();
		Owner probe = new Owner();
		probe.setLastName("Sm");
		BindingResult br = new BeanPropertyBindingResult(probe, "owner");

		String view = controller.processFindForm(1, probe, br, model);

		assertThat(view).isEqualTo("owners/ownersList");
		assertThat(model.getAttribute("currentPage")).isEqualTo(1);
		assertThat((Integer) model.getAttribute("totalPages")).isGreaterThanOrEqualTo(1);
		assertThat((Long) model.getAttribute("totalItems")).isEqualTo(2L);
		assertThat((List<?>) model.getAttribute("listOwners")).hasSize(2);
		verify(api).findByLastName(eq("Sm"), any());
		verifyNoMoreInteractions(api);
	}

	@Test
    void initUpdateOwnerForm_loads_owner_and_returns_form() {
        when(api.findById(2)).thenReturn(OwnerTestData.JANE_SMITH);
        ModelMap model = new ModelMap();

        String view = controller.initUpdateOwnerForm(2, model);

        assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
        assertThat(((Owner)model.get("owner")).getId()).isEqualTo(2);
        verify(api).findById(2);
        verifyNoMoreInteractions(api);
    }

	@Test
	void processUpdateOwnerForm_with_errors_stays_on_form_and_sets_message() {
		Owner update = OwnerBuilder.anOwner().build();
		BindingResult br = new BeanPropertyBindingResult(update, "owner");
		br.reject("bad");
		ModelMap model = new ModelMap();

		String view = controller.processUpdateOwnerForm(update, br, 9, new RedirectAttributesModelMap(), model);

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		assertThat(model.get("ownerMessage")).isEqualTo("There was an error updating the owner.");
		verifyNoInteractions(api);
	}

	@Test
    void processUpdateOwnerForm_success_sets_pets_and_redirects() {
        when(api.findPetByOwner(1)).thenReturn(List.of(OwnerPetTestData.FIDO));
        when(api.save(any(Owner.class))).thenReturn(1);

        Owner update = OwnerBuilder.anOwner().build();
        BindingResult br = new BeanPropertyBindingResult(update, "owner");
        ModelMap model = new ModelMap();
        var redirect = new RedirectAttributesModelMap();

        String view = controller.processUpdateOwnerForm(update, br, 1, redirect, model);

        assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
        verify(api).findPetByOwner(1);
        verify(api).save(argThat(o ->
                o.getId().equals(1) &&
                        o.getPets() != null &&
                        o.getPets().size() == 1 &&
                        "Fido".equals(o.getPets().get(0).getName())
        ));
        verifyNoMoreInteractions(api);
    }

	@Test
	void showOwner_returns_ownerDetails_with_owner_in_model() {
		Owner saved = OwnerTestData.JOHN_DOE;
		when(api.findById(1)).thenReturn(saved);

		ModelAndView mav = controller.showOwner(1);

		assertThat(mav.getViewName()).isEqualTo("owners/ownerDetails");
		assertThat(mav.getModel()).containsValue(saved);
		verify(api).findById(1);
		verifyNoMoreInteractions(api);
	}

}
