package applications.bank.report;

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
import applications.bank.model.InvestmentHistoryHandler;
import applications.bank.model.TransactionDetailsHandler;
import applications.bank.storage.BankMonitor;

public class BankingSummaryReport extends ReportCreator {

	private Table table = null;

	public BankingSummaryReport(String reportName) {
		super(reportName);
	}

	@Override
	public void writePdfReport() {
		LOGGER.entering(CLASS_NAME, "writePdfReport");
		document.add(new Paragraph("Summary")).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER);
		Money overallTotal = new Money("0.00");
		Money banksTotal = new Money("0.00");
		Money investmentsTotal = new Money("0.00");
		table = buildBanksTable();
		for (Bank bank : BankMonitor.instance().banks()) {
			Money bankTotal = new Money("0.00");
			for (Branch branch : bank.branches()) {
				for (Account account : branch.accounts()) {
					Money accountTotal = Money.sum(TransactionDetailsHandler.balance(account));
					bankTotal = bankTotal.plus(accountTotal);
					String accountDetails = account.accountId().accountHolder() + "/"
							+ account.accountId().accountNumber();
					addToBanksTable(account.owner().owner().toString(), accountDetails, accountTotal.cost());
				}
			}
			banksTotal = banksTotal.plus(bankTotal);
			addToBanksTable(bank.toString(), "", bankTotal.cost());
		}
		overallTotal = overallTotal.plus(banksTotal);

		document.add(table);

		document.add(new Paragraph(" ")).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER);
		document.add(new Paragraph("Total Banks Value = " + banksTotal.cost())).setFont(bold)
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
		document.add(new Paragraph(" ")).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER);

		table = buildInvestmentsTable();
		for (Investment investment : BankMonitor.instance().investments()) {
			Money value = InvestmentHistoryHandler.value(investment);
			investmentsTotal = investmentsTotal.plus(value);
			addToInvestmentsTable(investment.toString(), value.cost());
		}
		addToInvestmentsTable("", investmentsTotal.cost());
		document.add(table);
		overallTotal = overallTotal.plus(investmentsTotal);
		document.add(new Paragraph(" ")).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER);

		document.add(new Paragraph("Total Investment Value = " + investmentsTotal.cost())).setFont(bold)
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
		document.add(new Paragraph(" ")).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER);

		document.add(new Paragraph("Total Value = " + overallTotal.cost())).setFont(bold)
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
		LOGGER.exiting(CLASS_NAME, "writePdfReport");
	}

	private Table buildBanksTable() {
		LOGGER.entering(CLASS_NAME, "buildBanksTable");
		Table table = new Table(new float[] { 1, 1, 1 });
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Bank").setFont(bold)));
		table.addHeaderCell(new Cell().add(new Paragraph("Account").setFont(bold)));
		table.addHeaderCell(new Cell().add(new Paragraph("Balance").setFont(bold)));
		LOGGER.exiting(CLASS_NAME, "buildBanksTable");
		return table;
	}

	private void addToBanksTable(String bank, String account, String balance) {
		LOGGER.entering(CLASS_NAME, "addToBanksTable", new Object[] { bank, account, balance });
		table.addCell(new Cell().add(new Paragraph(bank).setFont(font)));
		table.addCell(new Cell().add(new Paragraph(account).setFont(font)));
		table.addCell(new Cell().add(new Paragraph(balance).setFont(font)));
		LOGGER.exiting(CLASS_NAME, "addToBanksTable");
	}

	private Table buildInvestmentsTable() {
		LOGGER.entering(CLASS_NAME, "buildInvestmentsTable");
		Table table = new Table(new float[] { 1, 1 });
		table.setWidth(UnitValue.createPercentValue(80));
		table.addHeaderCell(new Cell().add(new Paragraph("Investment").setFont(bold)));
		table.addHeaderCell(new Cell().add(new Paragraph("Value").setFont(bold)));
		LOGGER.exiting(CLASS_NAME, "buildInvestmentsTable");
		return table;
	}

	private void addToInvestmentsTable(String investment, String value) {
		LOGGER.entering(CLASS_NAME, "addToInvestmentsTable", new Object[] { investment, value });
		table.addCell(new Cell().add(new Paragraph(investment).setFont(font)));
		table.addCell(new Cell().add(new Paragraph(value).setFont(font)));
		LOGGER.exiting(CLASS_NAME, "addToInvestmentsTable");
	}

}
