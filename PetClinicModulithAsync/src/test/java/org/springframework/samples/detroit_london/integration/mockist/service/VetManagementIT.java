package org.springframework.samples.detroit_london.integration.mockist.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.repository.VetRepositoryImpl;
import org.springframework.samples.Vet.service.VetManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class VetManagementIT {

	@Autowired
	VetManagement service;

	@SpyBean
	VetRepositoryImpl vetRepository;

	@Test
	void findAll_pageable_delegates_to_repository_and_returns_page() {
		Pageable pageable = PageRequest.of(0, 3);

		Page<Vet> page = service.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getSize()).isEqualTo(3);
		assertThat(page.getContent()).isNotEmpty();

		Vet any = page.getContent().getFirst();
		assertThat(any.getId()).isNotNull();
		assertThat(any.getFirstName()).isNotBlank();
		assertThat(any.getLastName()).isNotBlank();
		assertThat(any.getSpecialties()).isNotNull();

		verify(vetRepository, times(1)).findAll(pageable);
	}

	@Test
	void findAll_collection_delegates_to_repository_and_returns_list() {
		Collection<Vet> vets = service.findAll();

		assertThat(vets).isNotNull();
		assertThat(vets).isNotEmpty();
		assertThat(vets.size()).isEqualTo(6);

		Vet first = vets.iterator().next();
		assertThat(first.getId()).isNotNull();
		assertThat(first.getFirstName()).isNotBlank();
		assertThat(first.getLastName()).isNotBlank();
		assertThat(first.getSpecialties()).isNotNull();

		verify(vetRepository, times(1)).findAll();
	}

}
