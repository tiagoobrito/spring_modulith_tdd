package org.springframework.samples.fake.repository;

import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.service.PetTypeRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Pure in-memory PetTypeRepository for unit tests (Detroit/classicist). */
public class FakePetTypeRepository implements PetTypeRepository {

	private final Map<Integer, PetType> types = new ConcurrentHashMap<>();

	private final AtomicInteger seq = new AtomicInteger(1);

	@Override
	public List<PetType> findPetTypes() {
		// mimic SQL ORDER BY name
		List<PetType> out = new ArrayList<>(types.values());
		out.sort(Comparator.comparing(PetType::getName, Comparator.nullsLast(String::compareToIgnoreCase)));
		return out;
	}

	@Override
	public PetType findById(Integer id) {
		PetType t = types.get(id);
		if (t == null)
			throw new NoSuchElementException("PetType not found: id=" + id);
		return t;
	}

	/* ---------- Test helpers ---------- */

	/** Clears all types and resets id sequence. */
	public void clear() {
		types.clear();
		seq.set(1);
	}

	/** Saves (upsert). Assigns id if null. */
	public PetType save(PetType type) {
		if (type.getId() == null)
			type.setId(seq.getAndIncrement());
		types.put(type.getId(), type);
		return type;
	}

	/** Bulk preload; null ids will be auto-assigned. */
	public void preload(PetType... toAdd) {
		for (PetType t : toAdd)
			save(t);
	}

	/** Returns current number of stored types. */
	public int size() {
		return types.size();
	}

}
