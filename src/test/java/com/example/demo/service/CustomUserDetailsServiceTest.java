package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.Role;

@SpringBootTest
@Transactional
class CustomUserDetailsServiceTest {
	@Autowired
	UserRepository repository;

	@Autowired
	CusomUserDetailsService service;

	@Autowired
	PasswordEncoder passwordEncoder;

	
	@Test
	@DisplayName("ユーザ存在時、ユーザ詳細を取得")
	void whenUsernameExists_exceptToGetUserDetails() {
		String email =  "test@test.co.jp";

		var user = new User();
		user.setPid(UUID.randomUUID().toString());
		user.setName("test");
		user.setPassword(passwordEncoder.encode("Passw0rd"));
		user.setEmail(email);
		user.setRole(Role.ADMIN.getId());
		repository.save(user);
		
		var actual = service.loadUserByUsername(email);
		assertEquals(user.getEmail(), actual.getUsername());
	}

	@Test
	@DisplayName("ユーザ未存在時、例外スロー")
	void whenUsernameDoseNotExist_throwException() {
		assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("HogeHoge"));
	}
}
