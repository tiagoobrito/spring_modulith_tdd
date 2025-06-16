package org.springframework.samples.petclinic.classicist.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VetTests {

    private Vet vet;
    private Specialty surgery;
    private Specialty dentistry;

    @BeforeEach
    void setUp() {
        vet = new Vet();

        surgery = new Specialty();
        surgery.setName("Surgery");

        dentistry = new Specialty();
        dentistry.setName("Dentistry");
    }

    @Test
    void addSpecialty_shouldIncreaseCount() {
        vet.addSpecialty(surgery);
        assertEquals(1, vet.getNrOfSpecialties());
        assertTrue(vet.getSpecialties().contains(surgery));
    }

    @Test
    void getSpecialties_shouldReturnSortedList() {
        vet.addSpecialty(surgery);
        vet.addSpecialty(dentistry);

        List<Specialty> sorted = vet.getSpecialties();
        assertEquals(List.of(dentistry, surgery), sorted); // alphabetically sorted

        assertThrows(UnsupportedOperationException.class, () -> sorted.add(new Specialty()));
    }

    @Test
    void getNrOfSpecialties_shouldReturnCorrectSize() {
        assertEquals(0, vet.getNrOfSpecialties());

        vet.addSpecialty(surgery);
        vet.addSpecialty(dentistry);

        assertEquals(2, vet.getNrOfSpecialties());
    }
}
