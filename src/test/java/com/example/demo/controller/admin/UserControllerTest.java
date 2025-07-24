package com.example.demo.controller.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.model.User;
import com.example.demo.repository.operation.UserOperation;
import com.example.demo.service.UserService;
import com.jayway.jsonpath.JsonPath;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  @Autowired
  private DataSource dataSource;

	@Autowired
	UserService userService;

  @BeforeEach
  public void setUp() {
    new DbSetup(new DataSourceDestination(dataSource), Operations.sequenceOf(UserOperation.DELETE_ALL, UserOperation.INSERT_USER)).launch();
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void 検索条件なしでデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/api/admin/users")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(9)));
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void 検索条件nameでデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/api/admin/users")
              .param("name", "講師")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(3)));
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void 検索条件emailでデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/api/admin/users")
              .param("email", "student")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(4)));
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void 検索条件roleでデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/admin/users")
              .param("role", "instructor")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
        .andReturn();

    List<Map<String, Object>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    list.forEach(row -> {
      assertEquals(row.get("role").toString(), "INSTRUCTOR");
    });
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void 複合検索条件でデータを取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/admin/users")
              .param("name", "生徒")
              .param("email", "2ro")
              .param("role", "student")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
        .andReturn();

    List<Map<String, Object>> list = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$");
    list.forEach(row -> {
      assertEquals(row.get("name").toString(), "生徒二郎");
      assertEquals(row.get("email").toString(), "student2ro@test.com");
      assertEquals(row.get("role").toString(), "STUDENT");
    });
  }

  @Test
  @WithMockUser(username = "instructor@test.com", roles = {"INSTRUCTOR"})
  void 講師ユーザのため権限がないことを確認(@Autowired MockMvc mvc) throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/api/admin/users")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "student@test.com", roles = {"STUDENT"})
  void 生徒ユーザのため権限がないことを確認(@Autowired MockMvc mvc) throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/api/admin/users")
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void ユーザを作成できることを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"name\":\"生徒新五郎\",\"email\":\"student5ro@test.com\",\"role\":\"student\"}";

    mvc.perform(
            MockMvcRequestBuilders.post("/api/admin/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("生徒新五郎"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("student5ro@test.com"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("STUDENT"));
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void エラーのため登録できないことを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"name\":\"\",\"email\":\"\",\"role\":\"\"}";

    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.post("/api/admin/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
        .andReturn();

    HashMap<String, Object> errors = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.errors");
    assertNotNull(errors.get("name"));
    assertNotNull(errors.get("email"));
    assertNotNull(errors.get("role"));
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void ユーザを更新できることを確認(@Autowired MockMvc mvc) throws Exception {
    User user = userService.createUser("生徒新五郎", "student5ro@test.com", "student");

    String requestBody = "{\"name\":\"更新五郎\",\"email\":\"update5ro@test.com\",\"role\":\"student\"}";

    mvc.perform(
            MockMvcRequestBuilders.put("/api/admin/users/" + user.getId().toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("更新五郎"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("update5ro@test.com"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("STUDENT"));
  }

  @Test
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void エラーのため更新できないことを確認(@Autowired MockMvc mvc) throws Exception {
    User user = userService.createUser("生徒新五郎", "student5ro@test.com", "student");

    String requestBody = "{\"name\":\"\",\"email\":\"\",\"role\":\"\"}";

    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.put("/api/admin/users/" + user.getId().toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
        .andReturn();

    HashMap<String, Object> errors = JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.errors");
    assertNotNull(errors.get("name"));
    assertNotNull(errors.get("email"));
    assertNotNull(errors.get("role"));
  }

}
