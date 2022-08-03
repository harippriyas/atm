package com.acme.atm.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.acme.atm.server.model.ResponseBean;
import com.acme.atm.server.security.AuthHandler;
import com.acme.atm.server.security.SessionBean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/atm/auth/v1")
@Api( 	value = "Authorization",
		tags="Authorization")
public class AuthenticationAPI
{

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/login", method = RequestMethod.POST, produces = {"application/JSON"})
    @ApiOperation( value = "Login with username and password", response = String.class,
                   notes = "This API is invoked by the ATM console application. " 
                           +"It returns an accessToken that can be used with subsequent API calls by the console application.<br><br>"
    					   +"Credentials must be passed in as JSON in the request body:<br>" 
                		   + "<pre style='background-color:white; color:black;'>"
                		   + "{<br/>"
                		   + "\"accountId\": \"2859459814\",<br/>"
                		   + "\"pin\": \"7386\",<br/>"
                		   + "\"machineId\": \"0000000001\" -- Change the quotes.<br/>"
                		   + "}</pre><br/>"
                		   + "Ensure that the JSON parameter names and values are enclosed in standard \" \" quotes.")
    @ApiResponses(value = {	@ApiResponse(code = 200, message = "Login success"),
    						@ApiResponse(code = 401, message = "Invalid credentials")})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "credentials", dataType = "string", paramType = "body")
	  })
    public ResponseEntity<?> login()
    {
    	// if we reached here, then this is a valid user.
		String accessToken = AuthHandler.getAuthTokenFromThreadLocal();
		SessionBean responseBean = AuthHandler.getSessionBean(accessToken);
		return new ResponseEntity(responseBean.toJson(), HttpStatus.OK);
    }
	
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ApiOperation( value = "Logout to end the session", response = String.class, notes="This API is invoked by the console to terminate the session.")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "<access_token>")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Logout success")})
	public ResponseEntity logout()
	{
		AuthHandler.endSession(AuthHandler.getAuthTokenFromThreadLocal());
		
		ResponseBean bean = new ResponseBean();
		bean.setMessage("Logout success");
		
		return new ResponseEntity(bean.toJson(), HttpStatus.OK);
    }


}

