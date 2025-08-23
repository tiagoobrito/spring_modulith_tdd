package org.springframework.samples.detroit_london.unitary.classicist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.Pet.controller.PetController;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetBuilder;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.service.PetManagement;
import org.springframework.samples.detroit_london.fake.data.PetTestData;
import org.springframework.samples.detroit_london.fake.repository.FakePetRepository;
import org.springframework.samples.detroit_london.fake.repository.FakePetTypeRepository;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class PetControllerTests {

	private FakePetRepository petRepo;

	private PetController controller;

	/** Collects published events (service emits SavePetEvent/AddVisitPet). */
	static class CollectingPublisher implements ApplicationEventPublisher {

		final java.util.List<Object> published = new ArrayList<>();

		@Override
		public void publishEvent(Object event) {
			published.add(event);
		}

	}

	@BeforeEach
	void setUp() {
		petRepo = new FakePetRepository();
		FakePetTypeRepository typeRepo = new FakePetTypeRepository();

		petRepo.preloadPets(PetTestData.FIDO, PetTestData.BELLA, PetTestData.MAX);
		petRepo.preloadVisits(PetTestData.FIDO_VACCINE, PetTestData.FIDO_TEETH, PetTestData.BELLA_ANNUAL);
		typeRepo.preload(PetTestData.DOG, PetTestData.CAT);

		CollectingPublisher events = new CollectingPublisher();

		// acts as PetExternalAPI
		PetManagement service = new PetManagement(petRepo, typeRepo, events);
		controller = new PetController(service);
	}

	@Test
	void initCreationForm_returns_form_with_blank_pet() {
		ModelMap model = new ModelMap();

		String view = controller.initCreationForm(model);

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		assertThat(model.get("pet")).isInstanceOf(Pet.class);
	}

	@Test
	void processCreationForm_invalid_birthDate_stays_on_form() {
		ModelMap model = new ModelMap();
		Pet pet = PetBuilder.aPet().withId(1).bornOn(LocalDate.now().plusDays(1)).build();
		BindingResult br = new BeanPropertyBindingResult(pet, "pet");

		String view = controller.processCreationForm(200, pet, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		assertThat(br.hasFieldErrors("birthDate")).isTrue();
		assertThat(model.get("pet")).isEqualTo(pet);
	}

	@Test
	void processCreationForm_success_sets_owner_and_redirects() {
		ModelMap model = new ModelMap();
		Pet pet = PetBuilder.aPet().withId(13).build();
		pet.setOwner_id(null);
		BindingResult br = new BeanPropertyBindingResult(pet, "pet");

		String view = controller.processCreationForm(200, pet, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
		// persisted in fake repo; name must exist
		assertThat(petRepo.findPetByName("Buddy")).isPresent();
		assertThat(petRepo.findPetByName("Buddy").get().getOwner_id()).isEqualTo(200);
	}

	@Test
	void initUpdateForm_loads_pet_and_returns_form() {
		ModelMap model = new ModelMap();

		String view = controller.initUpdateForm(PetTestData.FIDO.getId(), model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		assertThat(((Pet) model.get("pet")).getName()).isEqualTo("Fido");
	}

	@Test
	void processUpdateForm_future_birthDate_stays_on_form() {
		Pet update = PetTestData.FIDO;
		update.setBirthDate(LocalDate.now().plusDays(1));

		BindingResult br = new BeanPropertyBindingResult(update, "pet");
		ModelMap model = new ModelMap();

		String view = controller.processUpdateForm(100, update, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		assertThat(br.hasFieldErrors("birthDate")).isTrue();
		assertThat(model.get("pet")).isEqualTo(update);
	}

	@Test
	void processUpdateForm_success_sets_owner_and_redirects() {
		Pet update = PetTestData.FIDO;
		update.setBirthDate(LocalDate.of(2019, 5, 20));

		BindingResult br = new BeanPropertyBindingResult(update, "pet");
		ModelMap model = new ModelMap();

		String view = controller.processUpdateForm(101, update, br, model, new RedirectAttributesModelMap());

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
		assertThat(petRepo.findById(PetTestData.FIDO.getId()).getName()).isEqualTo("Fido");
		assertThat(petRepo.findById(PetTestData.FIDO.getId()).getOwner_id()).isEqualTo(101);
	}

	@Test
	void populatePetTypes_returns_types_from_api() {
		Collection<PetType> types = controller.populatePetTypes();
		assertThat(types).extracting(PetType::getName).contains("dog", "cat");
	}

	@Test
	void findPet_returns_new_when_null_and_loaded_when_id_present() {
		Pet fresh = controller.findPet(null);
		assertThat(fresh.getId()).isNull();

		Pet loaded = controller.findPet(PetTestData.FIDO.getId());
		assertThat(loaded.getName()).isEqualTo("Fido");
	}

}
