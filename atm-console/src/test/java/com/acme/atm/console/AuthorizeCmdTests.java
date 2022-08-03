package com.acme.atm.console;

import com.acme.atm.console.cmdhandler.APIHandler;
import com.acme.atm.console.cmdhandler.AuthorizationCmdHandler;
import com.acme.atm.console.model.SessionBean;

import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

import org.junit.jupiter.api.Test; 

public class AuthorizeCmdTests {

    private static final String TEST_ACCOUNT_ID = "2859459814";
    private static final String TEST_PIN = "7386";
    private static final String TEST_MACHINE_ID = "0000000001";
    
    private static final String AUTH_SUCCESS_MESSAGE = TEST_ACCOUNT_ID + " successfully authorized.";
    private static final String AUTH_FAILED_MESSAGE = "Authorization failed.";
    private static final String SERVICE_UNAVAILABLE_MESSAGE = " Service is unavailable.";

    /** Scenario Tested:
     *  Response for 'authorize' command when the login succeeds for the given machineId, accountId and pin.
     * 
     **/
	@Test
	void authorizationSuccess() {
		SessionBean mockSessionBean = Mockito.mock(SessionBean.class);
		Mockito.when(mockSessionBean.getToken()).thenReturn("123456789");
		Mockito.when(mockSessionBean.getAccountId()).thenReturn(TEST_ACCOUNT_ID);
		Mockito.when(mockSessionBean.isAdmin()).thenReturn(false);
		
		APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(mockApiHandler.invokeLoginAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockSessionBean);
		
		AuthorizationCmdHandler.setApiHandler(mockApiHandler);
		assertEquals(AUTH_SUCCESS_MESSAGE, AuthorizationCmdHandler.login(TEST_MACHINE_ID, TEST_ACCOUNT_ID, TEST_PIN), "Authorization login() success");
	}
	
	
	 /** Scenario Tested:
     *   Response for 'authorize' command when the login request fails due to invalid accountId or pin.
     * 
     **/
	@Test
    void authorizationFailedWhenCredentialsAreInvalid() {
        // Create a shell session bean with no token to indicate this type of failure (server returned 401)
        SessionBean mockSessionBean = Mockito.mock(SessionBean.class);
         
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.invokeLoginAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(mockSessionBean);
        
        AuthorizationCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(AUTH_FAILED_MESSAGE, AuthorizationCmdHandler.login(TEST_MACHINE_ID, TEST_ACCOUNT_ID, TEST_PIN), "Authorization login() failed - Invalid credentials");
    }
	
	
	 /** Scenario Tested:
     *  Response for 'authorize' command when the login request fails due to the ATM server being down/unreachable.
     * 
     **/
	@Test
    void authorizationFailedWhenServerIsUnreachable() {
        APIHandler mockApiHandler = Mockito.mock(APIHandler.class);
        Mockito.when(mockApiHandler.invokeLoginAPI(Mockito.anyString(), Mockito.any(Object.class))).thenReturn(null);
        
        AuthorizationCmdHandler.setApiHandler(mockApiHandler);
        assertEquals(AUTH_FAILED_MESSAGE+SERVICE_UNAVAILABLE_MESSAGE, AuthorizationCmdHandler.login(TEST_MACHINE_ID, TEST_ACCOUNT_ID, TEST_PIN), "Authorization login() failed - Server unavailable");
    }
	

}
