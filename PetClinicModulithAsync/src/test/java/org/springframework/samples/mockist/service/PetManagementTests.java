package org.springframework.samples.mockist.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.service.PetManagement;
import org.springframework.samples.Pet.service.PetRepository;
import org.springframework.samples.Pet.service.PetTypeRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PetManagementTests {

    protected PetRepository petRepository;
    protected PetTypeRepository petTypeRepository;
    protected ApplicationEventPublisher eventPublisher;
    protected PetManagement service;

    @BeforeEach
    void setUp() {
        petRepository = mock(PetRepository.class);
        petTypeRepository = mock(PetTypeRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new PetManagement(petRepository, petTypeRepository, eventPublisher);
    }

    @Test
    void findPetTypes_returnsAllTypes() {
        PetType type1 = mock(PetType.class);
        PetType type2 = mock(PetType.class);
        when(petTypeRepository.findPetTypes()).thenReturn(Arrays.asList(type1, type2));

        Collection<PetType> result = service.findPetTypes();

        assertThat(result).containsExactly(type1, type2);
    }

    @Test
    void getPetById_returnsPet() {
        Pet pet = mock(Pet.class);
        when(petRepository.findById(1)).thenReturn(pet);

        Pet found = service.getPetById(1);

        assertThat(found).isEqualTo(pet);
    }

    @Test
    void getPetByName_returnsCorrectPet() {
        Pet pet = mock(Pet.class);
        when(petRepository.findPetByName("Rex")).thenReturn(Optional.of(pet));

        Optional<Pet> result = service.getPetByName("Rex", false);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(pet);
    }

    @Test
    void save_newPet_persistsPet() {
        PetType type = mock(PetType.class);
        when(type.getId()).thenReturn(2);
        when(type.getName()).thenReturn("Dog");

        Pet pet = mock(Pet.class);
        when(pet.getId()).thenReturn(null, 10);
        when(pet.getName()).thenReturn("Buddy");
        when(pet.getBirthDate()).thenReturn(LocalDate.of(2022, 1, 2));
        when(pet.getType()).thenReturn(type);
        when(pet.getOwner_id()).thenReturn(5);

        service.save(pet);
    }
}