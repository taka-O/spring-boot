package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.model.User;
import com.example.demo.service.ResetPasswordService;
import com.example.demo.service.UserService;
import com.example.demo.util.Role;
import com.jayway.jsonpath.JsonPath;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

	static private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	UserService userService;

  @Autowired
  ResetPasswordService resetPasswordService;

  private static final Operation DELETE_ALL_USER = Operations.deleteAllFrom("users");
  private static final Operation INSERT_USER = Operations.insertInto("users")
			.columns("pid", "name", "email", "password", "role")
			.values(UUID.randomUUID().toString(), "test user", "hogehoge@test.com", passwordEncoder.encode("Hogehoge"), Role.ADMIN.getId())
			.values(UUID.randomUUID().toString(), "test student user", "student@test.com", passwordEncoder.encode("Hogehoge"), Role.STUDENT.getId())
      .build();

  @Autowired
  private DataSource dataSource;

  @BeforeEach
  public void setUp() {
    new DbSetup(new DataSourceDestination(dataSource), Operations.sequenceOf(DELETE_ALL_USER, INSERT_USER)).launch();
  }

  @Test
  void ログインできることを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"email\":\"hogehoge@test.com\",\"password\":\"Hogehoge\"}";

    mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
  }

  @Test
  void パスワード誤りでログインできないことを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"email\":\"hogehoge@test.com\",\"password\":\"ageage\"}";

    mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void メールアドレス誤りでログインできないことを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"email\":\"ageage@test.com\",\"password\":\"Hogehoge\"}";

    mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void ログインユーザの情報が取得できることを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"email\":\"hogehoge@test.com\",\"password\":\"Hogehoge\"}";

    // ログインしtokenを取得
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        ).andReturn();
    String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

    // Headerにtokenを設定
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Accept","application/json");
    httpHeaders.add("Authorization", "Bearer " + token);

    // ログインユーザ情報を取得
    MvcResult currentResult = mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/current")
              .headers(httpHeaders)
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    String email = JsonPath.read(currentResult.getResponse().getContentAsString(), "$.email");
    assertEquals(email, "hogehoge@test.com");
  }

  @Test
  void パスワードリセットトークンが送信できることを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"email\":\"student@test.com\",\"reset_url\":\"http://localhost:3001/reset_pawssword\"}";

    mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/send_reset_password_token")
              .content(requestBody)
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  void メールアドレスが存在しないためエラーとなることを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"email\":\"student_not_found@test.com\",\"reset_url\":\"http://localhost:3001/reset_pawssword\"}";

    mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/send_reset_password_token")
              .content(requestBody)
              .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void パスワードが変更できることを確認(@Autowired MockMvc mvc) throws Exception {
    User user = userService.createUser("生徒新五郎", "student5ro@test.com", "student");
    String token = resetPasswordService.generateToken(user);
    String requestBody = "{\"token\":\"" + token + "\",\"password\":\"H0gehoge-\",\"password_confirmation\":\"H0gehoge-\"}";

    mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/reset_password")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  void トークンが誤っているためエラーとなることを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"token\":\"hogehagehogehagehogehagehogehagehogehage\",\"password\":\"H0gehoge-\",\"password_confirmation\":\"H0gehoge-\"}";
    mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/reset_password")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
  }

  @Test
  void パスワードが一致しないためエラーとなることを確認(@Autowired MockMvc mvc) throws Exception {
    String requestBody = "{\"token\":\"hogehagehogehagehogehagehogehagehogehage\",\"password\":\"H0gehoge-\",\"password_confirmation\":\"Hogehoge-\"}";
    mvc.perform(
            MockMvcRequestBuilders.post("/api/auth/reset_password")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody)
        )
        .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
  }

}
