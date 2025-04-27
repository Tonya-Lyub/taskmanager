package com.example.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest  // важно для поднятия всего контекста
@ActiveProfiles("test")  // чтобы использовался профиль test (например, H2 база)
class TaskmanagerApplicationTests {

	@Test
	void contextLoads() {
		// Этот тест просто проверяет, что контекст приложения загружается без ошибок
	}
}
