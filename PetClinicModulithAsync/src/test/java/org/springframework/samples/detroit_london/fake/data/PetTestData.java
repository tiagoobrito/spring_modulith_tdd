package org.springframework.samples.detroit_london.fake.data;

import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

import java.time.LocalDate;

import static org.springframework.samples.Pet.model.PetBuilder.aPet;
import static org.springframework.samples.Pet.model.PetTypeBuilder.aPetType;
import static org.springframework.samples.Pet.model.PetVisitBuilder.aVisit;

public class PetTestData {

	public static PetType dog() {
		return aPetType().withId(1).named("dog").build();
	}

	public static PetType cat() {
		return aPetType().withId(2).named("cat").build();
	}

	public static Pet fido() {
		return aPet().withId(10).named("Fido").ownedBy(100).ofType(dog()).bornOn(LocalDate.of(2019, 5, 20)).build();
	}

	public static Pet bella() {
		return aPet().withId(11).named("Bella").ownedBy(101).ofType(cat()).bornOn(LocalDate.of(2021, 3, 15)).build();
	}

	public static Pet max() {
		return aPet().withId(12).named("Max").ownedBy(100).ofType(dog()).bornOn(LocalDate.of(2018, 12, 1)).build();
	}

	public static Pet.Visit fido_vaccine() {
		return aVisit().withId(1).forPet(10).on(LocalDate.of(2024, 6, 1)).withDescription("Vaccination").build();
	}

	public static Pet.Visit fido_teeth() {
		return aVisit().withId(2).forPet(10).on(LocalDate.of(2024, 10, 5)).withDescription("Teeth cleaning").build();
	}

	public static Pet.Visit bella_annual() {
		return aVisit().withId(3).forPet(11).on(LocalDate.of(2024, 2, 20)).withDescription("Annual check").build();
	}

}
