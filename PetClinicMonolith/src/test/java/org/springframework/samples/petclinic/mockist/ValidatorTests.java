package org.springframework.samples.petclinic.mockist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Person;

class ValidatorTests {

	@Test
	void shouldNotValidateWhenFirstNameEmpty() {
		Person person = new Person();
		person.setFirstName("");
		person.setLastName("smith");

		Validator validator = mock(Validator.class);

		@SuppressWarnings("unchecked")
		ConstraintViolation<Person> violation = mock(ConstraintViolation.class);

		Path propertyPath = mock(Path.class);
		when(propertyPath.toString()).thenReturn("firstName");
		when(violation.getPropertyPath()).thenReturn(propertyPath);
		when(violation.getMessage()).thenReturn("must not be blank");

		when(validator.validate(person)).thenReturn(Set.of(violation));

		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Person> retrievedViolation = constraintViolations.iterator().next();
		assertThat(retrievedViolation.getPropertyPath().toString()).isEqualTo("firstName");
		assertThat(retrievedViolation.getMessage()).isEqualTo("must not be blank");
	}

}