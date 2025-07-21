package com.example.demo.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.response.UserResponse;
import com.example.demo.service.UserService;
import com.example.demo.service.UserService.UserRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<UserResponse>> findUsers(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "email", required = false) String email,
                            @RequestParam(value = "role", required = false) String role) {

    List<User> users = userService.findUsers(name, email, role);

    return ResponseEntity.ok(UserResponse.convert(users));
  }
  
  @PostMapping("/users")
  public ResponseEntity<UserResponse> sotre(@RequestBody @Valid UserRequest request) {
    User user = userService.createUser(request.name(), request.email(), request.role());
    return new ResponseEntity<>(new UserResponse(user), HttpStatus.CREATED);
  }
  
  @PutMapping("/users/{id}")
  public ResponseEntity<UserResponse> sotre(@PathVariable Long id, @RequestBody @Validated UserRequest request) {
    User user = userService.createUser(request.name(), request.email(), request.role());
    return new ResponseEntity<>(new UserResponse(user), HttpStatus.CREATED);
  }
}
