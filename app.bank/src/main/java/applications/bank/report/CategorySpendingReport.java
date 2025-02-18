package applications.bank.report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import com.itextpdf.layout.element.Paragraph;

import application.inifile.IniFile;
import application.model.Money;
import application.model.TotalMoney;
import application.report.ReportCreator;
import applications.bank.gui.BankGUIConstants;
import applications.bank.model.Transaction;
import applications.bank.model.TransactionDetailsHandler;
import applications.bank.storage.BankMonitor;

public class CategorySpendingReport extends ReportCreator {

	private final Money zero = new Money("0.00");
	private final Predicate<Money> isSpending = m -> m.compareTo(zero) <= 0;
	private final Predicate<Money> isIncome = m -> m.compareTo(zero) >= 0;
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM uuuu", Locale.ENGLISH);

	private TotalMoney totalSpend = new TotalMoney();
	private TotalMoney totalIncome = new TotalMoney();
	private LocalDate fromDate = null;
	private LocalDate toDate = null;

	public CategorySpendingReport(String reportName, LocalDate fromDate, LocalDate toDate) {
		super(reportName);
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	@Override
	public void writePdfReport() {
		LOGGER.entering(CLASS_NAME, "writePdfReport");
		document.add(new Paragraph("Report of spending and income by category " + dtf.format(fromDate) + " to " + dtf.format(toDate)));
		document.add(new Paragraph("Spending "));
		String[] descriptions = IniFile.value(BankGUIConstants.DESCRIPTION_OPTIONS).split(",");
		for (String d : descriptions) {
			Money spending = getSpendingForCategory(d, fromDate, toDate, isSpending).money().negate();
			if (!spending.equals(zero)) {
				document.add(new Paragraph("Spending on '" + d + "' = " + spending.cost()));
				totalSpend.add(spending);
			}
		}
		document.add(new Paragraph(" "));
		document.add(new Paragraph("Income "));
		for (String d : descriptions) {
				Money income = getSpendingForCategory(d, fromDate, toDate, isIncome).money().negate();
				if (!income.equals(zero)) {
					document.add(new Paragraph("Income from '" + d + "' = " + income.cost()));
					totalIncome.add(income);
				}
		}
		document.add(new Paragraph(" "));
		document.add(new Paragraph("Total spending = " + totalSpend.cost()));
		document.add(new Paragraph("Total Income = " + totalIncome.cost()));
		LOGGER.exiting(CLASS_NAME, "writePdfReport");
	}

	private TotalMoney getSpendingForCategory(String description, LocalDate fromDate, LocalDate toDate, Predicate<? super Money> pred) {
		return spendingByCategory(getTransactionsForPurpose(description, fromDate, toDate), pred);
	}

	private List<Transaction> getTransactionsForPurpose(String purpose, LocalDate fromDate, LocalDate toDate) {
		return TransactionDetailsHandler
				.transactions(BankMonitor.instance().banks(), purpose, fromDate, toDate);
	}

	private TotalMoney spendingByCategory(List<Transaction> transactions, Predicate<? super Money> pred) {
		return transactions
				.stream()
				.map(t -> t.amount())
				.filter(pred)
				.collect(TotalMoney::new, TotalMoney::add, TotalMoney::add);
	}
}
