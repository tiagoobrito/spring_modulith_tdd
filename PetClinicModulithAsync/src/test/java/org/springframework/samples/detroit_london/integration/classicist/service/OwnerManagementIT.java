package org.springframework.samples.detroit_london.integration.classicist.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerBuilder;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class OwnerManagementIT {

	@Autowired
	OwnerManagement service;

	@Test
	void findById_enriches_pets_and_visits_from_seed() {
		Owner out = service.findById(6);

		assertThat(out.getId()).isEqualTo(6);
		assertThat(out.getPets()).extracting(OwnerPet::getName).containsExactlyInAnyOrder("Samantha", "Max");

		OwnerPet samantha = out.getPets()
			.stream()
			.filter(p -> "Samantha".equals(p.getName()))
			.findFirst()
			.orElseThrow();
		OwnerPet max = out.getPets().stream().filter(p -> "Max".equals(p.getName())).findFirst().orElseThrow();

		assertThat(samantha.getVisits()).isNotEmpty();
		assertThat(max.getVisits()).isNotEmpty();
	}

	@Test
	void findByLastName_returns_paged_owners_and_populates_pets_from_seed() {
		Page<Owner> page = service.findByLastName("Da", PageRequest.of(0, 5));

		assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
		List<Owner> owners = page.getContent();
		assertThat(owners).extracting(Owner::getLastName).allMatch(ln -> ln.startsWith("Da"));

		assertThat(owners.stream().filter(o -> o.getId().equals(2)).findFirst().orElseThrow().getPets())
			.extracting(OwnerPet::getName)
			.contains("Basil");

		assertThat(owners.stream().filter(o -> o.getId().equals(4)).findFirst().orElseThrow().getPets())
			.extracting(OwnerPet::getName)
			.contains("Iggy");
	}

	@Test
	void save_new_owner_then_findById_returns_owner_without_pets_initially() {
		Owner o = OwnerBuilder.anOwner().build();

		Integer id = service.save(o);
		Owner out = service.findById(id);

		assertThat(out.getId()).isEqualTo(id);
		assertThat(out.getFirstName()).isEqualTo("Tiago");
		assertThat(out.getPets()).isEmpty();
	}

	@Test
	void save_existing_owner_updates_fields() {
		Owner updated = OwnerBuilder.anOwner().withId(1).withFirstName("George").withLastName("Franklin").build();
		updated.setAddress("Updated Address");
		updated.setCity("Updated City");

		Integer outId = service.save(updated);
		Owner after = service.findById(outId);

		assertThat(after.getId()).isEqualTo(1);
		assertThat(after.getAddress()).isEqualTo("Updated Address");
		assertThat(after.getCity()).isEqualTo("Updated City");
	}

}
