package org.springframework.samples.fake.data;

import org.springframework.samples.Owner.model.Owner;

import static org.springframework.samples.Owner.model.OwnerBuilder.anOwner;

public class OwnerTestData {

	public static Owner JOHN_DOE = anOwner().withId(1)
		.withFirstName("John")
		.withLastName("Doe")
		.withAddress("123 Main St")
		.withCity("Springfield")
		.withTelephone("111111111")
		.build();

	public static Owner JANE_SMITH = anOwner().withId(2)
		.withFirstName("Jane")
		.withLastName("Smith")
		.withAddress("456 Elm St")
		.withCity("Metropolis")
		.withTelephone("222222222")
		.build();

	public static Owner ALICE_JOHNSON = anOwner().withId(3)
		.withFirstName("Alice")
		.withLastName("Johnson")
		.withAddress("789 Oak Ave")
		.withCity("Gotham")
		.withTelephone("333333333")
		.build();

	public static Owner ANTHONY_SMITH = anOwner().withId(4)
		.withFirstName("Anthony")
		.withLastName("Smith")
		.withAddress("123 Oak Ave")
		.withCity("Wakanda")
		.withTelephone("444444444")
		.build();

}
