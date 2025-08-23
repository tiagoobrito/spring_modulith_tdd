package org.springframework.samples.detroit_london.unitary.classicist.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerBuilder;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.samples.detroit_london.fake.data.OwnerPetTestData;
import org.springframework.samples.detroit_london.fake.data.OwnerTestData;
import org.springframework.samples.detroit_london.fake.repository.FakeOwnerPetRepository;
import org.springframework.samples.detroit_london.fake.repository.FakeOwnerRepository;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerManagementTests {

	private FakeOwnerRepository ownerRepo;

	private OwnerManagement service;

	@BeforeEach
	void setUp() {
		ownerRepo = new FakeOwnerRepository();
		FakeOwnerPetRepository petRepo = new FakeOwnerPetRepository();

		ownerRepo.preload(OwnerTestData.JOHN_DOE, OwnerTestData.JANE_SMITH, OwnerTestData.ALICE_JOHNSON,
				OwnerTestData.ANTHONY_SMITH);
		petRepo.preloadPets(OwnerPetTestData.FIDO, OwnerPetTestData.BELLA, OwnerPetTestData.MAX, OwnerPetTestData.LULU,
				OwnerPetTestData.LALA);
		petRepo.preloadVisits(OwnerPetTestData.BELLA_V1, OwnerPetTestData.FIDO_V1, OwnerPetTestData.FIDO_V2);

		service = new OwnerManagement(ownerRepo, petRepo);
	}

	@Test
	void findById_returns_owner_with_pets_and_visits() {
		Owner out = service.findById(1);

		assertThat(out.getId()).isEqualTo(1);
		assertThat(out.getPets()).extracting(OwnerPet::getName).containsExactly("Fido", "Bella");
		OwnerPet fido = out.getPets().get(0);
		assertThat(fido.getVisits()).hasSize(2);
		assertThat(fido.getVisits().iterator().next().description()).isEqualTo("Vaccination");
	}

	@Test
	void save_new_owner_returns_generated_id_and_persists() {
		Owner newOwner = OwnerBuilder.anOwner().build();

		Integer id = service.save(newOwner);

		assertThat(id).isNotNull();
		assertThat(ownerRepo.findById(id).getFirstName()).isEqualTo("Tiago");
	}

	@Test
	void save_existing_owner_updates_fields() {
		Owner updated = OwnerTestData.JOHN_DOE;
		updated.setAddress("New Address");

		Integer outId = service.save(updated);

		assertThat(outId).isEqualTo(updated.getId());
		Owner after = ownerRepo.findById(updated.getId());
		assertThat(after.getFirstName()).isEqualTo("John");
		assertThat(after.getAddress()).isEqualTo("New Address");
	}

	@Test
	void findByLastName_populates_pets_in_page() {
		Page<Owner> page = service.findByLastName("Smi", PageRequest.of(0, 10));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).allSatisfy(o -> assertThat(o.getPets()).isNotEmpty());
	}

	@Test
	void findPetByOwner_delegates_and_returns_pets() {
		List<OwnerPet> result = service.findPetByOwner(1);

		assertThat(result).extracting(OwnerPet::getName).containsExactly("Fido", "Bella");
	}

	@Test
	void findByName_delegates_and_returns_optional() {
		Optional<Owner> found = service.findByName("John", "Doe");

		assertThat(found).isPresent();
		assertThat(found.get().getId()).isEqualTo(1);
	}

}
