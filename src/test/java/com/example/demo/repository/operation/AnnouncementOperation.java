package com.example.demo.repository.operation;

import static com.ninja_squad.dbsetup.Operations.insertInto;

import java.time.LocalDateTime;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import com.ninja_squad.dbsetup.operation.Operation;

public class AnnouncementOperation {
	static final LocalDateTime current_time = LocalDateTime.now();

	/** テストデータ 投入 **/
	public static final Operation INSERT_ANNOUNCEMENT = insertInto("announcements")
			.columns("id", "title", "content", "category", "start_at", "end_at")
			.values(1, "全体告知１", "告知内容１", 0, current_time.minusMonths(1), current_time.plusMonths(1))
			.values(2, "全体告知２（期限後）", "告知内容２", 0, current_time.minusMonths(2), current_time.minusDays(1))
			.values(3, "全体告知３（期限前）", "告知内容３", 0, current_time.plusDays(1), current_time.plusMonths(2))
			.values(4, "コース告知１", "コース告知内容１", 1, current_time.minusMonths(1).plusSeconds(1), current_time.plusMonths(1))
			.values(5, "コース告知２", "コース告知内容２", 1, current_time.minusMonths(1).plusSeconds(2), current_time.plusMonths(1))
			.values(6, "コース告知３（期限後）", "コース告知内容３", 1, current_time.minusMonths(2), current_time.minusDays(1))
			.values(7, "コース告知４（期限前）", "コース告知内容４", 1, current_time.plusDays(1), current_time.plusMonths(2))
			.build();
	public static final Operation INSERT_ANNOUNCEMENT_COURSE = insertInto("announcement_courses")
			.columns("announcement_id", "course_id")
			.values(4, 1)
			.values(5, 2)
			.values(6, 1)
			.values(7, 2)
			.build();
	
	/** 全レコード 削除 **/
	public static final Operation DELETE_ALL_ANNOUNCEMENT = deleteAllFrom("announcements");
	public static final Operation DELETE_ALL_ANNOUNCEMENT_COURSE = deleteAllFrom("announcement_courses");

}
