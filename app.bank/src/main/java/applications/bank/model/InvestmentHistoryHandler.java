package applications.bank.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import application.model.Money;
import applications.bank.model.Investment.ValueOn;

public class InvestmentHistoryHandler {

	public static LocalDate[] valuedDates(Investment investment) {
		Set<LocalDate> dates = investment.history().stream().map(ValueOn::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates.toArray(new LocalDate[] {});
	}

	public static LocalDate[] valuedDates(List<Investment> investments) {
		Set<LocalDate> dates = investments.stream().map(Investment::history).flatMap(List::stream).map(ValueOn::date)
				.collect(Collectors.toCollection(() -> new TreeSet<>((t1, t2) -> t1.compareTo(t2))));
		return dates.toArray(new LocalDate[] {});
	}

	public static Money value(Investment investment) {
		return value(investment, LocalDate.now());
	}

	public static Money value(Investment investment, LocalDate onDate) {
		List<Money> monies = investment.history().stream().filter(v -> !v.date().isAfter(onDate)).map(ValueOn::value)
				.collect(Collectors.toList());
		Money[] allMonies = monies.toArray(new Money[] {});
		return allMonies[allMonies.length - 1];
	}

	public static Money value(List<Investment> investments) {
		return value(investments, LocalDate.now());
	}

	public static Money value(List<Investment> investments, LocalDate onDate) {
		List<Money> monies = investments.stream().map((i) -> InvestmentHistoryHandler.value(i, onDate))
				.collect(Collectors.toList());
		return Money.sum(monies);
	}
}
