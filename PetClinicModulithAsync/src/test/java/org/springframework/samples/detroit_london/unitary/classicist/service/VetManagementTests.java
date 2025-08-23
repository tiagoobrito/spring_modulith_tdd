package org.springframework.samples.detroit_london.unitary.classicist.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.service.VetManagement;
import org.springframework.samples.detroit_london.fake.data.VetTestData;
import org.springframework.samples.detroit_london.fake.repository.FakeVetRepository;

import static org.assertj.core.api.Assertions.assertThat;

class VetManagementTests {

	private FakeVetRepository vetRepo;

	private VetManagement service;

	@BeforeEach
	void setUp() {
		vetRepo = new FakeVetRepository();

		vetRepo.preload(VetTestData.JAMES_CARTER, VetTestData.HELEN_LEARY, VetTestData.LINDA_DOUGLAS);

		service = new VetManagement(vetRepo);
	}

	@Test
	void findAll_paged_returns_page_from_repo() {
		PageRequest page0 = PageRequest.of(0, 2);

		Page<Vet> page = service.findAll(page0);

		assertThat(page.getTotalElements()).isEqualTo(3);
		assertThat(page.getContent()).hasSize(2);
		// our FakeVetRepository sorts by id asc by default
		assertThat(page.getContent().get(0).getId()).isLessThan(page.getContent().get(1).getId());
	}

	@Test
	void findAll_unpaged_returns_all_vets() {
		var all = service.findAll();

		assertThat(all).hasSize(3);
		assertThat(all).extracting(Vet::getFirstName).contains("James", "Helen", "Linda");
	}

}
