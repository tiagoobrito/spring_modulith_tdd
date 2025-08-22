package org.springframework.samples.unitary.mockist.service;

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
import org.springframework.samples.fake.data.OwnerPetTestData;
import org.springframework.samples.fake.data.OwnerTestData;

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

	private static Owner owner(Integer id, String fn, String ln) {
		Owner o = new Owner();
		o.setId(id);
		o.setFirstName(fn);
		o.setLastName(ln);
		o.setAddress("Addr");
		o.setCity("City");
		o.setTelephone("999");
		return o;
	}

	private static OwnerPet pet(Integer id, Integer ownerId, String name, String type) {
		OwnerPet p = new OwnerPet();
		p.setId(id);
		p.setOwner_id(ownerId);
		p.setName(name);
		p.setType_name(type);
		p.setBirthDate(LocalDate.of(2020, 1, 1));
		return p;
	}

	@Test
	void findById_populates_pets_and_visits() {
		Owner owner = OwnerTestData.JOHN_DOE;
		OwnerPet fido = OwnerPetTestData.FIDO;
		OwnerPet bella = OwnerPetTestData.BELLA;

		when(ownerRepo.findById(1)).thenReturn(owner);
		when(petRepo.findPetByOwnerId(1)).thenReturn(List.of(fido, bella));
		when(petRepo.findVisitByPetId(10)).thenReturn(Set.of(OwnerPetTestData.FIDO_V1, OwnerPetTestData.FIDO_V2));
		when(petRepo.findVisitByPetId(11)).thenReturn(Set.of(OwnerPetTestData.BELLA_V1));

		Owner out = service.findById(1);

		assertThat(out.getId()).isEqualTo(1);
		assertThat(out.getPets()).extracting(OwnerPet::getName).containsExactly("Fido", "Bella");
		OwnerPet fidoOut = out.getPets().get(0);
		assertThat(fidoOut.getVisits()).hasSize(2);
		assertThat(fidoOut.getVisits()).extracting(OwnerPet.Visit::description)
			.contains("Vaccination", "Teeth cleaning");

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
		when(ownerRepo.findById(42)).thenReturn(saved);

		Integer id = service.save(input);

		assertThat(id).isNotNull();
		assertThat(ownerRepo.findById(id).getFirstName()).isEqualTo("Tiago");

		verify(ownerRepo).save(input);
		verify(ownerRepo).findById(42);
		verifyNoMoreInteractions(ownerRepo);
		verifyNoInteractions(petRepo);
	}

	@Test
	void save_existing_owner_loads_then_updates_then_saves() {
		Owner existing = OwnerBuilder.anOwner().withId(5).build();
		when(ownerRepo.findById(5)).thenReturn(existing);

		Owner update = OwnerBuilder.anOwner().withId(5).withAddress("New Address").build();

		Integer outId = service.save(update);

		assertThat(outId).isEqualTo(5);
		// capture the object that was saved after mutation
		verify(ownerRepo).findById(5);
		ArgumentCaptor<Owner> ownerCaptor = ArgumentCaptor.forClass(Owner.class);
		verify(ownerRepo).save(ownerCaptor.capture());
		Owner saved = ownerCaptor.getValue();
		assertThat(saved.getFirstName()).isEqualTo("Tiago");
		assertThat(saved.getAddress()).isEqualTo("New Address");
		verifyNoMoreInteractions(ownerRepo);
		verifyNoInteractions(petRepo);
	}

	@Test
	void findByLastName_populates_pets_in_page() {
		Owner o1 = OwnerTestData.JANE_SMITH;
		Owner o2 = OwnerTestData.ANTHONY_SMITH;
		Page<Owner> repoPage = new PageImpl<>(List.of(o1, o2), PageRequest.of(0, 10), 2);
		when(ownerRepo.findByLastName(eq("Smi"), any(Pageable.class))).thenReturn(repoPage);
		when(petRepo.findPetByOwnerId(o1.getId())).thenReturn(List.of(OwnerPetTestData.LALA));
		when(petRepo.findPetByOwnerId(o2.getId())).thenReturn(List.of(OwnerPetTestData.LULU));

		Page<Owner> page = service.findByLastName("Smi", PageRequest.of(0, 10));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).allSatisfy(o -> assertThat(o.getPets()).isNotEmpty());
		verify(ownerRepo).findByLastName(eq("Smi"), any(Pageable.class));
		verify(petRepo).findPetByOwnerId(o1.getId());
		verify(petRepo).findPetByOwnerId(o2.getId());
		verifyNoMoreInteractions(ownerRepo, petRepo);
	}

	@Test
    void findPetByOwner_delegates_to_pet_repo() {
        when(petRepo.findPetByOwnerId(1)).thenReturn(List.of(OwnerPetTestData.FIDO, OwnerPetTestData.BELLA));

        List<OwnerPet> result = service.findPetByOwner(1);

        assertThat(result).extracting(OwnerPet::getName).containsExactly("Fido", "Bella");
        verify(petRepo).findPetByOwnerId(1);
        verifyNoInteractions(ownerRepo);
    }

	@Test
    void findByName_delegates_to_owner_repo() {
        when(ownerRepo.findByName("John", "Doe")).thenReturn(Optional.of(OwnerTestData.JOHN_DOE));

        Optional<Owner> result = service.findByName("John", "Doe");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(ownerRepo).findByName("John", "Doe");
        verifyNoInteractions(petRepo);
    }

}
