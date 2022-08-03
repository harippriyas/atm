package com.acme.atm.console.cmdhandler;

import com.acme.atm.console.model.ResponseBean;
import com.acme.atm.console.model.SessionBean;

/** Handles the authorization related commands executed by the customer.
 *  This class is responsible for invoking the appropriate ATM server API call, 
 *  parsing the API response and returning the message to be displayed to the user.
 *  
 * 	@author SHarippriya
 *
 */
public class AuthorizationCmdHandler {
	
	private static APIHandler apiHandler;

	/** Processes for the 'authorize' command.
	 * 
	 * 	@param machineId	ATM machine ID on which all transactions must be performed.
	 * 	@param accountId	User account ID
	 * 	@param pin			User pin
	 * 	@return boolean		Flag indicating if the authorization was successful.
	 */
	public static String login(String machineId, String accountId, String pin) {
		String message = "Authorization failed.";
		try {
			// Login API input bean
			SessionBean inputBean = new SessionBean();
			inputBean.setMachineId(machineId);
			inputBean.setAccountId(accountId);
			inputBean.setPin(pin);
			// Invoke the login API
			SessionBean sessionBean = getApiHandler().invokeLoginAPI("/atm/auth/v1/login", inputBean);
			// Parse the response
			if(sessionBean != null && sessionBean.getToken() != null) {
				// Save the details for use with subsequent requests.
				getApiHandler().setAccessToken(sessionBean.getToken());
				getApiHandler().setAccountId(sessionBean.getAccountId());
				getApiHandler().setAdminAccount(sessionBean.isAdmin());
				message = accountId + " successfully authorized.";
			}
			else if(sessionBean == null) {
				 message = "Authorization failed. Service is unavailable.";
			}
		}
		catch(Exception e) {
			// Nothing to do here. Authorization failure message will be displayed to user.
			e.printStackTrace();
		}
		return message;
	}

	
	/** Processes the logout command.
	 * 	Invokes the logout API of the ATM server to invalidate the session in that service as well.
	 * 
	 * 	@return boolean	Flag indicating if the logout was successful.
	 */
	public static String logout() {
		String message = "Failed to logout.";
		
		if(getApiHandler().isUserAuthorized()) {
    		String loggedInAccountId = getApiHandler().getAccountId();
    		// Let the ATM service know to terminate the session
    		ResponseBean respBean = getApiHandler().invokePostAPI("/atm/auth/v1/logout", null);
    		
    		if(respBean.isSuccess()) {
    			// Proceed with cleaning up the session details in this application
    			getApiHandler().setAccessToken(null);
    			getApiHandler().setAccountId(null);
    			getApiHandler().setAdminAccount(false);
    			message = "Account " + loggedInAccountId + " logged out.";
    		}
        	else
        		message = "Failed to logout. Service is unavailable.";
    	}
    	else {
    		message  = "No account is currently authorized.";
    	}
		
		
		return message;
	}

	public static APIHandler getApiHandler() {
		return apiHandler != null ? apiHandler : APIHandler.getInstance();
	}

	public static void setApiHandler(APIHandler handler) {
		apiHandler = handler;
	}
	
	
}
