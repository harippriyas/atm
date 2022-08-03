package com.acme.atm.server.api;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import com.acme.atm.server.dao.DBService;
import com.acme.atm.server.model.Account;
import com.acme.atm.server.model.ResponseBean;
import com.acme.atm.server.model.TransactionHistory;
import com.acme.atm.server.model.WithdrawCmdResponse;
import com.acme.atm.server.security.SessionBean;

/** Workhorse class that contains the logic for all the transaction related commands.
 * 
 * @author SHarippriya
 *
 */
public class TransactionAPIHandler {
	
	private static final DecimalFormat amtFormatter = new DecimalFormat("0.00");
	
	static {
		amtFormatter.setRoundingMode(RoundingMode.DOWN);
	}
	
	/** Retrieves the balance for the specified account from the DB */
	public ResponseBean processBalanceCheck(String accountId, DBService dbService) {
		ResponseBean bean = new ResponseBean();
		Optional<Account> acctObj = dbService.getAccount(accountId);
		if(acctObj.isPresent()) {
			bean.setData(acctObj.get().getBalance());
		}
		else {
			bean.setMessage("Failed to retrieve balance information");
			bean.setStatus(500);
		}
		return bean;
	}
	
	/** Processes the deposit transaction and also records it */
	public synchronized ResponseBean processDeposit(String accountId, DBService dbService, double depositAmount) {
		ResponseBean bean = new ResponseBean();
		if(depositAmount > 0) {
			try {
				// Get and validate the account ID
				Optional<Account> acctObj = dbService.getAccount(accountId);
				if(acctObj.isPresent()) {
					// Round off the amount to 2 decimal places
					depositAmount = Double.valueOf(amtFormatter.format(depositAmount * 1.0));
					
					// Deposit the amount into the account
					Account userAccountObj = acctObj.get();
					double newBalance = userAccountObj.getBalance() + depositAmount;
					userAccountObj.setBalance(newBalance);
					dbService.updateAccount(userAccountObj);
					
					// Return the new balance 
					bean.setData(newBalance);
					
					// TODO: Should we increase the available balance in the ATM?
					// If yes, we need to understand how many deposit was made (cash or cheque), how many $20 bills were there (as ATM can only dispense in $20s)
					// Hence skipping the ATM balance for now
					
					// Record this user's transaction
					recordTransaction(dbService, userAccountObj.getAccountId(), depositAmount, newBalance, true);
				}
				else {
					bean.setMessage("Failed to process deposit");
					bean.setStatus(500); // unexpected error as the account was already validated during login, even before this code is invoked 
				}
			}
			catch(Exception e) {
				bean.setStatus(500);
				bean.setMessage("Failed to process deposit. Cause: " + e.getMessage());
			}
		}
		else {
			bean.setStatus(400);
			bean.setMessage("Deposit amount should be greater than 0");
		}
		return bean;
	}

	/** Processes withdrawal request and records the transaction */
	public synchronized WithdrawCmdResponse processWithdrawal(SessionBean sessionBean, DBService dbService, double amount) {
		WithdrawCmdResponse response = null;
		if(amount > 0) {
			Optional<Account> userAccount = dbService.getAccount(sessionBean.getAccountId());
			if(userAccount.isPresent()) {
				response = new WithdrawCmdResponse();
				
				// Round down the amount to 2 decimal places
				
				amount = Double.valueOf(amtFormatter.format(amount * 1.0));
				
				// Get the current balance of the account
				Account userAccountObj = userAccount.get();
				double currentBalance = userAccountObj.getBalance();
				// Get the available amount in the ATM machine
				Account atmAccount = dbService.getAccount(sessionBean.getMachineId()).get();
				double atmBalance = atmAccount.getBalance();
				
				if(currentBalance <= 0 ) {
					// Account is already overdrawn. Do not process this transaction.
					response.setOverdraftState(true);
				}
				else if(atmBalance == 0) {
					// ATM does not have the cash to allow this transaction to proceed
					response.setInsufficientAtmBalance(true);
				}
				else {
					// Alter the amount to be a multiple of 20, as the ATM can only dispense $20 bills.
					double adjustedAmount = amount - (amount % 20.0);
					if(atmBalance < adjustedAmount) {
						// ATM has less money than requested. Adjust the amount to what the ATM can dispense.
						adjustedAmount = atmBalance;
						response.setInsufficientAtmBalance(true);
					}
					
					if(currentBalance < adjustedAmount) {
						// Account has less money that what has been requested. Charge overdraft fees
						// TOOD: Do we still dispense the requested amount the first time it is overdrawn?
						currentBalance = currentBalance - 5.0;
						response.setOverdraftFeeCharged(true);
					}
					
					// Update the account balance
					double newBalance = currentBalance - adjustedAmount;
					newBalance = Double.valueOf(amtFormatter.format(newBalance * 1.0));
					
					userAccountObj.setBalance(newBalance);
					dbService.updateAccount(userAccountObj);
					
					// Update the ATM balance
					atmAccount.setBalance(atmBalance - adjustedAmount);
					dbService.updateAccount(atmAccount);
					
					// Build data to send to user
					response.setAmtDispensed(adjustedAmount);
					response.setNewBalance(newBalance);
					
					// TODO: Should we record the failure cases also - account in overdraft state and ATM has no funds?
					recordTransaction(dbService, userAccountObj.getAccountId(), adjustedAmount, newBalance, false);
				}
				
			}
		}
	
		return response;
	}
	
	/** Record the transaction to the history table. */
	private static void recordTransaction(DBService dbService, String accountId, double amount, double balance, boolean isDeposit) {
		TransactionHistory historyObj = new TransactionHistory();
		historyObj.setAccountId(accountId);
		historyObj.setBalance(balance);
		// withdrawals show up as negative numbers in transaction history
		if(!isDeposit)
			amount = amount * -1;
		historyObj.setAmount(amount);
		historyObj.setTxnDate(new Timestamp(System.currentTimeMillis()));
		dbService.addTransactionHistory(historyObj);
	}

	public static List<TransactionHistory> getTransactionHistory(String accountId, DBService dbService) {
		return dbService.getTransactionHistory(accountId);
	}
}
