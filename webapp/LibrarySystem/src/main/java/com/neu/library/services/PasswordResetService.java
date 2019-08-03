package com.neu.library.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.neu.library.dao.PasswordResetDao;
import com.neu.library.response.ApiResponse;
import com.neu.library.util.ValidateUtil;


@Service
public class PasswordResetService 
{
	@Autowired
	private PasswordResetDao passwordResetDAO;
	
	@Autowired
	private ValidateUtil validUtil;
	
	
	private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
	
	
	public ResponseEntity<Object> sendResetEmail (String email)
	{
		logger.info("Sending reset email:::"+email);
		if(!validUtil.varifyEmail(email))
		{
			ApiResponse apiError = new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid syntax for this request was provided.", "Not an valid email address");
			logger.error("Email validation failed");
			return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);
		}
		else if (!this.checkIfUserExists(email))
		{
			ApiResponse apiError = new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid syntax for this request was provided.", "User does not exist");
			logger.error("User does not exist");
			return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);
		}
		else {
			passwordResetDAO.sendEmailToUser(email);
			logger.info("Email successfully sent");
			return new ResponseEntity<Object>(null, HttpStatus.CREATED);
		}
		
	}
	
	public boolean checkIfUserExists(String userName) 
	{
		logger.info("Checking if user exists already exists in database");
		int count = this.passwordResetDAO.checkIfUserExists(userName);
		if (count > 0)
			return true;
		else
			return false;

	}

}

