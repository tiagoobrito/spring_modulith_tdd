package org.springframework.samples.detroit_london.integration.mockist.service;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerBuilder;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.samples.Owner.service.OwnerPetRepository;
import org.springframework.samples.Owner.service.OwnerRepository;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class OwnerManagementIT {

	@Autowired
	OwnerManagement service;

	@SpyBean
	OwnerRepository ownerRepository;

	@SpyBean
	OwnerPetRepository ownerPetRepository;

	@Test
	void findById_calls_repos_in_order_and_enriches_pets_and_visits_from_seed() {
		Owner out = service.findById(6);

		assertThat(out.getPets()).extracting(OwnerPet::getName).contains("Samantha", "Max");
		assertThat(out.getPets()
			.stream()
			.filter(p -> "Samantha".equals(p.getName()))
			.findFirst()
			.orElseThrow()
			.getVisits()).isNotEmpty();
		assertThat(out.getPets().stream().filter(p -> "Max".equals(p.getName())).findFirst().orElseThrow().getVisits())
			.isNotEmpty();

		// Verify interaction order
		InOrder in = inOrder(ownerRepository, ownerPetRepository);
		in.verify(ownerRepository).findById(6);
		in.verify(ownerPetRepository).findPetByOwnerId(6);
		// visits fetched for each pet id (7 and 8 in seed)
		verify(ownerPetRepository, atLeast(2)).findVisitByPetId(anyInt());
		verifyNoMoreInteractions(ownerRepository, ownerPetRepository);
	}

	@Test
	void findByLastName_calls_owner_repo_and_enriches_each_owner_with_pets() {
		// "Da" matches Davis owners (2, 4)
		var page = service.findByLastName("Da", PageRequest.of(0, 5));

		assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
		verify(ownerRepository).findByLastName(eq("Da"), any());
		// Enrichment uses petRepository per owner
		verify(ownerPetRepository, atLeast(2)).findPetByOwnerId(anyInt());
		verifyNoMoreInteractions(ownerRepository, ownerPetRepository);
	}

	@Test
	void save_new_owner_delegates_to_owner_repo_save() {
		Integer id = service.save(OwnerBuilder.anOwner().withFirstName("Alice").withLastName("Johnson").build());

		assertThat(id).isNotNull();
		verify(ownerRepository).save(argThat(o -> o.getId().equals(id) && o.getFirstName().equals("Alice")));
		verifyNoMoreInteractions(ownerRepository);
		verifyNoInteractions(ownerPetRepository);
	}

	@Test
	void save_existing_owner_loads_then_updates_then_saves() {
		Integer outId = service.save(OwnerBuilder.anOwner().withId(1).withFirstName("George").withLastName("Franklin").build());
		assertThat(outId).isEqualTo(1);

		InOrder in = inOrder(ownerRepository);
		in.verify(ownerRepository).findById(1);
		in.verify(ownerRepository)
			.save(argThat(o -> o.getId().equals(1) && o.getFirstName().equals("George")
					&& o.getLastName().equals("Franklin")));
		verifyNoMoreInteractions(ownerRepository);
		verifyNoInteractions(ownerPetRepository);
	}

}
