package com.acme.atm.console;

import com.acme.atm.console.cmdhandler.APIHandler;
import com.acme.atm.console.cmdhandler.TransactionCmdHandler;
import com.acme.atm.console.model.ResponseBean;

import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

import org.junit.jupiter.api.Test; 

public class TransactionHistoryCmdTests {

    private static final String TEST_ACCOUNT_ID = "2859459814";

    /** Scenario Tested:
     *  Response for 'deposit' command when the deposit is successful and return positive balance.
     * 
     **/
	@Test
	void historySuccess() {
		String expectedMsg = "2020-02-04 13:04:22 -20.00 140.67";
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn(expectedMsg);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokeGetAPI(Mockito.anyString())).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.getTransactionHistory(), "getTransactionHistory() success");
	}
	
	/** Scenario Tested:
     *  Response for 'deposit' command when the deposit is successful and return negative balance (overdraft account).
     * 
     **/
	@Test
	void historyNotAvailable() {
		String expectedMsg = "No history found.";
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		Mockito.when(mockResponseBean.getData()).thenReturn("");
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.invokeGetAPI(Mockito.anyString())).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, TransactionCmdHandler.getTransactionHistory(), "getTransactionHistory() success for overdraft account");
	}
	
	/** Scenario Tested:
     *   Response for 'deposit' command when the deposit fails due to invalid accountId or pin.
     * 
     **/
	@Test
    void historyCmdFailedWhenUserIsNotLoggedIn() {
		
		String expectedMsg = "Authorization required.";
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(false);
        
        TransactionCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, TransactionCmdHandler.getTransactionHistory(), "getTransactionHistory() failed - user not logged in");
    }
	
	
	 /** Scenario Tested:
     *  Response for 'deposit' command when the deposit fails due to the ATM server being down/unreachable.
     * 
     **/
	@Test
    void historyCmdFailedWhenServerIsUnreachable() {
		String expectedMsg = "Failed to process the command. Service is unavailable.";
		
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(false);
		Mockito.when(mockResponseBean.getStatus()).thenReturn(500);
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.getAccountId()).thenReturn(TEST_ACCOUNT_ID);
		Mockito.when(mockApiHandler.invokeGetAPI(Mockito.anyString())).thenReturn(mockResponseBean);
		
		TransactionCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, TransactionCmdHandler.getTransactionHistory(), "getTransactionHistory() failed - service unavailable");
    }
	

}
