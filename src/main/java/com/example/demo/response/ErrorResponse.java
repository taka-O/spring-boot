package com.example.demo.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.validation.BindingResult;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private String status;
    private String message;
    private HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public ErrorResponse(String status, String message) {
      this.status = status;
      this.message = message;
    }

    public ErrorResponse(String status, String message, BindingResult bindingResult) {
      this.status = status;
      this.message = message;

      bindingResult.getFieldErrors()
        .forEach(e -> addErrors(e.getField(), e.getDefaultMessage()));
    }

    private void addErrors(String field, String message) {
      List<String> messages = errors.get(field);
      if (messages == null) {
        messages = new ArrayList<String>();
        errors.put(field, messages);
      }

      messages.add(message);
    }
}
