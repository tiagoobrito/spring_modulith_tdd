package org.springframework.samples.Owner.model;

import java.time.LocalDate;

/** Fluent builder for OwnerPet.Visit (record-like) test data. */
public class OwnerPetVisitBuilder {

	private Integer id;

	private Integer pet_id = 1;

	private LocalDate visit_date = LocalDate.now();

	private String description = "Routine check";

	public static OwnerPetVisitBuilder aVisit() {
		return new OwnerPetVisitBuilder();
	}

	public OwnerPetVisitBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public OwnerPetVisitBuilder forPet(Integer petId) {
		this.pet_id = petId;
		return this;
	}

	public OwnerPetVisitBuilder on(LocalDate date) {
		this.visit_date = date;
		return this;
	}

	public OwnerPetVisitBuilder withDescription(String desc) {
		this.description = desc;
		return this;
	}

	public OwnerPet.Visit build() {
		return new OwnerPet.Visit(id, description, visit_date, pet_id);
	}

}
