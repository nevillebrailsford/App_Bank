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
import java.util.UUID;
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

class BankReadMultiInvestmentTest extends BankTestBase {

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
	void testBankReadNoInvestments() throws Exception {
		assertFalse(loadSuccess);
		createValidModelFileNoInvestments();
		loadData();
		assertTrue(BankMonitor.instance().investments().size() == 0);
	}

	@Test
	void testBankReadOneInvestment() throws Exception {
		createValidModelFileOneInvestment();
		loadData();
		assertTrue(BankMonitor.instance().investments().size() == 1);
		assertEquals("investment1", BankMonitor.instance().investments().get(0).name());
	}

	@Test
	void testBankReadTwoInvestments() throws Exception {
		createValidModelFileTwoInvestments();
		loadData();
		assertTrue(BankMonitor.instance().investments().size() == 2);
		assertEquals("investment1", BankMonitor.instance().investments().get(0).name());
		assertEquals("investment2", BankMonitor.instance().investments().get(1).name());
	}

	@Test
	void testBankReadNoInvestmentsInvalid() throws Exception {
		assertFalse(loadSuccess);
		createInvalidModelFileNoInvestments();
		loadData();
		assertTrue(loadFailed);
	}

	@Test
	void testBankReadOneInvestmentInvalid() throws Exception {
		createInvalidModelFileOneInvestment();
		loadData();
		assertTrue(loadFailed);
	}

	private void loadData() throws IOException, InterruptedException {
		bankRead.setFileName(modelFile.getAbsolutePath());
		Storage storage = new Storage();
		synchronized (waitForFinish) {
			storage.loadStoredData(bankRead);
			waitForFinish.wait();
		}
	}

	private void createValidModelFileNoInvestments() throws IOException {
		modelFile = new File(rootDirectory, "model.dat");
		String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String line2 = "<investments>";
		String line3 = "</investments>";
		List<String> lines = Arrays.asList(line1, line2, line3);
		assertFalse(modelFile.exists());
		Files.write(modelFile.toPath(), lines);
		assertTrue(modelFile.exists());
		assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
	}

	private void createValidModelFileOneInvestment() throws IOException {
		String uuid = UUID.randomUUID().toString();
		modelFile = new File(rootDirectory, "model.dat");
		String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String line2 = "<finances><banks/><investments>";
		String line3 = "<investment><investmentname>investment1</investmentname><investmentid>" + uuid
				+ "</investmentid><history/></investment>";
		String line4 = "</investments></finances>";
		List<String> lines = Arrays.asList(line1, line2, line3, line4);
		assertFalse(modelFile.exists());
		Files.write(modelFile.toPath(), lines);
		assertTrue(modelFile.exists());
		assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
	}

	private void createValidModelFileTwoInvestments() throws IOException {
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		modelFile = new File(rootDirectory, "model.dat");
		String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String line2 = "<finances><banks/><investments>";
		String line3 = "<investment><investmentname>investment1</investmentname><investmentid>" + uuid1
				+ "</investmentid><history/></investment>";
		String line4 = "<investment><investmentname>investment2</investmentname><investmentid>" + uuid2
				+ "</investmentid><history/></investment>";
		String line5 = "</investments></finances>";
		List<String> lines = Arrays.asList(line1, line2, line3, line4, line5);
		assertFalse(modelFile.exists());
		Files.write(modelFile.toPath(), lines);
		assertTrue(modelFile.exists());
		assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
	}

	private void createInvalidModelFileNoInvestments() throws IOException {
		modelFile = new File(rootDirectory, "model.dat");
		String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String line2 = "<investmentses>";
		String line3 = "</investmentses>";
		List<String> lines = Arrays.asList(line1, line2, line3);
		assertFalse(modelFile.exists());
		Files.write(modelFile.toPath(), lines);
		assertTrue(modelFile.exists());
		assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
	}

	private void createInvalidModelFileOneInvestment() throws IOException {
		modelFile = new File(rootDirectory, "model.dat");
		String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String line2 = "<investments>";
		String line3 = "<investment><name>investment1</name></investment>";
		String line4 = "</investment>";
		List<String> lines = Arrays.asList(line1, line2, line3, line4);
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
