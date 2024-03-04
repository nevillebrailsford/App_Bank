package applications.bank.gui.charts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import application.model.Money;
import applications.bank.gui.models.InvestmentPercentagesTableModel;
import applications.bank.model.Investment;

class InvestmentPercentagesTableModelTest {

	InvestmentPercentagesTableModel model;
	List<Investment> investments;
	Investment invest1 = new Investment.Builder().date(LocalDate.now()).name("invest1").value(new Money("20.00"))
			.build();
	Investment invest2 = new Investment.Builder().date(LocalDate.now()).name("invest2").value(new Money("20.00"))
			.build();

	@BeforeEach
	void setUp() throws Exception {
		investments = new ArrayList<>();
		investments.add(invest1);
		investments.add(invest2);
		model = new InvestmentPercentagesTableModel(investments);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

}
