package org.springframework.samples.detroit_london.unitary.mockist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.model.VisitBuilder;
import org.springframework.samples.Visit.service.VisitManagement;
import org.springframework.samples.Visit.service.VisitRepository;
import org.springframework.samples.detroit_london.fake.data.VisitTestData;
import org.springframework.samples.notifications.AddVisitEvent;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitManagementTests {

	@Mock
	VisitRepository visitRepository;

	@Mock
	ApplicationEventPublisher events;

	@InjectMocks
	VisitManagement service;

	@Test
	void save_publishes_AddVisitEvent_then_persists_visit() {
		Visit v = VisitBuilder.aVisit().withId(1000).build();

		service.save(v);

		InOrder inOrder = inOrder(events, visitRepository);
		inOrder.verify(events)
			.publishEvent((Object) argThat(
					o -> (o instanceof AddVisitEvent a) && a.getId().equals(1000) && a.getPet_id().equals(10)
							&& a.getDate().equals(LocalDate.of(2024, 10, 10)) && a.getDescription().equals("Checkup")));
		inOrder.verify(visitRepository).save(v);
		verifyNoMoreInteractions(visitRepository, events);
	}

	@Test
	void findAll_delegates_to_repo_and_returns_list() {
		Visit v1 = VisitTestData.fido_vaccine();
		Visit v2 = VisitTestData.fido_teeth();
		when(visitRepository.findAll()).thenReturn(List.of(v1, v2));

		List<Visit> out = service.findAll();

		assertThat(out).isNotNull();
		verify(visitRepository).findAll();
		verifyNoMoreInteractions(visitRepository);
		verifyNoInteractions(events);
	}

}
