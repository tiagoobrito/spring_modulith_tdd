package org.springframework.samples.petclinic.classicist.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerTests {

    private Owner owner;
    private Pet newPet;
    private Pet existingPet;

    @BeforeEach
    void setUp() {
        owner = new Owner();

        newPet = new Pet(); // isNew() returns true by default (no ID)
        newPet.setName("Luna");

        existingPet = new Pet();
        existingPet.setName("Max");
        existingPet.setId(1); // Not new
    }

    @Test
    void addPet_shouldAddOnlyIfNew() {
        owner.addPet(newPet);
        owner.addPet(existingPet); // should be ignored

        List<Pet> pets = owner.getPets();
        assertEquals(1, pets.size());
        assertTrue(pets.contains(newPet));
        assertFalse(pets.contains(existingPet));
    }

    @Test
    void getPetByName_shouldReturnMatchingPet() {
        owner.addPet(newPet);

        Pet found = owner.getPet("luna");
        assertEquals(newPet, found);
    }

    @Test
    void getPetByName_ignoreNewTrue_shouldNotReturnNewPets() {
        owner.addPet(newPet);

        Pet result = owner.getPet("luna", true);
        assertNull(result);
    }

    @Test
    void getPetById_shouldReturnExistingPetOnlyIfNotNew() {
        owner.addPet(existingPet);

        Pet result = owner.getPet(1);
        assertEquals(existingPet, result);
    }

    @Test
    void getPetById_shouldReturnNullIfPetIsNew() {
        owner.addPet(newPet);

        Pet result = owner.getPet("Bobi"); // null ID or wrong ID
        assertNull(result);
    }

    @Test
    void addVisit_shouldAddVisitToCorrectPet() {
        Visit visit = new Visit();
        existingPet.addVisit(visit);
        owner.addPet(existingPet); // Adding manually to owner's pet list

        Visit newVisit = new Visit();
        owner.addVisit(1, newVisit); // Should be added to existingPet

        assertTrue(existingPet.getVisits().contains(newVisit));
        assertEquals(2, existingPet.getVisits().size()); // including the original one
    }

    @Test
    void addVisit_shouldThrowIfPetIdNotFound() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> owner.addVisit(42, new Visit()));
        assertTrue(ex.getMessage().contains("Invalid Pet identifier"));
    }

    @Test
    void addVisit_shouldThrowIfVisitIsNull() {
        owner.addPet(existingPet);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> owner.addVisit(1, null));
        assertTrue(ex.getMessage().contains("Visit must not be null"));
    }

    @Test
    void addVisit_shouldThrowIfPetIdIsNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> owner.addVisit(null, new Visit()));
        assertTrue(ex.getMessage().contains("Pet identifier must not be null"));
    }
}
