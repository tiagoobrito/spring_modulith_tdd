package org.springframework.samples.Pet.model;

import org.springframework.samples.Owner.model.OwnerPetVisitBuilder;

import java.time.LocalDate;

/** Builder for Pet.Visit (no id field; matches repository usage). */
public class PetVisitBuilder {

	private Integer id;

	private Integer pet_id = 1;

	private LocalDate visit_date = LocalDate.now();

	private String description = "Routine check";

	public static PetVisitBuilder aVisit() {
		return new PetVisitBuilder();
	}

	public PetVisitBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public PetVisitBuilder forPet(Integer petId) {
		this.pet_id = petId;
		return this;
	}

	public PetVisitBuilder on(LocalDate date) {
		this.visit_date = date;
		return this;
	}

	public PetVisitBuilder withDescription(String desc) {
		this.description = desc;
		return this;
	}

	public Pet.Visit build() {
		return new Pet.Visit(id, description, visit_date, pet_id);
	}

}
