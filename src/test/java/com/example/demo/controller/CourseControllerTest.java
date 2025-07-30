package com.example.demo.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.repository.operation.CourseOperation;
import com.example.demo.repository.operation.UserOperation;
import com.jayway.jsonpath.JsonPath;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTest {

  @Autowired
  private DataSource dataSource;

  @BeforeEach
  public void setUp() {
    new DbSetup(new DataSourceDestination(dataSource),
      Operations.sequenceOf(UserOperation.DELETE_ALL,
                            UserOperation.INSERT_USER,
                            CourseOperation.DELETE_ALL_COURSE,
                            CourseOperation.DELETE_ALL_COURSE_USER,
                            CourseOperation.INSERT_COURSE,
                            CourseOperation.INSERT_COURSE_USER)).launch();
  }

  @Test
  @WithMockUser(username = "admin1ro@test.com", roles = {"ADMIN"})
  @WithUserDetails("admin1ro@test.com")
  void ADMINユーザにて有効なデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/courses")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
        .andReturn();

    List<Map<String, Object>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> names = list.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    assertThat(names.toArray(), is(arrayContaining("コース１", "コース２", "コース３")));

    List<Map<String, String>> course1_instructors = (List<Map<String, String>>) list.get(0).get("instructors");
    assertEquals(course1_instructors.size(), 1);
    assertEquals(course1_instructors.get(0).get("name").toString(), "講師一郎");
    List<Map<String, String>> course1_students = (List<Map<String, String>>) list.get(0).get("students");
    List<String>  course1_student_names = course1_students.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    assertThat(course1_student_names.toArray(), is(arrayContaining("生徒一郎", "生徒二郎", "生徒四郎")));

    List<Map<String, String>> course2_instructors = (List<Map<String, String>>) list.get(1).get("instructors");
    assertEquals(course2_instructors.size(), 1);
    assertEquals(course2_instructors.get(0).get("name").toString(), "講師二郎");
    List<Map<String, String>> course2_students = (List<Map<String, String>>) list.get(1).get("students");
    List<String> course2_student_names = course2_students.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    assertThat(course2_student_names.toArray(), is(arrayContaining("生徒三郎", "生徒四郎")));

    List<Map<String, String>> course3_instructors = (List<Map<String, String>>) list.get(2).get("instructors");
    assertEquals(course3_instructors.size(), 0);
    List<Map<String, String>> course3_students = (List<Map<String, String>>) list.get(2).get("students");
    assertEquals(course3_students.size(), 0);
  }

  @Test
  @WithMockUser(username = "instructor2ro@test.com", roles = {"INSTRUCTOR"})
  @WithUserDetails("instructor2ro@test.com")
  void INSTRUCTORユーザにて有効な全体告知と所属コースのデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/courses")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
        .andReturn();

    List<Map<String, Object>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> names = list.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    assertThat(names.toArray(), is(arrayContaining("コース２")));

    List<Map<String, String>> course_instructors = (List<Map<String, String>>) list.get(0).get("instructors");
    assertEquals(course_instructors.size(), 1);
    assertEquals(course_instructors.get(0).get("name").toString(), "講師二郎");
    List<Map<String, String>> course_students = (List<Map<String, String>>) list.get(0).get("students");
    List<String> course_student_names = course_students.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    assertThat(course_student_names.toArray(), is(arrayContaining("生徒三郎", "生徒四郎")));
  }

  @Test
  @WithMockUser(username = "student4ro@test.com", roles = {"STUDENT"})
  @WithUserDetails("student4ro@test.com")
  void STUDENTユーザにて有効な全体告知と所属コースのデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/courses")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
        .andReturn();

    List<Map<String, Object>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> names = list.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    assertThat(names.toArray(), is(arrayContaining("コース１", "コース２")));

    List<Map<String, String>> course1_instructors = (List<Map<String, String>>) list.get(0).get("instructors");
    assertEquals(course1_instructors.size(), 1);
    assertEquals(course1_instructors.get(0).get("name").toString(), "講師一郎");
    List<Map<String, String>> course1_students = (List<Map<String, String>>) list.get(0).get("students");
    List<String>  course1_student_names = course1_students.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    assertThat(course1_student_names.toArray(), is(arrayContaining("生徒一郎", "生徒二郎", "生徒四郎")));

    List<Map<String, String>> course2_instructors = (List<Map<String, String>>) list.get(1).get("instructors");
    assertEquals(course2_instructors.size(), 1);
    assertEquals(course2_instructors.get(0).get("name").toString(), "講師二郎");
    List<Map<String, String>> course2_students = (List<Map<String, String>>) list.get(1).get("students");
    List<String> course2_student_names = course2_students.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    assertThat(course2_student_names.toArray(), is(arrayContaining("生徒三郎", "生徒四郎")));
  }

}
