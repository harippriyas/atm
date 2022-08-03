package com.acme.atm.server.api;

import org.springframework.http.HttpStatus;

import com.acme.atm.server.model.ResponseBean;

/** Helper class to format the API responses */
public class GenericUtil {

	public static HttpStatus getStatus(ResponseBean bean)
	{
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		try
		{
			httpStatus = HttpStatus.valueOf(bean.getStatus());
		}
		catch(Exception e) {
			// This is unlikely as the status is set explicitly in code to be a valid HTTP status and has reasonable defaults.
			//e.printStackTrace();
		}

		return httpStatus;
	}

	public static ResponseBean buildExceptionResponse(String message, Exception e)
	{
		ResponseBean responseBean = new ResponseBean();
		responseBean.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		responseBean.setMessage(message );		
		return responseBean;
	}


}
