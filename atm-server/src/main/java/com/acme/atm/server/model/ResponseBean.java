package com.acme.atm.server.model;

import java.io.StringWriter;
import java.net.HttpURLConnection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseBean 
{
	private int status = HttpURLConnection.HTTP_OK; // default to 200 OK. will be overwritten if there is an error
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String	message;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
