package org.springframework.samples.detroit_london.unitary.classicist.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.Pet.service.PetManagement;
import org.springframework.samples.detroit_london.fake.data.PetTestData;
import org.springframework.samples.detroit_london.fake.repository.FakePetRepository;
import org.springframework.samples.detroit_london.fake.repository.FakePetTypeRepository;
import org.springframework.samples.notifications.SavePetEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Detroit/classicist: real domain objects + in-memory fakes. No Spring context, no DB,
 * state-based verification.
 */
class PetManagementTests {

	private FakePetRepository petRepo;

	private CollectingPublisher events;

	private PetManagement service;

	/** Simple collecting publisher (keeps published events for assertions). */
	static class CollectingPublisher implements ApplicationEventPublisher {

		final java.util.List<Object> events = new ArrayList<>();

		@Override
		public void publishEvent(Object event) {
			events.add(event);
		}

		<T> T findFirst(Class<T> type) {
			return type.cast(events.stream().filter(type::isInstance).findFirst().orElse(null));
		}

	}

	@BeforeEach
	void setUp() {
		petRepo = new FakePetRepository();
		FakePetTypeRepository typeRepo = new FakePetTypeRepository();

		petRepo.preloadPets(PetTestData.FIDO, PetTestData.BELLA, PetTestData.MAX);
		petRepo.preloadVisits(PetTestData.FIDO_VACCINE, PetTestData.FIDO_TEETH, PetTestData.BELLA_ANNUAL);
		typeRepo.preload(PetTestData.DOG, PetTestData.CAT);

		events = new CollectingPublisher();
		service = new PetManagement(petRepo, typeRepo, events);
	}

	@Test
	void findPetTypes_returns_types_from_repo() {
		Collection<PetType> out = service.findPetTypes();

		assertThat(out).extracting(PetType::getName).containsExactlyInAnyOrder("dog", "cat");
	}

	@Test
	void getPetById_returns_pet() {
		Pet out = service.getPetById(PetTestData.FIDO.getId());

		assertThat(out.getName()).isEqualTo("Fido");
	}

	@Test
	void getPetByName_returns_optional() {
		Optional<Pet> out = service.getPetByName("Fido", true);

		assertThat(out).isPresent();
		assertThat(out.get().getOwner_id()).isEqualTo(100);
	}

	@Test
	void save_new_pet_persists_and_publishes_SavePetEvent_with_isNew_true() {
		Pet newPet = new Pet();
		newPet.setName("Max");
		newPet.setOwner_id(200);
		newPet.setBirthDate(LocalDate.of(2024, 5, 1));
		PetType type = new PetType();
		type.setId(1);
		type.setName("dog");
		newPet.setType(type);

		service.save(newPet);

		Pet persisted = petRepo.findPetByName("Max").orElseThrow();
		assertThat(persisted.getId()).isNotNull();

		SavePetEvent ev = events.findFirst(SavePetEvent.class);
		assertThat(ev).isNotNull();
		assertThat(ev.getName()).isEqualTo("Max");
		assertThat(ev.getOwner_id()).isEqualTo(200);
		assertThat(ev.getType()).isEqualTo("dog");
		assertThat(ev.isNew()).isTrue();
	}

	@Test
	void save_existing_pet_updates_and_publishes_SavePetEvent_with_isNew_false() {
		Pet existing = petRepo.findById(PetTestData.FIDO.getId());
		existing.setName("Sir Fido");

		service.save(existing);

		Pet after = petRepo.findById(existing.getId());
		assertThat(after.getName()).isEqualTo("Sir Fido");

		SavePetEvent ev = events.findFirst(SavePetEvent.class);
		assertThat(ev).isNotNull();
		assertThat(ev.getId()).isEqualTo(existing.getId());
		assertThat(ev.isNew()).isFalse();
	}

}
