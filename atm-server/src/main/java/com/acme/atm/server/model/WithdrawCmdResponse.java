package com.acme.atm.server.model;

import java.io.Serializable;

public class WithdrawCmdResponse implements Serializable {
	
	private static final long serialVersionUID = -4386367147658037495L;
	private double amtDispensed;
	private double newBalance;
	private boolean overdraftState;
	private boolean overdraftFeeCharged;
	private boolean insufficientAtmBalance;
	
	public double getAmtDispensed() {
		return amtDispensed;
	}
	public void setAmtDispensed(double amtDispensed) {
		this.amtDispensed = amtDispensed;
	}
	public double getNewBalance() {
		return newBalance;
	}
	public void setNewBalance(double newBalance) {
		this.newBalance = newBalance;
	}
	public boolean isOverdraftState() {
		return overdraftState;
	}
	public void setOverdraftState(boolean overdraftState) {
		this.overdraftState = overdraftState;
	}
	public boolean isOverdraftFeeCharged() {
		return overdraftFeeCharged;
	}
	public void setOverdraftFeeCharged(boolean overdraftFeeCharged) {
		this.overdraftFeeCharged = overdraftFeeCharged;
	}
	public boolean isInsufficientAtmBalance() {
		return insufficientAtmBalance;
	}
	public void setInsufficientAtmBalance(boolean insufficientAtmBalance) {
		this.insufficientAtmBalance = insufficientAtmBalance;
	}
	

}
