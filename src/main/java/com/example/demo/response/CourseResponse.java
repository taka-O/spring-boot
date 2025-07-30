package com.example.demo.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.Course;
import com.example.demo.model.User;

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
  private List<UserResponse> instructors;
  private List<UserResponse> students;

  public CourseResponse(Course course) {
    this.id = course.getId();
    this.name = course.getName();
    this.description = course.getDescription();
    this.startAt = course.getStartAt();
    this.endAt = course.getEndAt();
    setInstructors(course.getUsers());
    setStudents(course.getUsers());
  }

  public static List<CourseResponse> convert(List<Course> courses) {
    return courses.stream()
              .map(c -> new CourseResponse(c))
              .collect(Collectors.toList());
  }

  public void setInstructors(List<User> users) {
    List<User> instructror_users = users.stream()
      .filter(u -> u.isInstructor())
      .collect(Collectors.toList());
    this.instructors = UserResponse.convert(instructror_users);
  }

  public void setStudents(List<User> users) {
    List<User> student_users = users.stream()
      .filter(u -> u.isStudent())
      .collect(Collectors.toList());
    this.students = UserResponse.convert(student_users);
  }
}
