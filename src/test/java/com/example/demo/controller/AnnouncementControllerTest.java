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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.repository.operation.AnnouncementOperation;
import com.example.demo.repository.operation.CourseOperation;
import com.example.demo.repository.operation.UserOperation;
import com.jayway.jsonpath.JsonPath;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;

@SpringBootTest
@AutoConfigureMockMvc
public class AnnouncementControllerTest {

  @Autowired
  private DataSource dataSource;

  @BeforeEach
  public void setUp() {
    new DbSetup(new DataSourceDestination(dataSource),
      Operations.sequenceOf(UserOperation.DELETE_ALL,
                            UserOperation.INSERT_USER,
                            AnnouncementOperation.DELETE_ALL_ANNOUNCEMENT,
                            CourseOperation.DELETE_ALL_COURSE,
                            CourseOperation.DELETE_ALL_COURSE_USER,
                            AnnouncementOperation.DELETE_ALL_ANNOUNCEMENT_COURSE,
                            AnnouncementOperation.INSERT_ANNOUNCEMENT,
                            CourseOperation.INSERT_COURSE,
                            CourseOperation.INSERT_COURSE_USER,
                            AnnouncementOperation.INSERT_ANNOUNCEMENT_COURSE)).launch();
  }

  @Test
  @WithMockUser(username = "admin1ro@test.com", roles = {"ADMIN"})
  void ADMINユーザにて有効なデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/announcements")
              .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject("admin1ro@test.com")))
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
        .andReturn();

    List<Map<String, Object>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> titles = list.stream().map(v -> v.get("title").toString()).collect(Collectors.toList());
    assertThat(titles.toArray(), is(arrayContaining("コース告知２", "コース告知１", "全体告知１")));

    List<Map<String, String>> announcement1_courses = (List<Map<String, String>>) list.get(0).get("courses");
    assertEquals(announcement1_courses.size(), 1);
    assertEquals(announcement1_courses.get(0).get("name").toString(), "コース２");
    List<Map<String, String>> announcement2_courses = (List<Map<String, String>>) list.get(1).get("courses");
    assertEquals(announcement2_courses.size(), 1);
    assertEquals(announcement2_courses.get(0).get("name").toString(), "コース１");
    List<Map<String, String>> announcement3_courses = (List<Map<String, String>>) list.get(2).get("courses");
    assertEquals(announcement3_courses.size(), 0);
  }

  @Test
  @WithMockUser(username = "instructor1ro@test.com", roles = {"INSTRUCTOR"})
  void INSTRUCTORユーザにて有効な全体告知と所属コースのデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/announcements")
              .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject("instructor1ro@test.com")))
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
        .andReturn();

    List<Map<String, Object>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> titles = list.stream().map(v -> v.get("title").toString()).collect(Collectors.toList());
    assertThat(titles.toArray(), is(arrayContaining("コース告知１", "全体告知１")));

    List<Map<String, String>> announcement1_courses = (List<Map<String, String>>) list.get(0).get("courses");
    assertEquals(announcement1_courses.size(), 1);
    assertEquals(announcement1_courses.get(0).get("name").toString(), "コース１");
    List<Map<String, String>> announcement2_courses = (List<Map<String, String>>) list.get(1).get("courses");
    assertEquals(announcement2_courses.size(), 0);
  }

  @Test
  @WithMockUser(username = "student3ro@test.com", roles = {"STUDENT"})
  void STUDENTユーザにて有効な全体告知と所属コースのデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/announcements")
              .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject("student3ro@test.com")))
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
        .andReturn();

    List<Map<String, Object>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> titles = list.stream().map(v -> v.get("title").toString()).collect(Collectors.toList());
    assertThat(titles.toArray(), is(arrayContaining("コース告知２", "全体告知１")));

    List<Map<String, String>> announcement1_courses = (List<Map<String, String>>) list.get(0).get("courses");
    assertEquals(announcement1_courses.size(), 1);
    assertEquals(announcement1_courses.get(0).get("name").toString(), "コース２");
    List<Map<String, String>> announcement2_courses = (List<Map<String, String>>) list.get(1).get("courses");
    assertEquals(announcement2_courses.size(), 0);
  }

}
