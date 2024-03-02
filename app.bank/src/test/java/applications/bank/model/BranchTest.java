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
import application.model.PostCode;
import application.model.SortCode;

class BranchTest {

	Bank bank1 = new Bank("bank1");
	PostCode postCode1 = new PostCode("BH21 4EZ");
	Address address1 = new Address(postCode1, new String[] { "line 1", "line 2", "line 3" });
	SortCode sortCode1 = new SortCode("01-23-45");
	Branch branch1 = new Branch(address1, sortCode1, bank1);
	AccountId accountId1 = new AccountId("name", "001");
	Account account1 = new Account(AccountType.CURRENT, accountId1, branch1);
	String accountToString = "Account named name, account numbered 001";

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
	Money money4 = new Money("10.00");
	Transaction transaction4 = new Transaction(LocalDate.now().minusDays(2), money3, account2, "tran4");
	Money checkBalance = new Money("50.00");

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		branch1.clear();
		branch2.clear();
		branch3.clear();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testValidConstructor() {
		new Branch(address1, sortCode1, bank1);
	}

	@Test
	void testValidBranchConstructor() {
		new Branch(branch1);
	}

	@Test
	void testClear() {
		branch1.addAccount(account1);
		assertEquals(1, branch1.accounts().size());
		branch1.clear();
		assertEquals(0, branch1.accounts().size());
	}

	@Test
	void testAddAccount() {
		assertEquals(0, branch1.accounts().size());
		branch1.addAccount(account1);
		assertEquals(1, branch1.accounts().size());
	}

	@Test
	void testBalance() {
		assertEquals(0, branch1.accounts().size());
		branch1.addAccount(account1);
		branch1.addAccount(account2);
		assertEquals(2, branch1.accounts().size());
		account1.addTransaction(transaction1);
		account1.addTransaction(transaction2);
		account1.addTransaction(transaction3);
		account2.addTransaction(transaction4);
		assertEquals(checkBalance, branch1.balance());
	}

	@Test
	void testCompareToSameObject() {
		assertTrue(branch1.compareTo(branch1) == 0);
	}

	@Test
	void testCompareToSameValues() {
		assertTrue(branch1.compareTo(branch3) == 0);
	}

	@Test
	void testEqualsSameObject() {
		assertTrue(branch1.equals(branch1));
	}

	@Test
	void testEqualsSameValues() {
		assertTrue(branch1.equals(branch3));
	}

	@Test
	void testEqualsWithNull() {
		assertFalse(branch1.equals(null));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testEqualsWithDifferentClass() {
		assertFalse(branch1.equals(sortCode1));
	}

	@Test
	void testHashCodesTheSameWhenTheValuesAreTheSame() {
		assertEquals(branch1.hashCode(), branch3.hashCode());
	}

	@Test
	void testHashCodesTheSameWhenTheObjectssAreTheSame() {
		assertEquals(branch1.hashCode(), branch1.hashCode());
	}

	@Test
	void testToFullString() {
		String branchToString = "Branch at line 1, line 2, line 3 BH21 4EZ with sort code 01-23-45";
		assertEquals(branch1.toFullString(), branchToString);
	}

	@Test
	void testValidBuilder() {
		Branch branchTest = new Branch.Builder().address(address1).sortCode(sortCode1).bank(bank1).build();
		assertNotNull(branchTest);
		assertEquals(branchTest, branch1);
	}

	@Test
	void testNullAddressTypeConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch(null, sortCode1, bank1);
		});
	}

	@Test
	void testNullAmountConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch(address1, null, bank1);
		});
	}

	@Test
	void testNullBankConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch(address1, sortCode1, null);
		});
	}

	@Test
	void testAddNullTransaction() {
		assertThrows(IllegalArgumentException.class, () -> {
			branch1.addAccount(null);
		});
	}

	@Test
	void testNullAddressBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch.Builder().address(null).sortCode(sortCode1).bank(bank1).build();
		});
	}

	@Test
	void testNullAmountBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch.Builder().address(address1).sortCode(null).bank(bank1).build();
		});
	}

	@Test
	void testNullBankBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch.Builder().address(address1).sortCode(sortCode1).bank(null).build();
		});
	}

	@Test
	void testMissingAddressCallBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch.Builder().sortCode(sortCode1).bank(bank1).build();
		});
	}

	@Test
	void testMissingAmountCallBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch.Builder().address(address1).bank(bank1).build();
		});
	}

	@Test
	void testMissingBankCallBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Branch.Builder().address(address1).sortCode(sortCode1).build();
		});
	}
}
