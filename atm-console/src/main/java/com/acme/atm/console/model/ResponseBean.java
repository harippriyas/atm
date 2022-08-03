package com.acme.atm.console.model;

import java.net.HttpURLConnection;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** API response bean returned by the ATM server.
 * 	TODO: Consider moving this bean to a common project for use by both projects.
 * 
 * 	@author SHarippriya
 *
 */
public class ResponseBean 
{
	/** Response HTTP status like 400 (for invalid input), 200 (for success), etc. */
	private int status = HttpURLConnection.HTTP_OK; // default to 200 OK. will be overwritten if there is an error
	
	/** Response message string. Optional as it depends on the API */
	private String	message;
	
	/** Response data returned by the API. Optional as it depends on the API */
	private Object data;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int responseCode) {
		this.status = responseCode;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Object getData() {
		return data;
	}
	public void setData(Object bean) {
		this.data = bean;
	}
	
	@JsonIgnore
	public boolean isSuccess() {
		return (this.getStatus() == HttpURLConnection.HTTP_OK);
	}

}
