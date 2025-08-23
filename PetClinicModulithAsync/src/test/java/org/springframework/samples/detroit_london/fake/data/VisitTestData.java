package org.springframework.samples.detroit_london.fake.data;

import org.springframework.samples.Visit.model.Visit;

import java.time.LocalDate;

import static org.springframework.samples.Visit.model.VisitBuilder.aVisit;

public final class VisitTestData {

	public static final Visit FIDO_VACCINE = aVisit().withId(1000)
		.forPet(10)
		.on(LocalDate.of(2024, 6, 1))
		.describedAs("Vaccination")
		.build();

	public static final Visit FIDO_TEETH = aVisit().withId(1001)
		.forPet(10)
		.on(LocalDate.of(2024, 10, 5))
		.describedAs("Teeth cleaning")
		.build();

	public static final Visit BELLA_ANNUAL = aVisit().withId(1002)
		.forPet(11) // let fake assign id if needed
		.on(LocalDate.of(2024, 2, 20))
		.describedAs("Annual check")
		.build();

}
