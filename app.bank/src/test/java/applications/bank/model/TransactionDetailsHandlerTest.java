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
	Money money3 = new Money("30.00");
	Money money4 = new Money("40.00");
	Money money5 = new Money("50.00");
	Money money6 = new Money("60.00");
	Money money7 = new Money("70.00");

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
	Transaction transaction4 = new Transaction(LocalDate.now().minusDays(3), money4, account2, "tran4");
	Transaction transaction5 = new Transaction(LocalDate.now().minusDays(4), money5, account3, "tran5");
	Transaction transaction6 = new Transaction(LocalDate.now().minusDays(5), money6, account4, "tran6");
	Transaction transaction7 = new Transaction(LocalDate.now().minusDays(6), money7, account3, "tran6");

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
		assertEquals(3, account1.transactions().count());
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(account1);
		assertEquals(3, dates.length);
	}

	@Test
	void testTransactionDatesForBranch() {
		assertEquals(2, branch1.accounts().count());
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(branch1);
		assertEquals(4, dates.length);
	}

	@Test
	void testTransactionDatesForBank() {
		assertEquals(2, bank1.branches().count());
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(bank1);
		assertEquals(6, dates.length);
	}

	@Test
	void testTransactionDatesForBanks() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(banks);
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
		LocalDate[] dates = TransactionDetailsHandler.transactionDates(banks);
		assertEquals(7, dates.length);
	}

	@Test
	void testTransactionsForAccount() {
		assertEquals(3, account1.transactions().count());
		Transaction[] trans = TransactionDetailsHandler.transactions(account1);
		assertEquals(3, trans.length);
	}

	@Test
	void testTransactionsForBranch() {
		assertEquals(2, branch1.accounts().count());
		Transaction[] trans = TransactionDetailsHandler.transactions(branch1);
		assertEquals(4, trans.length);
	}

	@Test
	void testTransactionsForBank() {
		assertEquals(2, bank1.branches().count());
		Transaction[] trans = TransactionDetailsHandler.transactions(bank1);
		assertEquals(6, trans.length);
	}

	@Test
	void testTransactionsForBanks() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		Transaction[] trans = TransactionDetailsHandler.transactions(banks);
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
		Transaction[] trans = TransactionDetailsHandler.transactions(banks);
		assertEquals(14, trans.length);
	}

	@Test
	void testBalanceForAccount() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		Money[] monies = TransactionDetailsHandler.balance(account1);
		assertEquals(3, monies.length);
		Money total = Money.sum(monies);
		assertEquals("£60.00", total.cost());

	}

	@Test
	void testBalanceForBranch() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		Money[] monies = TransactionDetailsHandler.balance(branch1);
		assertEquals(4, monies.length);
		Money total = Money.sum(monies);
		assertEquals("£100.00", total.cost());
	}

	@Test
	void testBalanceForBank() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		Money[] monies = TransactionDetailsHandler.balance(bank1);
		assertEquals(6, monies.length);
		Money total = Money.sum(monies);
		assertEquals("£210.00", total.cost());
	}

	@Test
	void testBalanceForBanks() {
		List<Bank> banks = new ArrayList<>();
		banks.add(bank1);
		banks.add(bank2);
		Money[] monies = TransactionDetailsHandler.balance(banks);
		assertEquals(7, monies.length);
		Money total = Money.sum(monies);
		assertEquals("£280.00", total.cost());
	}
}
