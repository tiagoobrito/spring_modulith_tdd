package org.springframework.samples.unitary.classicist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.Visit.controller.VisitController;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.model.VisitBuilder;
import org.springframework.samples.Visit.service.VisitManagement;
import org.springframework.samples.fake.data.VisitTestData;
import org.springframework.samples.fake.repository.FakeVisitRepository;
import org.springframework.samples.notifications.AddVisitEvent;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class VisitControllerTests {

	private FakeVisitRepository visitRepo;

	private CollectingPublisher events;

	private VisitController controller;

	/** collects published events for assertions */
	static class CollectingPublisher implements ApplicationEventPublisher {

		final java.util.List<Object> published = new ArrayList<>();

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
		visitRepo.preload(VisitTestData.FIDO_VACCINE, VisitTestData.FIDO_TEETH, VisitTestData.BELLA_ANNUAL);

		events = new CollectingPublisher();
		VisitManagement api = new VisitManagement(visitRepo, events);

		controller = new VisitController(api);
	}

	@Test
	void initNewVisitForm_returns_form_and_model_has_blank_visit() {
		ModelMap model = new ModelMap();

		String view = controller.initNewVisitForm(1, 10, model);

		assertThat(view).isEqualTo("pets/createOrUpdateVisitForm");
		assertThat(model.get("visit")).isInstanceOf(Visit.class);
	}

	@Test
	void processNewVisitForm_blank_description_stays_on_form_and_does_not_save() {
		Visit v = VisitBuilder.aVisit().describedAs("").build();
		BindingResult br = new BeanPropertyBindingResult(v, "visit");

		String view = controller.processNewVisitForm(v.getPet_id(), v, new RedirectAttributesModelMap(), br);

		assertThat(view).isEqualTo("pets/createOrUpdateVisitForm");
		assertThat(br.hasFieldErrors("description")).isTrue();
		assertThat(visitRepo.findAll()).hasSize(3);
		assertThat(events.published).isEmpty();
	}

	@Test
	void processNewVisitForm_success_sets_petId_persists_and_redirects() {
		Visit v = VisitBuilder.aVisit().withId(1003).forPet(null).build();
		BindingResult br = new BeanPropertyBindingResult(v, "visit");

		String view = controller.processNewVisitForm(10, v, new RedirectAttributesModelMap(), br);

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
		Visit stored = visitRepo.findById(1003);
		assertThat(stored).isNotNull();
		assertThat(stored.getPet_id()).isEqualTo(10);
		assertThat(stored.getDescription()).isEqualTo("Checkup");

		AddVisitEvent ev = events.first(AddVisitEvent.class);
		assertThat(ev).isNotNull();
		assertThat(ev.getPet_id()).isEqualTo(10);
		assertThat(ev.getDescription()).isEqualTo("Checkup");
		assertThat(ev.getDate()).isEqualTo(LocalDate.of(2024, 10, 10));
	}

	@Test
	void modelAttribute_loadPetWithVisit_puts_and_returns_new_visit() {
		var map = new HashMap<String, Object>();

		Visit out = controller.loadPetWithVisit(10, map);

		assertThat(out).isNotNull();
		assertThat(map.get("visit")).isSameAs(out);
	}

}
