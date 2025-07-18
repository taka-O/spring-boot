package com.example.demo.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DataLoader implements ApplicationRunner {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRipository;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		final String ADMIN_USER = "admin@example.com";
		
 		Optional<User> user = userRipository.findByEmail(ADMIN_USER);
		if (user.isEmpty()) {
			var new_user = new User();
			new_user.setPid(UUID.randomUUID().toString());
			new_user.setName("admin");
			new_user.setPassword(passwordEncoder.encode("Passw0rd"));
			new_user.setEmail(ADMIN_USER);
			new_user.setRole(Role.ADMIN.getId());
			userRipository.save(new_user);
		} else {
			User exist_user = user.orElseThrow();
			exist_user.setPassword(passwordEncoder.encode("Passw0rd"));
			userRipository.save(exist_user);
		}
 	}
}
