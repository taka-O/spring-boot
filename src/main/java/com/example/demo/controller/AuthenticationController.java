package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.UserResponse;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.AuthenticationService.AuthenticationRequest;
import com.example.demo.service.AuthenticationService.AuthenticationResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	private final AuthenticationService service;
	private final UserRepository userRipository;

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
   	return ResponseEntity.ok(service.login(request));
	}

  @PostMapping("/current")
  public ResponseEntity<UserResponse> current(@AuthenticationPrincipal Jwt jwt) {
    User user = userRipository.findByEmail(jwt.getSubject()).orElseThrow();

    UserResponse response = new UserResponse(user);
    return ResponseEntity.ok(response);
	}
}
