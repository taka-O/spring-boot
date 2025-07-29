package com.example.demo.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    List<Map<String, String>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> names = list.stream().map(v -> v.get("name")).collect(Collectors.toList());
    assertThat(names.toArray(), is(arrayContaining("コース１", "コース２", "コース３")));
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

    List<Map<String, String>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> names = list.stream().map(v -> v.get("name")).collect(Collectors.toList());
    assertThat(names.toArray(), is(arrayContaining("コース２")));
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

    List<Map<String, String>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    List<String> names = list.stream().map(v -> v.get("name")).collect(Collectors.toList());
    assertThat(names.toArray(), is(arrayContaining("コース１", "コース２")));
  }

}
