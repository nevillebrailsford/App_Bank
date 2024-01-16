package applications.bank.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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
import application.model.Money;
import application.notification.Notification;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import application.notification.NotificationMonitor;
import application.storage.StorageNotificationType;
import application.storage.StoreState;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Branch;
import applications.bank.model.Transaction;
import applications.bank.model.Transfer;

class BankMonitorTest extends BankTestBase {

	NotificationListener listener = new NotificationListener() {
		@Override
		public void notify(Notification notification) {
			if (notification.notificationType() instanceof StorageNotificationType) {
				assertTrue(notification.subject().isPresent());
				handleStorage(notification);
			} else if (notification.notificationType() instanceof BankNotificationType) {
				if (notification.notificationType() != BankNotificationType.Failed) {
					assertTrue(notification.subject().isPresent());
				}
				handleBank(notification);
			} else if (notification.notificationType() instanceof BranchNotificationType) {
				if (notification.notificationType() != BranchNotificationType.Failed) {
					assertTrue(notification.subject().isPresent());
				}
				handleBranch(notification);
			} else if (notification.notificationType() instanceof AccountNotificationType) {
				if (notification.notificationType() != AccountNotificationType.Failed) {
					assertTrue(notification.subject().isPresent());
				}
				handleAccount(notification);
			} else if (notification.notificationType() instanceof TransactionNotificationType) {
				if (notification.notificationType() != TransactionNotificationType.Failed) {
					assertTrue(notification.subject().isPresent());
				}
				handleTransaction(notification);
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
	}

	@AfterEach
	void tearDown() throws Exception {
		synchronized (waitForFinish) {
			BankMonitor.instance().clear();
			waitForFinish.wait();
		}
		ApplicationConfiguration.clear();
		LogConfigurer.shutdown();
		NotificationCentre.clear();
	}

	@Test
	void testInstanceNotNull() {
		assertNotNull(BankMonitor.instance());
	}

	@Test
	void testClear() throws InterruptedException {
		addBank1();
		synchronized (waitForFinish) {
			assertEquals(1, BankMonitor.instance().banks().size());
			BankMonitor.instance().clear();
			waitForFinish.wait();
			assertEquals(0, BankMonitor.instance().banks().size());
		}
	}

	@Test
	void testAddBank() throws InterruptedException {
		assertEquals(0, BankMonitor.instance().banks().size());
		addBank1();
		assertEquals(1, BankMonitor.instance().banks().size());
	}

	@Test
	void testAddBankWithListener() throws InterruptedException {
		assertFalse(addedBank);
		addBank1();
		assertTrue(addedBank);
	}

	@Test
	void testAddNullBank() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().addBank((Bank) null);
			});
			assertEquals("BankMonitor: newBank is null", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testAddDuplicateBank() throws InterruptedException {
		addBank1();
		assertFalse(failedIO);
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().addBank(bank1);
			});
			assertEquals("BankMonitor: bank bank1 already exists", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testRemoveBank() throws InterruptedException {
		addBank1();
		assertEquals(1, BankMonitor.instance().banks().size());
		removeBank1();
	}

	@Test
	void testRemoveBankWithListener() throws InterruptedException {
		assertFalse(addedBank);
		assertFalse(removedBank);
		addBank1();
		assertTrue(addedBank);
		removeBank1();
		assertTrue(removedBank);
	}

	@Test
	void testRemoveNullBank() throws InterruptedException {
		int banks = BankMonitor.instance().banks().size();
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().removeBank((Bank) null);
			});
			assertEquals("BankMonitor: oldBank is null", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
		assertEquals(banks, BankMonitor.instance().banks().size());
	}

	@Test
	void testRemoveUnknownBank() throws InterruptedException {
		int banks = BankMonitor.instance().banks().size();
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().removeBank(bank1);
			});
			assertEquals("BankMonitor: bank bank1 is not known", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
		assertEquals(banks, BankMonitor.instance().banks().size());
	}

	@Test
	void testAddBranch() throws InterruptedException {
		assertFalse(addedBank);
		assertFalse(addedBranch);
		addBank1();
		assertTrue(addedBank);
		addBranch1();
		assertTrue(addedBranch);
	}

	@Test
	void testBranches() throws InterruptedException {
		assertFalse(addedBank);
		assertFalse(addedBranch);
		addBank1();
		assertTrue(addedBank);
		addBranch1();
		assertTrue(addedBranch);
		assertEquals(1, BankMonitor.instance().branches().size());
	}

	@Test
	void testAddNullBranch() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().addBranch((Branch) null);
			});
			assertEquals("BankMonitor: newBranch is null", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testAddBranchUnknownOwner() throws InterruptedException {
		assertFalse(addedBank);
		assertFalse(addedBranch);
		addBank1();
		assertTrue(addedBank);
		synchronized (waitForFinish) {
			branch1.setOwner(bank2);
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().addBranch(branch1);
			});
			assertEquals("BankMonitor: owner is not known", exc.getMessage());
			waitForFinish.wait();
		}
		assertFalse(addedBranch);
		assertTrue(failedIO);
	}

	@Test
	public void testRemoveBranch() throws InterruptedException {
		assertFalse(addedBank);
		assertFalse(addedBranch);
		assertFalse(removedBranch);
		addBank1();
		assertTrue(addedBank);
		addBranch1();
		assertTrue(addedBranch);
		removeBranch1();
		assertTrue(removedBranch);
	}

	@Test
	void testRemoveNullBranch() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().removeBranch((Branch) null);
			});
			assertEquals("BankMonitor: oldBranch is null", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testRemoveBranchUnknownOwner() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().removeBranch(branch1);
			});
			assertEquals("BankMonitor: owner is not known", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testAddAccount() throws InterruptedException {
		assertFalse(addedBank);
		assertFalse(addedBranch);
		assertFalse(addedAccount);
		addBank1();
		assertTrue(addedBank);
		addBranch1();
		assertTrue(addedBranch);
		addAccount1();
		assertTrue(addedAccount);
	}

	@Test
	void testAccounts() throws InterruptedException {
		addBank1();
		addBranch1();
		addAccount1();
		addAccount2();
		assertEquals(2, BankMonitor.instance().accounts().size());
	}

	@Test
	void testTransfer() throws InterruptedException {
		addBank1();
		addBranch1();
		addAccount1();
		addAccount2();
		assertEquals(2, BankMonitor.instance().accounts().size());
		Transfer transfer = new Transfer(account1, account2, new Money("10.00"));
		transfer(transfer);
	}

	@Test
	void testAddNullAccount() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().addAccount((Account) null);
			});
			assertEquals("BankMonitor: newAccount is null", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testAddAccountUnknownOwner() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().addAccount(account1);
			});
			assertEquals("BankMonitor: owner is not known", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testRemoveNullAccount() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().removeAccount((Account) null);
			});
			assertEquals("BankMonitor: oldAccount is null", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testRemoveAccountUnknownOwner() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().removeAccount(account1);
			});
			assertEquals("BankMonitor: owner is not known", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	public void testRemoveAccount() throws InterruptedException {
		assertFalse(addedBank);
		assertFalse(addedBranch);
		assertFalse(addedAccount);
		assertFalse(removedAccount);
		addBank1();
		assertTrue(addedBank);
		addBranch1();
		assertTrue(addedBranch);
		addAccount1();
		assertTrue(addedAccount);
		removeAccount1();
		assertTrue(removedAccount);
	}

	@Test
	void testAddTransaction() throws InterruptedException {
		assertFalse(addedBank);
		assertFalse(addedBranch);
		assertFalse(addedAccount);
		assertFalse(addedTransaction);
		addBank1();
		assertTrue(addedBank);
		addBranch1();
		assertTrue(addedBranch);
		addAccount1();
		assertTrue(addedAccount);
		addTransaction1();
		assertTrue(addedTransaction);
	}

	@Test
	void testAddNullTransaction() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().addTransaction((Transaction) null);
			});
			assertEquals("BankMonitor: newTransaction is null", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	@Test
	void testRemoveTransactionUnknownOrigin() throws InterruptedException {
		synchronized (waitForFinish) {
			Exception exc = assertThrows(IllegalArgumentException.class, () -> {
				BankMonitor.instance().addTransaction(transaction1);
			});
			assertEquals("BankMonitor: owner is not known", exc.getMessage());
			waitForFinish.wait();
		}
		assertTrue(failedIO);
	}

	private void handleBank(Notification notification) {
		BankNotificationType type = (BankNotificationType) notification.notificationType();
		switch (type) {
			case Add -> addBank();
			case Changed -> changeBank();
			case Removed -> removeBank();
			case Failed -> failed();
		}
	}

	private void handleBranch(Notification notification) {
		BranchNotificationType type = (BranchNotificationType) notification.notificationType();
		switch (type) {
			case Add -> addBranch();
			case Changed -> changeBranch();
			case Removed -> removeBranch();
			case Failed -> failed();
		}
	}

	private void handleAccount(Notification notification) {
		AccountNotificationType type = (AccountNotificationType) notification.notificationType();
		switch (type) {
			case Add -> addAccount();
			case Changed -> changeAccount();
			case Removed -> removeAccount();
			case Failed -> failed();
		}
	}

	private void handleTransaction(Notification notification) {
		TransactionNotificationType type = (TransactionNotificationType) notification.notificationType();
		switch (type) {
			case Add -> addTransaction();
			case Failed -> failed();
			case Removed -> failed();
		}
	}

	private void handleStorage(Notification notification) {
		StorageNotificationType type = (StorageNotificationType) notification.notificationType();
		switch (type) {
			case Store -> {
				StoreState state = (StoreState) notification.subject().get();
				switch (state) {
					case Complete -> storeData();
					case Failed -> storeData();
					case Started -> ignore();
				}
			}
			case Load -> ignore();
		}
	}

	private void ignore() {
	}

	private void storeData() {
		synchronized (waitForFinish) {
			waitForFinish.notifyAll();
		}
	}

	private void failed() {
		synchronized (waitForFinish) {
			failedIO = true;
			waitForFinish.notifyAll();
		}
	}

	private void addBank() {
		addedBank = true;
	}

	private void removeBank() {
		removedBank = true;
	}

	private void changeBank() {
	}

	private void addBranch() {
		addedBranch = true;
	}

	private void changeBranch() {
	}

	private void removeBranch() {
		removedBranch = true;
	}

	private void addAccount() {
		addedAccount = true;
	}

	private void addTransaction() {
		addedTransaction = true;
	}

	private void changeAccount() {
	}

	private void removeAccount() {
		removedAccount = true;
	}

	private void addBank1() throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addBank(bank1);
			waitForFinish.wait();
		}
	}

	private void removeBank1() throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().removeBank(bank1);
			waitForFinish.wait();
		}
	}

	private void addBranch1() throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addBranch(branch1);
			waitForFinish.wait();
		}
	}

	private void removeBranch1() throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().removeBranch(branch1);
			waitForFinish.wait();
		}
	}

	private void addAccount1() throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addAccount(account1);
			waitForFinish.wait();
		}
	}

	private void addAccount2() throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addAccount(account2);
			waitForFinish.wait();
		}
	}

	private void transfer(Transfer transfer) throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().transfer(transfer);
			waitForFinish.wait();
		}
	}

	private void removeAccount1() throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().removeAccount(account1);
			waitForFinish.wait();
		}
	}

	private void addTransaction1() throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addTransaction(transaction1);
			waitForFinish.wait();
		}
	}
}
