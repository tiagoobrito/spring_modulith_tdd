package org.springframework.samples.detroit_london.unitary.mockist.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Vet.VetExternalAPI;
import org.springframework.samples.Vet.controller.VetController;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.model.Vets;
import org.springframework.samples.detroit_london.fake.data.VetTestData;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetControllerTests {

	@Mock
	VetExternalAPI api;

	@InjectMocks
	VetController controller;

	@Test
	void showVetList_populates_pagination_model_and_returns_view() {
		PageRequest expected = PageRequest.of(0, 5);
		Vet james = VetTestData.JAMES_CARTER;
		Vet helen = VetTestData.HELEN_LEARY;
		Vet linda = VetTestData.LINDA_DOUGLAS;
		Page<Vet> page = new PageImpl<>(List.of(james, helen, linda), expected, 3);

		when(api.findAll(any(PageRequest.class))).thenReturn(page);

		Model model = new ExtendedModelMap();
		String view = controller.showVetList(1, model);

		assertThat(view).isEqualTo("vets/vetList");
		assertThat(model.getAttribute("currentPage")).isEqualTo(1);
		assertThat(model.getAttribute("totalPages")).isEqualTo(1);
		assertThat(model.getAttribute("totalItems")).isEqualTo(3L);

		@SuppressWarnings("unchecked")
		List<Vet> listVets = (List<Vet>) model.getAttribute("listVets");
		assertThat(listVets).contains(james, helen, linda);

		verify(api).findAll(any(PageRequest.class));
		verifyNoMoreInteractions(api);
	}

	@Test
	void showResourcesVetList_returns_all_vets_in_body() {
		Vet james = VetTestData.JAMES_CARTER;
		Vet helen = VetTestData.HELEN_LEARY;
		when(api.findAll()).thenReturn(List.of(james, helen));

		Vets out = controller.showResourcesVetList();

		assertThat(out).isNotNull();
		assertThat(out.getVetList()).contains(james, helen);

		verify(api).findAll();
		verifyNoMoreInteractions(api);
	}

}
