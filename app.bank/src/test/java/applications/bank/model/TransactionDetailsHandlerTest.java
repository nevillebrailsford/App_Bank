package applications.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.model.Address;
import application.model.Money;
import application.model.PostCode;
import application.model.SortCode;

class TransactionDetailsHandlerTest {

	PostCode postCode1 = new PostCode("BH21 4EZ");
	Address address1 = new Address(postCode1, new String[] { "line 1", "line 2", "line 3" });
	SortCode sortCode1 = new SortCode("01-23-45");
	SortCode sortCode2 = new SortCode("00-23-45");
	AccountId accountId1 = new AccountId("name", "001");
	AccountId accountId2 = new AccountId("name", "002");
	AccountId accountId3 = new AccountId("name", "003");
	AccountId accountId4 = new AccountId("name", "004");
	AccountId accountId5 = new AccountId("name", "005");
	Money money1 = new Money("10.00");
	Money money2 = new Money("20.00");
	Money money3 = new Money("10.00");

	Bank bank1 = new Bank("bank1");
	Bank bank2 = new Bank("bank2");

	Branch branch1 = new Branch(address1, sortCode1, bank1);
	Branch branch2 = new Branch(address1, sortCode2, bank1);
	Branch branch3 = new Branch(address1, sortCode1, bank2);

	Account account1 = new Account(AccountType.CURRENT, accountId1, branch1);
	Account account2 = new Account(AccountType.CURRENT, accountId2, branch1);
	Account account3 = new Account(AccountType.CURRENT, accountId3, branch2);
	Account account4 = new Account(AccountType.DEPOSIT, accountId1, branch2);
	Account account5 = new Account(AccountType.CURRENT, accountId1, branch3);

	Transaction transaction1 = new Transaction(LocalDate.now(), money1, account1, "tran1");
	Transaction transaction2 = new Transaction(LocalDate.now().minusDays(1), money2, account1, "tran2");
	Transaction transaction3 = new Transaction(LocalDate.now().minusDays(2), money3, account1, "tran3");
	Transaction transaction4 = new Transaction(LocalDate.now().minusDays(3), money1, account2, "tran4");
	Transaction transaction5 = new Transaction(LocalDate.now().minusDays(4), money1, account3, "tran5");
	Transaction transaction6 = new Transaction(LocalDate.now().minusDays(5), money1, account4, "tran6");
	Transaction transaction7 = new Transaction(LocalDate.now().minusDays(6), money1, account3, "tran6");

	@BeforeEach
	void setUp() throws Exception {
		bank1.addBranch(branch1);
		bank1.addBranch(branch2);

		branch1.addAccount(account1);
		branch1.addAccount(account2);
		branch2.addAccount(account3);
		branch2.addAccount(account4);

		account1.addTransaction(transaction1);
		account1.addTransaction(transaction2);
		account1.addTransaction(transaction3);
		account2.addTransaction(transaction4);
		account3.addTransaction(transaction5);
		account4.addTransaction(transaction6);

		bank2.addBranch(branch3);
		branch3.addAccount(account5);
		account5.addTransaction(transaction7);
	}

	@Test
	void testTransactionDatesForAccount() {
		assertEquals(3, account1.transactions().size());
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(account1).toArray(new LocalDate[] {});
		assertEquals(3, dates.length);
	}

	@Test
	void testTransactionDatesForBranch() {
		assertEquals(2, branch1.accounts().size());
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(branch1).toArray(new LocalDate[] {});
		assertEquals(4, dates.length);
	}

	@Test
	void testTransactionDatesForBank() {
		assertEquals(2, bank1.branches().size());
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(bank1).toArray(new LocalDate[] {});
		assertEquals(6, dates.length);
	}

	@Test
	void testTransactionDatesForBanks() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(banks).toArray(new LocalDate[] {});
		assertEquals(7, dates.length);
	}

	@Test
	void testTransactionDatesForBanksWithDuplicateTransactions() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		account1.addTransaction(transaction1);
		account1.addTransaction(transaction2);
		account1.addTransaction(transaction3);
		account2.addTransaction(transaction4);
		account3.addTransaction(transaction5);
		account4.addTransaction(transaction6);
		account5.addTransaction(transaction7);
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(banks).toArray(new LocalDate[] {});
		assertEquals(7, dates.length);
	}

	@Test
	void testTransactionsForAccount() {
		assertEquals(3, account1.transactions().size());
		Transaction[] trans = TransactionDetailsHandler.transactions(account1).toArray(new Transaction[] {});
		assertEquals(3, trans.length);
	}

	@Test
	void testTransactionsForBranch() {
		assertEquals(2, branch1.accounts().size());
		Transaction[] trans = TransactionDetailsHandler.transactions(branch1).toArray(new Transaction[] {});
		assertEquals(4, trans.length);
	}

	@Test
	void testTransactionsForBank() {
		assertEquals(2, bank1.branches().size());
		Transaction[] trans = TransactionDetailsHandler.transactions(bank1).toArray(new Transaction[] {});
		assertEquals(6, trans.length);
	}

	@Test
	void testTransactionsForBanks() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		Transaction[] trans = TransactionDetailsHandler.transactions(banks).toArray(new Transaction[] {});
		assertEquals(7, trans.length);
	}

	@Test
	void testTransactionsForBanksWithDuplicateTransactions() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		account1.addTransaction(transaction1);
		account1.addTransaction(transaction2);
		account1.addTransaction(transaction3);
		account2.addTransaction(transaction4);
		account3.addTransaction(transaction5);
		account4.addTransaction(transaction6);
		account5.addTransaction(transaction7);
		Transaction[] trans = TransactionDetailsHandler.transactions(banks).toArray(new Transaction[] {});
		assertEquals(14, trans.length);
	}

}
