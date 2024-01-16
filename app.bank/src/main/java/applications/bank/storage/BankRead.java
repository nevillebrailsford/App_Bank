package applications.bank.storage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import application.definition.ApplicationConfiguration;
import application.storage.AbstractLoadData;
import application.storage.XMLErrorHandler;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Branch;
import applications.bank.model.Investment;
import applications.bank.model.StandingOrder;
import applications.bank.model.Transaction;
import applications.bank.model.XMLConstants;

public class BankRead extends AbstractLoadData {
	private static final String CLASS_NAME = BankRead.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	@Override
	public void readData() throws IOException {
		LOGGER.entering(CLASS_NAME, "readData");
		try (InputStream archive = new BufferedInputStream(new FileInputStream(fileName()))) {
			readDataFrom(archive);
		} catch (Exception e) {
			IOException exc = new IOException("BankRead: Exception occurred - " + e.getMessage(), e);
			LOGGER.throwing(CLASS_NAME, "readData", exc);
			throw exc;
		} finally {
			LOGGER.exiting(CLASS_NAME, "readData");
		}
		LOGGER.exiting(CLASS_NAME, "readData");
	}

	private void readDataFrom(InputStream archive) throws Exception {
		LOGGER.entering(CLASS_NAME, "readDataFrom");
		Document document = buildDocument(archive);
		process(document);
		LOGGER.exiting(CLASS_NAME, "readDataFrom");
	}

	private Document buildDocument(InputStream archive) throws Exception {
		LOGGER.entering(CLASS_NAME, "buildDocument");
		Document document = null;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		URL url = BankRead.class.getResource("bank.xsd");
		documentBuilderFactory
				.setSchema(schemaFactory.newSchema(new Source[] { new StreamSource(url.toExternalForm()) }));
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		XMLErrorHandler handler = new XMLErrorHandler();
		documentBuilder.setErrorHandler(handler);
		document = documentBuilder.parse(archive);
		handler.failFast();
		document.getDocumentElement().normalize();
		LOGGER.exiting(CLASS_NAME, "buildDocuments");
		return document;
	}

	private void process(Document document) {
		LOGGER.entering(CLASS_NAME, "process");
		processBanks(document);
		processInvestments(document);
		LOGGER.exiting(CLASS_NAME, "process");
	}

	private void processBanks(Document document) {
		LOGGER.entering(CLASS_NAME, "processBanks");
		NodeList list = document.getElementsByTagName(XMLConstants.BANK);
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element bankElement = (Element) node;
				Bank bank = new Bank(bankElement);
				BankMonitor.instance().addBank(bank);
				addBranchesToBank(bank, bankElement);
			}
		}
		LOGGER.exiting(CLASS_NAME, "processBanks");
	}

	private void processInvestments(Document document) {
		LOGGER.entering(CLASS_NAME, "processInvestments");
		NodeList list = document.getElementsByTagName(XMLConstants.INVESTMENT);
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element investmentElement = (Element) node;
				Investment investment = new Investment(investmentElement);
				BankMonitor.instance().addInvestment(investment);
			}
		}
		LOGGER.exiting(CLASS_NAME, "processInvestments");
	}

	private void addBranchesToBank(Bank bank, Element bankElement) {
		LOGGER.entering(CLASS_NAME, "addBranchesToBank", bank);
		NodeList list = bankElement.getElementsByTagName(XMLConstants.BRANCH);
		if (list == null || list.getLength() == 0) {
			return;
		}
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element branchElement = (Element) node;
				Branch branch = new Branch(branchElement);
				branch.setOwner(bank);
				BankMonitor.instance().addBranch(branch);
				addAccountsToBranch(branch, branchElement);
			}
		}
		LOGGER.exiting(CLASS_NAME, "addBranchesToBank");
	}

	private void addAccountsToBranch(Branch branch, Element branchElement) {
		LOGGER.entering(CLASS_NAME, "addAccountsToBranch", branch);
		NodeList list = branchElement.getElementsByTagName(XMLConstants.ACCOUNT);
		if (list == null || list.getLength() == 0) {
			return;
		}
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element accountElement = (Element) node;
				Account account = new Account(accountElement);
				account.setOwner(branch);
				BankMonitor.instance().addAccount(account);
				addTransactionsToAccount(account, accountElement);
				addStandingOrdersToAccount(account, accountElement);
			}
		}
		LOGGER.exiting(CLASS_NAME, "addAccountsToBranch");
	}

	private void addTransactionsToAccount(Account account, Element accountElement) {
		LOGGER.entering(CLASS_NAME, "addTransactionsToAccount", account);
		NodeList list = accountElement.getElementsByTagName(XMLConstants.TRANSACTION);
		if (list == null || list.getLength() == 0) {
			return;
		}
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element transactionElement = (Element) node;
				Transaction transaction = new Transaction(transactionElement);
				transaction.setOwner(account);
				BankMonitor.instance().addTransaction(transaction);
			}
		}
		LOGGER.exiting(CLASS_NAME, "addTransactionsToAccount");
	}

	private void addStandingOrdersToAccount(Account account, Element accountElement) {
		LOGGER.entering(CLASS_NAME, "addStandingOrdersToAccount", account);
		NodeList list = accountElement.getElementsByTagName(XMLConstants.STANDING_ORDER);
		if (list == null || list.getLength() == 0) {
			return;
		}
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element standingOrderElement = (Element) node;
				StandingOrder standingOrder = new StandingOrder(standingOrderElement);
				standingOrder.setOwner(account);
				BankMonitor.instance().addStandingOrder(standingOrder);
			}
		}
		LOGGER.exiting(CLASS_NAME, "addStandingOrdersToAccount");
	}
}
