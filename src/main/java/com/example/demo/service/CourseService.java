package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Course;
import com.example.demo.model.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.specification.CourseSpecification;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
  private final CourseRepository courseRepository;
  private final UserRepository userRipository;

  public List<Course> findCourses(String email) {
    User user = userRipository.findByEmail(email).orElseThrow();
    CourseSpecification spec = new CourseSpecification(user);

    return courseRepository.findAll(spec);
  }
}
