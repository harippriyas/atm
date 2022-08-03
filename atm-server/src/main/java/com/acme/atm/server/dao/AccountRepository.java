package com.acme.atm.server.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.acme.atm.server.model.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account, String>{

}
