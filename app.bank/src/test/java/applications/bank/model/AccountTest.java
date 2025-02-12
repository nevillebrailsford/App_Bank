package applications.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.model.Address;
import application.model.Money;
import application.model.Period;
import application.model.PostCode;
import application.model.SortCode;

class AccountTest {

	Bank bank1 = new Bank("bank1");
	PostCode postCode1 = new PostCode("BH21 4EZ");
	Address address1 = new Address(postCode1, new String[] { "line 1", "line 2", "line 3" });
	SortCode sortCode1 = new SortCode("01-23-45");
	Branch branch1 = new Branch(address1, sortCode1, bank1);
	AccountId accountId1 = new AccountId("name", "001");
	Account account1 = new Account(AccountType.CURRENT, accountId1, branch1);
	String accountToString = "001";
	String accountToFullString = "Account named name, account numbered 001";

	SortCode sortCode2 = new SortCode("00-23-45");
	Branch branch2 = new Branch(address1, sortCode2, bank1);
	AccountId accountId2 = new AccountId("name", "001");
	Account account2 = new Account(AccountType.CURRENT, accountId1, branch2);
	Branch branch3 = new Branch(address1, sortCode1, bank1);

	AccountId accountId3 = new AccountId("name", "002");
	Account account3 = new Account(AccountType.CURRENT, accountId3, branch1);

	Account account4 = new Account(AccountType.DEPOSIT, accountId1, branch1);

	Account account5 = new Account(AccountType.CURRENT, accountId1, branch1);

	Money money1 = new Money("10.00");
	Transaction transaction1 = new Transaction(LocalDate.now(), money1, account1, "tran1");
	Money money2 = new Money("20.00");
	Transaction transaction2 = new Transaction(LocalDate.now(), money2, account1, "tran2");
	Money money3 = new Money("10.00");
	Transaction transaction3 = new Transaction(LocalDate.now().minusDays(1), money3, account1, "tran3");
	Money checkBalance = new Money("30.00");
	Money chekcBalanceOnDayMinus0 = new Money("20.00");
	Money chekcBalanceOnDayMinus1 = new Money("10.00");
	Money chekcBalanceOnDayMinus2 = new Money("0.00");
	StandingOrder order1 = new StandingOrder(account1, Period.MONTHLY, money1, LocalDate.now(), "order1", account2);
	StandingOrder order2 = new StandingOrder(account1, Period.MONTHLY, money1, LocalDate.now(), "order2", account3);

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		account1.clear();
		account2.clear();
		account3.clear();
		account4.clear();
		account5.clear();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testValidConstructor() {
		new Account(AccountType.CURRENT, accountId1, branch1);
	}

	@Test
	void testValidAccountConstructor() {
		new Account(account1);
	}

	@Test
	void testClearTransactions() {
		account1.addTransaction(transaction1);
		assertEquals(1, account1.transactions().size());
		account1.clear();
		assertEquals(0, account1.transactions().size());
	}

	@Test
	void testClearStandingOrders() {
		account1.addStandingOrder(order1);
		assertEquals(1, account1.standingOrders().size());
		account1.clear();
		assertEquals(0, account1.standingOrders().size());
	}

	@Test
	void testClearBoth() {
		account1.addTransaction(transaction1);
		assertEquals(1, account1.transactions().size());
		account1.addStandingOrder(order1);
		assertEquals(1, account1.standingOrders().size());
		account1.clear();
		assertEquals(0, account1.transactions().size());
		assertEquals(0, account1.standingOrders().size());
	}

	@Test
	void testAddTransaction() {
		assertEquals(0, account1.transactions().size());
		account1.addTransaction(transaction1);
		assertEquals(1, account1.transactions().size());
		assertEquals(0, account1.standingOrders().size());
	}

	@Test
	void testAddStandingOrder() {
		assertEquals(0, account1.standingOrders().size());
		account1.addStandingOrder(order1);
		assertEquals(1, account1.standingOrders().size());
		assertEquals(0, account1.transactions().size());
	}

	@Test
	void testUpdateStandingOrder() {
		assertEquals(0, account1.standingOrders().size());
		account1.addStandingOrder(order1);
		assertEquals(1, account1.standingOrders().size());
		assertEquals(0, account1.transactions().size());
		StandingOrder change = new StandingOrder(order1);
		change.setReference("updated reference");
		account1.updateStandingOrder(change);
		assertEquals(1, account1.standingOrders().size());
		assertEquals(0, account1.transactions().size());
		assertEquals("updated reference", account1.standingOrders().get(0).reference());
	}

	@Test
	void testBalance() {
		assertEquals(0, account1.transactions().size());
		account1.addTransaction(transaction1);
		account1.addTransaction(transaction2);
		assertEquals(2, account1.transactions().size());
		assertEquals(checkBalance, Money.sum(TransactionDetailsHandler.balance(account1)));
	}

	@Test
	void testBalanceOnDate() {
		assertEquals(0, account1.transactions().size());
		account1.addTransaction(transaction1);
		account1.addTransaction(transaction3);
		assertEquals(2, account1.transactions().size());
		assertEquals(chekcBalanceOnDayMinus0, Money.sum(TransactionDetailsHandler.balance(account1, LocalDate.now())));
		assertEquals(chekcBalanceOnDayMinus1,
				Money.sum(TransactionDetailsHandler.balance(account1, LocalDate.now().minusDays(1))));
		assertEquals(chekcBalanceOnDayMinus2,
				Money.sum(TransactionDetailsHandler.balance(account1, LocalDate.now().minusDays(2))));
	}

	@Test
	void testCompareToSameObject() {
		assertTrue(account1.compareTo(account1) == 0);
	}

	@Test
	void testCompareToSameValues() {
		assertTrue(account1.compareTo(account5) == 0);
	}

	@Test
	void testCompareToWithDifferentSortCodes() {
		assertTrue(account1.compareTo(account2) > 0);
		assertTrue(account2.compareTo(account1) < 0);
	}

	@Test
	void testCompareToWithDifferentAccountNumbers() {
		assertTrue(account1.compareTo(account3) < 0);
		assertTrue(account3.compareTo(account1) > 0);
	}

	@Test
	void testCompareToWithDifferentAccountTypes() {
		assertTrue(account1.compareTo(account4) < 0);
		assertTrue(account4.compareTo(account1) > 0);
	}

	@Test
	void testEqualsSameObject() {
		assertTrue(account1.equals(account1));
	}

	@Test
	void testEqualsSameValues() {
		assertTrue(account1.equals(account5));
	}

	@Test
	void testEqualsWithDifferentSortCodes() {
		assertFalse(account1.equals(account2));
		assertFalse(account2.equals(account1));
	}

	@Test
	void testEqualsWithDifferentAccountNumbers() {
		assertFalse(account1.equals(account3));
		assertFalse(account3.equals(account1));
	}

	@Test
	void testEqualsWithDifferentAccountTypes() {
		assertFalse(account1.equals(account4));
		assertFalse(account4.equals(account1));
	}

	@Test
	void testAccountBuilder() {
		Account accountTest = new Account.Builder().accountType(AccountType.CURRENT).accountHolder("name")
				.accountNumber("001").branch(branch1).build();
		assertNotNull(accountTest);
		assertEquals(account1, accountTest);
	}

	@Test
	void testEqualsWithNull() {
		assertFalse(account1.equals(null));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testEqualsWithDifferentClass() {
		assertFalse(account1.equals(sortCode1));
	}

	@Test
	void testHashCodesTheSameWhenTheValuesAreTheSame() {
		assertEquals(account1.hashCode(), account5.hashCode());
	}

	@Test
	void testHashCodesTheSameWhenTheObjectssAreTheSame() {
		assertEquals(account1.hashCode(), account1.hashCode());
	}

	@Test
	void testToString() {
		assertEquals(account1.toString(), accountToString);
	}

	@Test
	void testToFullString() {
		assertEquals(account1.toFullString(), accountToFullString);
	}

	@Test
	void testActive() {
		assertTrue(account1.active());
	}

	@Test
	void testDeactivate() {
		assertTrue(account1.active());
		account1.deactivate();
		assertFalse(account1.active());
	}

	@Test
	void testReactivate() {
		account1.deactivate();
		assertFalse(account1.active());
		account1.reactivate();
		assertTrue(account1.active());
	}

	@Test
	void testMissingAccountTypeBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountHolder("name").accountNumber("001").branch(branch1).build();
		});
	}

	@Test
	void testMissingAccountHolderBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountNumber("001").branch(branch1).build();
		});
	}

	@Test
	void testMissingAccountNumberBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder("name").branch(branch1).build();
		});
	}

	@Test
	void testMissingBranchBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder("name").accountNumber("001").build();
		});
	}

	@Test
	void testNullAccountTypeBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(null).accountHolder("name").accountNumber("001").branch(branch1).build();
		});
	}

	@Test
	void testNullAccountHolderBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder(null).accountNumber("001")
					.branch(branch1).build();
		});
	}

	@Test
	void testEmptyAccountHolderBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder("").accountNumber("001")
					.branch(branch1).build();
		});
	}

	@Test
	void testBlankAccountHolderBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder("   ").accountNumber("001")
					.branch(branch1).build();
		});
	}

	@Test
	void testNullAccountNumberBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder("name").accountNumber(null)
					.branch(branch1).build();
		});
	}

	@Test
	void testEmptyAccountNumberBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder("name").accountNumber("")
					.branch(branch1).build();
		});
	}

	@Test
	void testBlankAccountNumberBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder("name").accountNumber("  ")
					.branch(branch1).build();
		});
	}

	@Test
	void testNullBranchBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account.Builder().accountType(AccountType.CURRENT).accountHolder("name").accountNumber("001")
					.branch(null).build();
		});
	}

	@Test
	void testNullAccountTypeConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account(null, accountId1, branch1);
		});
	}

	@Test
	void testNullAccountIdConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account(AccountType.CURRENT, null, branch1);
		});
	}

	@Test
	void testNullBranchConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account(AccountType.CURRENT, accountId1, null);
		});
	}

	@Test
	void testNullAccountConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Account((Account) null);
		});
	}

	@Test
	void testAddNullTransaction() {
		assertThrows(IllegalArgumentException.class, () -> {
			account1.addTransaction(null);
		});
	}
}
