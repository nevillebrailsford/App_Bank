package applications.bank.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.notification.Notification;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import application.notification.NotificationMonitor;
import application.storage.LoadState;
import application.storage.Storage;
import application.storage.StorageConstants;
import application.storage.StorageNotificationType;
import application.storage.StoreState;
import applications.bank.model.Bank;

public class BankReadMultiBranchTest extends BankTestBase {

	NotificationListener listener = new NotificationListener() {

		@Override
		public void notify(Notification notification) {
			if (notification.notificationType().category().equals(StorageConstants.STORAGE_CATEGORY)) {
				StorageNotificationType storeNotification = (StorageNotificationType) notification.notificationType();
				if (storeNotification == StorageNotificationType.Load) {
					LoadState state = (LoadState) notification.subject().get();
					switch (state) {
						case Complete -> loadComplete();
						case Failed -> loadFailed();
						default -> ignore();
					}
				}
				if (storeNotification == StorageNotificationType.Store) {
					StoreState state = (StoreState) notification.subject().get();
					switch (state) {
						case Complete -> storeComplete();
						case Failed -> storeFailed();
						default -> ignore();
					}
				}
			}
		}
	};

	@TempDir
	File rootDirectory;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		resetFlags();
		runMonitor = false;
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
		bankRead = new BankRead();
	}

	@AfterEach
	void tearDown() throws Exception {
		synchronized (waitForFinish) {
			BankMonitor.instance().clear();
			waitForFinish.wait();
		}
		NotificationCentre.removeListener(listener);
		ApplicationConfiguration.clear();
		LogConfigurer.shutdown();
	}

	@Test
	void testBankReadOneBankNoBranches() throws Exception {
		createValidModelFileOneBankNoBranches();
		loadData();
		assertTrue(BankMonitor.instance().banks().size() == 1);
		Bank bank = BankMonitor.instance().banks().get(0);
		assertEquals("testbank1", bank.name());
		assertTrue(bank.branches().size() == 0);
	}

	@Test
	void testBankReadOneBankOneBranch() throws Exception {
		createValidModelFileOneBankOneBranch();
		loadData();
		assertTrue(BankMonitor.instance().banks().size() == 1);
		Bank bank1 = BankMonitor.instance().banks().get(0);
		assertTrue(bank1.branches().size() == 1);
	}

	@Test
	void testBankReadOneBankTwoBranches() throws Exception {
		createValidModelFileOneBankTwoBranches();
		loadData();
		assertTrue(BankMonitor.instance().banks().size() == 1);
		Bank bank1 = BankMonitor.instance().banks().get(0);
		assertTrue(bank1.branches().size() == 2);
	}

	private void loadData() throws IOException, InterruptedException {
		bankRead.setFileName(modelFile.getAbsolutePath());
		Storage storage = new Storage();
		synchronized (waitForFinish) {
			storage.loadStoredData(bankRead);
			waitForFinish.wait();
		}
	}

	private void createValidModelFileOneBankNoBranches() throws IOException {
		modelFile = new File(rootDirectory, "model.dat");
		String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String line2 = "<banks>";
		String line3 = "<bank><bankname>testbank1</bankname></bank>";
		String line4 = "</banks>";
		List<String> lines = Arrays.asList(line1, line2, line3, line4);
		assertFalse(modelFile.exists());
		Files.write(modelFile.toPath(), lines);
		assertTrue(modelFile.exists());
		assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
	}

	private void createValidModelFileOneBankOneBranch() throws IOException {
		modelFile = new File(rootDirectory, "model.dat");
		String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String line2 = "<banks>";
		String line3 = "<bank><bankname>testbank1</bankname>";
		String line4 = "<branch>";
		String line5 = "<address><postcode>BJ21 4EZ</postcode>";
		String line6 = "<line>line 1</line><line>line 2</line><line>line 3</line></address><sortcode>10-23-34</sortcode></branch>";
		String line7 = "</bank></banks>";
		List<String> lines = Arrays.asList(line1, line2, line3, line4, line5, line6, line7);
		assertFalse(modelFile.exists());
		Files.write(modelFile.toPath(), lines);
		assertTrue(modelFile.exists());
		assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
	}

	private void createValidModelFileOneBankTwoBranches() throws IOException {
		modelFile = new File(rootDirectory, "model.dat");
		String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String line2 = "<banks>";
		String line3 = "<bank><bankname>test2</bankname>";
		String line4 = "<branch><address><postcode>BH21 4EZ</postcode><line>4 Primrose Close</line><line>WImborne</line><line>Dorset</line></address><sortcode>11-22-33</sortcode></branch>";
		String line5 = "<branch><address><postcode>BJ21 4EZ</postcode><line>line 1</line><line>line 2</line><line>line 3</line></address><sortcode>22-22-22</sortcode></branch>";
		String line6 = "</bank></banks>";

		List<String> lines = Arrays.asList(line1, line2, line3, line4, line5, line6);
		assertFalse(modelFile.exists());
		Files.write(modelFile.toPath(), lines);
		assertTrue(modelFile.exists());
		assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
	}

	private void ignore() {
	}

	private void loadFailed() {
		synchronized (waitForFinish) {
			waitForFinish.notifyAll();
			loadSuccess = false;
			loadFailed = true;
		}
	}

	private void loadComplete() {
		synchronized (waitForFinish) {
			waitForFinish.notifyAll();
			loadFailed = false;
			loadSuccess = true;
		}
	}

	private void storeFailed() {
		synchronized (waitForFinish) {
			waitForFinish.notifyAll();
		}
	}

	private void storeComplete() {
		synchronized (waitForFinish) {
			waitForFinish.notifyAll();
		}
	}

}
