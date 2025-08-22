package org.springframework.samples.fake.data;

import org.springframework.samples.Owner.model.OwnerPet;

import java.time.LocalDate;

import static org.springframework.samples.Owner.model.OwnerPetBuilder.aPet;
import static org.springframework.samples.Owner.model.OwnerPetVisitBuilder.aVisit;

public class OwnerPetTestData {

	// Pets
	public static final OwnerPet FIDO = aPet().withId(10)
		.withName("Fido")
		.bornOn(LocalDate.of(2019, 5, 20))
		.withOwnerId(1)
		.ofType("dog")
		.build();

	public static final OwnerPet BELLA = aPet().withId(11)
		.withName("Bella")
		.bornOn(LocalDate.of(2021, 3, 15))
		.withOwnerId(1)
		.ofType("cat")
		.build();

	public static final OwnerPet MAX = aPet().withId(12)
		.withName("Max")
		.bornOn(LocalDate.of(2018, 12, 1))
		.withOwnerId(3)
		.ofType("dog")
		.build();

	public static final OwnerPet LULU = aPet().withId(13)
		.withName("Lulu")
		.bornOn(LocalDate.of(2015, 2, 10))
		.withOwnerId(4)
		.ofType("dog")
		.build();

	public static final OwnerPet LALA = aPet().withId(14)
		.withName("Lala")
		.bornOn(LocalDate.of(2012, 2, 10))
		.withOwnerId(2)
		.ofType("dog")
		.build();

	// Visits
	public static final OwnerPet.Visit FIDO_V1 = aVisit().withId(1000)
		.forPet(10)
		.on(LocalDate.of(2023, 1, 10))
		.withDescription("Vaccination")
		.build();

	public static final OwnerPet.Visit FIDO_V2 = aVisit().withId(1001)
		.forPet(10)
		.on(LocalDate.of(2024, 6, 5))
		.withDescription("Teeth cleaning")
		.build();

	public static final OwnerPet.Visit BELLA_V1 = aVisit().forPet(11) // let fake assign
																		// id
		.on(LocalDate.of(2024, 2, 20))
		.withDescription("Annual check")
		.build();

}
