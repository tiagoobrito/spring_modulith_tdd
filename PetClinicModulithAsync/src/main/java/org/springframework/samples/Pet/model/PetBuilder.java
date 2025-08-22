package org.springframework.samples.Pet.model;

import java.time.LocalDate;

/** Fluent builder for Pet test data. */
public class PetBuilder {

	private Integer id;

	private String name = "Buddy";

	private Integer owner_id = 200;

	private PetType type = PetTypeBuilder.aPetType().withId(1).named("dog").build();

	private LocalDate birthDate = LocalDate.of(2024, 5, 1);

	public static PetBuilder aPet() {
		return new PetBuilder();
	}

	public PetBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public PetBuilder named(String name) {
		this.name = name;
		return this;
	}

	public PetBuilder ownedBy(Integer ownerId) {
		this.owner_id = ownerId;
		return this;
	}

	public PetBuilder ofType(PetType type) {
		this.type = type;
		return this;
	}

	public PetBuilder bornOn(LocalDate birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public Pet build() {
		Pet p = new Pet();
		p.setId(id);
		p.setName(name);
		p.setOwner_id(owner_id);
		p.setType(type);
		p.setBirthDate(birthDate);
		return p;
	}

}
