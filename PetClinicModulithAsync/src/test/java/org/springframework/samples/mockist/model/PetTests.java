package org.springframework.samples.mockist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PetTests {

    @Test
    void testPetWithMockedType() {
        PetType mockType = mock(PetType.class);
        when(mockType.getName()).thenReturn("Hamster");

        Pet pet = new Pet(3, "Nibbles", LocalDate.of(2021, 12, 5), mockType, 303);

        assertEquals("Nibbles", pet.getName());
        assertEquals("Hamster", pet.getType().getName());

        verify(mockType).getName();
        verifyNoMoreInteractions(mockType);
    }
}
