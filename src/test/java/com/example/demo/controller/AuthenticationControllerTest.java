package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

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

import com.example.demo.repository.AccessConfig;
import com.example.demo.util.Role;
import com.jayway.jsonpath.JsonPath;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

	static private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  private static final Operation DELETE_ALL_USER = Operations.deleteAllFrom("users");
  private static final Operation INSERT_USER = Operations.insertInto("users")
			.columns("pid", "name", "email", "password", "role")
			.values(UUID.randomUUID().toString(), "test user", "hogehoge@test.com", passwordEncoder.encode("Hogehoge"), Role.ADMIN.getId())
      .build();

  @BeforeEach
  public void setUp() {
    new DbSetup(AccessConfig.dest, Operations.sequenceOf(DELETE_ALL_USER, INSERT_USER)).launch();
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

}
