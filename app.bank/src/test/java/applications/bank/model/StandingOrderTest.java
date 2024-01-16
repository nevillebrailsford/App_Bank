package applications.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.model.Address;
import application.model.AppXMLConstants;
import application.model.Money;
import application.model.Period;
import application.model.PostCode;
import application.model.SortCode;

class StandingOrderTest {

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
	Money money2 = new Money("20.00");
	Money checkBalance = new Money("30.00");
	StandingOrder standingOrder1 = new StandingOrder(account1, Period.MONTHLY, money1, LocalDate.now(), "order1",
			account2);
	StandingOrder standingOrder2 = new StandingOrder(account1, Period.MONTHLY, money1, LocalDate.now(), "order2",
			account3);
	Document document;
	LocalDate date = LocalDate.now();

	@BeforeEach
	void setUp() throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.newDocument();
	}

	@Test
	void testValidConstructorNoRecipient() {
		StandingOrder order = new StandingOrder(account1, Period.MONTHLY, money1, LocalDate.now(), "test1");
		assertNotNull(order);
	}

	@Test
	void testValidConstructorOneRecipient() {
		StandingOrder order = new StandingOrder(account1, Period.MONTHLY, money1, LocalDate.now(), "test2", account2);
		assertNotNull(order);
	}

	@Test
	void testCompareToSameObject() {
		assertTrue(standingOrder1.compareTo(standingOrder1) == 0);
	}

	@Test
	void testCompareToDifferentObject() {
		assertEquals(-1, standingOrder1.compareTo(standingOrder2));
		assertEquals(1, standingOrder2.compareTo(standingOrder1));
	}

	@Test
	void testCompareToSameValuesNoRecipient() {
		StandingOrder test1 = new StandingOrder(account1, Period.MONTHLY, money1, LocalDate.now(), "test1");
		StandingOrder test2 = new StandingOrder(account1, Period.MONTHLY, money1, LocalDate.now(), "test2");
		assertTrue(test1.compareTo(test2) == 0);
	}

	@Test
	void testBuildElementNoRecipient() {
		StandingOrder standingOrder1 = new StandingOrder(account1, Period.MONTHLY, money1, date, "test1");
		Element testElement = standingOrder1.buildElement(document);
		assertNotNull(testElement);
		validateTestElement(testElement);
		assertEquals(0, testElement.getElementsByTagName(XMLConstants.RECIPIENT_ACCOUNT).getLength());
	}

	@Test
	void testBuildElementNullRecipient() {
		StandingOrder standingOrder1 = new StandingOrder(account1, Period.MONTHLY, money1, date, "test1",
				(Account) null);
		Element testElement = standingOrder1.buildElement(document);
		assertNotNull(testElement);
		validateTestElement(testElement);
		assertEquals(0, testElement.getElementsByTagName(XMLConstants.RECIPIENT_ACCOUNT).getLength());
	}

	@Test
	void testBuildElementOneRecipient() {
		StandingOrder standingOrder1 = new StandingOrder(account1, Period.MONTHLY, money1, date, "test1", account2);
		Element testElement = standingOrder1.buildElement(document);
		assertNotNull(testElement);
		validateTestElement(testElement);
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.RECIPIENT_ACCOUNT).getLength());
		Element accountElement = (Element) testElement.getElementsByTagName(XMLConstants.RECIPIENT_ACCOUNT).item(0);
		validateAccountElement(accountElement);
	}

	@Test
	void testElementConstructorNoRecipient() {
		StandingOrder standingOrder1 = new StandingOrder(account1, Period.MONTHLY, money1, date, "test1");
		Element testElement = standingOrder1.buildElement(document);
		StandingOrder standingOrder2 = new StandingOrder(testElement);
		assertEquals(money1, standingOrder2.amount());
		assertEquals(Period.MONTHLY, standingOrder2.frequency());
		assertEquals(date, standingOrder2.nextActionDue());
		assertNull(standingOrder2.recipient());
	}

	@Test
	void testElementConstructorOneRecipient() {
		StandingOrder standingOrder1 = new StandingOrder(account1, Period.MONTHLY, money1, date, "test1", account2);
		Element testElement = standingOrder1.buildElement(document);
		StandingOrder standingOrder2 = new StandingOrder(testElement);
		assertEquals(money1, standingOrder2.amount());
		assertEquals(Period.MONTHLY, standingOrder2.frequency());
		assertEquals(date, standingOrder2.nextActionDue());
		assertNotNull(standingOrder2.recipient());
		assertEquals(account2, standingOrder2.recipient());
	}

	@Test
	void testValidBuilderNoRecipient() {
		StandingOrder test = new StandingOrder.Builder().owner(account1).amount("10.00").frequency(Period.WEEKLY)
				.reference("test1").build();
		assertNotNull(test);
		assertEquals(account1, test.owner());
		assertEquals(money1, test.amount());
		assertEquals(Period.WEEKLY, test.frequency());
		assertNull(test.recipient());
	}

	@Test
	void testValidBuilderOneRecipient() {
		StandingOrder test = new StandingOrder.Builder().owner(account1).amount("10.00").frequency(Period.WEEKLY)
				.recipient(account3).reference("testBuild").build();
		assertNotNull(test);
		assertEquals(account1, test.owner());
		assertEquals(money1, test.amount());
		assertEquals(Period.WEEKLY, test.frequency());
		assertNotNull(test.recipient());
		assertEquals(account3, test.recipient());
	}

	@Test
	void testNullOwner() {
		assertThrows(IllegalArgumentException.class, () -> {
			new StandingOrder(null, Period.MONTHLY, money1, LocalDate.now(), "test1");
		});
	}

	@Test
	void testNullFrequency() {
		assertThrows(IllegalArgumentException.class, () -> {
			new StandingOrder(account1, null, money1, LocalDate.now(), "test1");
		});
	}

	@Test
	void testNullMoney() {
		assertThrows(IllegalArgumentException.class, () -> {
			new StandingOrder(account1, Period.MONTHLY, null, LocalDate.now(), "test1");
		});
	}

	@Test
	void testTooManyRecipients() {
		assertThrows(IllegalArgumentException.class, () -> {
			new StandingOrder(account1, Period.MONTHLY, null, LocalDate.now(), "test1", account2, account3);
		});
	}

	@Test
	void testMatchingRecipient() {
		assertThrows(IllegalArgumentException.class, () -> {
			new StandingOrder(account1, Period.MONTHLY, null, LocalDate.now(), "test1", account1);
		});
	}

	private void validateTestElement(Element testElement) {
		assertEquals(XMLConstants.STANDING_ORDER, testElement.getTagName());
		assertEquals(1, testElement.getElementsByTagName(AppXMLConstants.MONEY).getLength());
		assertEquals(money1.toString(),
				testElement.getElementsByTagName(AppXMLConstants.MONEY).item(0).getTextContent());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.FREQUENCY).getLength());
		assertEquals(Period.MONTHLY.toString().toString(),
				testElement.getElementsByTagName(XMLConstants.FREQUENCY).item(0).getTextContent());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.NEXT_ACTION_DUE).getLength());
		assertEquals(date.toString(),
				testElement.getElementsByTagName(XMLConstants.NEXT_ACTION_DUE).item(0).getTextContent());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.REFERENCE).getLength());
		assertEquals("test1", testElement.getElementsByTagName(XMLConstants.REFERENCE).item(0).getTextContent());
		assertEquals(1, testElement.getElementsByTagName(XMLConstants.ORDER_ID).getLength());
	}

	private void validateAccountElement(Element accountElement) {
		assertEquals(1, accountElement.getElementsByTagName(XMLConstants.ACCOUNTHOLDER).getLength());
		assertEquals(account2.accountId().accountHolder(),
				accountElement.getElementsByTagName(XMLConstants.ACCOUNTHOLDER).item(0).getTextContent());
		assertEquals(1, accountElement.getElementsByTagName(XMLConstants.ACCOUNTNUMBER).getLength());
		assertEquals(account2.accountId().accountNumber(),
				accountElement.getElementsByTagName(XMLConstants.ACCOUNTNUMBER).item(0).getTextContent());
		assertEquals(1, accountElement.getElementsByTagName(XMLConstants.ACCOUNTTYPE).getLength());
		assertEquals(account2.accountType().toString(),
				accountElement.getElementsByTagName(XMLConstants.ACCOUNTTYPE).item(0).getTextContent());
	}
}
