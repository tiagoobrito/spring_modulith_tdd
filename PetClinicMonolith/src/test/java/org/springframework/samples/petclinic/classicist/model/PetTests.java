package org.springframework.samples.petclinic.classicist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PetTests {

    @Test
    void addVisit_shouldAddVisit() {
        Pet pet = new Pet();
        Visit visit = new Visit();

        pet.addVisit(visit);

        Collection<Visit> visits = pet.getVisits();
        assertTrue(visits.contains(visit));
        assertEquals(1, visits.size());
    }
}
