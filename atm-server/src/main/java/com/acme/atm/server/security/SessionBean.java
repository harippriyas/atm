package com.acme.atm.server.security;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionBean {
	private String token;
	private String accountId;
	private String machineId;
	private boolean admin;
	private long lastAccess;
	
	// This field is only to deserialize the login JSON input. Cleared once user is validated.
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String pin; 
	
	
	public void generateToken()	{
		this.token = UUID.randomUUID().toString();
	}
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
	public void updateLastAccess()
	{
		this.lastAccess = Calendar.getInstance().getTimeInMillis();
	}
	@JsonIgnore
	public boolean isSessionActive()
	{
		// Inactivity timeout: 2 mins
		long minLastActivity = Calendar.getInstance().getTimeInMillis() - (2*60*1000);
		return (this.lastAccess >= minLastActivity);			
	}
	
	@JsonIgnore
	public String getLastAccessTimestamp() {
		return (new Timestamp(getLastAccess())).toString();
	}
	
	public String toJson()
	{
		String jsonStr;
		try
		{
			ObjectMapper om = new ObjectMapper();
			StringWriter sw = new StringWriter();
			om.writeValue(sw, this);
	
			jsonStr = sw.getBuffer().toString();
		}
		catch(Exception e)
		{
			jsonStr ="{\"message\":\"Unexpected error - " + e.getMessage() + "\"}";
		}
		
		return jsonStr;
	}
	
}
