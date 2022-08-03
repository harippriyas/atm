package com.acme.atm.server.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.acme.atm.server.dao.DBService;
import com.acme.atm.server.model.ResponseBean;
import com.acme.atm.server.model.TransactionHistory;
import com.acme.atm.server.model.WithdrawCmdResponse;
import com.acme.atm.server.security.AuthHandler;
import com.acme.atm.server.security.SessionBean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/atm/api/v1/transactions")
@Api(value = "Transactions", tags = "Transactions")
public class TransactionAPI {
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/balance", method = RequestMethod.GET, produces = { "application/JSON" })
	@ApiOperation(value = "Get the account balance.", response = ResponseBean.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success. <br/>See above for other common response codes.") })
	public ResponseEntity<?> getBalance(
			@ApiParam(value = "Authorization") @RequestHeader("Authorization") String accessToken) {
		ResponseBean responseBean = new ResponseBean();
		try {
			SessionBean sessionBean = AuthHandler.getSessionBean(accessToken);
			TransactionAPIHandler handler = new TransactionAPIHandler();
			responseBean = handler.processBalanceCheck(sessionBean.getAccountId(), DBService.getInstance());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(responseBean.getData() == null)
			responseBean.setData(0.0);
		
		return new ResponseEntity(responseBean.toJson(), GenericUtil.getStatus(responseBean));
	}


	@SuppressWarnings("unchecked")
	@RequestMapping(value="/deposit", method = RequestMethod.POST, produces = { "application/JSON" })
	@ApiOperation(value = "Deposit money into the account.", response = ResponseBean.class)
	public ResponseEntity<?> depositAmount(
			@ApiParam(value = "Deposit amount") @RequestBody double amount,
			@ApiParam(value = "Authorization") @RequestHeader("Authorization") String accessToken) {
		ResponseBean responseBean = new ResponseBean();
		try {
			SessionBean sessionBean = AuthHandler.getSessionBean(accessToken);
			TransactionAPIHandler handler = new TransactionAPIHandler();
			responseBean = handler.processDeposit(sessionBean.getAccountId(), DBService.getInstance(), amount);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity(responseBean.toJson(), GenericUtil.getStatus(responseBean));
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/withdraw", method = RequestMethod.POST, produces = { "application/JSON" })
	@ApiOperation(value = "Withdraw money from the account.", response = ResponseBean.class)
	public ResponseEntity<?> withdrawAmount(
			@ApiParam(value = "Withdrawal amount") @RequestBody double amount,
			@ApiParam(value = "Authorization") @RequestHeader("Authorization") String accessToken) {
		ResponseBean responseBean = new ResponseBean();
		try {
			SessionBean sessionBean = AuthHandler.getSessionBean(accessToken);
			TransactionAPIHandler handler = new TransactionAPIHandler();
			WithdrawCmdResponse withdrawResponse = handler.processWithdrawal(sessionBean, DBService.getInstance(), amount);
			
			StringBuilder responseStr = new StringBuilder();
			if(withdrawResponse == null) {
				responseStr.append("Withdrawal amount must be greater than 0.");
			}
			else if(withdrawResponse.isOverdraftState()) {
				responseStr.append("Your account is overdrawn! You may not make withdrawals at this time.");
    		}
    		else if(withdrawResponse.isInsufficientAtmBalance() && withdrawResponse.getAmtDispensed() == 0) {
    			responseStr.append("Unable to process your withdrawal at this time.");
    		}
    		else {
    			if(withdrawResponse.isInsufficientAtmBalance()) {
    				responseStr.append("Unable to dispense full amount requested at this time.\n");
    			}
    			responseStr.append("Amount dispensed: $");
    			responseStr.append(withdrawResponse.getAmtDispensed());
    			responseStr.append("\n");
    			
    			if(withdrawResponse.isOverdraftFeeCharged()) {
    				responseStr.append("You have been charged an overdraft fee of $5. Current balance: -$");
    				responseStr.append(withdrawResponse.getNewBalance() * -1.0); // remove the minus for display purpose
    			}
    			else {
    				responseStr.append("Current balance: $");
    				responseStr.append(withdrawResponse.getNewBalance());
    			}
    		}
			
			responseBean.setData(responseStr.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity(responseBean.toJson(), GenericUtil.getStatus(responseBean));
	}

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/history", method = RequestMethod.GET, produces = { "application/JSON" })
	@ApiOperation(value = "List of account transactions.", response = ResponseBean.class)
	public ResponseEntity<?> getHistory(
			@ApiParam(value = "Authorization") @RequestHeader("Authorization") String accessToken) {
		ResponseBean responseBean = new ResponseBean();
		try {
			SessionBean sessionBean = AuthHandler.getSessionBean(accessToken);
			TransactionAPIHandler handler = new TransactionAPIHandler();
			List<TransactionHistory> txnList = handler.getTransactionHistory(sessionBean.getAccountId(), DBService.getInstance());
			StringBuilder responseStr = new StringBuilder();
			if(txnList != null && !txnList.isEmpty()) {
				txnList.forEach(e -> {
					responseStr.append(e.getTransactionTimestamp());
					responseStr.append(" ");
					responseStr.append(e.getAmount());
					responseStr.append(" ");
					responseStr.append(e.getBalance());
					responseStr.append("\n");
				});
			}
			responseBean.setData(responseStr.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity(responseBean.toJson(), HttpStatus.OK);
	}

}
