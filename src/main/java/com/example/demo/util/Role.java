package com.example.demo.util;

import java.util.Optional;

public enum Role {
  ADMIN("管理者", 1),
  INSTRUCTOR("講師", 2),
  STUDENT("生徒", 3);

	private final Integer id;
	private final String label;

	private Role(final String label, final Integer id) {
		this.label = label;
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public Integer getId() {
		return id;
	}

	public static Optional<Integer> getIdByValue(String value) {
		if (value == null) return null;

		Role[] roles = Role.values();
		for (Role r : roles) {
			if (r.name().equals(value.toUpperCase())) return Optional.of(r.getId());
		}

		return null;
	}

	public static Optional<Role> getById(Integer id) {
		Role[] roles = Role.values();
		for (Role r : roles) {
			if (r.id.equals(id)) return Optional.of(r);
		}

		return null;
	}
}