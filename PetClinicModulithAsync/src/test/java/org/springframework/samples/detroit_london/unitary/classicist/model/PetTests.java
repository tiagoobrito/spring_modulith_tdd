package org.springframework.samples.detroit_london.unitary.classicist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PetTests {

	@Test
	void testCreatePet() {
		PetType petType = new PetType("Dog");
		petType.setId(1);

		Pet.Visit visit = new Pet.Visit(1, "Vaccination", LocalDate.of(2023, 6, 20), 1);

		Pet pet = new Pet(1, "Buddy", LocalDate.of(2020, 1, 15), petType, 101);
		pet.setVisits(Set.of(visit));

		assertEquals(1, pet.getId());
		assertEquals("Buddy", pet.getName());
		assertEquals(LocalDate.of(2020, 1, 15), pet.getBirthDate());
		assertEquals("Dog", pet.getType().getName());
		assertEquals(101, pet.getOwner_id());

		assertEquals(1, pet.getVisits().size());
		assertEquals("Vaccination", pet.getVisits().iterator().next().description());
	}

}
