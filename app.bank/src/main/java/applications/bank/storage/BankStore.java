package applications.bank.storage;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.definition.ApplicationConfiguration;
import application.storage.AbstractStoreData;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Branch;
import applications.bank.model.Investment;
import applications.bank.model.StandingOrder;
import applications.bank.model.Transaction;
import applications.bank.model.XMLConstants;

public class BankStore extends AbstractStoreData {
	private static final String CLASS_NAME = BankStore.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	@Override
	public void storeData() throws IOException {
		LOGGER.entering(CLASS_NAME, "storeData");
		try (OutputStream archive = new BufferedOutputStream(new FileOutputStream(fileName()))) {
			writeDataTo(archive);
		} catch (Exception e) {
			IOException exc = new IOException("PropertyStore: Exception occurred - " + e.getMessage(), e);
			LOGGER.throwing(CLASS_NAME, "storeData", exc);
			throw exc;
		} finally {
			LOGGER.exiting(CLASS_NAME, "storeData");
		}
	}

	private void writeDataTo(OutputStream archive) throws IOException {
		LOGGER.entering(CLASS_NAME, "writeDataTo");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			writeDataTo(document);
			writeXML(document, archive);
		} catch (ParserConfigurationException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			IOException exc = new IOException(e.getMessage());
			LOGGER.throwing(CLASS_NAME, "readDataFrom", exc);
			throw exc;
		} finally {
			LOGGER.exiting(CLASS_NAME, "writeDataTo");
		}
	}

	private void writeDataTo(Document document) {
		LOGGER.entering(CLASS_NAME, "writeDataTo", document);
		Element rootElement = document.createElement(XMLConstants.FINANCES);
		document.appendChild(rootElement);
		Element banksRootElement = document.createElement(XMLConstants.BANKS);
		rootElement.appendChild(banksRootElement);
		Element investmentsRootElement = document.createElement(XMLConstants.INVESTMENTS);
		rootElement.appendChild(investmentsRootElement);
		addBankElements(document, banksRootElement);
		addInvestmentElements(document, investmentsRootElement);
		LOGGER.exiting(CLASS_NAME, "writeDataTo");
	}

	private void addBankElements(Document document, Element banksRootElement) {
		LOGGER.entering(CLASS_NAME, "addBankElements");
		for (Bank bank : BankMonitor.instance().banks()) {
			Element bankElement = buildElementFor(bank, document);
			banksRootElement.appendChild(bankElement);
		}
		LOGGER.exiting(CLASS_NAME, "addBankElements");
	}

	private void addInvestmentElements(Document document, Element investmentsRootElement) {
		LOGGER.entering(CLASS_NAME, "addInvestmentElements");
		for (Investment investment : BankMonitor.instance().investments()) {
			Element investmentElement = buildElementFor(investment, document);
			investmentsRootElement.appendChild(investmentElement);
		}
		LOGGER.exiting(CLASS_NAME, "addInvestmentElements");
	}

	private Element buildElementFor(Bank bank, Document document) {
		LOGGER.entering(CLASS_NAME, "buildElementFor", new Object[] { bank, document });
		Element bankElement = bank.buildElement(document);
		bank.branches().forEach(branch -> {
			bankElement.appendChild(buildElementFor(branch, document));
		});
		LOGGER.exiting(CLASS_NAME, "buildElementFor");
		return bankElement;
	}

	private Element buildElementFor(Branch branch, Document document) {
		LOGGER.entering(CLASS_NAME, "buildElementFor", new Object[] { branch, document });
		Element branchElement = branch.buildElement(document);
		branch.accounts().forEach(account -> {
			branchElement.appendChild(buildElementFor(account, document));
		});
		LOGGER.exiting(CLASS_NAME, "buildElementFor");
		return branchElement;
	}

	private Element buildElementFor(Account account, Document document) {
		LOGGER.entering(CLASS_NAME, "buildElementFor", new Object[] { account, document });
		Element accountElement = account.buildElement(document);
		account.transactions().forEach(transaction -> {
			accountElement.appendChild(buildElementFor(transaction, document));
		});
		account.standingOrdersStream().forEach(standingOrder -> {
			accountElement.appendChild(buildElementFor(standingOrder, document));
		});
		LOGGER.exiting(CLASS_NAME, "buildElementFor");
		return accountElement;
	}

	private Element buildElementFor(Transaction transaction, Document document) {
		LOGGER.entering(CLASS_NAME, "buildElementFor", new Object[] { transaction, document });
		LOGGER.exiting(CLASS_NAME, "buildElementFor");
		return transaction.buildElement(document);
	}

	private Element buildElementFor(StandingOrder standingOrder, Document document) {
		LOGGER.entering(CLASS_NAME, "buildElementFor", new Object[] { standingOrder, document });
		LOGGER.exiting(CLASS_NAME, "buildElementFor");
		return standingOrder.buildElement(document);
	}

	private Element buildElementFor(Investment investment, Document document) {
		LOGGER.entering(CLASS_NAME, "buildElementFor", new Object[] { investment, document });
		Element investmentElement = investment.buildElement(document);
		LOGGER.exiting(CLASS_NAME, "buildElementFor");
		return investmentElement;
	}

	private void writeXML(Document doc, OutputStream output) throws IOException {
		LOGGER.entering(CLASS_NAME, "writeXML", new Object[] { doc, output });
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException(e.getMessage());
		} finally {
			LOGGER.exiting(CLASS_NAME, "writeXML", new Object[] { doc, output });
		}
	}

}
