package applications.bank.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import application.model.Money;

public class TransactionDetailsHandler {

	public static LocalDate[] transactionDates(Account account) {
		Set<LocalDate> dates = account.transactions().stream().map(Transaction::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates.toArray(new LocalDate[] {});
	}

	public static LocalDate[] transactionDates(Branch branch) {
		Set<LocalDate> dates = branch.accounts().stream().map(Account::transactions).flatMap(List::stream)
				.map(Transaction::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates.toArray(new LocalDate[] {});
	}

	public static LocalDate[] transactionDates(Bank bank) {
		Set<LocalDate> dates = bank.branches().stream().map(Branch::accounts).flatMap(List::stream)
				.map(Account::transactions).flatMap(List::stream).map(Transaction::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates.toArray(new LocalDate[] {});
	}

	public static LocalDate[] transactionDates(List<Bank> banks) {
		Set<LocalDate> dates = banks.stream().map(Bank::branches).flatMap(List::stream).map(Branch::accounts)
				.flatMap(List::stream).map(Account::transactions).flatMap(List::stream).map(Transaction::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates.toArray(new LocalDate[] {});
	}

	public static Transaction[] transactions(Account account) {
		List<Transaction> transactions = account.transactions();
		return transactions.toArray(new Transaction[] {});
	}

	public static Transaction[] transactions(Branch branch) {
		List<Transaction> transactions = branch.accounts().stream().map(Account::transactions).flatMap(List::stream)
				.sorted().collect(Collectors.toList());
		return transactions.toArray(new Transaction[] {});
	}

	public static Transaction[] transactions(Bank bank) {
		List<Transaction> transactions = bank.branches().stream().map(Branch::accounts).flatMap(List::stream)
				.map(Account::transactions).flatMap(List::stream).sorted().collect(Collectors.toList());
		return transactions.toArray(new Transaction[] {});
	}

	public static Transaction[] transactions(List<Bank> banks) {
		List<Transaction> transactions = banks.stream().map(Bank::branches).flatMap(List::stream).map(Branch::accounts)
				.flatMap(List::stream).map(Account::transactions).flatMap(List::stream).sorted()
				.collect(Collectors.toList());
		return transactions.toArray(new Transaction[] {});
	}

	public static List<Transaction> transactions(List<Bank> banks, String search, LocalDate fromDate,
			LocalDate toDate) {
		List<Transaction> transactions = banks.stream().map(Bank::branches).flatMap(List::stream).map(Branch::accounts)
				.flatMap(List::stream).map(Account::transactions).flatMap(List::stream)
				.filter(t -> t.description().toLowerCase().contains(search.toLowerCase()))
				.filter(t -> t.date().isAfter(fromDate)).filter(t -> t.date().isBefore(toDate)).sorted()
				.collect(Collectors.toList());
		return transactions;
	}

	public static Money[] balance(Account account) {
		return balance(account, LocalDate.now());
	}

	public static Money[] balance(Account account, LocalDate onDate) {
		List<Money> monies = account.transactions().stream().filter(t -> !t.date().isAfter(onDate))
				.map(Transaction::amount).collect(Collectors.toList());
		return monies.toArray(new Money[] {});
	}

	public static Money[] balance(Branch branch) {
		return balance(branch, LocalDate.now());
	}

	public static Money[] balance(Branch branch, LocalDate onDate) {
		List<Money> monies = branch.accounts().stream().map(Account::transactions).flatMap(List::stream)
				.filter(t -> !t.date().isAfter(onDate)).map(Transaction::amount).collect(Collectors.toList());
		return monies.toArray(new Money[] {});
	}

	public static Money[] balance(Bank bank) {
		return balance(bank, LocalDate.now());
	}

	public static Money[] balance(Bank bank, LocalDate onDate) {
		List<Money> monies = bank.branches().stream().map(Branch::accounts).flatMap(List::stream)
				.map(Account::transactions).flatMap(List::stream).filter(t -> !t.date().isAfter(onDate))
				.map(Transaction::amount).collect(Collectors.toList());
		return monies.toArray(new Money[] {});
	}

	public static Money[] balance(List<Bank> banks) {
		return balance(banks, LocalDate.now());
	}

	public static Money[] balance(List<Bank> banks, LocalDate onDate) {
		List<Money> monies = banks.stream().map(Bank::branches).flatMap(List::stream).map(Branch::accounts)
				.flatMap(List::stream).map(Account::transactions).flatMap(List::stream)
				.filter(t -> !t.date().isAfter(onDate)).map(Transaction::amount).collect(Collectors.toList());
		return monies.toArray(new Money[] {});
	}
}
