package org.springframework.samples.petclinic.mockist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class PetTests {

    @Test
    void addVisit_shouldAddVisit() {
        // Arrange
        Pet pet = new Pet();
        Visit mockVisit = mock(Visit.class);

        // Act
        pet.addVisit(mockVisit);

        // Assert
        Collection<Visit> visits = pet.getVisits();
        assertTrue(visits.contains(mockVisit));
        assertEquals(1, visits.size());
    }
}
