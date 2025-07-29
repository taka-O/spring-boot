package com.example.demo.repository.operation;

import static com.ninja_squad.dbsetup.Operations.insertInto;

import java.time.LocalDateTime;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import com.ninja_squad.dbsetup.operation.Operation;

public class CourseOperation {
	static final LocalDateTime current_time = LocalDateTime.now();

	/** テストデータ 投入 **/
	public static final Operation INSERT_COURSE = insertInto("courses")
			.columns("id", "name", "description", "start_at", "end_at")
			.values(1, "コース１", "コース１内容", current_time.minusMonths(1), current_time.plusMonths(1))
			.values(2, "コース２", "コース２内容", current_time.minusMonths(1), current_time.plusMonths(1))
			.values(3, "コース３", "コース３内容", current_time.minusMonths(1), current_time.plusMonths(1))
			.build();
	public static final Operation INSERT_COURSE_USER = insertInto("course_users")
			.columns("course_id", "user_id", "user_type")
			.values(1, 3, "instructor")
			.values(2, 4, "instructor")
			.values(1, 6, "student")
			.values(1, 7, "student")
			.values(1, 9, "student")
			.values(2, 8, "student")
			.values(2, 9, "student")
			.build();
	
	/** 全レコード 削除 **/
	public static final Operation DELETE_ALL_COURSE = deleteAllFrom("courses");
	public static final Operation DELETE_ALL_COURSE_USER = deleteAllFrom("course_users");

}
