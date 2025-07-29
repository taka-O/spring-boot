package com.example.demo.repository.operation;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import com.ninja_squad.dbsetup.operation.Operation;

import com.example.demo.util.Role;
import com.github.javafaker.Faker;

public class UserOperation {
	static final Faker faker = new Faker();

	/** テストデータ 投入 **/
	public static final Operation INSERT_USER = insertInto("users")
			.columns("id","pid", "name", "email", "password", "role")
			.values(1, faker.internet().uuid(), "管理者一郎", "admin1ro@test.com", faker.internet().password(), Role.ADMIN.getId())
			.values(2, faker.internet().uuid(), "管理者二郎", "admin2ro@test.com", faker.internet().password(), Role.ADMIN.getId())
			.values(3, faker.internet().uuid(), "講師一郎", "instructor1ro@test.com", faker.internet().password(), Role.INSTRUCTOR.getId())
			.values(4, faker.internet().uuid(), "講師二郎", "instructor2ro@test.com", faker.internet().password(), Role.INSTRUCTOR.getId())
			.values(5, faker.internet().uuid(), "講師三郎", "instructor3ro@test.com", faker.internet().password(), Role.INSTRUCTOR.getId())
			.values(6, faker.internet().uuid(), "生徒一郎", "student1ro@test.com", faker.internet().password(), Role.STUDENT.getId())
			.values(7, faker.internet().uuid(), "生徒二郎", "student2ro@test.com", faker.internet().password(), Role.STUDENT.getId())
			.values(8, faker.internet().uuid(), "生徒三郎", "student3ro@test.com", faker.internet().password(), Role.STUDENT.getId())
			.values(9, faker.internet().uuid(), "生徒四郎", "student4ro@test.com", faker.internet().password(), Role.STUDENT.getId())
			.build();
	
	/** 全レコード 削除 **/
	public static final Operation DELETE_ALL = deleteAllFrom("users");

}
