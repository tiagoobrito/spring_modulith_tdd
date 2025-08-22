package org.springframework.samples.fake.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.service.OwnerRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FakeOwnerRepository implements OwnerRepository {

	private final Map<Integer, Owner> store = new ConcurrentHashMap<>();

	private final AtomicInteger seq = new AtomicInteger(1);

	@Override
	public Owner findById(Integer id) {
		return store.get(id);
	}

	@Override
	public Page<Owner> findByLastName(String lastName, Pageable pageable) {
		final String prefix = Optional.ofNullable(lastName).orElse("");
		List<Owner> filtered = store.values().stream().filter(o -> {
			String ln = Optional.ofNullable(o.getLastName()).orElse("");
			return ln.toLowerCase().startsWith(prefix.toLowerCase());
		}).sorted(buildComparator(pageable.getSort())).collect(Collectors.toList());

		int total = filtered.size();
		int start = pageable.getPageSize() * pageable.getPageNumber();
		int end = Math.min(pageable.getPageSize() * (pageable.getPageNumber() + 1), total);
		List<Owner> pageContent = start > total ? Collections.emptyList() : filtered.subList(start, end);

		return new PageImpl<>(pageContent, pageable, total);
	}

	@Override
	public Owner save(Owner owner) {
		if (owner.getId() == null) {
			owner.setId(seq.getAndIncrement());
		}
		// Upsert semantics
		store.put(owner.getId(), owner);
		seq.set(owner.getId() + 1);
		return owner;
	}

	@Override
	public Optional<Owner> findByName(String firstName, String lastName) {
		return store.values()
			.stream()
			.filter(o -> Objects.equals(o.getFirstName(), firstName) && Objects.equals(o.getLastName(), lastName))
			.findFirst();
	}

	/** Clear all data (handy in @BeforeEach) */
	public void clear() {
		store.clear();
		seq.set(1);
	}

	/** Preload owners */
	public void preload(Owner... owners) {
		for (Owner o : owners) {
			save(o);
		}
	}

	private Comparator<Owner> buildComparator(Sort sort) {
		// Default: by id ASC for stable paging
		Comparator<Owner> cmp = Comparator.comparing(Owner::getId, Comparator.nullsLast(Integer::compareTo));

		if (sort == null || sort.isUnsorted()) {
			return cmp;
		}

		for (Sort.Order order : sort) {
			Comparator<Owner> next = comparatorForProperty(order.getProperty());
			if (order.isDescending())
				next = next.reversed();
			cmp = cmp.thenComparing(next);
		}
		return cmp;
	}

	private Comparator<Owner> comparatorForProperty(String property) {
		// Support common Owner fields; fall back to id
		return switch (property) {
			case "firstName" ->
				Comparator.comparing(Owner::getFirstName, Comparator.nullsFirst(String::compareToIgnoreCase));
			case "lastName" ->
				Comparator.comparing(Owner::getLastName, Comparator.nullsFirst(String::compareToIgnoreCase));
			case "city" -> Comparator.comparing(Owner::getCity, Comparator.nullsFirst(String::compareToIgnoreCase));
			case "address" ->
				Comparator.comparing(Owner::getAddress, Comparator.nullsFirst(String::compareToIgnoreCase));
			case "telephone" ->
				Comparator.comparing(Owner::getTelephone, Comparator.nullsFirst(String::compareToIgnoreCase));
			default -> Comparator.comparing(Owner::getId, Comparator.nullsLast(Integer::compareTo));
		};
	}

}
