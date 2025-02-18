package applications.bank.report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;

import application.model.Money;
import application.model.TotalMoney;
import application.report.ReportCreator;
import applications.bank.model.Transaction;
import applications.bank.model.TransactionDetailsHandler;
import applications.bank.storage.BankMonitor;

public class TaxReturnReport extends ReportCreator {

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM uuuu", Locale.ENGLISH);
	
	private LocalDate fromDate;
	private LocalDate toDate;

	public TaxReturnReport(String reportName, LocalDate fromDate, LocalDate toDate) {
		super(reportName);
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	@Override
	public void writePdfReport() {
		LOGGER.entering(CLASS_NAME, "writePdfReport");
		document.add(new Paragraph("Tax Report for " + dtf.format(fromDate) + " to " + dtf.format(toDate)))
			.setFont(bold).setHorizontalAlignment(HorizontalAlignment.CENTER);
		Money taxableInterest = gatherTaxableInterest(fromDate, toDate).money();
		Money income = gatherIncome(fromDate, toDate).money();
		Money charity = gatherCharityContributions(fromDate, toDate).money().negate();

		document.add(new Paragraph(" "));
		document.add(new Paragraph("Total taxable interest =   \t" + taxableInterest.cost()));
		document.add(new Paragraph("Total income =             \t" + income.cost()));
		document.add(new Paragraph("Charitable contributions = \t" + charity.cost()));
		LOGGER.exiting(CLASS_NAME, "writePdfReport");
	}

	private TotalMoney gatherTaxableInterest(LocalDate fromDate, LocalDate toDate) {
		return totalListOfTransactions(getTransactionsForPurpose("taxable interest", fromDate, toDate));
	}

	private TotalMoney gatherIncome(LocalDate fromDate, LocalDate toDate) {
		return totalListOfTransactions(getTransactionsForPurpose("pension", fromDate, toDate));
	}

	private TotalMoney gatherCharityContributions(LocalDate fromDate, LocalDate toDate) {
		return totalListOfTransactions(getTransactionsForPurpose("charity", fromDate, toDate));
	}

	private List<Transaction> getTransactionsForPurpose(String purpose, LocalDate fromDate, LocalDate toDate) {
		return TransactionDetailsHandler
				.transactions(BankMonitor.instance().banks(), purpose, fromDate, toDate);
	}

	private TotalMoney totalListOfTransactions(List<Transaction> transactions) {
		return transactions
				.stream()
				.map(t -> t.amount())
				.collect(TotalMoney::new, TotalMoney::add, TotalMoney::add);
	}

}
