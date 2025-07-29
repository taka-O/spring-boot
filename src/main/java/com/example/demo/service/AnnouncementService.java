package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Announcement;
import com.example.demo.model.User;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.repository.specification.AnnouncementSpecification;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
  private final AnnouncementRepository announcementRepository;
  private final UserRepository userRipository;

  public List<Announcement> findAnnouncements(String email) {
    User user = userRipository.findByEmail(email).orElseThrow();
    AnnouncementSpecification spec = new AnnouncementSpecification(user);

    return announcementRepository.findAll(spec);
  }
}
