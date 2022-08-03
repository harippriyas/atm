package com.acme.atm.server.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account implements Serializable{
	
	public enum ACCOUNT_TYPE { user, admin, atm_machine };
	
	private static final long serialVersionUID = 5903579974824488323L;
	
	@Id
	private String accountId;
	
	private String  pin;
	private double balance;
	private String type;
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isUser() {
		return (getType()!= null) ? ACCOUNT_TYPE.user.name().equals(getType()) : true;
	}
	
	public boolean isAdmin() {
		return ACCOUNT_TYPE.admin.name().equals(getType());
	}
	
	public boolean isAtmMachine() {
		return ACCOUNT_TYPE.atm_machine.name().equals(getType());
	}
}
