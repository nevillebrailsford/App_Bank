package applications.bank.gui.charts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.model.Money;
import applications.bank.gui.models.TotalHistoryTableModel;
import applications.bank.model.Investment;

class TotalHistoryTableModelTest {

	List<Investment> emptyInvestments = new ArrayList<>();
	List<Investment> investments;
	Investment investment1;
	Investment investment2;

	@TempDir
	File rootDirectory;

	@BeforeEach
	void setUp() throws Exception {
		investments = new ArrayList<>();
		ApplicationDefinition app = new ApplicationDefinition("test") {

			@Override
			public Level level() {
				return Level.OFF;
			}

		};
		ApplicationConfiguration.registerApplication(app, rootDirectory.getAbsolutePath());
		LogConfigurer.setUp();

		investment1 = new Investment.Builder().date(LocalDate.now()).name("investment1").value(new Money("100.00"))
				.build();
		investment2 = new Investment.Builder().date(LocalDate.now().plusDays(1)).name("investment2")
				.value(new Money("200.00")).build();
	}

	@AfterEach
	void tearDown() throws Exception {
		ApplicationConfiguration.clear();
		LogConfigurer.shutdown();
	}

	@Test
	void testEmptyList() {
		TotalHistoryTableModel tm = new TotalHistoryTableModel(emptyInvestments);
		assertEquals(0, tm.getRowCount());
	}

	@Test
	void testEmptyListGetValueAt() {
		TotalHistoryTableModel tm = new TotalHistoryTableModel(emptyInvestments);
		assertEquals(0, tm.getRowCount());
		assertEquals("Unknown", tm.getValueAt(0, 0));
	}

	@Test
	void testOneInList() {
		investments.add(investment1);
		TotalHistoryTableModel tm = new TotalHistoryTableModel(investments);
		assertEquals(1, tm.getRowCount());
	}

	@Test
	void testOneInListGetValueAt() {
		investments.add(investment1);
		TotalHistoryTableModel tm = new TotalHistoryTableModel(investments);
		assertEquals(1, tm.getRowCount());
		assertEquals(LocalDate.now().toString(), tm.getValueAt(0, 0));
		assertEquals("£100.00", tm.getValueAt(0, 1));
	}

	@Test
	void testMoreThanOneInList() {
		investments.add(investment1);
		investments.add(investment2);
		TotalHistoryTableModel tm = new TotalHistoryTableModel(investments);
		assertEquals(2, tm.getRowCount());
	}

	@Test
	void testMoreThanOneInListGetValueAt() {
		investments.add(investment1);
		investments.add(investment2);
		TotalHistoryTableModel tm = new TotalHistoryTableModel(investments);
		assertEquals(2, tm.getRowCount());
		assertEquals(LocalDate.now().toString(), tm.getValueAt(0, 0));
		assertEquals("£100.00", tm.getValueAt(0, 1));
		assertEquals(LocalDate.now().plusDays(1).toString(), tm.getValueAt(1, 0));
		assertEquals("£300.00", tm.getValueAt(1, 1));
	}

}
