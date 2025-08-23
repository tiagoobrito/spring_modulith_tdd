package org.springframework.samples.detroit_london.fake.data;

import org.springframework.samples.Vet.model.Specialty;
import org.springframework.samples.Vet.model.Vet;

import java.util.Set;

import static org.springframework.samples.Vet.model.SpecialtyBuilder.aSpecialty;
import static org.springframework.samples.Vet.model.VetBuilder.aVet;

public class VetTestData {

	// Specialties
	public static Specialty radiology() {
		return aSpecialty().withId(1).named("radiology").build();
	}

	public static Specialty surgery() {
		return aSpecialty().withId(2).named("surgery").build();
	}

	public static Specialty dentistry() {
		return aSpecialty().withId(3).named("dentistry").build();
	}

	// Vets
	public static Vet james_carter() {
		return aVet().withId(10).named("James", "Carter").withSpecialty(radiology()).build();
	}

	public static Vet helen_leary() {
		return aVet().withId(11).named("Helen", "Leary").withSpecialties(Set.of(surgery(), dentistry())).build();
	}

	public static Vet linda_douglas() {
		return aVet().withId(12).named("Linda", "Douglas").build();
	}

}
