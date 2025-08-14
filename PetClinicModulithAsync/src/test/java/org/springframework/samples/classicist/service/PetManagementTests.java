package org.springframework.samples.classicist.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.service.PetManagement;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PetManagementTests {

    @Autowired
    PetManagement service;

    @Test
    void findPetTypes_returnsAllTypes() {
        Collection<PetType> types = service.findPetTypes();
        assertThat(types).isNotEmpty();
    }

    @Test
    void getPetById_returnsPet() {
        Pet pet = new Pet();
        pet.setName("Rex");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));

        service.save(pet);

        Pet found = service.getPetById(pet.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Rex");
    }

    @Test
    void getPetByName_returnsCorrectPet() {
        Pet pet = new Pet();
        pet.setName("Fido");
        pet.setBirthDate(LocalDate.of(2020, 3, 3));
        service.save(pet);

        Optional<Pet> found = service.getPetByName("Fido", false);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Fido");
    }

    @Test
    void save_newPet_persistsPet() {
        Pet pet = new Pet();
        pet.setName("Bella");
        pet.setBirthDate(LocalDate.of(2021, 5, 15));

        service.save(pet);

        Pet saved = service.getPetById(pet.getId());
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Bella");
    }
}
