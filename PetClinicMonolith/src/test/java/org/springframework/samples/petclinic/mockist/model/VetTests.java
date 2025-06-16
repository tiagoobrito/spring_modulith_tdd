package org.springframework.samples.petclinic.mockist.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VetTests {

    private Vet vet;

    @Mock
    private Specialty surgery;

    @Mock
    private Specialty dentistry;

    @BeforeEach
    void setUp() {
        vet = new Vet();
    }

    @Test
    void addSpecialty_ShouldAddToInternalSet() {
        vet.addSpecialty(surgery);

        assertEquals(1, vet.getNrOfSpecialties());
        assertTrue(vet.getSpecialties().contains(surgery));
    }

    @Test
    void getSpecialties_ShouldReturnSortedUnmodifiableList() {
        // Simulate specialty names for sorting
        when(surgery.getName()).thenReturn("Surgery");
        when(dentistry.getName()).thenReturn("Dentistry");

        vet.addSpecialty(surgery);
        vet.addSpecialty(dentistry);

        List<Specialty> specialties = vet.getSpecialties();

        assertEquals(2, specialties.size());
        assertEquals("Dentistry", specialties.get(0).getName()); // alphabetically first
        assertThrows(UnsupportedOperationException.class, () -> specialties.add(mock(Specialty.class)));
    }

    @Test
    void getNrOfSpecialties_ShouldReturnCorrectCount() {
        assertEquals(0, vet.getNrOfSpecialties());

        vet.addSpecialty(surgery);
        vet.addSpecialty(dentistry);

        assertEquals(2, vet.getNrOfSpecialties());
    }
}
