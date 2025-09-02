package org.springframework.samples.detroit_london.unitary.mockist.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.Pet.PetExternalAPI;
import org.springframework.samples.Pet.controller.PetController;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetBuilder;
import org.springframework.samples.detroit_london.fake.data.PetTestData;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetControllerTests {

	@Mock
	PetExternalAPI api;

	@InjectMocks
	PetController controller;

	@Test
	void initCreationForm_returns_form_with_blank_pet() {
		ModelMap model = new ModelMap();

		String view = controller.initCreationForm(model);

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");

		verifyNoInteractions(api);
	}

	@Test
	void processCreationForm_invalid_birthDate_stays_on_form() {
		Pet bad = PetTestData.fido();
		bad.setBirthDate(LocalDate.now().plusDays(1));
		BindingResult br = new BeanPropertyBindingResult(bad, "pet");
		ModelMap model = new ModelMap();

		String view = controller.processCreationForm(100, bad, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");

		verifyNoInteractions(api);
	}

	@Test
	void processCreationForm_success_sets_owner_and_redirects() {
		Pet good = PetBuilder.aPet().build();
		BindingResult br = new BeanPropertyBindingResult(good, "pet");

		String view = controller.processCreationForm(200, good, br, new ModelMap(), new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");

		verify(api).save(argThat(p -> p.getName().equals("Buddy") && p.getOwner_id().equals(200) && p.getType() != null
				&& p.getType().getName().equals("dog") && p.getBirthDate().equals(good.getBirthDate())));
		verifyNoMoreInteractions(api);
	}

	@Test
	void initUpdateForm_loads_pet_and_returns_form() {
		Pet stored = PetTestData.fido();
		ModelMap model = new ModelMap();

		when(api.getPetById(10)).thenReturn(stored);

		String view = controller.initUpdateForm(10, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");

		verify(api).getPetById(10);
		verifyNoMoreInteractions(api);
	}

	@Test
	void processUpdateForm_future_birthDate_stays_on_form() {
		Pet bad = PetTestData.fido();
		bad.setBirthDate(LocalDate.now().plusDays(1));
		BindingResult br = new BeanPropertyBindingResult(bad, "pet");
		ModelMap model = new ModelMap();

		String view = controller.processUpdateForm(100, bad, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");

		verifyNoInteractions(api);
	}

	@Test
	void processUpdateForm_success_sets_owner_and_redirects() {
		Pet update = PetTestData.fido();
		update.setBirthDate(LocalDate.of(2019, 5, 20));
		BindingResult br = new BeanPropertyBindingResult(update, "pet");
		ModelMap model = new ModelMap();

		String view = controller.processUpdateForm(100, update, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");

		verify(api)
			.save(argThat(p -> p.getId().equals(10) && p.getOwner_id().equals(100) && p.getName().equals("Fido")));
		verifyNoMoreInteractions(api);
	}

	@Test
    void populatePetTypes_returns_types_from_api() {
        when(api.findPetTypes()).thenReturn(List.of(PetTestData.dog(), PetTestData.cat()));

        var types = controller.populatePetTypes();

        assertThat(types).isNotNull();

        verify(api).findPetTypes();
        verifyNoMoreInteractions(api);
    }

	@Test
	void findPet_returns_new_when_null_and_loaded_when_id_present() {
		Pet stored = PetTestData.fido();

		when(api.getPetById(10)).thenReturn(stored);

		Pet fresh = controller.findPet(null);
		assertThat(fresh).isNotNull();

		verifyNoInteractions(api);

		Pet loaded = controller.findPet(10);
		assertThat(loaded).isNotNull();

		verify(api).getPetById(10);
		verifyNoMoreInteractions(api);
	}

}