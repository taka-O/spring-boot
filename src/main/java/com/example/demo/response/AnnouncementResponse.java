package com.example.demo.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.Announcement;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private Integer category;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private List<CourseResponse> courses;

    public AnnouncementResponse(Announcement announcement) {
      this.id = announcement.getId();
      this.title = announcement.getTitle();
      this.content = announcement.getContent();
      this.category = announcement.getCategory();
      this.startAt = announcement.getStartAt();
      this.endAt = announcement.getEndAt();
      //this.courses = CourseResponse.convert(announcement.getCourses());
    }

    public static List<AnnouncementResponse> convert(List<Announcement> announcements) {
      return announcements.stream()
                .map(a -> new AnnouncementResponse(a))
                .collect(Collectors.toList());
    }

}
