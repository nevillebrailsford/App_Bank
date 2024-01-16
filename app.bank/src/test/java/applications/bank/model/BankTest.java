package applications.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.model.Address;
import application.model.PostCode;
import application.model.SortCode;

class BankTest {

	Bank bank1 = new Bank("bank1");
	Bank bank3 = new Bank("bank1");
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
		new Bank("test");
	}

	@Test
	void testValidBankConstructor() {
		new Bank(bank1);
	}

	@Test
	void testClear() {
		bank1.addBranch(branch1);
		assertEquals(1, bank1.branches().size());
		bank1.clear();
		assertEquals(0, bank1.branches().size());
	}

	@Test
	void testAddBranch() {
		assertEquals(0, bank1.branches().size());
		bank1.addBranch(branch1);
		assertEquals(1, bank1.branches().size());
	}

	@Test
	void testCompareToSameObject() {
		assertTrue(bank1.compareTo(bank1) == 0);
	}

	@Test
	void testCompareToSameValues() {
		assertTrue(bank1.compareTo(bank3) == 0);
	}

	@Test
	void testEqualsSameObject() {
		assertTrue(bank1.equals(bank1));
	}

	@Test
	void testEqualsSameValues() {
		assertTrue(bank1.equals(bank3));
	}

	@Test
	void testEqualsWithNull() {
		assertFalse(bank1.equals(null));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testEqualsWithDifferentClass() {
		assertFalse(bank1.equals(sortCode1));
	}

	@Test
	void testHashCodesTheSameWhenTheValuesAreTheSame() {
		assertEquals(bank1.hashCode(), bank3.hashCode());
	}

	@Test
	void testHashCodesTheSameWhenTheObjectssAreTheSame() {
		assertEquals(bank1.hashCode(), bank1.hashCode());
	}

	@Test
	void testToString() {
		assertEquals(bank1.toString(), bank1.name());
	}

	@Test
	void testValidBuilder() {
		Bank bankTest = new Bank.Builder().name("bank1").build();
		assertNotNull(bankTest);
		assertEquals(bank1, bankTest);
	}

	@Test
	void testMissingNameBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Bank.Builder().build();
		});
	}

	@Test
	void testNullNameBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Bank.Builder().name(null).build();
		});
	}

	@Test
	void testNullNameTypeConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Bank((String) null);
		});
	}

	@Test
	void testBlankNameTypeConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Bank("  ");
		});
	}

	@Test
	void testEmptyNameTypeConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Bank("");
		});
	}

	@Test
	void testNullBankConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Bank((Bank) null);
		});
	}

	@Test
	void testAddNullTransaction() {
		assertThrows(IllegalArgumentException.class, () -> {
			bank1.addBranch(null);
		});
	}
}
