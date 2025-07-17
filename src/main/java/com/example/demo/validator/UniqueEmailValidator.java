package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repository.UserRepository;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UserRepository userRepository;

    public UniqueEmailValidator() {
    	this.userRepository = null;
    }
    
    @Autowired
    public UniqueEmailValidator(UserRepository userRepositor) {
    	this.userRepository = userRepositor;
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
    	return userRepository == null || userRepository.findByEmail(value).isEmpty();
    }
}
