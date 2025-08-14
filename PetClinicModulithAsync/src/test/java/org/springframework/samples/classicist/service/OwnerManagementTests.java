package org.springframework.samples.classicist.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.samples.Owner.service.OwnerPetRepository;
import org.springframework.samples.Owner.service.OwnerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OwnerManagementTests {
    @Autowired
    protected OwnerManagement service;

    @Test
    void findById_shouldReturnOwnerWithPetsAndVisits() {
        Owner owner = service.findById(6);

        assertThat(owner).isNotNull();
        assertThat(owner.getPets()).isNotEmpty();
        assertThat(owner.getPets().get(0).getVisits()).isNotEmpty();
    }

    @Test
    void save_shouldUpdateExistingOwner() {
        Owner existing = service.findById(1);
        existing.setFirstName("Updated");

        Integer id = service.save(existing);
        Owner updated = service.findById(id);

        assertThat(updated.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void findByLastName_shouldReturnOwnerPageWithPets() {
        Page<Owner> result = service.findByLastName("Estaban", PageRequest.of(1, 10));
        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getPets()).isNotEmpty();
    }

    @Test
    void findByName_shouldReturnCorrectOwner() {
        Optional<Owner> result = service.findByName("Betty", "Davis");
        assertThat(result).isPresent();
    }
}
