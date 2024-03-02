package applications.bank.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TransactionDetailsHandler {

	public static Set<LocalDate> transactionDates(Account account) {
		Set<LocalDate> dates = account.transactions().stream().map(Transaction::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates;
	}

	public static Set<LocalDate> transactionDates(Branch branch) {
		Set<LocalDate> dates = branch.accounts().stream().map(Account::transactions).flatMap(List::stream)
				.map(Transaction::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates;
	}

	public static Set<LocalDate> transactionDates(Bank bank) {
		Set<LocalDate> dates = bank.branches().stream().map(Branch::accounts).flatMap(List::stream)
				.map(Account::transactions).flatMap(List::stream).map(Transaction::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates;
	}

	public static Set<LocalDate> transactionDates(List<Bank> banks) {
		Set<LocalDate> dates = banks.stream().map(Bank::branches).flatMap(List::stream).map(Branch::accounts)
				.flatMap(List::stream).map(Account::transactions).flatMap(List::stream).map(Transaction::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates;
	}

	public static List<Transaction> transactions(Account account) {
		List<Transaction> transactions = account.transactions();
		return transactions;
	}

	public static List<Transaction> transactions(Branch branch) {
		List<Transaction> transactions = branch.accounts().stream().map(Account::transactions).flatMap(List::stream)
				.collect(Collectors.toList());
		return transactions;
	}

	public static List<Transaction> transactions(Bank bank) {
		List<Transaction> transactions = bank.branches().stream().map(Branch::accounts).flatMap(List::stream)
				.map(Account::transactions).flatMap(List::stream).collect(Collectors.toList());
		return transactions;
	}

	public static List<Transaction> transactions(List<Bank> banks) {
		List<Transaction> transactions = banks.stream().map(Bank::branches).flatMap(List::stream).map(Branch::accounts)
				.flatMap(List::stream).map(Account::transactions).flatMap(List::stream).collect(Collectors.toList());
		return transactions;
	}
}
