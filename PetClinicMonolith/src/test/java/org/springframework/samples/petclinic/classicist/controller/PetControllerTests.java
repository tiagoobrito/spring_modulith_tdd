package org.springframework.samples.petclinic.classicist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.controller.PetController;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.PetTypeFormatter;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ PetController.class, PetTypeFormatter.class })
class PetControllerTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private OwnerRepository ownerRepository;

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		PetType cat = new PetType();
		cat.setId(3);
		cat.setName("cat");

		Pet pet = new Pet();
		pet.setId(TEST_PET_ID);
		pet.setType(cat);

		Owner owner = new Owner();
		owner.setId(TEST_OWNER_ID);
		owner.setFirstName("Tiago");
		owner.setLastName("Brito");
		owner.setAddress("Rua do Brito");
		owner.setCity("Paris");
		owner.setTelephone("910791381");
		owner.addPet(pet);

		ownerRepository.save(owner);
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().attributeExists("pet"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(
					post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Betty")
						.param("type", "cat")
						.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(
					post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Betty")
						.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

}
