package org.springframework.samples.detroit_london.integration.classicist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.Pet.PetExternalAPI;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class PetControllerIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    PetExternalAPI service;

    @Test
    void get_newPetForm_renders_template_and_types() throws Exception {
        mvc.perform(get("/owners/6/pets/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeExists("pet"))
                .andExpect(model().attributeExists("types"));
    }

    @Test
    void post_newPet_with_future_birthDate_stays_on_form() throws Exception {
        mvc.perform(post("/owners/6/pets/new")
                        .param("name", "Rocky")
                        .param("birthDate", LocalDate.now().plusDays(1).toString())
                        .param("type.id", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"));
    }

    @Test
    void post_newPet_success_persists_and_redirects() throws Exception {
        mvc.perform(post("/owners/6/pets/new")
                        .param("name", "Buddy")
                        .param("birthDate", "2019-05-20")
                        .param("type.id", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/6"));

        Optional<Pet> saved = service.getPetByName("Buddy", true);
        assertThat(saved).isPresent();
        assertThat(saved.get().getOwner_id()).isEqualTo(6);
        assertThat(saved.get().getBirthDate()).isEqualTo(LocalDate.of(2019, 5, 20));
    }

    @Test
    void get_editForm_loads_pet_and_renders_template() throws Exception {
        // Assumes petId=7 exists and belongs to ownerId=6 in seed
        mvc.perform(get("/owners/6/pets/7/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeExists("pet"))
                .andExpect(model().attributeExists("types"));
    }

    @Test
    void post_editPet_with_future_birthDate_stays_on_form() throws Exception {
        mvc.perform(post("/owners/6/pets/7/edit")
                        .param("id", "7")
                        .param("name", "Samantha")
                        .param("birthDate", LocalDate.now().plusDays(2).toString())
                        .param("type.id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"));
    }

    @Test
    void post_editPet_success_updates_and_redirects() throws Exception {
        mvc.perform(post("/owners/6/pets/7/edit")
                        .param("id", "7")
                        .param("name", "Samantha edited")
                        .param("birthDate", "2012-09-04")
                        .param("type.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/6"));

        Optional<Pet> updated = service.getPetByName("Samantha edited", false);
        assertThat(updated).isPresent();
        assertThat(updated.get().getId()).isEqualTo(7);
        assertThat(updated.get().getOwner_id()).isEqualTo(6);
    }
}
