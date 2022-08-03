package com.acme.atm.server.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.acme.atm.server.model.TransactionHistory;

@Repository
public interface TransactionHistoryRepository extends CrudRepository<TransactionHistory, Integer>{
	
	List<TransactionHistory> findByAccountIdOrderByTxnDateDesc(String accountId);
}
