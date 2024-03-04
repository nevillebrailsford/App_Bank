package applications.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import application.model.AppXMLConstants;
import application.model.Money;

class InvestmentTest {

	Document document;

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
	void testValidConstructorNoDate() {
		new Investment("test1", new Money("22.22"));
	}

	@Test
	void testValidConstructorWithDate() {
		new Investment("test1", new Money("22.22"), LocalDate.now());
	}

	@Test
	void testToString() {
		Investment investment = new Investment("test1", new Money("22.22"));
		assertEquals("test1", investment.toString());
	}

	@Test
	void testName() {
		Investment investment = new Investment("test1", new Money("22.22"));
		assertEquals("test1", investment.name());
	}

	@Test
	void testHistoryHasOneEntry() {
		Investment investment = new Investment("test1", new Money("22.22"), LocalDate.now());
		assertEquals(1, investment.historyEntries());
	}

	@Test
	void testValueHasOneEntry() {
		Investment investment = new Investment("test1", new Money("22.22"), LocalDate.now());
		assertEquals(1, investment.historyEntries());
		assertEquals("£22.22", InvestmentHistoryHandler.value(investment).cost());
	}

	@Test
	void testValueAfterUpdate() {
		Investment investment = new Investment("test1", new Money("22.22"), LocalDate.now().minusDays(1));
		assertEquals(1, investment.historyEntries());
		investment.update(new Money("33.33"), LocalDate.now());
		assertEquals(2, investment.historyEntries());
		assertEquals("£33.33", InvestmentHistoryHandler.value(investment).cost());
		assertEquals("£22.22", InvestmentHistoryHandler.value(investment, LocalDate.now().minusDays(1)).cost());
	}

	@Test
	void testValueWithDate() {
		Investment investment = new Investment("test1", new Money("22.22"), LocalDate.now().minusDays(1));
		assertEquals(1, investment.historyEntries());
		investment.update(new Money("33.33"), LocalDate.now());
		assertEquals(2, investment.historyEntries());
		assertEquals("£33.33", InvestmentHistoryHandler.value(investment, LocalDate.now()).cost());
		assertEquals("£22.22", InvestmentHistoryHandler.value(investment, LocalDate.now().minusDays(1)).cost());
	}

	@Test
	void testValueWithLaterDate() {
		Investment investment = new Investment("test1", new Money("22.22"), LocalDate.now());
		assertEquals(1, investment.historyEntries());
		investment.update(new Money("33.33"), LocalDate.now().plusDays(2));
		assertEquals(2, investment.historyEntries());
		assertEquals("£22.22", InvestmentHistoryHandler.value(investment, LocalDate.now().plusDays(1)).cost());
	}

	@Test
	void testValueWithEarlierDate() {
		Investment investment = new Investment("test1", new Money("22.22"), LocalDate.now());
		assertEquals(1, investment.historyEntries());
		investment.update(new Money("33.33"), LocalDate.now().minusDays(2));
		assertEquals(2, investment.historyEntries());
		assertEquals("£33.33", InvestmentHistoryHandler.value(investment, LocalDate.now().minusDays(1)).cost());
	}

	@Test
	void testValidBuilderNoDate() {
		Investment investment = new Investment.Builder().name("test2").value(new Money("11.11")).build();
		assertNotNull(investment);
		assertEquals(1, investment.historyEntries());
		assertEquals("test2", investment.name());
		assertEquals("£11.11", InvestmentHistoryHandler.value(investment).cost());
	}

	@Test
	void testValidBuilderWithDate() {
		Investment investment = new Investment.Builder().name("test2").value(new Money("11.11")).date(LocalDate.now())
				.build();
		assertNotNull(investment);
		assertEquals(1, investment.historyEntries());
		assertEquals("test2", investment.name());
		assertEquals("£11.11", InvestmentHistoryHandler.value(investment).cost());
	}

	@Test
	void testValidConstructorInvestment() {
		Investment investment = new Investment.Builder().name("test2").value(new Money("11.11")).date(LocalDate.now())
				.build();
		assertNotNull(investment);
		assertEquals(1, investment.historyEntries());
		Investment investment2 = new Investment(investment);
		assertNotNull(investment2);
		assertEquals("test2", investment2.name());
		assertEquals(1, investment2.historyEntries());
	}

	@Test
	void testBuildElement() {
		Investment investment = new Investment.Builder().name("test2").value(new Money("11.11")).date(LocalDate.now())
				.build();
		assertNotNull(investment);
		assertEquals(1, investment.historyEntries());
		Element investmentElement = investment.buildElement(document);
		assertNotNull(investmentElement);
		assertEquals("test2",
				investmentElement.getElementsByTagName(XMLConstants.INVESTMENTNAME).item(0).getTextContent());
		NodeList historyNodes = investmentElement.getElementsByTagName(XMLConstants.HISTORY);
		assertEquals(1, historyNodes.getLength());
		Element historyElement = (Element) historyNodes.item(0);
		NodeList valueOnNodes = historyElement.getElementsByTagName(XMLConstants.VALUE_ON);
		assertEquals(1, valueOnNodes.getLength());
		Element valueOnElement = (Element) valueOnNodes.item(0);
		String date = valueOnElement.getElementsByTagName(XMLConstants.DATE).item(0).getTextContent();
		String value = valueOnElement.getElementsByTagName(AppXMLConstants.MONEY).item(0).getTextContent();
		assertEquals(LocalDate.now().toString(), date);
		assertEquals("11.11", value);
	}

	@Test
	void testConstructorElement() {
		Investment investment = new Investment.Builder().name("test2").value(new Money("11.11")).date(LocalDate.now())
				.build();
		Element investmentElement = investment.buildElement(document);
		Investment investment2 = new Investment(investmentElement);
		assertNotNull(investment2);
		assertEquals("test2", investment2.name());
		assertEquals("£11.11", InvestmentHistoryHandler.value(investment).cost());
	}

	@Test
	void testConstructorNullInvestment() {
		Investment investment = null;
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment(investment);
		});
	}

	@Test
	void testConstructorNullElement() {
		Element element = null;
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment(element);
		});
	}

	@Test
	void testMissingNameBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment.Builder().build();
		});
	}

	@Test
	void testNullNameBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment.Builder().name(null).build();
		});
	}

	@Test
	void testMissingValueBuilder() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment.Builder().name("Test3").build();
		});
	}

	@Test
	void testNullNameConstructor() {
		String test1 = null;
		Money test2 = null;
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment(test1, test2);
		});
	}

	@Test
	void testEmptyNameConstructor() {
		String test1 = "";
		Money test2 = null;
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment(test1, test2);
		});
	}

	@Test
	void testBlankNameConstructor() {
		String test1 = "  ";
		Money test2 = null;
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment(test1, test2);
		});
	}

	@Test
	void testNullValueConstructor() {
		String test1 = "valid";
		Money test2 = null;
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment(test1, test2);
		});
	}

	@Test
	void testNullDateConstructor() {
		String test1 = "valid";
		Money test2 = new Money("12.12");
		LocalDate test3 = null;
		assertThrows(IllegalArgumentException.class, () -> {
			new Investment(test1, test2, test3);
		});
	}
}
