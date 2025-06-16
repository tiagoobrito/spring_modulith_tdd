package org.springframework.samples.petclinic.mockist.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.PetTypeFormatter;
import org.springframework.samples.petclinic.repository.OwnerRepository;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PetTypeFormatterTests {
    @Mock
    OwnerRepository ownerRepository;

    @InjectMocks
    PetTypeFormatter formatter;

    PetType cat;
    PetType dog;

    @BeforeEach
    void setUp() {
        cat = new PetType();
        cat.setName("Cat");

        dog = new PetType();
        dog.setName("Dog");
    }

    @Test
    void print_shouldReturnPetTypeName() {
        String result = formatter.print(cat, Locale.ENGLISH);
        assertEquals("Cat", result);
    }

    @Test
    void parse_shouldReturnCorrectPetType() throws ParseException {
        when(ownerRepository.findPetTypes()).thenReturn(Arrays.asList(cat, dog));

        PetType result = formatter.parse("Dog", Locale.ENGLISH);

        assertEquals("Dog", result.getName());
    }

    @Test
    void parse_shouldThrowParseException() {
        when(ownerRepository.findPetTypes()).thenReturn(Collections.singletonList(cat));

        ParseException exception = assertThrows(ParseException.class, () -> {
            formatter.parse("Rabbit", Locale.ENGLISH);
        });

        assertTrue(exception.getMessage().contains("type not found"));
    }
}
