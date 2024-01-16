package applications.bank.report;

import java.time.format.DateTimeFormatter;

import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;

import application.model.Money;
import application.report.ReportCreator;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Branch;
import applications.bank.model.Investment;
import applications.bank.model.Investment.ValueOn;
import applications.bank.model.Transaction;
import applications.bank.storage.BankMonitor;

public class BankingReport extends ReportCreator {

	private Table table = null;
	private boolean notFirst = false;
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");

	public BankingReport(String reportName) {
		super(reportName);
	}

	@Override
	public void writePdfReport() {
		LOGGER.entering(CLASS_NAME, "writePdfReport");
		notFirst = false;
		document.add(new Paragraph("Banks")).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER);
		document.add(new AreaBreak());
		for (Bank bank : BankMonitor.instance().banks()) {
			if (notFirst) {
				document.add(new AreaBreak());
			}
			document.add(
					new Paragraph(bank.toString()).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER));
			for (Branch branch : bank.branches()) {
				document.add(new Paragraph(branch.toFullString()).setFont(bold)
						.setHorizontalAlignment(HorizontalAlignment.CENTER));
				for (Account account : branch.accounts()) {
					document.add(new Paragraph(account.toString()).setFont(bold));
					table = buildBanksTable();
					Money startingBalance = account.balance();
					for (Transaction t : account.transactions()) {
						startingBalance = startingBalance.minus(t.amount());
					}
					addToBanksTable("", "Opening balance", "", startingBalance.cost());
					for (Transaction transaction : account.transactions()) {
						startingBalance = startingBalance.plus(transaction.amount());
						addToBanksTable(transaction.date().format(dateFormatter), transaction.description(),
								transaction.amount().cost(), startingBalance.cost());
					}
					addToBanksTable("", "Closing balance", "", account.balance().cost());
					document.add(table);
				}
			}
			notFirst = true;
		}
		notFirst = false;
		document.add(new AreaBreak());
		document.add(new Paragraph("Investments")).setFont(font).setHorizontalAlignment(HorizontalAlignment.CENTER);
		for (Investment investment : BankMonitor.instance().investments()) {
			if (notFirst) {
				document.add(new AreaBreak());
			}
			document.add(
					new Paragraph(investment.name()).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER));
			table = buildInvestmentsTable();
			for (ValueOn valueOn : investment.history()) {
				addToInvestmentsTable(valueOn.date().format(dateFormatter), valueOn.value().cost());
			}
			document.add(table);
			notFirst = true;
		}
		LOGGER.exiting(CLASS_NAME, "writePdfReport");
	}

	private Table buildBanksTable() {
		LOGGER.entering(CLASS_NAME, "buildBanksTable");
		Table table = new Table(new float[] { 1, 4, 1, 1 });
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Date").setFont(bold)));
		table.addHeaderCell(new Cell().add(new Paragraph("Description                   ").setFont(bold)));
		table.addHeaderCell(new Cell().add(new Paragraph("Amount").setFont(bold)));
		table.addHeaderCell(new Cell().add(new Paragraph("Balance").setFont(bold)));
		LOGGER.exiting(CLASS_NAME, "buildBanksTable");
		return table;
	}

	private void addToBanksTable(String date, String description, String cost, String balance) {
		LOGGER.entering(CLASS_NAME, "addToBanksTable", new Object[] { date, description, cost });
		table.addCell(new Cell().add(new Paragraph(date).setFont(font)));
		table.addCell(new Cell().add(new Paragraph(description).setFont(font)));
		table.addCell(new Cell().add(new Paragraph(cost).setFont(font)));
		table.addCell(new Cell().add(new Paragraph(balance).setFont(font)));
		LOGGER.exiting(CLASS_NAME, "addToBanksTable");
	}

	private Table buildInvestmentsTable() {
		LOGGER.entering(CLASS_NAME, "buildInvestmentsTable");
		Table table = new Table(new float[] { 1, 1 });
		table.setWidth(UnitValue.createPercentValue(80));
		table.addHeaderCell(new Cell().add(new Paragraph("Date").setFont(bold)));
		table.addHeaderCell(new Cell().add(new Paragraph("Amount").setFont(bold)));
		LOGGER.exiting(CLASS_NAME, "buildInvestmentsTable");
		return table;
	}

	private void addToInvestmentsTable(String date, String value) {
		LOGGER.entering(CLASS_NAME, "addToInvestmentsTable", new Object[] { date, value });
		table.addCell(new Cell().add(new Paragraph(date).setFont(font)));
		table.addCell(new Cell().add(new Paragraph(value).setFont(font)));
		LOGGER.exiting(CLASS_NAME, "addToInvestmentsTable");
	}

}
