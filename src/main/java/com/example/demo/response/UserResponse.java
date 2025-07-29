package com.example.demo.response;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.User;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponse {
    private Long id;
    private String pid;
    private String name;
    private String email;
    private String role;

    public UserResponse(User user) {
      this.id = user.getId();
      this.pid = user.getPid();
      this.name = user.getName();
      this.email = user.getEmail();
      this.role = user.getAuthority();
    }

    public static List<UserResponse> convert(List<User> users) {
      return users.stream()
                .map(u -> new UserResponse(u))
                .collect(Collectors.toList());
    }
}
