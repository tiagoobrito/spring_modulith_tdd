package org.springframework.samples.unitary.classicist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.Vet.controller.VetController;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.model.Vets;
import org.springframework.samples.Vet.service.VetManagement;
import org.springframework.samples.fake.data.VetTestData;
import org.springframework.samples.fake.repository.FakeVetRepository;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VetControllerTests {

	private VetController controller;

	@BeforeEach
	void setUp() {
		FakeVetRepository vetRepo = new FakeVetRepository();

		vetRepo.preload(VetTestData.JAMES_CARTER, VetTestData.HELEN_LEARY, VetTestData.LINDA_DOUGLAS);

		// real service (still unit: uses fake repo)
		VetManagement service = new VetManagement(vetRepo);
		controller = new VetController(service);
	}

	@Test
	void showVetList_populates_pagination_model_and_returns_view() {
		Model model = new ExtendedModelMap();

		String view = controller.showVetList(1, model);

		assertThat(view).isEqualTo("vets/vetList");
		assertThat(model.getAttribute("currentPage")).isEqualTo(1);
		assertThat(model.getAttribute("totalPages")).isEqualTo(1);
		assertThat(model.getAttribute("totalItems")).isEqualTo(3L);

		@SuppressWarnings("unchecked")
		List<Vet> listVets = (List<Vet>) model.getAttribute("listVets");
		assertThat(listVets).hasSize(3);
		assertThat(listVets).extracting(Vet::getFirstName).contains("James", "Helen", "Linda");
	}

	@Test
	void showResourcesVetList_returns_all_vets_in_body() {
		Vets out = controller.showResourcesVetList();

		assertThat(out).isNotNull();
		assertThat(out.getVetList()).hasSize(3);
		assertThat(out.getVetList()).extracting(Vet::getLastName).contains("Carter", "Leary", "Douglas");
	}

}
