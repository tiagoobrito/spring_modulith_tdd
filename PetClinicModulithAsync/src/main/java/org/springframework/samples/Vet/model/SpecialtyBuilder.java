package org.springframework.samples.Vet.model;

/** Fluent builder for Specialty test data. */
public class SpecialtyBuilder {

	private Integer id;

	private String name = "radiology";

	public static SpecialtyBuilder aSpecialty() {
		return new SpecialtyBuilder();
	}

	public SpecialtyBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public SpecialtyBuilder named(String name) {
		this.name = name;
		return this;
	}

	public Specialty build() {
		Specialty s = new Specialty();
		s.setId(id);
		s.setName(name);
		return s;
	}

}
