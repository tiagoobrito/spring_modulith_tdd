package org.springframework.samples.Owner.model;

import java.time.LocalDate;

public class OwnerPetBuilder {

	private Integer id;

	private String name = "Luna";

	private LocalDate birthDate = LocalDate.of(2015, 11, 11);

	private Integer owner_id = 1;

	private String type_name = "dog";

	public static OwnerPetBuilder aPet() {
		return new OwnerPetBuilder();
	}

	public OwnerPetBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public OwnerPetBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public OwnerPetBuilder bornOn(LocalDate birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public OwnerPetBuilder withOwnerId(Integer ownerId) {
		this.owner_id = ownerId;
		return this;
	}

	public OwnerPetBuilder ofType(String typeName) {
		this.type_name = typeName;
		return this;
	}

	public OwnerPet build() {
		OwnerPet p = new OwnerPet();
		p.setId(id);
		p.setName(name);
		p.setBirthDate(birthDate);
		p.setOwner_id(owner_id);
		p.setType_name(type_name);
		return p;
	}

}
