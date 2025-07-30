package com.example.demo.repository.specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.example.demo.model.Course;
import com.example.demo.model.CourseUser;
import com.example.demo.model.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CourseSpecification implements Specification<Course> {
    private User user;

    public CourseSpecification(User user) {
        this.user = user;
    }

    @SuppressWarnings("null")
    @Override
    public Predicate toPredicate(@NonNull Root<Course> root, @Nullable CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
      List<Predicate> predicates = new ArrayList<>();
      LocalDateTime now = LocalDateTime.now();

      root.fetch("users", JoinType.LEFT);

      predicates.add(builder.lessThanOrEqualTo(root.get("startAt"), now));
      predicates.add(builder.greaterThanOrEqualTo(root.get("endAt"), now));

      if (!user.isAdmin()) {
        Join<Course, CourseUser> courseUserJoin = root.join("courseUsers");
        predicates.add(builder.lessThanOrEqualTo(root.get("startAt"), now));
        predicates.add(builder.greaterThanOrEqualTo(root.get("endAt"), now));
        predicates.add(builder.equal(courseUserJoin.get("userId"), user.getId()));
      }

      query.orderBy(builder.asc(root.get("startAt")));
      
      return builder.and(predicates.toArray(new Predicate[0]));
    }
}
