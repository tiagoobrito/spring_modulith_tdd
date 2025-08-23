package org.springframework.samples.detroit_london.unitary.classicist.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.model.VisitBuilder;
import org.springframework.samples.Visit.service.VisitManagement;
import org.springframework.samples.detroit_london.fake.data.VisitTestData;
import org.springframework.samples.detroit_london.fake.repository.FakeVisitRepository;
import org.springframework.samples.notifications.AddVisitEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisitManagementTests {

	private FakeVisitRepository visitRepo;

	private CollectingPublisher events;

	private VisitManagement service;

	/** Simple collecting publisher for asserting emitted events. */
	static class CollectingPublisher implements ApplicationEventPublisher {

		final List<Object> published = new ArrayList<>();

		@Override
		public void publishEvent(Object event) {
			published.add(event);
		}

		@SuppressWarnings("unchecked")
		<T> T first(Class<T> type) {
			return (T) published.stream().filter(type::isInstance).findFirst().orElse(null);
		}

	}

	@BeforeEach
	void setUp() {
		visitRepo = new FakeVisitRepository();

		visitRepo.preload(VisitTestData.fido_vaccine(), VisitTestData.fido_teeth(), VisitTestData.bella_annual());

		events = new CollectingPublisher();
		service = new VisitManagement(visitRepo, events);
	}

	@Test
	void save_publishes_AddVisitEvent_and_persists_visit() {
		Visit v = VisitBuilder.aVisit().withId(1003).build();

		service.save(v);

		Visit stored = visitRepo.findById(1003);
		assertThat(stored).isNotNull();
		assertThat(stored.getDescription()).isEqualTo("Checkup");

		AddVisitEvent ev = events.first(AddVisitEvent.class);
		assertThat(ev).isNotNull();
		assertThat(ev.getId()).isEqualTo(1003);
		assertThat(ev.getPet_id()).isEqualTo(10);
		assertThat(ev.getDate()).isEqualTo(LocalDate.of(2024, 10, 10));
		assertThat(ev.getDescription()).isEqualTo("Checkup");
	}

	@Test
	void findAll_returns_all_visits_from_repo() {
		List<Visit> out = service.findAll();

		assertThat(out).hasSize(3);
		assertThat(out).extracting(Visit::getDescription).contains("Vaccination", "Annual check", "Teeth cleaning");
	}

}
