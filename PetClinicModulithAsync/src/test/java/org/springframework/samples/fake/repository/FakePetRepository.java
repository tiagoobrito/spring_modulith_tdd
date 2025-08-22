package org.springframework.samples.fake.repository;

import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.service.PetRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** Pure in-memory PetRepository for unit tests (Detroit/classicist). */
public class FakePetRepository implements PetRepository {

	private final Map<Integer, Pet> pets = new ConcurrentHashMap<>();

	private final Map<Integer, List<Pet.Visit>> visitsByPet = new ConcurrentHashMap<>();

	private final AtomicInteger petSeq = new AtomicInteger(1);

	@Override
	public List<Pet> findPetByOwnerId(Integer ownerId) {
		return pets.values()
			.stream()
			.filter(p -> Objects.equals(p.getOwner_id(), ownerId))
			.sorted(Comparator.comparing(Pet::getId))
			.collect(Collectors.toList());
	}

	@Override
	public Optional<Pet> findPetByName(String name) {
		return pets.values().stream().filter(p -> Objects.equals(p.getName(), name)).findFirst();
	}

	@Override
	public Pet findById(Integer id) {
		Pet p = pets.get(id);
		if (p == null)
			throw new NoSuchElementException("Pet not found: id=" + id);
		return p;
	}

	@Override
	public void save(Pet pet, boolean isNew) {
		if (isNew || pet.getId() == null) {
			pet.setId(petSeq.getAndIncrement());
		}
		pets.put(pet.getId(), pet);
		visitsByPet.computeIfAbsent(pet.getId(), k -> new ArrayList<>());
	}

	@Override
	public void save(Pet.Visit petVisit) {
		Integer petId = petVisit.pet_id();
		if (petId == null || !pets.containsKey(petId)) {
			throw new IllegalArgumentException("Cannot save visit for non-existing pet_id=" + petId);
		}
		visitsByPet.computeIfAbsent(petId, k -> new ArrayList<>()).add(petVisit);
	}

	/* ---------- Test helpers ---------- */
	public void clear() {
		pets.clear();
		visitsByPet.clear();
		petSeq.set(1);
	}

	public void preloadPets(Pet... toAdd) {
		for (Pet p : toAdd)
			save(p, p.getId() == null);
	}

	public void preloadVisits(Pet.Visit... toAdd) {
		for (Pet.Visit v : toAdd)
			save(v);
	}

	public List<Pet.Visit> getVisitsOf(Integer petId) {
		return new ArrayList<>(visitsByPet.getOrDefault(petId, List.of()));
	}

	public int peekNextPetId() {
		return petSeq.get();
	}

}
