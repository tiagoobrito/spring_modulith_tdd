package org.springframework.samples.detroit_london.unitary.mockist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.service.VetManagement;
import org.springframework.samples.Vet.service.VetRepository;
import org.springframework.samples.detroit_london.fake.data.VetTestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetManagementTests {

	@Mock
	VetRepository vetRepository;

	@InjectMocks
	VetManagement service;

	@Test
	void findAll_paged_delegates_and_returns_page() {
		PageRequest p0 = PageRequest.of(0, 2);
		Vet james = VetTestData.JAMES_CARTER;
		Vet helen = VetTestData.HELEN_LEARY;
		when(vetRepository.findAll(p0)).thenReturn(new PageImpl<>(List.of(james, helen), p0, 3));

		Page<Vet> page = service.findAll(p0);

		assertThat(page.getTotalElements()).isEqualTo(3);
		assertThat(page.getContent()).containsExactly(james, helen);
		verify(vetRepository).findAll(p0);
		verifyNoMoreInteractions(vetRepository);
	}

	@Test
	void findAll_unpaged_delegates_and_returns_collection() {
		Vet james = VetTestData.JAMES_CARTER;
		Vet helen = VetTestData.HELEN_LEARY;
		Vet linda = VetTestData.LINDA_DOUGLAS;
		when(vetRepository.findAll()).thenReturn(List.of(james, helen, linda));

		var out = service.findAll();

		assertThat(out).containsExactly(james, helen, linda);
		verify(vetRepository).findAll();
		verifyNoMoreInteractions(vetRepository);
	}

}
