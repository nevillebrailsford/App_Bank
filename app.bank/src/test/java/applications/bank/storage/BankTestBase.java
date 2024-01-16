package applications.bank.storage;

import java.io.File;
import java.time.LocalDate;

import org.junit.jupiter.api.io.TempDir;

import application.model.Address;
import application.model.Money;
import application.model.PostCode;
import application.model.SortCode;
import application.notification.Notification;
import application.notification.NotificationListener;
import application.notification.NotificationMonitor;
import application.storage.Storage;
import application.storage.StorageConstants;
import application.storage.StorageNotificationType;
import application.storage.StoreState;
import applications.bank.model.Account;
import applications.bank.model.AccountId;
import applications.bank.model.AccountType;
import applications.bank.model.Bank;
import applications.bank.model.Branch;
import applications.bank.model.Investment;
import applications.bank.model.Transaction;

public abstract class BankTestBase {
	static boolean runMonitor = false;
	NotificationMonitor notificationMonitor = null;

	Bank bank1 = new Bank("bank1");
	Bank bank2 = new Bank("bank2");

	PostCode postcode1 = new PostCode("SO51 7FA");
	Address address1 = new Address(postcode1, new String[] { "line1", "line2", "line3" });
	SortCode sortcode1 = new SortCode("12-34-56");
	Branch branch1 = new Branch(address1, sortcode1, bank1);
	PostCode postcode2 = new PostCode("SO51 7FB");
	Address address2 = new Address(postcode1, new String[] { "line1", "line2", "line3" });
	SortCode sortcode2 = new SortCode("98-76-54");
	Branch branch2 = new Branch(address2, sortcode2, bank1);
	PostCode postcode3 = new PostCode("SO51 7FB");
	Address address3 = new Address(postcode1, new String[] { "line1", "line2", "line3" });
	SortCode sortcode3 = new SortCode("98-76-54");
	Branch branch3 = new Branch(address3, sortcode3, bank2);

	AccountId accountid1 = new AccountId("accountholder", "accountnumber");
	Account account1 = new Account.Builder().accountType(AccountType.CURRENT).accountHolder("accountholder")
			.accountNumber("accountnumber").branch(branch1).build();
	Account account2 = new Account.Builder().accountType(AccountType.CURRENT).accountHolder("accountholder2")
			.accountNumber("accountnumber2").branch(branch1).build();
	Account account3 = new Account.Builder().accountType(AccountType.CURRENT).accountHolder("accountholder3")
			.accountNumber("accountnumber3").branch(branch2).build();
	AccountId accountid2 = new AccountId("accountholder2", "accountnumber2");
	AccountId accountid3 = new AccountId("accountholder3", "accountnumber3");

	Money money1 = new Money("15.34");
	Money money2 = new Money("27.34");
	Transaction transaction1 = new Transaction.Builder().date(LocalDate.now()).amount(money1).account(account1)
			.description("tran1").build();
	Transaction transaction2 = new Transaction.Builder().date(LocalDate.now()).amount(money1).account(account1)
			.description("tran2").build();
	Transaction transaction3 = new Transaction.Builder().date(LocalDate.now()).amount(money1).account(account2)
			.description("tran3").build();

	Investment investment1 = new Investment.Builder().name("investment1").date(LocalDate.now()).value(new Money(money1))
			.build();
	Investment investment2 = new Investment.Builder().name("investment2").date(LocalDate.now().minusDays(1))
			.value(new Money(money1)).build();

	Object waitForFinish = new Object();

	boolean storeSuccess = false;
	boolean storeFailed = false;
	boolean loadSuccess = false;
	boolean loadFailed = false;

	boolean addedBank = false;
	boolean removedBank = false;
	boolean addedBranch = false;
	boolean removedBranch = false;
	boolean addedAccount = false;
	boolean removedAccount = false;
	boolean addedTransaction = false;
	boolean failedIO = false;

	BankStore bankStore = null;
	BankRead bankRead = null;

	File modelFile = null;

	@TempDir
	File rootDirectory;

	NotificationListener listener = new NotificationListener() {

		@Override
		public void notify(Notification notification) {
			if (notification.notificationType().category().equals(StorageConstants.STORAGE_CATEGORY)) {
				StorageNotificationType storeNotification = (StorageNotificationType) notification.notificationType();
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

	private void storeComplete() {
		synchronized (waitForFinish) {
			waitForFinish.notifyAll();
			storeFailed = false;
			storeSuccess = true;
		}
	}

	private void storeFailed() {
		synchronized (waitForFinish) {
			waitForFinish.notifyAll();
			storeSuccess = false;
			storeFailed = true;
		}
	}

	private void ignore() {
	}

	void resetFlags() {
		storeSuccess = false;
		storeFailed = false;
		loadSuccess = false;
		loadFailed = false;
		addedBank = false;
		removedBank = false;
		addedBranch = false;
		removedBranch = false;
		addedAccount = false;
		removedAccount = false;
		addedTransaction = false;
		failedIO = false;
	}

	Storage initStorage() {
		modelFile = new File(rootDirectory, "model.dat");
		bankStore.setFileName(modelFile.getAbsolutePath());
		Storage storage = new Storage();
		return storage;
	}

	void writeToStore(Storage storage) throws InterruptedException {
		synchronized (waitForFinish) {
			storage.storeData(bankStore);
			waitForFinish.wait();
		}
	}

	void storeBank(Bank bank) throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addBank(bank);
			waitForFinish.wait();
		}
	}

	void storeBranch(Branch branch) throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addBranch(branch);
			waitForFinish.wait();
		}
	}

	void storeAccount(Account account) throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addAccount(account);
			waitForFinish.wait();
		}
	}

	void storeTransaction(Transaction transaction) throws InterruptedException {
		synchronized (waitForFinish) {
			BankMonitor.instance().addTransaction(transaction);
			waitForFinish.wait();
		}
	}
}
