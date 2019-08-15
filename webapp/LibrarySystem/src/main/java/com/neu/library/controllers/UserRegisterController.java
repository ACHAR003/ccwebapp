package com.neu.library.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.neu.library.json.UserCredential;
import com.neu.library.services.StatMetric;
import com.neu.library.services.UserRegisterService;


@RestController
public class UserRegisterController {

	
	@Autowired
	UserRegisterService registerService;
	@Autowired
	StatMetric statsMetric;

	private static final Logger logger = LoggerFactory.getLogger(UserRegisterController.class);
	
	@RequestMapping(value="/user/register" ,method=RequestMethod.POST)
	public ResponseEntity<Object>registerUser(@Valid @RequestBody UserCredential cred){
		logger.info("Adding user");
		statsMetric.increementStat("POST /user/register/");
		logger.info("User added");
		return registerService.registerUser(cred);
		
	}
	
}
