package org.springframework.samples.petclinic.classicist.model;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.PetTypeFormatter;
import org.springframework.samples.petclinic.repository.OwnerRepository;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PetTypeFormatterTests {

    private final OwnerRepository fakeRepo = new OwnerRepository() {
        @Override
        public List<PetType> findPetTypes() {
            PetType dog = new PetType();
            dog.setName("Dog");
            PetType cat = new PetType();
            cat.setName("Cat");
            return List.of(dog, cat);
        }

        @Override
        public Page<Owner> findByLastName(String lastName, Pageable pageable) {
            return null;
        }

        @Override
        public Owner findById(Integer id) {
            return null;
        }

        @Override
        public void save(Owner owner) {

        }

        @Override
        public Page<Owner> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public Pet findPetById(Integer id) {
            return null;
        }
    };

    private final PetTypeFormatter formatter = new PetTypeFormatter(fakeRepo);

    @Test
    void print_shouldReturnName() {
        PetType pt = new PetType();
        pt.setName("Dog");

        assertEquals("Dog", formatter.print(pt, Locale.ENGLISH));
    }

    @Test
    void parse_shouldReturnMatchingPetType() throws ParseException {
        PetType result = formatter.parse("Cat", Locale.ENGLISH);
        assertEquals("Cat", result.getName());
    }

    @Test
    void parse_shouldThrowParseException() {
        assertThrows(ParseException.class, () -> formatter.parse("Rabbit", Locale.ENGLISH));
    }
}
