package org.springframework.samples.integration.classicist.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Owner.model.Owner;
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
	OwnerManagement service; // ONLY the service is autowired

	@Test
	void findById_enriches_pets_and_visits_from_seed() {
		// Seed says: owner 6 (Jean Coleman) has Samantha(id=7) & Max(id=8) with visits.
		Owner out = service.findById(6);

		assertThat(out.getId()).isEqualTo(6);
		assertThat(out.getPets()).extracting(OwnerPet::getName).containsExactlyInAnyOrder("Samantha", "Max");

		// Samantha (id 7) has visits; Max (id 8) has visits
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
		// Davis: Betty (id 2) & Harold (id 4), both have pets (Basil & Iggy)
		Page<Owner> page = service.findByLastName("Da", PageRequest.of(0, 5));

		assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
		List<Owner> owners = page.getContent();
		assertThat(owners).extracting(Owner::getLastName).allMatch(ln -> ln.startsWith("Da"));

		// Ensure both have pets attached
		assertThat(owners.stream().filter(o -> o.getId().equals(2)).findFirst().orElseThrow().getPets())
			.extracting(OwnerPet::getName)
			.contains("Basil");

		assertThat(owners.stream().filter(o -> o.getId().equals(4)).findFirst().orElseThrow().getPets())
			.extracting(OwnerPet::getName)
			.contains("Iggy");
	}

	@Test
	void save_new_owner_then_findById_returns_owner_without_pets_initially() {
		Owner o = owner(null, "Alice", "Johnson");

		Integer id = service.save(o);
		Owner out = service.findById(id);

		assertThat(out.getId()).isEqualTo(id);
		assertThat(out.getFirstName()).isEqualTo("Alice");
		assertThat(out.getPets()).isEmpty(); // no pets created for this new owner
	}

	@Test
	void save_existing_owner_updates_fields() {
		// Update an existing seeded owner — e.g., George Franklin (id 1)
		Owner updated = owner(1, "George", "Franklin");
		updated.setAddress("Updated Address");
		updated.setCity("Updated City");

		Integer outId = service.save(updated);
		Owner after = service.findById(outId);

		assertThat(after.getId()).isEqualTo(1);
		assertThat(after.getAddress()).isEqualTo("Updated Address");
		assertThat(after.getCity()).isEqualTo("Updated City");
	}

	// helper
	private static Owner owner(Integer id, String fn, String ln) {
		Owner o = new Owner();
		o.setId(id);
		o.setFirstName(fn);
		o.setLastName(ln);
		o.setAddress("Addr");
		o.setCity("City");
		o.setTelephone("999");
		return o;
	}

}
