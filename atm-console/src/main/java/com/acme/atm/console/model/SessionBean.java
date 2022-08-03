package com.acme.atm.console.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Bean used for Login request. This serves as the data sent to and received from the Login API.
 * 	TODO: Consider moving this bean to a common project for use by both projects.
 * 
 * 	@author SHarippriya
 *
 */
public class SessionBean {
	
	//////////////////// LOGIN API INPUT  ////////////////////
	/** AccountID of the logged in user */
	private String accountId;
	
	/** ATM machine ID from where the login was attempted */
	private String machineId;
	
	// This field is only to deserialize the login JSON input. Cleared once user is validated.
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String pin; 
		
	//////////////////// LOGIN API Response  ////////////////////
	/** Access token for the successful login. This token must be used for subsequent API calls. */
	private String token;
	
	/** Flag to indicate if the logged in user is admin */
	private boolean admin;
	
	/** Time of last access (can be ignored for now) */
	private long lastAccess;
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String password) {
		this.pin = password;
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String id) {
		this.machineId = id;
	}
	public long getLastAccess() {
		return lastAccess;
	}
	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}
		
}
