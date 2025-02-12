package applications.bank.storage;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import application.audit.AuditService;
import application.definition.ApplicationConfiguration;
import application.model.Money;
import application.notification.Notification;
import application.notification.NotificationCentre;
import application.storage.Storage;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Branch;
import applications.bank.model.Investment;
import applications.bank.model.InvestmentHistoryHandler;
import applications.bank.model.StandingOrder;
import applications.bank.model.Transaction;
import applications.bank.model.TransactionDetailsHandler;
import applications.bank.model.Transfer;

public class BankMonitor {
	private static final String CLASS_NAME = BankMonitor.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private static BankMonitor instance = null;

	private final ArrayList<Bank> banks;
	private List<Bank> bankList = null;

	private final ArrayList<Investment> investments;
	private List<Investment> investmentList = null;

	public synchronized static BankMonitor instance() {
		LOGGER.entering(CLASS_NAME, "instance");
		if (instance == null) {
			instance = new BankMonitor();
		}
		LOGGER.exiting(CLASS_NAME, "instance", instance);
		return instance;
	}

	private BankMonitor() {
		banks = new ArrayList<>();
		investments = new ArrayList<>();
	}

	public void clear() {
		LOGGER.entering(CLASS_NAME, "clear");
		synchronized (banks) {
			banks.clear();
			investments.clear();
			clearCopyLists();
		}
		updateStorage();
		LOGGER.exiting(CLASS_NAME, "clear");
	}

	public List<Bank> banks() {
		LOGGER.entering(CLASS_NAME, "banks");
		if (bankList == null) {
			synchronized (banks) {
				bankList = banks.stream().collect(Collectors.toList());
			}
			Collections.sort(bankList);
		}
		LOGGER.exiting(CLASS_NAME, "banks", bankList);
		return bankList;
	}

	public List<Branch> branches() {
		LOGGER.entering(CLASS_NAME, "branches");
		List<Branch> copyList = new ArrayList<>();
		synchronized (banks) {
			for (Bank bank : banks) {
				for (Branch branch : bank.branches()) {
					copyList.add(branch);
				}
			}
		}
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "branches", copyList);
		return copyList;
	}

	public List<Account> accounts() {
		LOGGER.entering(CLASS_NAME, "accounts");
		List<Account> copyList = new ArrayList<>();
		synchronized (banks) {
			for (Bank bank : banks) {
				for (Branch branch : bank.branches()) {
					for (Account account : branch.accounts()) {
						copyList.add(account);
					}
				}
			}
		}
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "accounts", copyList);
		return copyList;
	}

	public List<StandingOrder> standingOrders() {
		LOGGER.entering(CLASS_NAME, "standingOrders");
		List<StandingOrder> copyList = new ArrayList<>();
		synchronized (banks) {
			for (Bank bank : banks) {
				for (Branch branch : bank.branches()) {
					for (Account account : branch.accounts()) {
						for (StandingOrder standingOrder : account.standingOrders()) {
							copyList.add(standingOrder);
						}
					}
				}
			}
		}
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "standingOrders", copyList);
		return copyList;
	}

	public List<Investment> investments() {
		LOGGER.entering(CLASS_NAME, "investments");
		if (investmentList == null) {
			synchronized (banks) {
				investmentList = investments.stream().collect(Collectors.toList());
			}
			Collections.sort(investmentList);
		}
		LOGGER.exiting(CLASS_NAME, "investments", investmentList);
		return investmentList;
	}

	public Money balance() {
		LOGGER.entering(CLASS_NAME, "balance");
		Money balance = new Money("0.00");
		balance = balance.plus(balanceBanks());
		balance = balance.plus(balanceInvestments());
		LOGGER.exiting(CLASS_NAME, "balance", balance);
		return balance;
	}

	public Money balanceBanks() {
		LOGGER.entering(CLASS_NAME, "balanceBanks");
		Money balance = balanceBanks(LocalDate.now());
		LOGGER.exiting(CLASS_NAME, "balanceBanks", balance);
		return balance;
	}

	public Money balanceBanks(LocalDate onDate) {
		LOGGER.entering(CLASS_NAME, "balanceBanks", onDate);
		Money balance = Money.sum(TransactionDetailsHandler.balance(banks, onDate));
		LOGGER.exiting(CLASS_NAME, "balanceBanks", balance);
		return balance;
	}

	public Money balanceBank(Bank bank) {
		LOGGER.entering(CLASS_NAME, "balanceBank");
		Bank theBank = findBank(bank);
		Money balance = Money.sum(TransactionDetailsHandler.balance(theBank));
		LOGGER.exiting(CLASS_NAME, "balanceBank", balance);
		return balance;
	}

	public Money balanceAccount(Account account) {
		LOGGER.entering(CLASS_NAME, "balanceAccount", account);
		Account theAccount = findAccount(account);
		Money balance = Money.sum(TransactionDetailsHandler.balance(theAccount));
		LOGGER.exiting(CLASS_NAME, "balanceAccount", balance);
		return balance;
	}

	public Money balanceInvestments() {
		LOGGER.entering(CLASS_NAME, "balanceInvestments");
		Money balance = InvestmentHistoryHandler.value(investments);
		LOGGER.exiting(CLASS_NAME, "balanceInvestments", balance);
		return balance;
	}

	public boolean banksExist() {
		LOGGER.entering(CLASS_NAME, "banksExist");
		boolean result = banks().size() > 0;
		LOGGER.exiting(CLASS_NAME, "banksExist", result);
		return result;
	}

	public boolean branchesExist() {
		LOGGER.entering(CLASS_NAME, "branchesExist");
		boolean result = branches().size() > 0;
		LOGGER.exiting(CLASS_NAME, "branchesExist", result);
		return result;
	}

	public boolean accountsExist() {
		LOGGER.entering(CLASS_NAME, "accountsExist");
		boolean result = accounts().size() > 0;
		LOGGER.exiting(CLASS_NAME, "accountsExist", result);
		return result;
	}

	public boolean investmentsExist() {
		LOGGER.entering(CLASS_NAME, "investmentsExist");
		boolean result = investments().size() > 0;
		LOGGER.exiting(CLASS_NAME, "investmentsExist", result);
		return result;
	}

	public void addBank(Bank newBank) {
		LOGGER.entering(CLASS_NAME, "addBank", newBank);
		if (newBank == null) {
			Notification notification = new Notification(BankNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: newBank is null");
			LOGGER.throwing(CLASS_NAME, "addBank", exc);
			LOGGER.exiting(CLASS_NAME, "addBank");
			throw exc;
		}
		if (banks.contains(newBank)) {
			Notification notification = new Notification(BankNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException(
					"BankMonitor: bank " + newBank + " already exists");
			LOGGER.throwing(CLASS_NAME, "addBank", exc);
			LOGGER.exiting(CLASS_NAME, "addBank");
			throw exc;
		}
		try {
			synchronized (banks) {
				banks.add(newBank);
			}
			AuditService.writeAuditInformation(BankAuditType.Added, BankAuditObject.Bank, newBank.toString());
			updateStorage();
			Notification notification = new Notification(BankNotificationType.Add, this, newBank);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(BankNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addBank", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addBank");
		}
	}

	public void removeBank(Bank oldBank) {
		LOGGER.entering(CLASS_NAME, "removeBank", oldBank);
		if (oldBank == null) {
			Notification notification = new Notification(BankNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: oldBank is null");
			LOGGER.throwing(CLASS_NAME, "removeBank", exc);
			LOGGER.exiting(CLASS_NAME, "removeBank");
			throw exc;
		}
		if (!banks.contains(oldBank)) {
			Notification notification = new Notification(BankNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException(
					"BankMonitor: bank " + oldBank + " is not known");
			LOGGER.throwing(CLASS_NAME, "removeBank", exc);
			LOGGER.exiting(CLASS_NAME, "removeBank");
			throw exc;
		}
		try {
			synchronized (banks) {
				banks.remove(oldBank);
			}
			AuditService.writeAuditInformation(BankAuditType.Removed, BankAuditObject.Bank, oldBank.toString());
			updateStorage();
			Notification notification = new Notification(BankNotificationType.Removed, this, oldBank);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(BankNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeBank", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeBank");
		}
	}

	public void addBranch(Branch newBranch) {
		LOGGER.entering(CLASS_NAME, "addBranch", newBranch);
		if (newBranch == null) {
			Notification notification = new Notification(BranchNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: newBranch is null");
			LOGGER.throwing(CLASS_NAME, "addBranch", exc);
			LOGGER.exiting(CLASS_NAME, "addBranch");
			throw exc;
		}
		Bank bank = null;
		bank = newBranch.owner();
		if (bank == null) {
			Notification notification = new Notification(BranchNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "addBranch", exc);
			LOGGER.exiting(CLASS_NAME, "addBranch");
			throw exc;
		}
		Bank knownBank = findBank(bank);
		if (knownBank == null) {
			Notification notification = new Notification(BranchNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "addBranch", exc);
			LOGGER.exiting(CLASS_NAME, "addBranch");
			throw exc;
		}
		if (bank != knownBank) {
			LOGGER.fine("Setting owner for branch");
			newBranch.setOwner(knownBank);
		}
		try {
			synchronized (banks) {
				knownBank.addBranch(newBranch);
			}
			AuditService.writeAuditInformation(BranchAuditType.Added, BankAuditObject.Branch, newBranch.toString());
			updateStorage();
			Notification notification = new Notification(BranchNotificationType.Add, this, newBranch);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(BranchNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addBranch", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addBranch");
		}
	}

	public void removeBranch(Branch oldBranch) {
		LOGGER.entering(CLASS_NAME, "removeBranch", oldBranch);
		if (oldBranch == null) {
			Notification notification = new Notification(BranchNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: oldBranch is null");
			LOGGER.throwing(CLASS_NAME, "removeBranch", exc);
			LOGGER.exiting(CLASS_NAME, "removeBranch");
			throw exc;
		}
		Bank bank = null;
		bank = oldBranch.owner();
		if (bank == null) {
			Notification notification = new Notification(BranchNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "removeBranch", exc);
			LOGGER.exiting(CLASS_NAME, "removeBranch");
			throw exc;
		}
		Bank knownBank = findBank(bank);
		if (knownBank == null) {
			Notification notification = new Notification(BranchNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "removeBranch", exc);
			LOGGER.exiting(CLASS_NAME, "removeBranch");
			throw exc;
		}
		try {
			synchronized (banks) {
				knownBank.removeBranch(oldBranch);
			}
			AuditService.writeAuditInformation(BranchAuditType.Removed, BankAuditObject.Branch, oldBranch.toString());
			updateStorage();
			Notification notification = new Notification(BranchNotificationType.Removed, this, oldBranch);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(BranchNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeBranch", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeBranch");
		}
	}

	public void addAccount(Account newAccount) {
		LOGGER.entering(CLASS_NAME, "addAccount", newAccount);
		if (newAccount == null) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: newAccount is null");
			LOGGER.throwing(CLASS_NAME, "addAccount", exc);
			LOGGER.exiting(CLASS_NAME, "addAccount");
			throw exc;
		}
		Branch branch = null;
		branch = newAccount.owner();
		if (branch == null) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "addAccount", exc);
			LOGGER.exiting(CLASS_NAME, "addAccount");
			throw exc;
		}
		Branch knownBranch = findBranch(branch);
		if (knownBranch == null) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "addAccount", exc);
			LOGGER.exiting(CLASS_NAME, "addAccount");
			throw exc;
		}
		if (branch != knownBranch) {
			LOGGER.fine("Setting owner for accountAccount");
			newAccount.setOwner(knownBranch);
		}
		try {
			synchronized (banks) {
				knownBranch.addAccount(newAccount);
			}
			AuditService.writeAuditInformation(AccountAuditType.Added, BankAuditObject.Account, newAccount.toString());
			updateStorage();
			Notification notification = new Notification(AccountNotificationType.Add, this, newAccount);
			NotificationCentre.broadcast(notification);
		} catch (Exception e) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addAccount", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addAccount");
		}
	}

	public void removeAccount(Account oldAccount) {
		LOGGER.entering(CLASS_NAME, "removeAccount", oldAccount);
		if (oldAccount == null) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: oldAccount is null");
			LOGGER.throwing(CLASS_NAME, "removeAccount", exc);
			LOGGER.exiting(CLASS_NAME, "removeAccount");
			throw exc;
		}
		Branch branch = null;
		branch = oldAccount.owner();
		if (branch == null) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "removeAccount", exc);
			LOGGER.exiting(CLASS_NAME, "removeAccount");
			throw exc;
		}
		Branch knownBranch = findBranch(branch);
		if (knownBranch == null) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "removeAccount", exc);
			LOGGER.exiting(CLASS_NAME, "removeAccount");
			throw exc;
		}
		try {
			synchronized (banks) {
				knownBranch.removeAccount(oldAccount);
			}
			AuditService.writeAuditInformation(AccountAuditType.Removed, BankAuditObject.Account,
					oldAccount.toString());
			updateStorage();
			Notification notification = new Notification(AccountNotificationType.Removed, this, oldAccount);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeAccount", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeAccount");
		}
	}

	public void deactivateAccount(Account account) {
		LOGGER.entering(CLASS_NAME, "deactivateAccount", account);
		Account existingAccount = findAccount(account);
		if (existingAccount == null) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: account is not known");
			LOGGER.throwing(CLASS_NAME, "deactivateAccount", exc);
			LOGGER.exiting(CLASS_NAME, "deactivateAccount");
			throw exc;
		}
		synchronized (banks) {
			existingAccount.deactivate();
		}
		updateStorage();
		Notification notification = new Notification(AccountNotificationType.Changed, this, existingAccount);
		NotificationCentre.broadcast(notification);
		LOGGER.exiting(CLASS_NAME, "deactivateAccount");
	}

	public void reactivateAccount(Account account) {
		LOGGER.entering(CLASS_NAME, "reactivateAccount", account);
		Account existingAccount = findAccount(account);
		if (existingAccount == null) {
			Notification notification = new Notification(AccountNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: account is not known");
			LOGGER.throwing(CLASS_NAME, "reactivateAccount", exc);
			LOGGER.exiting(CLASS_NAME, "reactivateAccount");
			throw exc;
		}
		synchronized (banks) {
			existingAccount.reactivate();
		}
		updateStorage();
		Notification notification = new Notification(AccountNotificationType.Changed, this, existingAccount);
		NotificationCentre.broadcast(notification);
		LOGGER.exiting(CLASS_NAME, "reactivateAccount");
	}

	public void addTransaction(Transaction newTransaction) {
		LOGGER.entering(CLASS_NAME, "add", newTransaction);
		try {
			Account knownAccount = prepareToAddTransaction(newTransaction);
			performAddTransaction(newTransaction, knownAccount);
			updateStorage();
			clearCopyLists();
		} catch (IllegalArgumentException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "performAdd", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "add");
		}
	}

	public void removeTransaction(Transaction oldTransaction) {
		LOGGER.entering(CLASS_NAME, "removeTransaction", oldTransaction);
		if (oldTransaction == null) {
			Notification notification = new Notification(TransactionNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: oldTransaction is null");
			LOGGER.throwing(CLASS_NAME, "removeTransaction", exc);
			LOGGER.exiting(CLASS_NAME, "removeTransaction");
			throw exc;
		}
		Account account = null;
		account = oldTransaction.owner();
		if (account == null) {
			Notification notification = new Notification(TransactionNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "removeTransaction", exc);
			LOGGER.exiting(CLASS_NAME, "removeTransaction");
			throw exc;
		}
		Account knownAccount = findAccount(account);
		if (knownAccount == null) {
			Notification notification = new Notification(TransactionNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "removeTransaction", exc);
			LOGGER.exiting(CLASS_NAME, "removeTransaction");
			throw exc;
		}
		try {
			synchronized (banks) {
				knownAccount.removeTransaction(oldTransaction);
			}
			AuditService.writeAuditInformation(TransactionAuditType.Removed, BankAuditObject.Transaction,
					oldTransaction.toString());
			updateStorage();
			Notification notification = new Notification(TransactionNotificationType.Removed, this, oldTransaction);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(TransactionNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeTransaction", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeTransaction");
		}
	}

	public void addStandingOrder(StandingOrder newStandingOrder) {
		LOGGER.entering(CLASS_NAME, "addStandingOrder", newStandingOrder);
		if (newStandingOrder == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: newStandingOrder is null");
			LOGGER.throwing(CLASS_NAME, "addStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "addStandingOrder");
			throw exc;
		}
		Account account = null;
		account = newStandingOrder.owner();
		if (account == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "addStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "addStandingOrder");
			throw exc;
		}
		Account knownAccount = findAccount(account);
		if (knownAccount == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "addStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "addStandingOrder");
			throw exc;
		}
		if (account != knownAccount) {
			LOGGER.fine("Setting owner for standing order");
			newStandingOrder.setOwner(knownAccount);
		}
		try {
			synchronized (banks) {
				knownAccount.addStandingOrder(newStandingOrder);
			}
			AuditService.writeAuditInformation(StandingOrderAuditType.Added, BankAuditObject.StandingOrder,
					newStandingOrder.toString());
			updateStorage();
			Notification notification = new Notification(StandingOrderNotificationType.Add, this, newStandingOrder);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addStandingOrder", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addStandingOrder");
		}
	}

	public void updateStandingOrder(StandingOrder changedStandingOrder) {
		LOGGER.entering(CLASS_NAME, "updateStandingOrder", changedStandingOrder);
		if (changedStandingOrder == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: newStandingOrder is null");
			LOGGER.throwing(CLASS_NAME, "updateStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "updateStandingOrder");
			throw exc;
		}
		Account account = null;
		account = changedStandingOrder.owner();
		if (account == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "updateStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "updateStandingOrder");
			throw exc;
		}
		Account knownAccount = findAccount(account);
		if (knownAccount == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "updateStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "updateStandingOrder");
			throw exc;
		}
		if (account != knownAccount) {
			LOGGER.fine("Setting owner for standing order");
			changedStandingOrder.setOwner(knownAccount);
		}
		try {
			knownAccount.updateStandingOrder(changedStandingOrder);
			AuditService.writeAuditInformation(StandingOrderAuditType.Changed, BankAuditObject.StandingOrder,
					changedStandingOrder.toString());
			updateStorage();
			Notification notification = new Notification(StandingOrderNotificationType.Changed, this,
					changedStandingOrder);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addStandingOrder", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "updateStandingOrder");
		}
	}

	public void removeStandingOrder(StandingOrder oldStandingOrder) {
		LOGGER.entering(CLASS_NAME, "removeStandingOrder", oldStandingOrder);
		if (oldStandingOrder == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: oldStandingOrder is null");
			LOGGER.throwing(CLASS_NAME, "removeStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "removeStandingOrder");
			throw exc;
		}
		Account account = null;
		account = oldStandingOrder.owner();
		if (account == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "removeStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "removeStandingOrder");
			throw exc;
		}
		Account knownAccount = findAccount(account);
		if (knownAccount == null) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "removeStandingOrder", exc);
			LOGGER.exiting(CLASS_NAME, "removeStandingOrder");
			throw exc;
		}
		try {
			synchronized (banks) {
				knownAccount.removeStandingOrder(oldStandingOrder);
			}
			AuditService.writeAuditInformation(StandingOrderAuditType.Removed, BankAuditObject.StandingOrder,
					oldStandingOrder.toString());
			updateStorage();
			Notification notification = new Notification(StandingOrderNotificationType.Removed, this, oldStandingOrder);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(StandingOrderNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeStandingOrder", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeStandingOrder");
		}
	}

	public void transfer(Transfer transfer) {
		LOGGER.entering(CLASS_NAME, "transfer", transfer);
		addTransactionNoUpdate(transfer.from());
		addTransaction(transfer.to());
		LOGGER.exiting(CLASS_NAME, "transfer");
	}

	public void addInvestment(Investment newInvestment) {
		LOGGER.entering(CLASS_NAME, "addInvestment", newInvestment);
		if (newInvestment == null) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: newInvestment is null");
			LOGGER.throwing(CLASS_NAME, "addInvestment", exc);
			LOGGER.exiting(CLASS_NAME, "addInvestment");
			throw exc;
		}
		if (investments.contains(newInvestment)) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException(
					"BankMonitor: investment " + newInvestment + " already exists");
			LOGGER.throwing(CLASS_NAME, "addInvestment", exc);
			LOGGER.exiting(CLASS_NAME, "addInvestment");
			throw exc;
		}
		try {
			synchronized (investments) {
				investments.add(newInvestment);
			}
			AuditService.writeAuditInformation(InvestmentAuditType.Added, BankAuditObject.Investment,
					newInvestment.toString());
			updateStorage();
			Notification notification = new Notification(InvestmentNotificationType.Add, this, newInvestment);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addInvestment", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addInvestment");
		}
	}

	public Investment updateInvestment(Investment modInvestment) {
		LOGGER.entering(CLASS_NAME, "updateInvestment", modInvestment);
		Investment original = null;
		if (modInvestment == null) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: modInvestment is null");
			LOGGER.throwing(CLASS_NAME, "updateInvestment", exc);
			LOGGER.exiting(CLASS_NAME, "updateInvestment");
			throw exc;
		}
		if (!investments.contains(modInvestment)) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException(
					"BankMonitor: investment " + modInvestment + " is not known");
			LOGGER.throwing(CLASS_NAME, "updateInvestment", exc);
			LOGGER.exiting(CLASS_NAME, "updateInvestment");
			throw exc;
		}
		try {
			synchronized (investments) {
				int index = investments.indexOf(modInvestment);
				original = investments.get(index);
				investments.remove(modInvestment);
				investments.add(index, modInvestment);
			}
			AuditService.writeAuditInformation(InvestmentAuditType.Changed, BankAuditObject.Investment,
					modInvestment.toString());
			updateStorage();
			Notification notification = new Notification(InvestmentNotificationType.Changed, this, modInvestment);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "updateInvestment", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "updateInvestment");
		}
		return original;
	}

	public void removeInvestment(Investment oldInvestment) {
		LOGGER.entering(CLASS_NAME, "removeInvestment", oldInvestment);
		if (oldInvestment == null) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: oldInvestment is null");
			LOGGER.throwing(CLASS_NAME, "removeInvestment", exc);
			LOGGER.exiting(CLASS_NAME, "removeInvestment");
			throw exc;
		}
		if (!investments.contains(oldInvestment)) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException(
					"BankMonitor: investment " + oldInvestment + " is not known");
			LOGGER.throwing(CLASS_NAME, "removeInvestment", exc);
			LOGGER.exiting(CLASS_NAME, "removeInvestment");
			throw exc;
		}
		try {
			synchronized (investments) {
				investments.remove(oldInvestment);
			}
			AuditService.writeAuditInformation(InvestmentAuditType.Removed, BankAuditObject.Investment,
					oldInvestment.toString());
			updateStorage();
			Notification notification = new Notification(InvestmentNotificationType.Removed, this, oldInvestment);
			NotificationCentre.broadcast(notification);
			clearCopyLists();
		} catch (Exception e) {
			Notification notification = new Notification(InvestmentNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeInvestment", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeInvestment");
		}
	}

	private void updateStorage() {
		LOGGER.entering(CLASS_NAME, "updateStorage");
		BankStore bankStore = new BankStore();
		File modelDirectory = obtainModelDirectory();
		File dataFile = new File(modelDirectory, ModelConstants.BANK_FILE);
		bankStore.setFileName(dataFile.getAbsolutePath());
		Storage storage = new Storage();
		storage.storeData(bankStore);
		LOGGER.exiting(CLASS_NAME, "updateStorage");
	}

	private File obtainModelDirectory() {
		LOGGER.entering(CLASS_NAME, "obtainModelDirectory");
		File rootDirectory = ApplicationConfiguration.rootDirectory();
		File applicationDirectory = new File(rootDirectory,
				ApplicationConfiguration.applicationDefinition().applicationName());
		File modelDirectory = new File(applicationDirectory, ModelConstants.MODEL);
		if (!modelDirectory.exists()) {
			LOGGER.fine("Model directory " + modelDirectory.getAbsolutePath() + " does not exist");
			if (!modelDirectory.mkdirs()) {
				LOGGER.warning("Unable to create model directory");
				modelDirectory = null;
			} else {
				LOGGER.fine("Created model directory " + modelDirectory.getAbsolutePath());
			}
		} else {
			LOGGER.fine("Model directory " + modelDirectory.getAbsolutePath() + " does exist");
		}
		LOGGER.exiting(CLASS_NAME, "obtainModelDirectory", modelDirectory);
		return modelDirectory;
	}

	private void addTransactionNoUpdate(Transaction newTransaction) {
		LOGGER.entering(CLASS_NAME, "addTransactionNoUpdate", newTransaction);
		try {
			Account knownAccount = prepareToAddTransaction(newTransaction);
			performAddTransaction(newTransaction, knownAccount);
		} catch (IllegalArgumentException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addTransactionNoUpdate", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addTransactionNoUpdate");
		}
	}

	private void clearCopyLists() {
		LOGGER.entering(CLASS_NAME, "clearCopyLists");
		bankList = null;
		investmentList = null;
		LOGGER.exiting(CLASS_NAME, "clearCopyLists");
	}

	public Bank findBank(Bank bank) {
		LOGGER.entering(CLASS_NAME, "findBank", bank);
		Bank found = null;
		synchronized (banks) {
			for (Bank b : banks) {
				LOGGER.fine(b.toString());
				if (b.equals(bank)) {
					found = b;
					break;
				}
			}
		}
		LOGGER.exiting(CLASS_NAME, "findBank", found);
		return found;
	}

	public Branch findBranch(Branch branch) {
		LOGGER.entering(CLASS_NAME, "findBranch", branch);
		Branch found = null;
		Bank owner = findBank(branch.owner());
		if (owner != null) {
			found = owner.locateBranch(branch);
		}
		LOGGER.exiting(CLASS_NAME, "findBranch", found);
		return found;
	}

	public Account findAccount(Account account) {
		LOGGER.entering(CLASS_NAME, "findAccount", account);
		Account found = null;
		Branch owner = null;
		if (account != null) {
			owner = findBranch(account.owner());
		}
		if (owner != null) {
			found = owner.locateAccount(account);
		}
		LOGGER.exiting(CLASS_NAME, "findAccount", found);
		return found;
	}

	public Investment findInvestment(Investment investment) {
		LOGGER.entering(CLASS_NAME, "findInvestment", investment);
		Investment found = null;
		synchronized (investments) {
			for (Investment i : investments) {
				LOGGER.fine(i.toString());
				if (i.equals(investment)) {
					found = i;
					break;
				}
			}
		}
		LOGGER.exiting(CLASS_NAME, "findInvestment", found);
		return found;
	}

	public StandingOrder findStandingOrder(StandingOrder standingOrder) {
		LOGGER.entering(CLASS_NAME, "findStandingOrder", standingOrder);
		StandingOrder found = null;
		Account owner = findAccount(standingOrder.owner());
		if (owner != null) {
			found = owner.locateStandingOrder(standingOrder);
		}
		LOGGER.exiting(CLASS_NAME, "findStandingOrder", found);
		return found;
	}

	private void performAddTransaction(Transaction newTransaction, Account knownAccount) {
		LOGGER.entering(CLASS_NAME, "performAddTransaction", new Object[] { newTransaction, knownAccount });
		try {
			synchronized (banks) {
				knownAccount.addTransaction(newTransaction);
			}
			AuditService.writeAuditInformation(TransactionAuditType.Added, BankAuditObject.Transaction,
					newTransaction.toString());
			Notification notification = new Notification(TransactionNotificationType.Add, this, newTransaction);
			NotificationCentre.broadcast(notification);
		} catch (Exception e) {
			Notification notification = new Notification(TransactionNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "performAddTransaction", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "preformAddTransaction");
		}
	}

	private Account prepareToAddTransaction(Transaction newTransaction) {
		LOGGER.entering(CLASS_NAME, "prepareToAddTransaction", newTransaction);
		if (newTransaction == null) {
			Notification notification = new Notification(TransactionNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: newTransaction is null");
			LOGGER.throwing(CLASS_NAME, "prepareToAddTransaction", exc);
			LOGGER.exiting(CLASS_NAME, "prepareToAddTransaction");
			throw exc;
		}
		Account account = null;
		account = newTransaction.owner();
		if (account == null) {
			Notification notification = new Notification(TransactionNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is null");
			LOGGER.throwing(CLASS_NAME, "prepareToAddTransaction", exc);
			LOGGER.exiting(CLASS_NAME, "prepareToAddTransaction");
			throw exc;
		}
		Account knownAccount = findAccount(account);
		if (knownAccount == null) {
			Notification notification = new Notification(TransactionNotificationType.Failed, this);
			NotificationCentre.broadcast(notification);
			IllegalArgumentException exc = new IllegalArgumentException("BankMonitor: owner is not known");
			LOGGER.throwing(CLASS_NAME, "prepareToAddTransaction", exc);
			LOGGER.exiting(CLASS_NAME, "prepareToAddTransaction");
			throw exc;
		}
		if (account != knownAccount) {
			LOGGER.fine("Setting owner for transaction");
			newTransaction.setOwner(knownAccount);
		}
		LOGGER.exiting(CLASS_NAME, "prepareToAddTransaction", knownAccount);
		return knownAccount;
	}

}
