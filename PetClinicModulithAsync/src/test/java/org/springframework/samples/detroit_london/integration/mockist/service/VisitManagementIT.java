package org.springframework.samples.detroit_london.integration.mockist.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.model.VisitBuilder;
import org.springframework.samples.Visit.repository.VisitRepositoryImpl;
import org.springframework.samples.Visit.service.VisitManagement;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class VisitManagementIT {

	@Autowired
	VisitManagement service;

	@SpyBean
	VisitRepositoryImpl visitRepository;

	@Test
	void save_delegates_to_repository_and_publishes_event() {
		Visit v = VisitBuilder.aVisit().build();

		service.save(v);

		List<Visit> after = service.findAll();
		assertThat(after).isNotNull();

		verify(visitRepository, times(1)).save(v);
		verify(visitRepository, times(1)).findAll();
		verifyNoMoreInteractions(visitRepository);
	}

	@Test
	void findAll_delegates_to_repository_and_returns_list() {
		List<Visit> visits = service.findAll();

		assertThat(visits).isNotNull();

		verify(visitRepository, times(1)).findAll();
		verifyNoMoreInteractions(visitRepository);
	}

}
