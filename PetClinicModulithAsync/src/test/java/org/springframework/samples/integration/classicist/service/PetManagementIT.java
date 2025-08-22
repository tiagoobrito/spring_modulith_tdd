package org.springframework.samples.integration.classicist.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.service.PetManagement;
import org.springframework.samples.notifications.AddVisitEvent;
import org.springframework.samples.notifications.AddVisitPet;
import org.springframework.samples.notifications.SavePetEvent;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@AutoConfigureTestDatabase
@Transactional
class PetManagementIT {

	@Autowired
	PetManagement service;

	private static PetType type(int id, String name) {
		PetType t = new PetType();
		t.setId(id);
		t.setName(name);
		return t;
	}

	private static Pet pet(Integer id, String name, Integer ownerId, PetType type, LocalDate birth) {
		Pet p = new Pet();
		p.setId(id);
		p.setName(name);
		p.setOwner_id(ownerId);
		p.setType(type);
		p.setBirthDate(birth);
		return p;
	}

	@Test
	void findPetTypes_returns_seeded_types() {
		Collection<PetType> types = service.findPetTypes();
		assertThat(types).extracting(PetType::getName)
			.containsExactlyInAnyOrder("cat", "dog", "lizard", "snake", "bird", "hamster");
	}

	@Test
	void getPetById_reads_existing_seeded_pet() {
		Pet leo = service.getPetById(1);
		assertThat(leo.getName()).isEqualTo("Leo");
		assertThat(leo.getOwner_id()).isEqualTo(1);
	}

	@Test
	void getPetByName_finds_existing_seeded_pet() {
		Optional<Pet> max = service.getPetByName("Max", false);
		assertThat(max).isPresent();
		assertThat(max.get().getOwner_id()).isEqualTo(6);
	}

	@Test
	void save_new_pet_persists_and_publishes_SavePetEvent() {
		Pet newPet = pet(null, "Zazu", 1, type(2, "dog"), LocalDate.of(2020, 5, 2));

		service.save(newPet);

		Optional<Pet> persisted = service.getPetByName("Zazu", true);
		assertThat(persisted).isPresent();
		assertThat(persisted.get().getOwner_id()).isEqualTo(1);
	}

}
