package org.springframework.samples.fake.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.samples.Vet.model.Specialty;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.service.VetRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** Pure in-memory VetRepository for unit tests (Detroit/classicist). */
public class FakeVetRepository implements VetRepository {

	private final Map<Integer, Vet> vets = new ConcurrentHashMap<>();

	private final Map<Integer, Set<Specialty>> specialtiesByVet = new ConcurrentHashMap<>();

	private final AtomicInteger vetSeq = new AtomicInteger(1);

	private final AtomicInteger specSeq = new AtomicInteger(1);

	@Override
	public Collection<Vet> findAll() {
		return vets.values()
			.stream()
			.map(this::withSpecialtiesCopy)
			.sorted(Comparator.comparing(Vet::getId, Comparator.nullsLast(Integer::compareTo)))
			.collect(Collectors.toList());
	}

	@Override
	public Optional<Vet> findById(Integer id) {
		Vet v = vets.get(id);
		if (v == null)
			return Optional.empty();
		return Optional.of(withSpecialtiesCopy(v));
	}

	@Override
	public Page<Vet> findAll(Pageable pageable) {
		List<Vet> all = vets.values()
			.stream()
			.sorted(buildComparator(pageable.getSort()))
			.map(this::withSpecialtiesCopy)
			.toList();

		int total = all.size();
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), total);
		List<Vet> content = start >= total ? List.of() : all.subList(start, end);

		return new PageImpl<>(content, pageable, total);
	}

	/* ------------------- Helpers for tests ------------------- */

	/**
	 * Save (upsert) a Vet; assigns id if null. Specialties on the Vet object are also
	 * stored.
	 */
	public Vet save(Vet vet) {
		if (vet.getId() == null)
			vet.setId(vetSeq.getAndIncrement());
		vets.put(vet.getId(), shallowCopyWithoutSpecialties(vet));
		// store specialties
		Set<Specialty> copy = new LinkedHashSet<>();
		if (vet.getSpecialties() != null) {
			for (Specialty s : vet.getSpecialties()) {
				Specialty stored = new Specialty();
				stored.setId(s.getId() != null ? s.getId() : specSeq.getAndIncrement());
				stored.setName(s.getName());
				copy.add(stored);
			}
		}
		specialtiesByVet.put(vet.getId(), copy);
		return withSpecialtiesCopy(vet);
	}

	/** Bulk preload vets; ids or specialty ids will be assigned if null. */
	public void preload(Vet... toAdd) {
		for (Vet v : toAdd)
			save(v);
	}

	/**
	 * Add/replace the set of specialties for a given vet id. Null ids on specialties will
	 * be assigned.
	 */
	public void setSpecialtiesForVet(Integer vetId, Collection<Specialty> specialties) {
		if (!vets.containsKey(vetId))
			throw new NoSuchElementException("Unknown vetId " + vetId);
		Set<Specialty> set = new LinkedHashSet<>();
		for (Specialty s : specialties) {
			Specialty stored = new Specialty();
			stored.setId(s.getId() != null ? s.getId() : specSeq.getAndIncrement());
			stored.setName(s.getName());
			set.add(stored);
		}
		specialtiesByVet.put(vetId, set);
	}

	/** Add one specialty to a vet. */
	public void addSpecialtyToVet(Integer vetId, Specialty specialty) {
		if (!vets.containsKey(vetId))
			throw new NoSuchElementException("Unknown vetId " + vetId);
		Specialty stored = new Specialty();
		stored.setId(specialty.getId() != null ? specialty.getId() : specSeq.getAndIncrement());
		stored.setName(specialty.getName());
		specialtiesByVet.computeIfAbsent(vetId, k -> new LinkedHashSet<>()).add(stored);
	}

	/** Clear everything (useful in @BeforeEach). */
	public void clear() {
		vets.clear();
		specialtiesByVet.clear();
		vetSeq.set(1);
		specSeq.set(1);
	}

	/* ------------------- Internals ------------------- */

	private Vet withSpecialtiesCopy(Vet v) {
		Vet copy = shallowCopyWithoutSpecialties(v);
		Set<Specialty> specs = specialtiesByVet.getOrDefault(v.getId(), Set.of());
		Set<Specialty> cloned = specs.stream().map(s -> {
			Specialty c = new Specialty();
			c.setId(s.getId());
			c.setName(s.getName());
			return c;
		}).collect(Collectors.toCollection(LinkedHashSet::new));
		copy.setSpecialties(cloned);
		return copy;
	}

	private Vet shallowCopyWithoutSpecialties(Vet src) {
		Vet v = new Vet();
		v.setId(src.getId());
		v.setFirstName(src.getFirstName());
		v.setLastName(src.getLastName());
		return v;
	}

	private Comparator<Vet> buildComparator(Sort sort) {
		Comparator<Vet> cmp = Comparator.comparing(Vet::getId, Comparator.nullsLast(Integer::compareTo));
		if (sort == null || sort.isUnsorted())
			return cmp;

		for (Sort.Order order : sort) {
			Comparator<Vet> next = switch (order.getProperty()) {
				case "firstName" ->
					Comparator.comparing(Vet::getFirstName, Comparator.nullsFirst(String::compareToIgnoreCase));
				case "lastName" ->
					Comparator.comparing(Vet::getLastName, Comparator.nullsFirst(String::compareToIgnoreCase));
				default -> Comparator.comparing(Vet::getId, Comparator.nullsLast(Integer::compareTo));
			};
			if (order.isDescending())
				next = next.reversed();
			cmp = cmp.thenComparing(next);
		}
		return cmp;
	}

}
