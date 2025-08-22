package org.springframework.samples.unitary.mockist.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.Visit.VisitExternalAPI;
import org.springframework.samples.Visit.controller.VisitController;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.model.VisitBuilder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitControllerTests {

	@Mock
	VisitExternalAPI api;

	@InjectMocks
	VisitController controller;

	@Test
	void initNewVisitForm_returns_form_and_model_has_blank_visit() {
		ModelMap model = new ModelMap();

		String view = controller.initNewVisitForm(1, 10, model);

		assertThat(view).isEqualTo("pets/createOrUpdateVisitForm");
		assertThat(model.get("visit")).isInstanceOf(Visit.class);
		verifyNoInteractions(api);
	}

	@Test
	void processNewVisitForm_blank_description_stays_on_form_and_does_not_call_api() {
		Visit v = VisitBuilder.aVisit().describedAs("").build();
		BindingResult br = new BeanPropertyBindingResult(v, "visit");

		String view = controller.processNewVisitForm(v.getPet_id(), v, new RedirectAttributesModelMap(), br);

		assertThat(view).isEqualTo("pets/createOrUpdateVisitForm");
		assertThat(br.hasFieldErrors("description")).isTrue();
		verifyNoInteractions(api);
	}

	@Test
	void processNewVisitForm_success_sets_petId_calls_api_and_redirects() {
		Visit v = VisitBuilder.aVisit().withId(1000).forPet(null).build();
		BindingResult br = new BeanPropertyBindingResult(v, "visit");

		String view = controller.processNewVisitForm(10, v, new RedirectAttributesModelMap(), br);

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
		verify(api).save(argThat(saved -> saved.getId().equals(1000) && saved.getPet_id().equals(10)
				&& saved.getDescription().equals("Checkup") && saved.getDate().equals(LocalDate.of(2024, 10, 10))));
		verifyNoMoreInteractions(api);
	}

	@Test
	void modelAttribute_loadPetWithVisit_puts_and_returns_new_visit() {
		var map = new HashMap<String, Object>();

		Visit out = controller.loadPetWithVisit(10, map);

		assertThat(out).isNotNull();
		assertThat(map.get("visit")).isSameAs(out);
		verifyNoInteractions(api);
	}

}
