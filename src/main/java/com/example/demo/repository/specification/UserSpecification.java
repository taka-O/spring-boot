package com.example.demo.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.example.demo.model.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserSpecification implements Specification<User> {
    private String name;
    private String email;
    private Optional<Integer> role_id;

    public UserSpecification(String name, String email, Optional<Integer> role_id) {
        this.name = name;
        this.email = email;
        this.role_id = role_id;
    }

    @SuppressWarnings("null")
    @Override
    public Predicate toPredicate(@NonNull Root<User> root, @Nullable CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
      List<Predicate> predicates = new ArrayList<>();

      if (name != null && !name.isEmpty()) {
          predicates.add(builder.like(root.get("name"), "%" + name + "%"));
      }
      if (email != null && !email.isEmpty()) {
          predicates.add(builder.like(root.get("email"), "%" + email + "%"));
      }
      if (role_id != null) {
          predicates.add(builder.equal(root.get("role"), role_id.orElseThrow()));
      }

      query.orderBy(builder.asc(root.get("email")));
      
      return builder.and(predicates.toArray(new Predicate[0]));
    }
}
