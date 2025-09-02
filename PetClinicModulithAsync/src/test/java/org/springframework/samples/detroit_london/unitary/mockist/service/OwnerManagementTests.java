package org.springframework.samples.detroit_london.unitary.mockist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerBuilder;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.samples.Owner.service.OwnerPetRepository;
import org.springframework.samples.Owner.service.OwnerRepository;
import org.springframework.samples.detroit_london.fake.data.OwnerPetTestData;
import org.springframework.samples.detroit_london.fake.data.OwnerTestData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * London/mockist unit tests for OwnerManagement: - All collaborators mocked - Real domain
 * objects as inputs/outputs - Verify interactions & arguments
 */
@ExtendWith(MockitoExtension.class)
class OwnerManagementTests {

	@Mock
	OwnerRepository ownerRepo;

	@Mock
	OwnerPetRepository petRepo;

	@InjectMocks
	OwnerManagement service;

	@Test
	void findById_populates_pets_and_visits() {
		Owner owner = OwnerTestData.john_doe();
		OwnerPet fido = OwnerPetTestData.fido();
		OwnerPet bella = OwnerPetTestData.bella();

		when(ownerRepo.findById(1)).thenReturn(owner);
		when(petRepo.findPetByOwnerId(1)).thenReturn(List.of(fido, bella));
		when(petRepo.findVisitByPetId(10)).thenReturn(Set.of(OwnerPetTestData.fido_v1(), OwnerPetTestData.fido_v2()));
		when(petRepo.findVisitByPetId(11)).thenReturn(Set.of(OwnerPetTestData.bella_v1()));

		Owner out = service.findById(1);

		assertThat(out).isNotNull();

		verify(ownerRepo).findById(1);
		verify(petRepo).findPetByOwnerId(1);
		verify(petRepo).findVisitByPetId(10);
		verify(petRepo).findVisitByPetId(11);
		verifyNoMoreInteractions(ownerRepo, petRepo);
	}

	@Test
	void save_new_owner_calls_repo_save_and_returns_id() {
		Owner input = OwnerBuilder.anOwner().build();
		Owner saved = OwnerBuilder.anOwner().withId(42).build();
		when(ownerRepo.save(input)).thenReturn(saved);

		Integer id = service.save(input);

		assertThat(id).isNotNull();

		verify(ownerRepo).save(input);
		verifyNoMoreInteractions(ownerRepo);
		verifyNoInteractions(petRepo);
	}

	@Test
	void save_existing_owner_loads_then_updates_then_saves() {
		Owner existing = OwnerBuilder.anOwner().withId(5).build();
		when(ownerRepo.findById(5)).thenReturn(existing);

		Owner update = OwnerBuilder.anOwner().withId(5).withAddress("New Address").build();

		Integer outId = service.save(update);

		assertThat(outId).isNotNull();

		verify(ownerRepo).findById(5);
		ArgumentCaptor<Owner> ownerCaptor = ArgumentCaptor.forClass(Owner.class);
		verify(ownerRepo).save(ownerCaptor.capture());
		verifyNoMoreInteractions(ownerRepo);
		verifyNoInteractions(petRepo);
	}

	@Test
	void findByLastName_populates_pets_in_page() {
		Owner o1 = OwnerTestData.jane_smith();
		Owner o2 = OwnerTestData.anthony_smith();
		Page<Owner> repoPage = new PageImpl<>(List.of(o1, o2), PageRequest.of(0, 10), 2);
		when(ownerRepo.findByLastName(eq("Smi"), any(Pageable.class))).thenReturn(repoPage);
		when(petRepo.findPetByOwnerId(o1.getId())).thenReturn(List.of(OwnerPetTestData.lala()));
		when(petRepo.findPetByOwnerId(o2.getId())).thenReturn(List.of(OwnerPetTestData.lulu()));

		Page<Owner> page = service.findByLastName("Smi", PageRequest.of(0, 10));

		assertThat(page).isNotNull();

		verify(ownerRepo).findByLastName(eq("Smi"), any(Pageable.class));
		verify(petRepo).findPetByOwnerId(o1.getId());
		verify(petRepo).findPetByOwnerId(o2.getId());
		verifyNoMoreInteractions(ownerRepo, petRepo);
	}

	@Test
    void findPetByOwner_delegates_to_pet_repo() {
        when(petRepo.findPetByOwnerId(1)).thenReturn(List.of(OwnerPetTestData.fido(), OwnerPetTestData.bella()));

        List<OwnerPet> result = service.findPetByOwner(1);

        assertThat(result).isNotNull();

        verify(petRepo).findPetByOwnerId(1);
		verifyNoMoreInteractions(petRepo);
        verifyNoInteractions(ownerRepo);
    }

	@Test
    void findByName_delegates_to_owner_repo() {
        when(ownerRepo.findByName("John", "Doe")).thenReturn(Optional.of(OwnerTestData.john_doe()));

        Optional<Owner> result = service.findByName("John", "Doe");

        assertThat(result).isPresent();

        verify(ownerRepo).findByName("John", "Doe");
		verifyNoMoreInteractions(ownerRepo);
        verifyNoInteractions(petRepo);
    }

}
