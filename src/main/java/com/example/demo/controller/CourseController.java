package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Course;
import com.example.demo.response.CourseResponse;
import com.example.demo.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CourseController {
  private final CourseService courseService;

  @GetMapping("/api/courses")
  public ResponseEntity<List<CourseResponse>> findAll(@AuthenticationPrincipal UserDetails userDetails) {
    List<Course> courses = courseService.findCourses(userDetails.getUsername());

    return ResponseEntity.ok(CourseResponse.convert(courses));
  }

}
