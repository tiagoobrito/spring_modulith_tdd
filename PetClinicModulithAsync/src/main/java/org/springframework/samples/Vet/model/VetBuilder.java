package org.springframework.samples.Vet.model;

import java.util.LinkedHashSet;
import java.util.Set;

/** Fluent builder for Vet test data. */
public class VetBuilder {

	private Integer id;

	private String firstName = "James";

	private String lastName = "Carter";

	private final Set<Specialty> specialties = new LinkedHashSet<>();

	public static VetBuilder aVet() {
		return new VetBuilder();
	}

	public VetBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public VetBuilder named(String first, String last) {
		this.firstName = first;
		this.lastName = last;
		return this;
	}

	public VetBuilder withSpecialty(Specialty spec) {
		this.specialties.add(spec);
		return this;
	}

	public VetBuilder withSpecialties(Set<Specialty> specs) {
		this.specialties.addAll(specs);
		return this;
	}

	public Vet build() {
		Vet v = new Vet();
		v.setId(id);
		v.setFirstName(firstName);
		v.setLastName(lastName);
		v.setSpecialties(new LinkedHashSet<>(specialties));
		return v;
	}

}
