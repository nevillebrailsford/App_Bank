package applications.bank.report;

import java.time.LocalDate;
import java.util.List;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;

import application.inifile.IniFile;
import application.model.Money;
import application.model.TotalMoney;
import application.report.ReportCreator;
import applications.bank.gui.BankGUIConstants;
import applications.bank.model.Transaction;
import applications.bank.model.TransactionDetailsHandler;
import applications.bank.storage.BankMonitor;

public class CategorySpendingReport extends ReportCreator {

	public CategorySpendingReport(String reportName) {
		super(reportName);
	}

	@Override
	public void writePdfReport() {
		LOGGER.entering(CLASS_NAME, "writePdfReport");
		document.add(new Paragraph("Category Spending")).setFont(bold)
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
		LocalDate fromDate = LocalDate.of(2000, 1, 1);
		LocalDate toDate = LocalDate.now();
		String[] descriptions = IniFile.value(BankGUIConstants.DESCRIPTION_OPTIONS).split(",");
		for (String d : descriptions) {
			Money spending = getSpendingForCategory(d, fromDate, toDate).money().negate();
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Spending on " + d + " =   \t" + spending.cost()));
		}

		LOGGER.exiting(CLASS_NAME, "writePdfReport");
	}

	private TotalMoney getSpendingForCategory(String description, LocalDate fromDate, LocalDate toDate) {
		return totalListOfTransactions(getTransactionsForPurpose(description, fromDate, toDate));
	}

	private List<Transaction> getTransactionsForPurpose(String purpose, LocalDate fromDate, LocalDate toDate) {
		return TransactionDetailsHandler.transactions(BankMonitor.instance().banks(), purpose, fromDate, toDate);
	}

	private TotalMoney totalListOfTransactions(List<Transaction> transactions) {
		return transactions.stream().map(t -> t.amount()).collect(TotalMoney::new, TotalMoney::add, TotalMoney::add);
	}

}
