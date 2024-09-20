package applications.bank.model;

public record AccountId(String accountHolder, String accountNumber) implements Comparable<AccountId> {
	/**
	 * 
	 * @author nevil
	 * @param accountHolder is the name of the account holder
	 * @param accountNumber is the number of the account
	 * 
	 */

	public AccountId {
		if (accountHolder == null) {
			throw new IllegalArgumentException("AccountId: accountHolder is null");
		}
		if (accountHolder.isBlank()) {
			throw new IllegalArgumentException("AccountId: accountHolder is blank");
		}
		if (accountNumber == null) {
			throw new IllegalArgumentException("AccountId: accountNumber is null");
		}
		if (accountNumber.isBlank()) {
			throw new IllegalArgumentException("AccountId: accountNumber is blank");
		}
	}

	@Override
	public int compareTo(AccountId that) {
		return accountNumber.compareTo(that.accountNumber);
	}
}
