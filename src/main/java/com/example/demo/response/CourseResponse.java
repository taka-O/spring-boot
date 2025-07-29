package com.example.demo.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.Course;
import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseResponse {
  private Long id;
  private String name;
  private String description;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  @JsonIgnore
  private List<User> users;

  public CourseResponse(Course course) {
    this.id = course.getId();
    this.name = course.getName();
    this.description = course.getDescription();
    this.startAt = course.getStartAt();
    this.endAt = course.getEndAt();
  }

  public static List<CourseResponse> convert(List<Course> courses) {
    return courses.stream()
              .map(c -> new CourseResponse(c))
              .collect(Collectors.toList());
  }

  @JsonIgnore
  public List<User> getInstructors() {
    List<User> instructors = new ArrayList<>();
    users.stream().forEach(user -> {
      if (user.isInstructor()) { instructors.add(user); }
    });

    return instructors;
  }

  @JsonIgnore
  public List<User> getStudents() {
    List<User> instructors = new ArrayList<>();
    users.stream().forEach(user -> {
      if (user.isStudent()) { instructors.add(user); }
    });

    return instructors;
  }
}
