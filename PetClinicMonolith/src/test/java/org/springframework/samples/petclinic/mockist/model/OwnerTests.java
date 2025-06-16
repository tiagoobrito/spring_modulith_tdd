package org.springframework.samples.petclinic.mockist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OwnerTests {

    @Test
    void addVisit_shouldCallAddVisitOnCorrectPet() {
        // Arrange
        Owner owner = new Owner();
        Visit mockVisit = mock(Visit.class);
        Pet mockPet = mock(Pet.class);

        when(mockPet.getId()).thenReturn(1);
        when(mockPet.isNew()).thenReturn(false);

        owner.getPets().add(mockPet);
        owner.addVisit(1, mockVisit);

        verify(mockPet).addVisit(mockVisit);
    }

    @Test
    void getPet_shouldReturnMatchingPet() {
        Owner owner = new Owner();
        Pet mockPet = mock(Pet.class);

        when(mockPet.getName()).thenReturn("Luna");
        when(mockPet.isNew()).thenReturn(true);

        owner.getPets().add(mockPet);

        Pet result = owner.getPet("Luna", false);

        assertSame(mockPet, result);
    }

    @Test
    void getPet_shouldReturnNull() {
        Owner owner = new Owner();
        Pet mockPet = mock(Pet.class);

        when(mockPet.getName()).thenReturn("Luna");
        when(mockPet.isNew()).thenReturn(true);

        owner.getPets().add(mockPet);

        Pet result = owner.getPet("Luna", true);

        assertNull(result);
    }

    @Test
    void addPet_shouldAddPet() {
        Owner owner = new Owner();
        Pet newPet = mock(Pet.class);
        when(newPet.isNew()).thenReturn(true);

        owner.addPet(newPet);

        assertTrue(owner.getPets().contains(newPet));
    }

    @Test
    void addPet_shouldNotAddPet() {
        Owner owner = new Owner();
        Pet existingPet = mock(Pet.class);
        when(existingPet.isNew()).thenReturn(false);

        owner.addPet(existingPet);

        assertFalse(owner.getPets().contains(existingPet));
    }
}
