package applications.bank.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.notification.NotificationCentre;
import application.notification.NotificationMonitor;
import application.storage.Storage;

class BankStoreMultiBankTest extends BankTestBase {
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		resetFlags();
		ApplicationDefinition app = new ApplicationDefinition("test") {
			@Override
			public Level level() {
				return Level.OFF;
			}
		};
		ApplicationConfiguration.registerApplication(app, rootDirectory.getAbsolutePath());
		LogConfigurer.setUp();
		NotificationCentre.addListener(listener);
		if (runMonitor)
			new NotificationMonitor(System.out);
		bankStore = new BankStore();
	}

	@AfterEach
	void tearDown() throws Exception {
		synchronized (waitForFinish) {
			BankMonitor.instance().clear();
			waitForFinish.wait();
		}
		NotificationCentre.clear();
		ApplicationConfiguration.clear();
		LogConfigurer.shutdown();
	}

	@Test
	void testBankStoreNoBanks() throws Exception {
		Storage storage = initStorage();
		writeToStore(storage);
		List<String> lines = Files.readAllLines(modelFile.toPath());
		assertEquals(5, lines.size());
		assertTrue(storeSuccess);
	}

	@Test
	void testBankStoreWithOneBank() throws Exception {
		assertFalse(storeSuccess);
		storeBank(bank1);
		assertTrue(storeSuccess);
		storeSuccess = false;
		Storage storage = initStorage();
		assertFalse(storeSuccess);
		writeToStore(storage);
		List<String> lines = Files.readAllLines(modelFile.toPath());
		assertEquals(9, lines.size());
		assertTrue(storeSuccess);
	}

	@Test
	void testBankStoreWithTwoBanks() throws Exception {
		assertFalse(storeSuccess);
		storeBank(bank1);
		assertTrue(storeSuccess);
		storeSuccess = false;
		storeBank(bank2);
		assertTrue(storeSuccess);
		storeSuccess = false;
		Storage storage = initStorage();
		assertFalse(storeSuccess);
		writeToStore(storage);
		List<String> lines = Files.readAllLines(modelFile.toPath());
		assertEquals(12, lines.size());
		assertTrue(storeSuccess);
	}

}
