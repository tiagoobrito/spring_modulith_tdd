package org.springframework.samples.detroit_london.integration.mockist.service;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetBuilder;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.model.PetTypeBuilder;
import org.springframework.samples.Pet.service.PetManagement;
import org.springframework.samples.Pet.service.PetRepository;
import org.springframework.samples.Pet.service.PetTypeRepository;
import org.springframework.samples.notifications.AddVisitEvent;
import org.springframework.samples.notifications.AddVisitPet;
import org.springframework.samples.notifications.SavePetEvent;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest()
@AutoConfigureTestDatabase
@Transactional
class PetManagementIT {

	@Autowired
	PetManagement service;

	@SpyBean
	PetRepository petRepository; // real beans, spied

	@SpyBean
	PetTypeRepository petTypeRepository;

	@Test
	void findPetTypes_delegates_and_returns_seeded_types() {
		Collection<PetType> types = service.findPetTypes();

		assertThat(types).hasSizeGreaterThanOrEqualTo(6);
		verify(petTypeRepository).findPetTypes();
		verifyNoMoreInteractions(petTypeRepository);
	}

	@Test
	void getPetById_delegates_to_repo() {
		Pet leo = service.getPetById(1);

		assertThat(leo.getName()).isEqualTo("Leo");
		verify(petRepository).findById(1);
		verifyNoMoreInteractions(petRepository);
	}

	@Test
	void getPetByName_delegates_to_repo() {
		Optional<Pet> max = service.getPetByName("Max", true);

		assertThat(max).isPresent();
		verify(petRepository).findPetByName("Max");
		verifyNoMoreInteractions(petRepository);
	}

	@Test
	void save_new_pet_calls_repo_then_publishes_SavePetEvent() {
		Pet newPet = PetBuilder.aPet().named("Nala")
				.ownedBy(1)
				.ofType(PetTypeBuilder.aPetType().withId(2).build())
				.bornOn(LocalDate.of(2021, 2, 3)).build();

		service.save(newPet);

		InOrder in = inOrder(petRepository);
		in.verify(petRepository)
			.save(argThat(
					p -> p.getName().equals("Nala") && p.getOwner_id().equals(1) && p.getType().getId().equals(2)),
					eq(true));

		verifyNoMoreInteractions(petRepository);
	}

}
