package org.springframework.samples.detroit_london.unitary.classicist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.Owner.controller.OwnerController;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerBuilder;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.model.OwnerPetBuilder;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.samples.detroit_london.fake.data.OwnerPetTestData;
import org.springframework.samples.detroit_london.fake.data.OwnerTestData;
import org.springframework.samples.detroit_london.fake.repository.FakeOwnerPetRepository;
import org.springframework.samples.detroit_london.fake.repository.FakeOwnerRepository;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerControllerTests {

	private FakeOwnerRepository ownerRepo;

	private FakeOwnerPetRepository petRepo;

	private OwnerController controller;

	@BeforeEach
	void setUp() {
		ownerRepo = new FakeOwnerRepository();
		petRepo = new FakeOwnerPetRepository();

		ownerRepo.preload(OwnerTestData.john_doe(), OwnerTestData.jane_smith(), OwnerTestData.alice_johnson(),
				OwnerTestData.anthony_smith());
		petRepo.preloadPets(OwnerPetTestData.fido(), OwnerPetTestData.bella(), OwnerPetTestData.max(),
				OwnerPetTestData.lulu(), OwnerPetTestData.lala());
		petRepo.preloadVisits(OwnerPetTestData.bella_v1(), OwnerPetTestData.fido_v1(), OwnerPetTestData.fido_v2());

		// real service under test’s dependency
		OwnerManagement api = new OwnerManagement(ownerRepo, petRepo);
		controller = new OwnerController(api);
	}

	private static Owner owner(Integer id, String fn, String ln) {
		Owner o = new Owner();
		o.setId(id);
		o.setFirstName(fn);
		o.setLastName(ln);
		o.setAddress("Addr");
		o.setCity("City");
		o.setTelephone("999");
		return o;
	}

	private static OwnerPet pet(Integer id, Integer ownerId, String name, String type) {
		OwnerPet p = new OwnerPet();
		p.setId(id);
		p.setOwner_id(ownerId);
		p.setName(name);
		p.setType_name(type);
		p.setBirthDate(LocalDate.of(2020, 1, 1));
		return p;
	}

	@Test
	void initCreationForm_returns_form_with_empty_owner() {
		var model = new java.util.HashMap<String, Object>();

		String view = controller.initCreationForm(model);

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		assertThat(model).containsKey("owner");
		assertThat(model.get("owner")).isInstanceOf(Owner.class);
	}

	@Test
	void processCreationForm_duplicate_names_adds_field_errors_and_stays_on_form() {
		ownerRepo.save(owner(null, "John", "Doe")); // duplicate will be detected
		Owner input = owner(null, "John", "Doe");
		BindingResult br = new BeanPropertyBindingResult(input, "owner");
		var redirect = new RedirectAttributesModelMap();
		var model = new ModelMap();

		String view = controller.processCreationForm(input, br, redirect, model);

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		assertThat(br.hasFieldErrors("firstName")).isTrue();
		assertThat(br.hasFieldErrors("lastName")).isTrue();
	}

	@Test
	void processCreationForm_with_binding_errors_stays_on_form() {
		Owner input = owner(null, "Jane", "Smith");
		BindingResult br = new BeanPropertyBindingResult(input, "owner");
		br.rejectValue("lastName", "invalid"); // simulate @Valid error
		var redirect = new RedirectAttributesModelMap();

		String view = controller.processCreationForm(input, br, redirect, new ModelMap());

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
	}

	@Test
	void processCreationForm_success_redirects_to_details() {
		Owner input = OwnerBuilder.anOwner().build();
		BindingResult br = new BeanPropertyBindingResult(input, "owner");
		var redirect = new RedirectAttributesModelMap();

		String view = controller.processCreationForm(input, br, redirect, new ModelMap());

		assertThat(view).startsWith("redirect:/owners/");
		// id actually persisted in fake repo
		Integer id = Integer.parseInt(view.substring("redirect:/owners/".length()));
		assertThat(ownerRepo.findById(id)).isNotNull();
	}

	@Test
	void initFindForm_returns_findOwners_view() {
		String view = controller.initFindForm();
		assertThat(view).isEqualTo("owners/findOwners");
	}

	@Test
	void processFindForm_no_results_adds_error_and_returns_findOwners() {
		Model model = new ExtendedModelMap();
		Owner probe = new Owner();
		probe.setLastName("Nobody");
		BindingResult br = new BeanPropertyBindingResult(probe, "owner");

		String view = controller.processFindForm(1, probe, br, model);

		assertThat(view).isEqualTo("owners/findOwners");
		assertThat(br.hasFieldErrors("lastName")).isTrue();
	}

	@Test
	void processFindForm_single_result_redirects_to_details() {
		Owner saved = OwnerTestData.john_doe();
		Model model = new ExtendedModelMap();
		Owner probe = new Owner();
		probe.setLastName("Doe");
		BindingResult br = new BeanPropertyBindingResult(probe, "owner");

		String view = controller.processFindForm(1, probe, br, model);

		assertThat(view).isEqualTo("redirect:/owners/" + saved.getId());
	}

	@Test
	void processFindForm_multiple_results_returns_list_with_pagination_model() {
		Model model = new ExtendedModelMap();
		Owner probe = new Owner();
		probe.setLastName("Smi");
		BindingResult br = new BeanPropertyBindingResult(probe, "owner");

		String view = controller.processFindForm(1, probe, br, model);

		assertThat(view).isEqualTo("owners/ownersList");
		assertThat(model.getAttribute("currentPage")).isEqualTo(1);
		assertThat((Integer) model.getAttribute("totalPages")).isGreaterThanOrEqualTo(1);
		assertThat((Long) model.getAttribute("totalItems")).isEqualTo(2L);
		assertThat((List<?>) model.getAttribute("listOwners")).hasSize(2);
	}

	@Test
	void initUpdateOwnerForm_loads_owner_and_returns_form() {
		Owner saved = ownerRepo.save(owner(null, "Mike", "Miles"));
		ModelMap model = new ModelMap();

		String view = controller.initUpdateOwnerForm(saved.getId(), model);

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		assertThat(model.get("owner")).isInstanceOf(Owner.class);
		assertThat(((Owner) model.get("owner")).getId()).isEqualTo(saved.getId());
	}

	@Test
	void processUpdateOwnerForm_with_errors_stays_on_form_and_sets_message() {
		Owner saved = ownerRepo.save(owner(null, "Carl", "Kent"));
		Owner update = owner(saved.getId(), "Carl", "Kent");
		BindingResult br = new BeanPropertyBindingResult(update, "owner");
		br.reject("bad");
		ModelMap model = new ModelMap();
		var redirect = new RedirectAttributesModelMap();

		String view = controller.processUpdateOwnerForm(update, br, saved.getId(), redirect, model);

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		assertThat(model.get("ownerMessage")).isEqualTo("There was an error updating the owner.");
	}

	@Test
	void processUpdateOwnerForm_success_sets_pets_and_redirects() {
		Owner saved = ownerRepo.save(OwnerBuilder.anOwner().build());
		petRepo.save(true, OwnerPetBuilder.aPet().withOwnerId(saved.getId()).build());
		Owner update = OwnerBuilder.anOwner().withAddress("Updated Address").build();
		BindingResult br = new BeanPropertyBindingResult(update, "owner");
		ModelMap model = new ModelMap();
		var redirect = new RedirectAttributesModelMap();

		String view = controller.processUpdateOwnerForm(update, br, saved.getId(), redirect, model);

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
		Owner after = ownerRepo.findById(saved.getId());
		assertThat(after.getPets()).extracting(OwnerPet::getName).containsExactly("Luna");
	}

	@Test
	void showOwner_returns_ownerDetails_with_owner_in_model() {
		Owner saved = ownerRepo.save(owner(null, "Ana", "Silva"));

		ModelAndView mav = controller.showOwner(saved.getId());

		assertThat(mav.getViewName()).isEqualTo("owners/ownerDetails");
		assertThat(mav.getModel()).containsValue(saved);
	}

}
