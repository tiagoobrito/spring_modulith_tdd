package org.springframework.samples.detroit_london.fake.data;

import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

import java.time.LocalDate;

import static org.springframework.samples.Pet.model.PetBuilder.aPet;
import static org.springframework.samples.Pet.model.PetTypeBuilder.aPetType;
import static org.springframework.samples.Pet.model.PetVisitBuilder.aVisit;

public class PetTestData {

	public static final PetType DOG = aPetType().withId(1).named("dog").build();

	public static final PetType CAT = aPetType().withId(2).named("cat").build();

	public static final Pet FIDO = aPet().withId(10)
		.named("Fido")
		.ownedBy(100)
		.ofType(DOG)
		.bornOn(LocalDate.of(2019, 5, 20))
		.build();

	public static final Pet BELLA = aPet().withId(11)
		.named("Bella")
		.ownedBy(101)
		.ofType(CAT)
		.bornOn(LocalDate.of(2021, 3, 15))
		.build();

	public static final Pet MAX = aPet().withId(12)
		.named("Max")
		.ownedBy(100)
		.ofType(DOG)
		.bornOn(LocalDate.of(2018, 12, 1))
		.build();

	// Visits (no id; repo assigns nothing—just stores)
	public static final Pet.Visit FIDO_VACCINE = aVisit().withId(1)
		.forPet(10)
		.on(LocalDate.of(2024, 6, 1))
		.withDescription("Vaccination")
		.build();

	public static final Pet.Visit FIDO_TEETH = aVisit().withId(2)
		.forPet(10)
		.on(LocalDate.of(2024, 10, 5))
		.withDescription("Teeth cleaning")
		.build();

	public static final Pet.Visit BELLA_ANNUAL = aVisit().withId(3)
		.forPet(11)
		.on(LocalDate.of(2024, 2, 20))
		.withDescription("Annual check")
		.build();

}
