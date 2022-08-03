package com.acme.atm.console;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.acme.atm.console.cmdhandler.APIHandler;
import com.acme.atm.console.cmdhandler.AuthorizationCmdHandler;
import com.acme.atm.console.cmdhandler.TransactionCmdHandler;

/** Shell commands that are supported by the ATM application.
 * 	This mimics the operations that could be performed on an ATM machine's console.
 * 
 * 	@author SHarippriya
 *
 */
@ShellComponent
public class ATMCommands {
	
	private APIHandler apiHandler = APIHandler.getInstance();
	
	@ShellMethod(value = "Login using the command 'authorize <accountId> <pin>'")
    public void authorize(@ShellOption String accountId, @ShellOption String pin)
    {
        String message = AuthorizationCmdHandler.login(getApiHandler().getMachineId(), accountId, pin);
        cmdOutput(message);
    }

	@ShellMethod(value = "Logout to end the session")
    public void logout()
    {
    	String message = AuthorizationCmdHandler.logout();
    	cmdOutput(message);
    }
	
	@ShellMethod(value = "Check account balance")
    public void balance()
    {
		String message = TransactionCmdHandler.getBalance();
		cmdOutput(message);
    }
    
    
    @ShellMethod(value = "Deposit money using the command 'deposit <value>'")
    public void deposit(double amount)
    {
    	String message = TransactionCmdHandler.deposit(amount);
		cmdOutput(message);
    }
    
    
    @ShellMethod(value = "Withdraw money using the command 'withdraw <value>'")
    public void withdraw(double amount)
    {
    	String message = TransactionCmdHandler.withdraw(amount);
		cmdOutput(message);
    }
    
    
    @ShellMethod(value = "View the account's transaction history")
    public void txnhistory()
    {
    	String message = TransactionCmdHandler.getTransactionHistory();
		cmdOutput(message);
     }
    
    /*
    public Availability isAuthenticatedUser()
    {
        return AuthenticationHandler.getLoggedInUserAccount() != null ?
                Availability.available() : Availability.unavailable("Authorization required.");
    }
    */
    
    public boolean isUserAuthorized() {
    	return (apiHandler.getAccessToken() != null);
    }
    
    private void cmdOutput(String message) {
    	System.out.println(message);
    }
    
    public APIHandler getApiHandler() {
		return apiHandler;
	}

	public void setApiHandler(APIHandler apiHandler) {
		this.apiHandler = apiHandler;
	}
    
    
}