package com.progressoft.fxdeals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class FxdealsApplicationTests {

	@Test
	void contextLoads() {
		// Test that the Spring application context loads successfully
		// This verifies that all beans are properly configured and can be wired together
	}

	@Test
	void applicationStarts() {
		// This test verifies that the main application can start without errors
		// It validates the overall application configuration
	}
}
