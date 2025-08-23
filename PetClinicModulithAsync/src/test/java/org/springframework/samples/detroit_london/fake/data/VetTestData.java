package org.springframework.samples.detroit_london.fake.data;

import org.springframework.samples.Vet.model.Specialty;
import org.springframework.samples.Vet.model.Vet;

import java.util.Set;

import static org.springframework.samples.Vet.model.SpecialtyBuilder.aSpecialty;
import static org.springframework.samples.Vet.model.VetBuilder.aVet;

public final class VetTestData {

	// Specialties
	public static final Specialty RADIOLOGY = aSpecialty().withId(1).named("radiology").build();

	public static final Specialty SURGERY = aSpecialty().withId(2).named("surgery").build();

	public static final Specialty DENTISTRY = aSpecialty().withId(3).named("dentistry").build();

	// Vets
	public static final Vet JAMES_CARTER = aVet().withId(10).named("James", "Carter").withSpecialty(RADIOLOGY).build();

	public static final Vet HELEN_LEARY = aVet().withId(11)
		.named("Helen", "Leary")
		.withSpecialties(Set.of(SURGERY, DENTISTRY))
		.build();

	public static final Vet LINDA_DOUGLAS = aVet().withId(12).named("Linda", "Douglas").build(); // no
																									// specialties

}
