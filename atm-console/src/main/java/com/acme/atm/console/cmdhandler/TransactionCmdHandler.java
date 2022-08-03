package com.acme.atm.console.cmdhandler;

import java.text.DecimalFormat;

import com.acme.atm.console.model.ResponseBean;

/** Processes ATM transaction commands like deposit, withdrawal, etc.
 * 
 * @author SHarippriya
 *
 */
public class TransactionCmdHandler {
	
	private static APIHandler apiHandler;
	private static final DecimalFormat amtFormatter = new DecimalFormat("0.00");
	
	public static APIHandler getApiHandler() {
		return apiHandler != null ? apiHandler : APIHandler.getInstance();
	}

	public static void setApiHandler(APIHandler handler) {
		apiHandler = handler;
	}

	/** Handles the balance check request for the currently authorized account.
	 * 
	 * @return	double	Balance amount for this account
	 */
	public static String getBalance() {
		String response = "Failed to process the command.";
		if(getApiHandler().isUserAuthorized()) {
			ResponseBean apiResponse = getApiHandler().invokeGetAPI("/atm/api/v1/transactions/balance");
			if(apiResponse.isSuccess()) {
				try {
					double balance = ((Double)apiResponse.getData()).doubleValue();
					response = buildBalanceStr(balance);
				}
				catch(Exception e) {
					// Nothing to do here as the failure message is automatically returned.
					e.printStackTrace();
				}
			}
			else {
				response = handleError(apiResponse);
			}
		}
		else {
			response = "Authorization required.";
		}
		return response;
	}

	/** Handles the deposit request for the currently authorized account.
	 * 	Note: Depositing an amount will only add to the specific account's balance. It will not increase the available cash in the ATM.
	 *  
	 * 	@param depositAmount	Amount to be deposited into the account
	 * @return boolean			Returns true if amount was deposited into the account.
	 */
	public static synchronized String deposit(double depositAmount) {
		String response = "Failed to process the command.";
		if(getApiHandler().isUserAuthorized()) {
			if(depositAmount > 0) {
				ResponseBean apiResponse = getApiHandler().invokePostAPI("/atm/api/v1/transactions/deposit", depositAmount);
				if(apiResponse.isSuccess()) {
					try {
						double balance = ((Double)apiResponse.getData()).doubleValue();
						response = buildBalanceStr(balance);
					}
					catch(Exception e) {
						// Nothing to do here as the failure message is automatically returned.
						e.printStackTrace();
					}
				}
				else {
					response = handleError(apiResponse);
				}
			}
			else {
				response = "Deposit amount should be greater than 0";
			}
		}
		else {
			response = "Authorization required.";
		}
		return response;
	}

	/** Handles the withdrawal of amount from the currently authorized account.
	 * 
	 * @param 	withdrawalAmount	Amount to withdraw
	 * @return	String				Status message to be displayed to the user.
	 */
	public static synchronized String withdraw(double withdrawalAmount) {
		if(getApiHandler().isUserAuthorized()) {
			ResponseBean respBean = getApiHandler().invokePostAPI("/atm/api/v1/transactions/withdraw", withdrawalAmount);
			return (respBean.getData() != null ? respBean.getData().toString() : handleError(respBean));
		}
		else {
			return "Authorization required.";
		}
	}

	/** Handles the request to obtain the transaction history for the currently authorized account.
	 * 
	 * @return String 	Transaction history in reverse chronological order
	 */
	public static String getTransactionHistory() {
		if(getApiHandler().isUserAuthorized()) {
			ResponseBean respBean = getApiHandler().invokeGetAPI("/atm/api/v1/transactions/history");
			String response =  (respBean.getData() != null ? respBean.getData().toString() : handleError(respBean));
			if(response == null || response.length() == 0)
				response = "No history found.";
			return response;
		}
		else {
			return "Authorization required.";
		}
	}
	
	private static String handleError(ResponseBean respBean) {
		String response = "Failed to process the command";
		if(respBean.getStatus() == 401) {
			response = "Authorization required.";
		}
		else if(respBean.getStatus() == 404 || respBean.getStatus() == 500) {
			response = "Failed to process the command. Service is unavailable.";
		}
		else if(respBean.getMessage() != null & respBean.getMessage().length() > 0) {
			response = respBean.getMessage();
		}
		return response;
	}
	
	/** Helper method to construct the message to be displayed for current balance.
	 * 	Handle positive balance like $70 and negative balance like -$20.
	 * 
	 * @param balance
	 * @return
	 */
	private static String buildBalanceStr(double balance) {
		String response = "Current balance: ";
		if(balance >= 0)
			response += ("$" + amtFormatter.format(balance));
		else
			response += "-$" + amtFormatter.format(balance * -1);
		
		return response;
	}
}
