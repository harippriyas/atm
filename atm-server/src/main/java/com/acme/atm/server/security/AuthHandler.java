package com.acme.atm.server.security;


import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.acme.atm.server.dao.DBService;
import com.acme.atm.server.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class AuthHandler {

	private static final HashMap<String, SessionBean> sessionMap = new HashMap<>();
	
	public static boolean isValidUser(HttpServletRequest httpRequest) 
    {
    	boolean isValid = false;
    	SessionBean sessionObj = null;
    	String requestURI = httpRequest.getRequestURI();
    	
    	boolean isLogoutRequest = requestURI.contains("/logout");
    	if(isLogoutRequest)
    	{
    		// Just return true so we can invalidate the token even if it has expired.
        	return true;
    	}
    	
    	final String authorization = httpRequest.getHeader("Authorization");
    	if (authorization != null)
    	{
    			// Logged in with access token. Check if valid.
    			sessionObj = sessionMap.get(authorization.trim());
    			if(sessionObj != null && sessionObj.isSessionActive()) {
    				sessionObj.updateLastAccess();
    				setSessionBean(sessionObj);
    				isValid = true;
    			}
    	}
		else
		{
			// logged in through API.
			try
			{
				String credentialsJson = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
				if(credentialsJson!= null && credentialsJson.contains("{"))
				{
					ObjectMapper mapper = new ObjectMapper();
					SessionBean authInput = mapper.readValue(credentialsJson, SessionBean.class);	
					sessionObj = doValidate(authInput, DBService.getInstance());
					if(sessionObj != null) 
					{
						setSessionBean(sessionObj);
						isValid = true;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
    	
    	return isValid;
        
    }

	public static SessionBean doValidate(SessionBean authInput, DBService dbService) {
		SessionBean sessionObj = null;
		Optional<Account> accountObj = dbService.getAccount(authInput.getAccountId());
		// Validate the account exists and the PIN is correct. 
		if(accountObj.isPresent() && 
			accountObj.get().getPin().equals(authInput.getPin()) &&
			!accountObj.get().isAtmMachine()) 
		{
			// Validate that the ATM machine ID is correct.
			// This is not a user error. The docker container might have been started with an invalid machine ID
			Optional<Account> atmObj = dbService.getAccount(authInput.getMachineId());
			if(atmObj.isPresent()) {
				sessionObj = new SessionBean();
				sessionObj.setAccountId(accountObj.get().getAccountId());
				sessionObj.setAdmin(accountObj.get().isAdmin());
				sessionObj.setMachineId(authInput.getMachineId());
				sessionObj.generateToken();
				sessionObj.updateLastAccess();
			}
		}
		return sessionObj;
	}
	
 	public static void endSession(String accessToken)
	{
		if(accessToken != null && sessionMap.containsKey(accessToken))
			sessionMap.remove(accessToken);
	}
	
 	public static SessionBean getSessionBean(String token) {
 		return sessionMap.get(token);
 	}
 	
 	public static void setSessionBean(SessionBean sessionObj) {
 		sessionMap.put(sessionObj.getToken(), sessionObj);
 		setAuthTokenInThreadLocal(sessionObj.getToken());
 	}
 	
 	public static String getAuthTokenFromThreadLocal() {
 		return MDC.get("authtoken");
 	}
 	
 	public static void setAuthTokenInThreadLocal(String token) {
 		MDC.put("authtoken", token);
 	}
}
