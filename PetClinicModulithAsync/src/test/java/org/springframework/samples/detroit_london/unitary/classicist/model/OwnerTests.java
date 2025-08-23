package org.springframework.samples.detroit_london.unitary.classicist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OwnerTests {

	@Test
	void testCreateOwnerWithPets() {
		OwnerPet pet1 = new OwnerPet(1, "Fluffy", LocalDate.of(2020, 5, 1), 1, "Cat");
		OwnerPet pet2 = new OwnerPet(2, "Rex", LocalDate.of(2019, 3, 15), 1, "Dog");

		pet1.setVisits(Set.of(new OwnerPet.Visit(1, "General Checkup", LocalDate.of(2023, 6, 20), 1)));

		Owner owner = new Owner(1, "John", "Doe", "123 Main St", "Springfield", "1234567890", List.of(pet1, pet2));

		assertEquals(1, owner.getId());
		assertEquals("John", owner.getFirstName());
		assertEquals("Doe", owner.getLastName());
		assertEquals("123 Main St", owner.getAddress());
		assertEquals("Springfield", owner.getCity());
		assertEquals("1234567890", owner.getTelephone());

		assertEquals(2, owner.getPets().size());
		assertEquals("Fluffy", owner.getPets().get(0).getName());
		assertEquals("Rex", owner.getPets().get(1).getName());

		assertEquals("General Checkup", owner.getPets().get(0).getVisits().iterator().next().description());
	}

}
