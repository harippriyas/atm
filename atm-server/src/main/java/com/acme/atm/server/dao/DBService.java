package com.acme.atm.server.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.util.Streamable;

import com.acme.atm.server.model.Account;
import com.acme.atm.server.model.TransactionHistory;

public class DBService {

	private List<Account> defaultAccountList;
	
	private String atmAccountId;
	
	private AccountRepository accountDbRepo;
	
	private TransactionHistoryRepository historyDbRepo;
	
	// Singleton instance
	private static class InstanceHolder {
		private static final DBService instance = new DBService();
	}

	public static DBService getInstance() {
		return InstanceHolder.instance;
	}

	protected DBService()
	{
		super();
	}

	public void setAccountRepository(AccountRepository repo) {
		this.accountDbRepo = repo;		
	}

	public void setHistoryRepository(TransactionHistoryRepository repo) {
		this.historyDbRepo = repo;		
	}
	
	public List<Account> getDefaultAccountList() {
		return defaultAccountList;
	}

	public void setDefaultAccountList(List<Account> defaultAccountList) {
		this.defaultAccountList = defaultAccountList;
		initializeDB();
	}
	
	public Optional<Account> getAccount(String accountId) {
		return accountDbRepo.findById(accountId);
	}

	public Account getAtmAccount() {
		return accountDbRepo.findById(atmAccountId).get();
	}
	
	public void updateAccount(Account accountObj) {
		accountDbRepo.save(accountObj);
	}

	public List<TransactionHistory> getTransactionHistory(String accountId) {
		return historyDbRepo.findByAccountIdOrderByTxnDateDesc(accountId);
	}
	
	public void addTransactionHistory(TransactionHistory historyObj) {
		historyDbRepo.save(historyObj);
	}

	public void reinitializeDB() {
		accountDbRepo.deleteAll();
		historyDbRepo.deleteAll();
		initializeDB();
	}

	private void initializeDB() {
		// Check if this is the first time the application is run and the persistence db is empty
		List<Account> accountsInDB = Streamable.of(accountDbRepo.findAll()).toList();
		if(accountsInDB.isEmpty()) {
			System.out.println("Initializing with default accounts...");
			defaultAccountList.forEach(e -> accountDbRepo.save(e));
		}
		
		accountsInDB = Streamable.of(accountDbRepo.findAll()).toList();
		if(accountsInDB.isEmpty()) {
			System.out.println("DB is still empty");
		}
		else {
			accountsInDB.forEach(e -> {
				System.out.println(e.getAccountId());
			});
		}
		
	}

}
