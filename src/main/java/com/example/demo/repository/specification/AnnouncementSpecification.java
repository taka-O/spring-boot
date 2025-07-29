package com.example.demo.repository.specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.example.demo.model.Announcement;
import com.example.demo.model.Course;
import com.example.demo.model.CourseUser;
import com.example.demo.model.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class AnnouncementSpecification implements Specification<Announcement> {
    private User user;

    public AnnouncementSpecification(User user) {
        this.user = user;
    }

    @SuppressWarnings("null")
    @Override
    public Predicate toPredicate(@NonNull Root<Announcement> root, @Nullable CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
      List<Predicate> predicates = new ArrayList<>();
      Subquery<Long> subquery = null;
      LocalDateTime now = LocalDateTime.now();

      predicates.add(builder.lessThanOrEqualTo(root.get("startAt"), now));
      predicates.add(builder.greaterThanOrEqualTo(root.get("endAt"), now));

      if (!user.isAdmin()) {
        predicates.add(builder.equal(root.get("category"), 0));

        subquery = query.subquery(Long.class);
        Root<Announcement> subRoot = subquery.from(Announcement.class);
        subquery.select(subRoot.get("id"));
        Join<Announcement, Course> couseJoin = subRoot.join("courses");
        Join<Course, CourseUser> courseUserJoin = couseJoin.join("courseUsers");
        subquery.where(builder.and(builder.lessThanOrEqualTo(root.get("startAt"), now),
                                   builder.greaterThanOrEqualTo(root.get("endAt"), now),
                                   builder.equal(courseUserJoin.get("userId"), user.getId()),
                                   builder.notEqual(subRoot.get("category"), 0)));
      }

      query.orderBy(builder.desc(root.get("startAt")));
      
      if (subquery == null) {
        return builder.and(predicates.toArray(new Predicate[0]));
      } else {
        return builder.or(builder.and(predicates.toArray(new Predicate[0])),
                          builder.in(root.get("id")).value(subquery));
      }
    }
}
