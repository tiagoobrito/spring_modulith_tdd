package org.springframework.samples.detroit_london.fake.data;

import org.springframework.samples.Visit.model.Visit;

import java.time.LocalDate;

import static org.springframework.samples.Visit.model.VisitBuilder.aVisit;

public class VisitTestData {

	public static Visit fido_vaccine() {
		return aVisit().withId(1000).forPet(10).on(LocalDate.of(2024, 6, 1)).describedAs("Vaccination").build();
	}

	public static Visit fido_teeth() {
		return aVisit().withId(1001).forPet(10).on(LocalDate.of(2024, 10, 5)).describedAs("Teeth cleaning").build();
	}

	public static Visit bella_annual() {
		return aVisit().withId(1002)
			.forPet(11) // let fake assign id if needed
			.on(LocalDate.of(2024, 2, 20))
			.describedAs("Annual check")
			.build();
	}

}
