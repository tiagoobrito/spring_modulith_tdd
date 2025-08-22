package org.springframework.samples.Pet.model;

/** Simple builder for PetType used by Pet. */
public class PetTypeBuilder {

	private Integer id = 1;

	private String name = "dog";

	public static PetTypeBuilder aPetType() {
		return new PetTypeBuilder();
	}

	public PetTypeBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public PetTypeBuilder named(String name) {
		this.name = name;
		return this;
	}

	public PetType build() {
		PetType t = new PetType();
		t.setId(id);
		t.setName(name);
		return t;
	}

}
