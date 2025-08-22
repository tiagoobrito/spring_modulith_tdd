package org.springframework.samples.unitary.mockist.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.Pet.PetExternalAPI;
import org.springframework.samples.Pet.controller.PetController;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetBuilder;
import org.springframework.samples.fake.data.PetTestData;
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
		assertThat(model.get("pet")).isInstanceOf(Pet.class);
		verifyNoInteractions(api);
	}

	@Test
	void processCreationForm_invalid_birthDate_stays_on_form() {
		Pet bad = PetTestData.FIDO;
		bad.setBirthDate(LocalDate.now().plusDays(1));
		BindingResult br = new BeanPropertyBindingResult(bad, "pet");
		ModelMap model = new ModelMap();

		String view = controller.processCreationForm(100, bad, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		assertThat(br.hasFieldErrors("birthDate")).isTrue();
		assertThat(model.get("pet")).isEqualTo(bad);
		verifyNoInteractions(api);
	}

	@Test
	void processCreationForm_success_sets_owner_and_redirects() {
		Pet good = PetBuilder.aPet().build();
		BindingResult br = new BeanPropertyBindingResult(good, "pet");

		String view = controller.processCreationForm(200, good, br, new ModelMap(), new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
		// verify it saved a Pet with owner_id set and our fields
		verify(api).save(argThat(p -> p.getName().equals("Buddy") && p.getOwner_id().equals(200) && p.getType() != null
				&& p.getType().getName().equals("dog") && p.getBirthDate().equals(good.getBirthDate())));
		verifyNoMoreInteractions(api);
	}

	@Test
	void initUpdateForm_loads_pet_and_returns_form() {
		Pet stored = PetTestData.FIDO;
		when(api.getPetById(10)).thenReturn(stored);
		ModelMap model = new ModelMap();

		String view = controller.initUpdateForm(10, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		assertThat(((Pet) model.get("pet")).getName()).isEqualTo("Fido");
		verify(api).getPetById(10);
		verifyNoMoreInteractions(api);
	}

	@Test
	void processUpdateForm_future_birthDate_stays_on_form() {
		Pet bad = PetTestData.FIDO;
		bad.setBirthDate(LocalDate.now().plusDays(1));
		BindingResult br = new BeanPropertyBindingResult(bad, "pet");
		ModelMap model = new ModelMap();

		String view = controller.processUpdateForm(100, bad, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		assertThat(br.hasFieldErrors("birthDate")).isTrue();
		assertThat(model.get("pet")).isEqualTo(bad);
		verifyNoInteractions(api);
	}

	@Test
	void processUpdateForm_success_sets_owner_and_redirects() {
		Pet update = PetTestData.FIDO;
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
        when(api.findPetTypes()).thenReturn(List.of(PetTestData.DOG, PetTestData.CAT));

        var types = controller.populatePetTypes();

        assertThat(types).hasSize(2);
        verify(api).findPetTypes();
        verifyNoMoreInteractions(api);
    }

	@Test
	void findPet_returns_new_when_null_and_loaded_when_id_present() {
		Pet stored = PetTestData.FIDO;

		Pet fresh = controller.findPet(null);
		assertThat(fresh.getId()).isNull();

		when(api.getPetById(10)).thenReturn(stored);
		Pet loaded = controller.findPet(10);
		assertThat(loaded.getName()).isEqualTo("Fido");

		verify(api).getPetById(10);
		verifyNoMoreInteractions(api);
	}

}