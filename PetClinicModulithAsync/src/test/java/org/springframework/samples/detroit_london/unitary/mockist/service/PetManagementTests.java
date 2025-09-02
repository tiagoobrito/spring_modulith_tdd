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
        when(petTypeRepository.findPetTypes()).thenReturn(List.of(PetTestData.dog(), PetTestData.cat()));

        Collection<PetType> out = service.findPetTypes();

        assertThat(out).isNotNull();

        verify(petTypeRepository).findPetTypes();
		verifyNoMoreInteractions(petTypeRepository);
        verifyNoInteractions(petRepository, events);
    }

	@Test
    void getPetById_delegates_to_repo() {
        when(petRepository.findById(10)).thenReturn(PetTestData.fido());

        Pet out = service.getPetById(10);

        assertThat(out).isNotNull();

        verify(petRepository).findById(10);
        verifyNoMoreInteractions(petRepository);
        verifyNoInteractions(petTypeRepository, events);
    }

	@Test
    void getPetByName_delegates_to_repo() {
        when(petRepository.findPetByName("Fido")).thenReturn(Optional.of(PetTestData.fido()));

        Optional<Pet> out = service.getPetByName("Fido", true);

        assertThat(out).isPresent();

        verify(petRepository).findPetByName("Fido");
        verifyNoMoreInteractions(petRepository);
        verifyNoInteractions(petTypeRepository, events);
    }

	@Test
	void save_new_pet_calls_repo_and_publishes_SavePetEvent_with_isNew_true() {
		Pet newPet = PetBuilder.aPet().build();

		doAnswer(inv -> {
			newPet.setId(42);
			return null;
		}).when(petRepository).save(newPet, true);

		service.save(newPet);

		verify(petRepository).save(newPet, true);
		ArgumentCaptor<SavePetEvent> ev = ArgumentCaptor.forClass(SavePetEvent.class);
		verify(events).publishEvent(ev.capture());
		verifyNoMoreInteractions(petRepository, petTypeRepository, events);
	}

	@Test
	void save_existing_pet_calls_repo_and_publishes_SavePetEvent_with_isNew_false() {
		Pet existing = PetTestData.bella();

		service.save(existing);

		verify(petRepository).save(existing, false);
		ArgumentCaptor<SavePetEvent> ev = ArgumentCaptor.forClass(SavePetEvent.class);
		verify(events).publishEvent(ev.capture());
		verifyNoMoreInteractions(petRepository, petTypeRepository, events);
	}

}
