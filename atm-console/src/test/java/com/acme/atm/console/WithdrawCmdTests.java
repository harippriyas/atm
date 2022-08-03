package com.acme.atm.console;

import com.acme.atm.console.cmdhandler.APIHandler;
import com.acme.atm.console.cmdhandler.TransactionCmdHandler;
import com.acme.atm.console.model.ResponseBean;

import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

import org.junit.jupiter.api.Test; 

public class WithdrawCmdTests {

    private static final String TEST_ACCOUNT_ID = "2859459814";

    /** Scenario Tested:
     *  Response for 'withdraw' command when the withdraw is successful and return positive balance.
     * 
     **/
	@Test
	void withdrawSuccessForPositiveBalance() {
		String expectedMsg = "Amount dispensed: $100.00\nCurrent balance: $50.00";
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn(expectedMsg);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.withdraw(100), "withdraw() success");
	}
	
	/** Scenario Tested:
     *  Response for 'withdraw' command when the withdraw is successful and return negative balance (overdraft account).
     * 
     **/
	@Test
	void withdrawFailedForOverdraftAccount() {
		String expectedMsg = "Your account is overdrawn! You may not make withdrawals at this time.";
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn(expectedMsg);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.withdraw(100), "withdraw() success for overdraft account");
	}
	
	/** Scenario Tested:
     *  Response for 'withdraw' command when the withdraw fails due to an invalid withdraw amount (value less than or equal to 0).
     * 
     **/
	@Test
	void withdrawInvalidAmount() {
		String expectedMsg = "Withdrawal amount must be greater than 0.";
		
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn(expectedMsg);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockResponseBean);
	
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.withdraw(-100), "withdraw() failed for invalid amount");
	}
	
	
	 /** Scenario Tested:
     *   Response for 'withdraw' command when the withdraw fails due to invalid accountId or pin.
     * 
     **/
	@Test
    void withdrawFailedWhenUserIsNotLoggedIn() {
		
		String expectedMsg = "Authorization required.";
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(false);
        
        TransactionCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, TransactionCmdHandler.withdraw(100), "withdraw() failed - user not logged in");
    }
	
	
	 /** Scenario Tested:
     *  Response for 'withdraw' command when the withdraw fails due to the ATM server being down/unreachable.
     * 
     **/
	@Test
    void withdrawFailedWhenServerIsUnreachable() {
		String expectedMsg = "Failed to process the command. Service is unavailable.";
		
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(false);
		Mockito.when(mockResponseBean.getStatus()).thenReturn(500);
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.getAccountId()).thenReturn(TEST_ACCOUNT_ID);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, TransactionCmdHandler.withdraw(100), "withdraw() failed - service unavailable");
    }
	

}
