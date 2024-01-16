package applications.bank.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountIdTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testValidConstructor() {
		new AccountId("name", "number");
	}

	@Test
	void testNullName() {
		assertThrows(IllegalArgumentException.class, () -> {
			new AccountId(null, "number");
		});
	}

	@Test
	void testEmptyName() {
		assertThrows(IllegalArgumentException.class, () -> {
			new AccountId("", "number");
		});
	}

	@Test
	void testBlankName() {
		assertThrows(IllegalArgumentException.class, () -> {
			new AccountId("    ", "number");
		});
	}

	@Test
	void testNullNumber() {
		assertThrows(IllegalArgumentException.class, () -> {
			new AccountId("name", null);
		});
	}

	@Test
	void testEmptyNumber() {
		assertThrows(IllegalArgumentException.class, () -> {
			new AccountId("name", "");
		});
	}

	@Test
	void testBlankNumber() {
		assertThrows(IllegalArgumentException.class, () -> {
			new AccountId("name", "    ");
		});
	}
}
