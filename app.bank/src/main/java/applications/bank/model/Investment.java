package applications.bank.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import application.model.AppXMLConstants;
import application.model.ElementBuilder;
import application.model.ElementChecker;
import application.model.Money;

public class Investment implements Comparable<Investment> {

	private String name = "";
	private List<ValueOn> history = new ArrayList<>();
	UUID investmentId = null;

	public Investment(String name, Money value) {
		this(name, value, LocalDate.now());
	}

	public Investment(String name, Money value, LocalDate date) {
		if (name == null) {
			throw new IllegalArgumentException("Investment: name is null");
		}
		if (name.isBlank()) {
			throw new IllegalArgumentException("Investment: name is missing");
		}
		if (value == null) {
			throw new IllegalArgumentException("Investment: value is null");
		}
		if (date == null) {
			throw new IllegalArgumentException("Investment: date is null");
		}
		this.name = name;
		ValueOn valueOn = new ValueOn(date, value);
		history.add(valueOn);
		this.investmentId = UUID.randomUUID();
	}

	public Investment(Investment that) {
		if (that == null) {
			throw new IllegalArgumentException("Investment: investment is null");
		}
		this.name = that.name;
		history = new ArrayList<>();
		for (ValueOn valueOn : that.history) {
			ValueOn val = new ValueOn(valueOn.date, valueOn.value);
			history.add(val);
		}
		Collections.sort(this.history);
		this.investmentId = that.investmentId;
	}

	public Investment(Element investmentElement) {
		this(investmentElement, XMLConstants.INVESTMENT);
	}

	public Investment(Element investmentElement, String elementName) {
		if (investmentElement == null) {
			throw new IllegalArgumentException("Investment: investmentElement is null");
		}
		if (!ElementChecker.verifyTag(investmentElement, elementName)) {
			throw new IllegalArgumentException("Investment: investmentElement is not for investment");
		}
		String name = investmentElement.getElementsByTagName(XMLConstants.INVESTMENTNAME).item(0).getTextContent();
		this.name = name;
		this.investmentId = UUID.fromString(
				investmentElement.getElementsByTagName(XMLConstants.INVESTMENT_ID).item(0).getTextContent());
		NodeList historyNodes = investmentElement.getElementsByTagName(XMLConstants.HISTORY);
		if (historyNodes.getLength() > 1) {
			throw new IllegalArgumentException("Investment: investmentElement has too many children");
		}
		if (historyNodes.getLength() == 1) {
			Element historyElement = (Element) historyNodes.item(0);
			NodeList valueOnNodes = historyElement.getElementsByTagName(XMLConstants.VALUE_ON);
			for (int i = 0; i < valueOnNodes.getLength(); i++) {
				Element valueOnElement = (Element) valueOnNodes.item(i);
				String date = valueOnElement.getElementsByTagName(XMLConstants.DATE).item(0).getTextContent();
				String value = valueOnElement.getElementsByTagName(AppXMLConstants.MONEY).item(0).getTextContent();
				ValueOn valueOn = new ValueOn(LocalDate.parse(date), new Money(value));
				history.add(valueOn);
			}
		}
		Collections.sort(history);
	}

	public Element buildElement(Document document) {
		return buildElement(document, XMLConstants.INVESTMENT);
	}

	public Element buildElement(Document document, String elementName) {
		if (document == null) {
			throw new IllegalArgumentException("Investment: document is null");
		}
		Element result = document.createElement(elementName);
		result.appendChild(ElementBuilder.build(XMLConstants.INVESTMENTNAME, name, document));
		result.appendChild(ElementBuilder.build(XMLConstants.INVESTMENT_ID, investmentId.toString(), document));
		Element historyElement = document.createElement(XMLConstants.HISTORY);
		for (ValueOn valueOn : history) {
			Element valueOnElement = document.createElement(XMLConstants.VALUE_ON);
			valueOnElement.appendChild(valueOn.value.buildElement(document));
			valueOnElement.appendChild(ElementBuilder.build(XMLConstants.DATE, valueOn.date.toString(), document));
			historyElement.appendChild(valueOnElement);
		}
		result.appendChild(historyElement);
		return result;
	}

	public String name() {
		return name;
	}

	public int historyEntries() {
		return history.size();
	}

	public List<ValueOn> history() {
		List<ValueOn> copyList = new ArrayList<>();
		for (ValueOn value : history) {
			copyList.add(value);
		}
		Collections.sort(copyList);
		return copyList;
	}

	public void update(Money value, LocalDate date) {
		ValueOn valueOn = new ValueOn(date, new Money(value));
		history.add(valueOn);
	}

	public Money value() {
		return value(LocalDate.now());
	}

	public Money value(LocalDate date) {
		Money result = new Money("0.00");
		List<ValueOn> values = history();
		for (int i = values.size() - 1; i >= 0; i--) {
			LocalDate historicalDate = values.get(i).date();
			if (historicalDate.equals(date) || historicalDate.isBefore(date)) {
				result = values.get(i).value();
				break;
			}
		}
		return result;
	}

	@Override
	public int compareTo(Investment other) {
		return name.compareTo(other.name);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(investmentId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Investment other = (Investment) obj;
		return Objects.equals(investmentId, other.investmentId);
	}

	public static class Builder {
		private String name;
		private Money value;
		private LocalDate date;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder value(Money value) {
			this.value = value;
			return this;
		}

		public Builder date(LocalDate date) {
			this.date = date;
			return this;
		}

		public Investment build() {
			if (name == null) {
				throw new IllegalArgumentException("Investment.Builder: name is null");
			}
			if (name.isBlank()) {
				throw new IllegalArgumentException("Investment.Builder: name is missing");
			}
			if (value == null) {
				throw new IllegalArgumentException("Investment.Builder: value is null");
			}
			if (date == null) {
				return new Investment(this.name, this.value);
			} else {
				return new Investment(this.name, this.value, this.date);
			}
		}
	}

	public static class ValueOn implements Comparable<ValueOn> {
		private LocalDate date;
		private Money value;

		public ValueOn(LocalDate date, Money value) {
			this.date = date;
			this.value = new Money(value);
		}

		public LocalDate date() {
			return this.date;
		}

		public Money value() {
			return this.value;
		}

		@Override
		public int compareTo(ValueOn other) {
			return this.date.compareTo(other.date);
		}
	}
}
