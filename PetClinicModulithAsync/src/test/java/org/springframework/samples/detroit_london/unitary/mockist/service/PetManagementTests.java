package org.springframework.samples.detroit_london.unitary.mockist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetBuilder;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.service.PetManagement;
import org.springframework.samples.Pet.service.PetRepository;
import org.springframework.samples.Pet.service.PetTypeRepository;
import org.springframework.samples.detroit_london.fake.data.PetTestData;
import org.springframework.samples.notifications.SavePetEvent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * London/mockist: all collaborators mocked, verify interactions & arguments. Keep this
 * test class in the same package to call onNewVisitEvent (package-private).
 */
@ExtendWith(MockitoExtension.class)
class PetManagementTests {

	@Mock
	PetRepository petRepository;

	@Mock
	PetTypeRepository petTypeRepository;

	@Mock
	ApplicationEventPublisher events;

	@InjectMocks
	PetManagement service;

	@Test
    void findPetTypes_delegates_to_repo() {
        when(petTypeRepository.findPetTypes()).thenReturn(List.of(PetTestData.DOG, PetTestData.CAT));

        Collection<PetType> out = service.findPetTypes();

        assertThat(out).extracting(PetType::getName).containsExactlyInAnyOrder("dog", "cat");
        verify(petTypeRepository).findPetTypes();
        verifyNoInteractions(petRepository, events);
    }

	@Test
    void getPetById_delegates_to_repo() {
        when(petRepository.findById(10)).thenReturn(PetTestData.FIDO);

        Pet out = service.getPetById(10);

        assertThat(out.getName()).isEqualTo("Fido");
        verify(petRepository).findById(10);
        verifyNoMoreInteractions(petRepository);
        verifyNoInteractions(petTypeRepository, events);
    }

	@Test
    void getPetByName_delegates_to_repo() {
        when(petRepository.findPetByName("Fido")).thenReturn(Optional.of(PetTestData.FIDO));

        Optional<Pet> out = service.getPetByName("Fido", true);

        assertThat(out).isPresent();
        assertThat(out.get().getOwner_id()).isEqualTo(100);
        verify(petRepository).findPetByName("Fido");
        verifyNoMoreInteractions(petRepository);
        verifyNoInteractions(petTypeRepository, events);
    }

	@Test
	void save_new_pet_calls_repo_and_publishes_SavePetEvent_with_isNew_true() {
		Pet newPet = PetBuilder.aPet().build();

		// Simulate repo assigning an id (service reads pet.getId() AFTER save)
		doAnswer(inv -> {
			newPet.setId(42);
			return null;
		}).when(petRepository).save(newPet, true);

		service.save(newPet);

		verify(petRepository).save(newPet, true);
		// capture the published SavePetEvent and validate fields
		ArgumentCaptor<SavePetEvent> ev = ArgumentCaptor.forClass(SavePetEvent.class);
		verify(events).publishEvent(ev.capture());
		SavePetEvent e = ev.getValue();
		assertThat(e.getId()).isEqualTo(42);
		assertThat(e.getName()).isEqualTo("Buddy");
		assertThat(e.getType()).isEqualTo("dog");
		assertThat(e.isNew()).isTrue();

		verifyNoMoreInteractions(petRepository, petTypeRepository, events);
	}

	@Test
	void save_existing_pet_calls_repo_and_publishes_SavePetEvent_with_isNew_false() {
		Pet existing = PetTestData.BELLA;

		service.save(existing);

		verify(petRepository).save(existing, false);
		ArgumentCaptor<SavePetEvent> ev = ArgumentCaptor.forClass(SavePetEvent.class);
		verify(events).publishEvent(ev.capture());
		assertThat(ev.getValue().isNew()).isFalse();
		assertThat(ev.getValue().getId()).isEqualTo(11);
		assertThat(ev.getValue().getType()).isEqualTo("cat");
		assertThat(ev.getValue().getType_id()).isEqualTo(2);
		verifyNoMoreInteractions(petRepository, petTypeRepository, events);
	}

}
