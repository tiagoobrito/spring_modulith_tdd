package org.springframework.samples.detroit_london.fake.repository;

import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.service.VisitRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Pure in-memory VisitRepository for unit tests (Detroit/classicist). */
public class FakeVisitRepository implements VisitRepository {

	private final Map<Integer, Visit> byId = new ConcurrentHashMap<>();

	private final Map<Integer, LinkedHashSet<Visit>> byPet = new ConcurrentHashMap<>();

	private final AtomicInteger seq = new AtomicInteger(1);

	@Override
	public void save(Visit visit) {
		if (visit.getId() == null) {
			visit.setId(seq.getAndIncrement());
		}
		else {
			// replace old entry for consistent semantics
			Visit existing = byId.get(visit.getId());
			if (existing != null) {
				LinkedHashSet<Visit> set = byPet.get(existing.getPet_id());
				if (set != null)
					set.removeIf(v -> Objects.equals(v.getId(), visit.getId()));
			}
		}
		byId.put(visit.getId(), cloneVisit(visit));
		byPet.computeIfAbsent(visit.getPet_id(), k -> new LinkedHashSet<>()).add(cloneVisit(visit));
	}

	@Override
	public Set<Visit> findVisitByPetId(Integer petId) {
		// return a defensive copy to prevent external mutation
		LinkedHashSet<Visit> set = byPet.getOrDefault(petId, new LinkedHashSet<>());
		LinkedHashSet<Visit> copy = new LinkedHashSet<>();
		for (Visit v : set)
			copy.add(cloneVisit(v));
		return copy;
	}

	@Override
	public List<Visit> findAll() {
		// keep stable ordering by id asc
		List<Visit> all = new ArrayList<>(byId.values());
		all.sort(Comparator.comparing(Visit::getId));
		List<Visit> copy = new ArrayList<>(all.size());
		for (Visit v : all)
			copy.add(cloneVisit(v));
		return copy;
	}

	@Override
	public Visit findById(Integer id) {
		Visit v = byId.get(id);
		return v == null ? null : cloneVisit(v);
	}

	/* ---------------- Test helpers ---------------- */

	/** Clears all visits and resets the id sequence. */
	public void clear() {
		byId.clear();
		byPet.clear();
		seq.set(1);
	}

	/** Bulk preload; null ids will be auto-assigned. */
	public void preload(Visit... visits) {
		for (Visit v : visits)
			save(v);
	}

	/** For assertions or setups that need the next id. */
	public int peekNextId() {
		return seq.get();
	}

	private Visit cloneVisit(Visit src) {
		Visit v = new Visit();
		v.setId(src.getId());
		v.setDate(src.getDate());
		v.setDescription(src.getDescription());
		v.setPet_id(src.getPet_id());
		return v;
	}

}
