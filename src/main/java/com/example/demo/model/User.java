package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.util.Role;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String pid;

	@NotBlank
	@Size(max = 255)
	private String name;

	@NotBlank
	@Email
	@Size(max = 255)
	private String email;

	@NotBlank
	private String password;

	@NotNull
	private Integer role;

	@Column(name = "created_at", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public String getAuthority() {
		Role role = Role.getById(this.role).orElseThrow(NoSuchElementException::new);
		return role.name();
	}

	public boolean isAdmin() {
		return role == Role.ADMIN.getId();
	}

	public boolean isInstructor() {
		return role == Role.INSTRUCTOR.getId();
	}

	public boolean isStudent() {
		return role == Role.STUDENT.getId();
	}
}
