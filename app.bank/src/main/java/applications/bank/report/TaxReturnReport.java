package applications.bank.report;

import java.time.LocalDate;
import java.util.List;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;

import application.model.Money;
import application.report.ReportCreator;
import applications.bank.model.Transaction;
import applications.bank.model.TransactionDetailsHandler;
import applications.bank.storage.BankMonitor;

public class TaxReturnReport extends ReportCreator {

	public TaxReturnReport(String reportName) {
		super(reportName);
	}

	@Override
	public void writePdfReport() {
		LOGGER.entering(CLASS_NAME, "writePdfReport");
		document.add(new Paragraph("Tax Report")).setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER);
		LocalDate fromDate = LocalDate.of(2024, 4, 6);
		LocalDate toDate = LocalDate.of(2025, 4, 5);
		Money taxableInterest = gatherTaxableInterest(fromDate, toDate);
		Money income = gatherIncome(fromDate, toDate);
		Money charity = gatherCharityContributions(fromDate, toDate).negate();

		document.add(new Paragraph(" "));
		document.add(new Paragraph("Total taxable interest =   \t" + taxableInterest.cost()));
		document.add(new Paragraph("Total income =             \t" + income.cost()));
		document.add(new Paragraph("Charitable contributions = \t" + charity.cost()));
		LOGGER.exiting(CLASS_NAME, "writePdfReport");
	}

	private Money gatherTaxableInterest(LocalDate fromDate, LocalDate toDate) {
		List<Transaction> taxableInterestTransactions = getTransactionsForPurpose("taxable interest", fromDate, toDate);
		Money taxableInterest = totalListOfTransactions(taxableInterestTransactions);
		return taxableInterest;
	}

	private Money gatherIncome(LocalDate fromDate, LocalDate toDate) {
		List<Transaction> incomeTransactions = getTransactionsForPurpose("pension", fromDate, toDate);
		Money income = totalListOfTransactions(incomeTransactions);
		return income;
	}

	private Money gatherCharityContributions(LocalDate fromDate, LocalDate toDate) {
		List<Transaction> charityTransactions = getTransactionsForPurpose("charity", fromDate, toDate);
		Money charity = totalListOfTransactions(charityTransactions);
		return charity;
	}

	private List<Transaction> getTransactionsForPurpose(String purpose, LocalDate fromDate, LocalDate toDate) {
		return TransactionDetailsHandler.transactions(BankMonitor.instance().banks(), purpose, fromDate, toDate);
	}

	private Money totalListOfTransactions(List<Transaction> transactions) {
		Money total = new Money("0.00");
		for (Transaction transaction : transactions) {
			total = total.plus(transaction.amount());
		}
		return total;

	}

}
