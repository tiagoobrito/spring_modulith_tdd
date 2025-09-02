package org.springframework.samples.detroit_london.integration.mockist.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.service.PetManagement;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
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

    @SpyBean
    PetManagement serviceSpy;

    @AfterEach
    void resetSpy() {
        reset(serviceSpy);
    }

    @Test
    void get_newPetForm_calls_findPetTypes_and_renders_form() throws Exception {
        mvc.perform(get("/owners/6/pets/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeExists("pet", "types"));

        verify(serviceSpy, atLeastOnce()).findPetTypes();
        verifyNoMoreInteractions(serviceSpy);
    }

    @Test
    void post_newPet_invalid_birthDate_does_not_call_save_and_stays_on_form() throws Exception {
        mvc.perform(post("/owners/6/pets/new")
                        .param("name", "Rocky")
                        .param("birthDate", LocalDate.now().plusDays(1).toString())
                        .param("type.id", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"));

        verify(serviceSpy, atLeastOnce()).findPetTypes(); // due to @ModelAttribute
        verify(serviceSpy, never()).save(any(Pet.class));
        verifyNoMoreInteractions(serviceSpy);
    }

    @Test
    void post_newPet_success_calls_save_with_ownerId_and_redirects() throws Exception {
        mvc.perform(post("/owners/6/pets/new")
                        .param("name", "Buddy")
                        .param("birthDate", "2019-05-20")
                        .param("type.id", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/6"));

        verify(serviceSpy, atLeastOnce()).findPetTypes(); // @ModelAttribute
        verify(serviceSpy).save(argThat(p ->
                "Buddy".equals(p.getName())
                        && LocalDate.of(2019, 5, 20).equals(p.getBirthDate())
                        && Integer.valueOf(6).equals(p.getOwner_id())
                        && p.getType() != null
                        && Integer.valueOf(2).equals(p.getType().getId())
        ));
        verifyNoMoreInteractions(serviceSpy);
    }

    @Test
    void get_editForm_calls_getPetById_and_renders_form() throws Exception {
        mvc.perform(get("/owners/6/pets/7/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeExists("pet", "types"));

        verify(serviceSpy, atLeastOnce()).findPetTypes(); // @ModelAttribute
        verify(serviceSpy, atLeastOnce()).getPetById(7);
        verifyNoMoreInteractions(serviceSpy);
    }

    @Test
    void post_editPet_future_birthDate_does_not_save_and_stays_on_form() throws Exception {
        mvc.perform(post("/owners/6/pets/7/edit")
                        .param("id", "7")
                        .param("name", "Samantha")
                        .param("birthDate", LocalDate.now().plusDays(2).toString())
                        .param("type.id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"));

        verify(serviceSpy, atLeastOnce()).findPetTypes();
        verify(serviceSpy, atLeastOnce()).getPetById(7);
        verify(serviceSpy, never()).save(any(Pet.class));
        verifyNoMoreInteractions(serviceSpy);
    }

    @Test
    void post_editPet_success_calls_save_with_existing_id_and_redirects() throws Exception {
        mvc.perform(post("/owners/6/pets/7/edit")
                        .param("id", "7")
                        .param("name", "Samantha edited")
                        .param("birthDate", "2012-09-04")
                        .param("type.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/6"));

        verify(serviceSpy, atLeastOnce()).findPetTypes();
        verify(serviceSpy, atLeastOnce()).getPetById(7);
        verify(serviceSpy, atLeastOnce()).save(argThat(p ->
                Integer.valueOf(7).equals(p.getId())
                        && Integer.valueOf(6).equals(p.getOwner_id())
                        && "Samantha edited".equals(p.getName())
                        && LocalDate.of(2012, 9, 4).equals(p.getBirthDate())
                        && p.getType() != null
                        && Integer.valueOf(1).equals(p.getType().getId())
        ));
        verifyNoMoreInteractions(serviceSpy);
    }
}
