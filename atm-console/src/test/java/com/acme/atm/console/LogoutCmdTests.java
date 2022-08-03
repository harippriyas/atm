package com.acme.atm.console;

import com.acme.atm.console.cmdhandler.APIHandler;
import com.acme.atm.console.cmdhandler.AuthorizationCmdHandler;
import com.acme.atm.console.model.ResponseBean;

import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

import org.junit.jupiter.api.Test; 

public class LogoutCmdTests {

    private static final String TEST_ACCOUNT_ID = "2859459814";

    /** Scenario Tested:
     *  Response for 'logout' command when the logout succeeds.
     * 
     **/
	@Test
	void logoutSuccess() {
		String expectedMsg = "Account " + TEST_ACCOUNT_ID + " logged out.";
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(true);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.getAccountId()).thenReturn(TEST_ACCOUNT_ID);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.eq(null))).thenReturn(mockResponseBean);
		
		AuthorizationCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(expectedMsg, AuthorizationCmdHandler.logout(), "logout() success");
	}
	
	
	 /** Scenario Tested:
     *   Response for 'authorize' command when the login request fails due to invalid accountId or pin.
     * 
     **/
	@Test
    void logoutFailedWhenUserIsNotLoggedIn() {
		
		String expectedMsg = "No account is currently authorized.";
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(false);
        
        AuthorizationCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, AuthorizationCmdHandler.logout(), "logout() failed - user not logged in");
    }
	
	
	 /** Scenario Tested:
     *  Response for 'authorize' command when the login request fails due to the ATM server being down/unreachable.
     * 
     **/
	@Test
    void logoutFailedWhenServerIsUnreachable() {
		String expectedMsg = "Failed to logout. Service is unavailable.";
		
		ResponseBean mockResponseBean = Mockito.mock(ResponseBean.class);
		Mockito.when(mockResponseBean.isSuccess()).thenReturn(false);
		
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.isUserAuthorized()).thenReturn(true);
		Mockito.when(mockApiHandler.getAccountId()).thenReturn(TEST_ACCOUNT_ID);
		Mockito.when(mockApiHandler.invokePostAPI(Mockito.anyString(), Mockito.eq(null))).thenReturn(mockResponseBean);
		
        AuthorizationCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(expectedMsg, AuthorizationCmdHandler.logout(), "logout() failed - service unavailable");
    }
	

}
