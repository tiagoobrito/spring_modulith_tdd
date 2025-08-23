package org.springframework.samples.detroit_london.unitary.classicist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.Vet.model.Specialty;
import org.springframework.samples.Vet.model.Vet;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VetTests {

	@Test
	void testCreateVetWithSpecialties() {
		Specialty surgery = new Specialty("Surgery");
		surgery.setId(1);
		Specialty dentistry = new Specialty("Dentistry");
		dentistry.setId(2);

		Vet vet = new Vet("Alice", "Smith", Set.of(surgery, dentistry));
		vet.setId(1001);

		assertEquals(1001, vet.getId());
		assertEquals("Alice", vet.getFirstName());
		assertEquals("Smith", vet.getLastName());
		assertEquals(2, vet.getNrOfSpecialties());

		// Sorted alphabetically by name: Dentistry, Surgery
		assertEquals("Dentistry", vet.getSpecialties().get(0).getName());
		assertEquals("Surgery", vet.getSpecialties().get(1).getName());
	}

	@Test
	void testAddSpecialty() {
		Vet vet = new Vet();
		vet.setFirstName("John");
		vet.setLastName("Doe");

		Specialty radiology = new Specialty("Radiology");
		vet.addSpecialty(radiology);

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
