package org.springframework.samples.Owner.model;

public class OwnerBuilder {

	private Integer id;

	private String firstName = "Tiago";

	private String lastName = "Brito";

	private String address = "Rua das Brutas, 104";

	private String city = "Povoa de Varzim";

	private String telephone = "987123456";

	public static OwnerBuilder anOwner() {
		return new OwnerBuilder();
	}

	public OwnerBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public OwnerBuilder withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public OwnerBuilder withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public OwnerBuilder withAddress(String address) {
		this.address = address;
		return this;
	}

	public OwnerBuilder withCity(String city) {
		this.city = city;
		return this;
	}

	public OwnerBuilder withTelephone(String telephone) {
		this.telephone = telephone;
		return this;
	}

	public Owner build() {
		Owner owner = new Owner();
		owner.setId(id);
		owner.setFirstName(firstName);
		owner.setLastName(lastName);
		owner.setAddress(address);
		owner.setCity(city);
		owner.setTelephone(telephone);
		return owner;
	}

}
