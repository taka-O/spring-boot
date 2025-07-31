package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Announcement;
import com.example.demo.response.AnnouncementResponse;
import com.example.demo.service.AnnouncementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AnnouncementController {
  private final AnnouncementService announcementService;

  @GetMapping("/api/announcements")
  public ResponseEntity<List<AnnouncementResponse>> findAll(@AuthenticationPrincipal Jwt jwt) {
    List<Announcement> annoucements = announcementService.findAnnouncements(jwt.getSubject());

    return ResponseEntity.ok(AnnouncementResponse.convert(annoucements));
  }

}
