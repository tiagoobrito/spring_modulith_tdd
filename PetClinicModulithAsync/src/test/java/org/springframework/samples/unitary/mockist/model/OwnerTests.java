package org.springframework.samples.unitary.mockist.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OwnerTests {

	@Test
	void testOwnerWithMockedPets() {
		OwnerPet pet1 = mock(OwnerPet.class);
		OwnerPet pet2 = mock(OwnerPet.class);

		when(pet1.getName()).thenReturn("Shadow");
		when(pet2.getName()).thenReturn("Luna");

		Owner owner = new Owner(2, "Emma", "Jones", "456 Elm St", "Lakeside", "5556667777", List.of(pet1, pet2));

		assertEquals(2, owner.getPets().size());
		assertEquals("Shadow", owner.getPets().get(0).getName());
		assertEquals("Luna", owner.getPets().get(1).getName());

		// Interaction verification — London-style emphasis
		verify(pet1).getName();
		verify(pet2).getName();
		verifyNoMoreInteractions(pet1, pet2);
	}

}
