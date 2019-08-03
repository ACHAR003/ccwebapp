package com.neu.library.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.neu.library.services.PasswordResetService;
import com.neu.library.services.StatMetric;



@RestController
public class PasswordResetController 
{
	
	private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
	
	@Autowired
	StatMetric statMetric;
	
	@Autowired
	PasswordResetService passwordResetService;
	
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public ResponseEntity<Object> registerUser(@Valid @RequestBody  Map<String, String>  email) {
		
		logger.info("Resetting password for user");
		statMetric.increementStat("POST /reset");
		
		Map.Entry<String,String> entry = email.entrySet().iterator().next();
		
		String value = entry.getValue();
		
		return this.passwordResetService.sendResetEmail(value);
	}

}
