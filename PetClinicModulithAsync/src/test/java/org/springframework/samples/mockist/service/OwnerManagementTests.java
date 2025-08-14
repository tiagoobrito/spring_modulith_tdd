package org.springframework.samples.mockist.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.samples.Owner.service.OwnerPetRepository;
import org.springframework.samples.Owner.service.OwnerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OwnerManagementTests {

    protected OwnerRepository ownerRepository;
    protected OwnerPetRepository petRepository;
    protected OwnerManagement service;

    @BeforeEach
    void setUp() {
        ownerRepository = mock(OwnerRepository.class);
        petRepository = mock(OwnerPetRepository.class);
        service = new OwnerManagement(ownerRepository, petRepository);
    }

    @Test
    void findById_shouldLoadPetsAndVisits() {
        Owner owner = new Owner();
        owner.setId(1);

        OwnerPet pet = new OwnerPet(10, "Fluffy", LocalDate.now(), 1, "cat");

        OwnerPet.Visit visit = new OwnerPet.Visit(20, "Checkup", LocalDate.now(), 10);

        when(ownerRepository.findById(1)).thenReturn(owner);
        when(petRepository.findPetByOwnerId(1)).thenReturn(List.of(pet));
        when(petRepository.findVisitByPetId(10)).thenReturn(Set.of(visit));

        Owner result = service.findById(1);

        assertThat(result).isSameAs(owner);
        assertThat(result.getPets()).containsExactly(pet);
        assertThat(pet.getVisits()).containsExactly(visit);
    }

    @Test
    void save_shouldUpdateOwnerIfExists() {
        Owner existing = new Owner();
        existing.setId(1);

        when(ownerRepository.findById(1)).thenReturn(existing);
        when(ownerRepository.save(any())).thenReturn(existing);

        Owner updated = new Owner();
        updated.setId(1);
        updated.setFirstName("NewFirst");

        Integer result = service.save(updated);

        assertThat(result).isEqualTo(1);

        ArgumentCaptor<Owner> captor = ArgumentCaptor.forClass(Owner.class);
        Owner saved = captor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("NewFirst");
    }

    @Test
    void save_shouldCreateOwnerIfNew() {
        Owner newOwner = new Owner();
        newOwner.setFirstName("Alice");
        newOwner.setLastName("Brown");

        Owner savedOwner = new Owner();
        savedOwner.setId(99);
        savedOwner.setFirstName("Alice");
        savedOwner.setLastName("Brown");

        when(ownerRepository.save(newOwner)).thenReturn(savedOwner);

        Integer result = service.save(newOwner);
        assertThat(result).isEqualTo(99);
    }

    @Test
    void findByLastName_shouldAttachPetsToEachOwner() {
        Owner owner = new Owner();
        owner.setId(2);

        Page<Owner> page = new PageImpl<>(List.of(owner));
        OwnerPet pet = new OwnerPet();
        pet.setId(5);

        when(ownerRepository.findByLastName(eq("Smith"), any())).thenReturn(page);
        when(petRepository.findPetByOwnerId(2)).thenReturn(List.of(pet));

        Page<Owner> result = service.findByLastName("Smith", PageRequest.of(0, 10));
        assertThat(result.getContent()).containsExactly(owner);
        assertThat(owner.getPets()).containsExactly(pet);
    }

    @Test
    void findByName_shouldReturnOwner() {
        Owner owner = new Owner();
        when(ownerRepository.findByName("John", "Smith")).thenReturn(Optional.of(owner));

        Optional<Owner> result = service.findByName("John", "Smith");
        assertThat(result).isPresent().contains(owner);
    }
}
