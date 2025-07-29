CREATE TABLE `announcement_courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `announcement_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_announcement_courses_on_announcement_id` (`announcement_id`),
  KEY `index_announcement_courses_on_course_id` (`course_id`)
)
