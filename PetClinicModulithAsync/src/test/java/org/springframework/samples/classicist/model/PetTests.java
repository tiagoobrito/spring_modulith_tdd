package org.springframework.samples.classicist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PetTests {

    @Test
    void testCreatePet() {
        PetType petType = new PetType("Dog");
        petType.setId(1);

        Pet.Visit visit = new Pet.Visit(1, "Vaccination", LocalDate.of(2023, 6, 20), 1);

        Pet pet = new Pet(1, "Buddy", LocalDate.of(2020, 1, 15), petType, 101);
        pet.setVisits(Set.of(visit));

        assertEquals(1, pet.getId());
        assertEquals("Buddy", pet.getName());
        assertEquals(LocalDate.of(2020, 1, 15), pet.getBirthDate());
        assertEquals("Dog", pet.getType().getName());
        assertEquals(101, pet.getOwner_id());

        assertEquals(1, pet.getVisits().size());
        assertEquals("Vaccination", pet.getVisits().iterator().next().description());
    }

    @Test
    void testSetters() {
        Pet pet = new Pet();
        PetType type = new PetType("Cat");

        pet.setId(2);
        pet.setName("Milo");
        pet.setBirthDate(LocalDate.of(2019, 9, 10));
        pet.setType(type);
        pet.setOwner_id(202);

        assertEquals(2, pet.getId());
        assertEquals("Milo", pet.getName());
        assertEquals("Cat", pet.getType().getName());
        assertEquals(LocalDate.of(2019, 9, 10), pet.getBirthDate());
        assertEquals(202, pet.getOwner_id());
    }
}
