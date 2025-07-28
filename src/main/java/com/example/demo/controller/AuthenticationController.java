package com.example.demo.controller;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ErrorResponse;
import com.example.demo.response.UserResponse;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.AuthenticationService.AuthenticationRequest;
import com.example.demo.service.AuthenticationService.AuthenticationResponse;
import com.example.demo.service.ResetPasswordService;
import com.example.demo.service.ResetPasswordService.ResetPasswordRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	private final AuthenticationService service;
	private final UserRepository userRipository;
	private final ResetPasswordService resetPasswordService;

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

  @PostMapping("/send_reset_password_token")
  public ResponseEntity<Object> sendResetPasswordToken(@RequestParam(value = "email", required = false) String email,
                                           @RequestParam(value = "reset_url", required = false) String resetUrl) {

    try {
      resetPasswordService.sendResetPasswordMail(email, resetUrl);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    } catch (NotFoundException exception) {
      ErrorResponse response = new ErrorResponse("error", "not found");
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/reset_password")
  public ResponseEntity<Object> resetPassword(@RequestBody @Validated ResetPasswordRequest request) {

    try {
      resetPasswordService.resetPassword(request);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    } catch (NotFoundException exception) {
      ErrorResponse response = new ErrorResponse("error", "not found");
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    } catch (BadJwtException exception) {
      ErrorResponse response = new ErrorResponse("error", "token is not correct");
      return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }
}
