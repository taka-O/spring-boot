package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.specification.UserSpecification;
import com.example.demo.util.Role;
import com.example.demo.validator.UniqueEmail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  	private final UserRepository userRipository;
    private final PasswordEncoder passwordEncoder;

    public record UserRequest(
      @NotBlank
      @Size(max = 255)
      String name,

      @NotBlank
      @Email
      @Size(max = 255)
      @UniqueEmail
      String email,

      @NotBlank
      @Pattern(regexp = "^(ADMIN|INSTRUCTOR|STUDENT|admin|instructor|student)$")
      String role
    ) {
    }

    public List<User> findUsers(String name, String email, String roleString) {
      Optional<Integer> role_id = Role.getIdByValue(roleString);
      UserSpecification spec = new UserSpecification(name, email, role_id);

      return userRipository.findAll(spec);
    }

    public User createUser(String name, String email, String roleString) {
      User user = new User();
      user.setPid(UUID.randomUUID().toString());
      user.setName(name);
      user.setEmail(email);
      user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString().subSequence(0, 15)));
      user.setRole(Role.getIdByValue(roleString).orElse(null));
      userRipository.save(user);

      return user;
    }

    public User updateUser(Long id, String name, String email, String roleString) {
      User user = userRipository.findById(id).orElseThrow();

      user.setName(name);
      user.setEmail(email);
      user.setRole(Role.getIdByValue(roleString).orElse(null));
      userRipository.save(user);

      return user;
    }
}
