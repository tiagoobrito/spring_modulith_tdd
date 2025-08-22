package org.springframework.samples.Visit.model;

import java.time.LocalDate;

/** Fluent builder for Visit test data. */
public class VisitBuilder {

	private Integer id;

	private LocalDate date = LocalDate.of(2024, 10, 10);

	private String description = "Checkup";

	private Integer pet_id = 10;

	public static VisitBuilder aVisit() {
		return new VisitBuilder();
	}

	public VisitBuilder withId(Integer id) {
		this.id = id;
		return this;
	}

	public VisitBuilder on(LocalDate date) {
		this.date = date;
		return this;
	}

	public VisitBuilder describedAs(String description) {
		this.description = description;
		return this;
	}

	public VisitBuilder forPet(Integer petId) {
		this.pet_id = petId;
		return this;
	}

	public Visit build() {
		Visit v = new Visit();
		v.setId(id);
		v.setDate(date);
		v.setDescription(description);
		v.setPet_id(pet_id);
		return v;
	}

}
