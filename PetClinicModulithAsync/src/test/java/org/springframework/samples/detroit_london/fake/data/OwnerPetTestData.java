package org.springframework.samples.detroit_london.fake.data;

import org.springframework.samples.Owner.model.OwnerPet;

import java.time.LocalDate;

import static org.springframework.samples.Owner.model.OwnerPetBuilder.aPet;
import static org.springframework.samples.Owner.model.OwnerPetVisitBuilder.aVisit;

public class OwnerPetTestData {

	// Pets
	public static OwnerPet fido() {
		return aPet().withId(10)
			.withName("Fido")
			.bornOn(LocalDate.of(2019, 5, 20))
			.withOwnerId(1)
			.ofType("dog")
			.build();
	}

	public static OwnerPet bella() {
		return aPet().withId(11)
			.withName("Bella")
			.bornOn(LocalDate.of(2021, 3, 15))
			.withOwnerId(1)
			.ofType("cat")
			.build();
	}

	public static OwnerPet max() {
		return aPet().withId(12).withName("Max").bornOn(LocalDate.of(2018, 12, 1)).withOwnerId(3).ofType("dog").build();
	}

	public static OwnerPet lulu() {
		return aPet().withId(13)
			.withName("Lulu")
			.bornOn(LocalDate.of(2015, 2, 10))
			.withOwnerId(4)
			.ofType("dog")
			.build();
	}

	public static OwnerPet lala() {
		return aPet().withId(14)
			.withName("Lala")
			.bornOn(LocalDate.of(2012, 2, 10))
			.withOwnerId(2)
			.ofType("dog")
			.build();
	}

	// Visits
	public static OwnerPet.Visit fido_v1() {
		return aVisit().withId(1000).forPet(10).on(LocalDate.of(2023, 1, 10)).withDescription("Vaccination").build();
	}

	public static OwnerPet.Visit fido_v2() {
		return aVisit().withId(1001).forPet(10).on(LocalDate.of(2024, 6, 5)).withDescription("Teeth cleaning").build();
	}

	public static OwnerPet.Visit bella_v1() {
		return aVisit().withId(1002).forPet(11).on(LocalDate.of(2024, 2, 20)).withDescription("Annual check").build();
	}

}
