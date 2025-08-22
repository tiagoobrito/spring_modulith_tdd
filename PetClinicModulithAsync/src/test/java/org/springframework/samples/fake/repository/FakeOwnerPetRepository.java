package org.springframework.samples.fake.repository;

import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.service.OwnerPetRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FakeOwnerPetRepository implements OwnerPetRepository {

	private final Map<Integer, OwnerPet> pets = new ConcurrentHashMap<>();

	private final Map<Integer, Set<OwnerPet.Visit>> visitsByPet = new ConcurrentHashMap<>();

	private final AtomicInteger petSeq = new AtomicInteger(1);

	private final AtomicInteger visitSeq = new AtomicInteger(1);

	@Override
	public List<OwnerPet> findPetByOwnerId(Integer ownerId) {
		return pets.values()
			.stream()
			.filter(p -> Objects.equals(p.getOwner_id(), ownerId))
			.sorted(Comparator.comparing(OwnerPet::getId))
			.collect(Collectors.toList());
	}

	@Override
	public OwnerPet findById(Integer id) {
		OwnerPet pet = pets.get(id);
		if (pet == null) {
			throw new NoSuchElementException("OwnerPet not found: id=" + id);
		}
		return pet;
	}

	@Override
	public void save(boolean isNew, OwnerPet pet) {
		if (isNew || pet.getId() == null) {
			pet.setId(petSeq.getAndIncrement());
		}
		pets.put(pet.getId(), pet);
		petSeq.set(pet.getId() + 1);
		// ensure visits map has an entry for this pet
		visitsByPet.computeIfAbsent(pet.getId(), k -> new LinkedHashSet<>());
	}

	@Override
	public void saveVisit(OwnerPet.Visit visit) {
		Integer petId = visit.pet_id();
		if (petId == null || !pets.containsKey(petId)) {
			throw new IllegalArgumentException("Cannot save visit for non-existing pet_id=" + petId);
		}

		OwnerPet.Visit toStore;
		if (visit.id() == null) {
			toStore = new OwnerPet.Visit(visitSeq.getAndIncrement(), visit.description(), visit.visit_date(), petId);
		}
		else {
			toStore = visit;
		}

		Set<OwnerPet.Visit> set = visitsByPet.computeIfAbsent(petId, k -> new LinkedHashSet<>());
		// replace existing visit with same id if present
		if (toStore.id() != null) {
			set.removeIf(v -> Objects.equals(v.id(), toStore.id()));
		}
		set.add(toStore);
		visitSeq.set(toStore.id() + 1);
	}

	@Override
	public Set<OwnerPet.Visit> findVisitByPetId(Integer petId) {
		// return a copy to avoid external mutation leaking into repo
		return new LinkedHashSet<>(visitsByPet.getOrDefault(petId, Collections.emptySet()));
	}

	/* ---------- Test helpers ---------- */
	/** Clears all pets and visits and resets id sequences. */
	public void clear() {
		pets.clear();
		visitsByPet.clear();
		petSeq.set(1);
		visitSeq.set(1);
	}

	public void preloadPets(OwnerPet... toAdd) {
		for (OwnerPet p : toAdd) {
			save(p.getId() == null, p);
		}
	}

	public void preloadVisits(OwnerPet.Visit... toAdd) {
		for (OwnerPet.Visit v : toAdd) {
			saveVisit(v);
		}
	}

}
