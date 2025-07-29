package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "course_users")
public class CourseUser {
  @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

  @NotNull
	@Column(name = "course_id")
  private long courseId;

  @NotNull
	@Column(name = "user_id")
  private long userId;

  @NotBlank
  @Column(name = "user_type")
  private String userType;

	@Column(name = "created_at", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedAt;

}
