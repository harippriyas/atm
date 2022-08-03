package com.acme.atm.console;

import com.acme.atm.console.cmdhandler.APIHandler;
import com.acme.atm.console.cmdhandler.TransactionCmdHandler;
import com.acme.atm.console.model.ResponseBean;

import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

import org.junit.jupiter.api.Test; 

public class DepositCmdTests {

    private static final String TEST_ACCOUNT_ID = "2859459814";

    /** Scenario Tested:
     *  Response for 'deposit' command when the deposit is successful and return positive balance.
     * 
     **/
	@Test
	void depositSuccessForPositiveBalance() {
		Double balance = 120.04;
		String expectedMsg = "Current balance: $" + balance.doubleValue();
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn(balance);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.deposit(100), "deposit() success");
	}
	
	/** Scenario Tested:
     *  Response for 'deposit' command when the deposit is successful and return negative balance (overdraft account).
     * 
     **/
	@Test
	void depositSuccessForNegativeBalance() {
		Double balance = -75.00;
		String expectedMsg = "Current balance: -$75.00";
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn(balance);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.deposit(100), "deposit() success for overdraft account");
	}
	
	/** Scenario Tested:
     *  Response for 'deposit' command when the deposit fails due to an invalid deposit amount (value less than or equal to 0).
     * 
     **/
	@Test
	void depositInvalidAmount() {
		String expectedMsg = "Deposit amount should be greater than 0";
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.deposit(-100), "deposit() failed for invalid amount");
	}
	
	
	 /** Scenario Tested:
     *   Response for 'deposit' command when the deposit fails due to invalid accountId or pin.
     * 
     **/
	@Test
    void depositFailedWhenUserIsNotLoggedIn() {
		
		String expectedMsg = "Authorization required.";
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(false);
        
        TransactionCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, TransactionCmdHandler.deposit(100), "deposit() failed - user not logged in");
    }
	
	
	 /** Scenario Tested:
     *  Response for 'deposit' command when the deposit fails due to the ATM server being down/unreachable.
     * 
     **/
	@Test
    void depositFailedWhenServerIsUnreachable() {
		String expectedMsg = "Failed to process the command. Service is unavailable.";
		
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(false);
		Mockito.when(mockResponseBean.getStatus()).thenReturn(500);
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.getAccountId()).thenReturn(TEST_ACCOUNT_ID);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, TransactionCmdHandler.deposit(100), "deposit() failed - service unavailable");
    }
	

}
