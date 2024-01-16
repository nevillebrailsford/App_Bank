package applications.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.model.Address;
import application.model.Money;
import application.model.PostCode;
import application.model.SortCode;

class TransactionTest {

	private static final String VALID = "valid";
	private LocalDate date = LocalDate.now();
	private LocalDate yesterday = LocalDate.now().minusDays(1);
	private LocalDate tomorrow = LocalDate.now().plusDays(1);
	private PostCode postCode = new PostCode("BH21 4EZ");
	private Address address = new Address(postCode, new String[] { "line1", "line2", "line3" });
	private SortCode sortCode = new SortCode("00-11-22");
	private Bank bank = new Bank("bank");
	private Branch branch = new Branch(address, sortCode, bank);
	private Account owner = new Account(AccountType.CURRENT, new AccountId("current1", "0000"), branch);
	private Money amount = new Money("10.00");

	Document document;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.newDocument();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testValidConstructorAllPresent() {
		new Transaction(date, amount, owner, VALID);
	}

	@Test
	void testValidConstructorNoDestination() {
		new Transaction(date, amount, owner, VALID);
	}

	@Test
	void testValidConstructorFromTransaction() {
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		new Transaction(transaction1);
	}

	@Test
	void testValidConstructorFromTransactionNoDestination() {
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		new Transaction(transaction1);
	}

	@Test
	void testOwnerStoredCorrectly() {
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		assertEquals(owner, transaction1.owner());
	}

	@Test
	void testEqualsOnSameObject() {
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		assertTrue(transaction1.equals(transaction1));
	}

	@Test
	void testEqualsForSameDetails() {
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		Transaction transaction2 = new Transaction(date, amount, owner, VALID);
		assertTrue(transaction1.equals(transaction2));
	}

	@Test
	void testCompareTo() {
		Transaction transaction1 = new Transaction(yesterday, amount, owner, VALID);
		Transaction transaction2 = new Transaction(date, amount, owner, VALID);
		Transaction transaction3 = new Transaction(tomorrow, amount, owner, VALID);
		Transaction transaction4 = new Transaction(tomorrow, amount, owner, VALID);
		assertTrue(transaction1.compareTo(transaction2) < 0);
		assertTrue(transaction2.compareTo(transaction1) > 0);
		assertTrue(transaction2.compareTo(transaction3) < 0);
		assertTrue(transaction3.compareTo(transaction2) > 0);
		assertTrue(transaction1.compareTo(transaction3) < 0);
		assertTrue(transaction3.compareTo(transaction1) > 0);
		assertTrue(transaction3.compareTo(transaction4) == 0);
	}

	@Test
	void testToStringAllPresent() {
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		transaction1.toString();
	}

	@Test
	void testToStringNoDestination() {
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		transaction1.toString();
	}

	@Test
	void testHashCode() {
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		Transaction transaction2 = new Transaction(date, amount, owner, VALID);
		Transaction transaction3 = new Transaction(date.plusDays(1), amount, owner, VALID);
		assertNotEquals(0, transaction1.hashCode());
		assertNotEquals(0, transaction2.hashCode());
		assertEquals(transaction1.hashCode(), transaction2.hashCode());
		assertNotEquals(transaction1.hashCode(), transaction3.hashCode());
	}

	@Test
	void testBuildElementNoDestination() {
		assertNotNull(document);
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		Element testElement = transaction1.buildElement(document);
		assertNotNull(testElement);
		assertEquals(XMLConstants.TRANSACTION, testElement.getTagName());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.DATE).getLength());
		assertEquals(date.toString(), testElement.getElementsByTagName(XMLConstants.DATE).item(0).getTextContent());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.MONEY).getLength());
		assertEquals(amount.toString(), testElement.getElementsByTagName(XMLConstants.MONEY).item(0).getTextContent());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.DESCRIPTION).getLength());
		assertEquals(VALID, testElement.getElementsByTagName(XMLConstants.DESCRIPTION).item(0).getTextContent());
	}

	@Test
	void testBuildElementWithDestination() {
		assertNotNull(document);
		Transaction transaction1 = new Transaction(date, amount, owner, VALID);
		Element testElement = transaction1.buildElement(document);
		assertNotNull(testElement);
		assertEquals(XMLConstants.TRANSACTION, testElement.getTagName());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.DATE).getLength());
		assertEquals(date.toString(), testElement.getElementsByTagName(XMLConstants.DATE).item(0).getTextContent());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.MONEY).getLength());
		assertEquals(amount.toString(), testElement.getElementsByTagName(XMLConstants.MONEY).item(0).getTextContent());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.DESCRIPTION).getLength());
		assertEquals(VALID, testElement.getElementsByTagName(XMLConstants.DESCRIPTION).item(0).getTextContent());
	}

	@Test
	void testValidBuilderWithoutTransactionId() {
		Transaction transactionTest = new Transaction.Builder().date(date).amount(amount).account(owner)
				.description(VALID).build();
		assertNotNull(transactionTest);
		assertNotNull(transactionTest.transactionId());
	}

	@Test
	void testValidBuilderWithTransactionId() {
		UUID transactionId = UUID.randomUUID();
		Transaction transactionTest = new Transaction.Builder().date(date).amount(amount).account(owner)
				.description(VALID).transactionId(transactionId.toString()).build();
		assertNotNull(transactionTest);
		assertNotNull(transactionTest.transactionId());
	}

	@Test
	void testNullDate() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(null, amount, owner, VALID);
		});
	}

	@Test
	void testNullAmount() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(date, null, owner, VALID);
		});
	}

	@Test
	void testNullOrigin() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(date, amount, null, VALID);
		});
	}

	@Test
	void testNullDescrption() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(date, amount, owner, null);
		});
	}

	@Test
	void testEmptyDescrption() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(date, amount, owner, "");
		});
	}

	@Test
	void testBlankDescrption() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(date, amount, owner, "  ");
		});
	}

	@Test
	void testMissingDateBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().amount(amount).account(owner).description(VALID).build();
		});
	}

	@Test
	void testMissingAmountBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(date).account(owner).description(VALID).build();
		});
	}

	@Test
	void testMissingAccountBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(date).amount(amount).description(VALID).build();
		});
	}

	@Test
	void testNullDateBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(null).amount(amount).account(owner).description(VALID).build();
		});
	}

	@Test
	void testNullAmountBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(date).amount(null).account(null).description(VALID).build();
		});
	}

	@Test
	void testNullAccountBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(date).amount(amount).account(null).description(VALID).build();
		});
	}

	@Test
	void testMissingDescriptionBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(date).amount(amount).account(owner).build();
		});
	}

	@Test
	void testNullDescriptionBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(date).amount(amount).account(owner).description(null).build();
		});
	}

	@Test
	void testEmptyDescriptionBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(date).amount(amount).account(owner).description("").build();
		});
	}

	@Test
	void testBlankDescriptionBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction.Builder().date(date).amount(amount).account(owner).description("   ").build();
		});
	}

	@Test
	void testWithTwoTransactionIds() {
		UUID transactionId = UUID.randomUUID();
		assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(date, amount, owner, VALID, transactionId.toString(), transactionId.toString());
		});
	}
}
