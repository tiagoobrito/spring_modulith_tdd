package org.springframework.samples.detroit_london.unitary.mockist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.Vet.model.Specialty;
import org.springframework.samples.Vet.model.Vet;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VetTests {

	@Test
	void testVetWithMockedSpecialties() {
		Specialty mockSurgery = mock(Specialty.class);
		Specialty mockDermatology = mock(Specialty.class);

		when(mockSurgery.getName()).thenReturn("Surgery");
		when(mockDermatology.getName()).thenReturn("Dermatology");

		Vet vet = new Vet("Emma", "Taylor", Set.of(mockSurgery, mockDermatology));

		assertEquals("Emma", vet.getFirstName());
		assertEquals("Taylor", vet.getLastName());
		assertEquals(2, vet.getNrOfSpecialties());

		assertTrue(vet.getSpecialties().stream().anyMatch(s -> s.getName().equals("Surgery")));
		assertTrue(vet.getSpecialties().stream().anyMatch(s -> s.getName().equals("Dermatology")));

		verify(mockSurgery, atLeastOnce()).getName();
		verify(mockDermatology, atLeastOnce()).getName();
	}

	@Test
	void testAddSpecialty() {
		Vet vet = new Vet();
		vet.setFirstName("John");
		vet.setLastName("Doe");

		Specialty mockRadiology = mock(Specialty.class);

		when(mockRadiology.getName()).thenReturn("Radiology");

		vet.addSpecialty(mockRadiology);

		assertEquals(1, vet.getNrOfSpecialties());
		assertEquals("Radiology", vet.getSpecialties().get(0).getName());
	}

	@Test
	void testIsNew() {
		Vet vet = new Vet();
		assertTrue(vet.isNew());

		vet.setId(42);
		assertFalse(vet.isNew());
	}

}
