package com.acme.atm.console.cmdhandler;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.acme.atm.console.model.ResponseBean;
import com.acme.atm.console.model.SessionBean;

/** Helper class that has functions to invoke the ATM server REST APIs to perform the console operations.
 * 
 * @author SHarippriya
 *
 */
public class APIHandler {
	
	// It is efficient to create the client and reuse it
	private Client client = ClientBuilder.newClient();
	
	// These parameters can be overwritten by specifying them as env variables.
	private String serverBaseUrl = "http://localhost:9090";
	private String machineId = "0000000001";
	
	// NOTE: This application mimics an ATM machine and hence there can be only one active user at a given time.
	// The following details are obtained after a user logs in.
	
	// Access token for accessing the server APIs
	private String accessToken = null;
	
	// Account ID of the user who is currently authorized
	private String accountId = null;
	
	// Flag to indicate if the user has admin privileges (for future use)
	private boolean adminAccount = false;
	
	
	// Singleton instance
	private static class InstanceHolder {
		private static final APIHandler instance = new APIHandler();
	}

	public static APIHandler getInstance() {
		return InstanceHolder.instance;
	}

	protected APIHandler()
	{
		super();
	}

	/** Invokes the specified GET API URL of the atm server application.
	 * 
	 * 	@param url				Server API URL
	 * 	@return	ResponseBean	Response returned by the server API
	 */
	public ResponseBean invokeGetAPI(String url) {
		ResponseBean responseBean = new ResponseBean();
		try {
			Response apiResponse = client.target(serverBaseUrl + url)
													.request(MediaType.APPLICATION_JSON)
													.header("Authorization", getAccessToken())
													.get();
			if(apiResponse.getStatus() == 200) {
				responseBean = apiResponse.readEntity(ResponseBean.class);
			}
			else {
				responseBean.setStatus(apiResponse.getStatus());
			}
		} 
		catch (Exception e) {
			responseBean.setStatus(500);
			responseBean.setMessage(e.getMessage());
		}
		return responseBean;
	}
	
	/** Invokes the specified POST API URL of the atm server application.
	 * 
	 * 	@param url				Server API URL
	 *  @param input			API input object
	 * 	@return	ResponseBean	Response returned by the server API
	 */
	public ResponseBean invokePostAPI(String url, Object input) {
		ResponseBean responseBean = new ResponseBean();
		try {
			Response apiResponse = client.target(serverBaseUrl + url)
													.request(MediaType.APPLICATION_JSON)
													.header("Authorization", getAccessToken())
													.post(Entity.entity(input, MediaType.APPLICATION_JSON));
			if(apiResponse.getStatus() == 200) {
				responseBean = apiResponse.readEntity(ResponseBean.class);
			}
			else {
				responseBean.setStatus(apiResponse.getStatus());
			}
		} 
		catch (Exception e) {
			responseBean.setStatus(500);
			responseBean.setMessage(e.getMessage());
		}
		return responseBean;
	}

	
	/** Invokes the login API of the atm server application.
	 * 
	 * 	@param url				Login API URL
	 *  @param input			Login input object
	 * 	@return	SessionBean	Response returned by the server API
	 */
	public SessionBean invokeLoginAPI(String url, Object input) {
		SessionBean sessionBean = null;
		try {
			Response apiResponse = client.target(serverBaseUrl + url)
													.request(MediaType.APPLICATION_JSON)
													.post(Entity.entity(input, MediaType.APPLICATION_JSON));
			if(apiResponse.getStatus() == 200) {
				sessionBean = apiResponse.readEntity(SessionBean.class);
			}
			else if(apiResponse.getStatus() == 401) {
				// create a session bean without token so that the invoker can distinguish between 401 and 404
				sessionBean = new SessionBean();
			}
		} 
		catch (Exception e) {
//			e.printStackTrace();
		}
		return sessionBean;
	}

	public boolean isUserAuthorized() {
    	return (getAccessToken() != null);
    }
	
	public String getServerBaseUrl() {
		return serverBaseUrl;
	}

	public void setServerBaseUrl(String serverBaseUrl) {
		this.serverBaseUrl = serverBaseUrl;
	}

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public boolean isAdminAccount() {
		return adminAccount;
	}

	public void setAdminAccount(boolean adminAccount) {
		this.adminAccount = adminAccount;
	}

}
