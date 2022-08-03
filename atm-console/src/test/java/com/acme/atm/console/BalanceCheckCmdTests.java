package com.acme.atm.console;

import com.acme.atm.console.cmdhandler.APIHandler;
import com.acme.atm.console.cmdhandler.TransactionCmdHandler;
import com.acme.atm.console.model.ResponseBean;

import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

import org.junit.jupiter.api.Test; 

public class BalanceCheckCmdTests {

    private static final String TEST_ACCOUNT_ID = "2859459814";

    /** Scenario Tested:
     *  Response for 'balance' command when the balance check is successful and return positive balance.
     * 
     **/
	@Test
	void balanceCheckSuccessForPositiveBalance() {
		Double balance = 10.24;
		String expectedMsg = "Current balance: $" + balance.doubleValue();
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn(balance);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokeGetAPI(Mockito.anyString())).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.getBalance(), "balance() check method success");
	}
	
	/** Scenario Tested:
     *  Response for 'balance' command when the balance check is successful and return negative balance (overdraft account).
     * 
     **/
	@Test
	void balanceCheckSuccessForNegativeBalance() {
		Double balance = -75.00;
		String expectedMsg = "Current balance: -$75.00";
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn(balance);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokeGetAPI(Mockito.anyString())).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.getBalance(), "balance() check method success for overdraft account");
	}
	
	
	 /** Scenario Tested:
     *   Response for 'balance' command when the balance check fails due to invalid accountId or pin.
     * 
     **/
	@Test
    void balanceCheckFailedWhenUserIsNotLoggedIn() {
		
		String expectedMsg = "Authorization required.";
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(false);
        
        TransactionCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, TransactionCmdHandler.getBalance(), "balance() check failed - user not logged in");
    }
	
	
	 /** Scenario Tested:
     *  Response for 'balance' command when the balance check fails due to the ATM server being down/unreachable.
     * 
     **/
	@Test
    void balanceCheckFailedWhenServerIsUnreachable() {
		String expectedMsg = "Failed to process the command. Service is unavailable.";
		
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(false);
		Mockito.when(mockResponseBean.getStatus()).thenReturn(500);
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.getAccountId()).thenReturn(TEST_ACCOUNT_ID);
		Mockito.when(mockApiHandler.invokeGetAPI(Mockito.anyString())).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, TransactionCmdHandler.getBalance(), "balance() check failed - service unavailable");
    }
	

}
