package com.neu.library.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import ch.qos.logback.classic.Logger;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	CustomAuthenticationProvider customAuthenticationProvider;


	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpSecurity.httpBasic().and().authorizeRequests().antMatchers("/").authenticated();
		httpSecurity.httpBasic().and().authorizeRequests().antMatchers("/book").authenticated();
		httpSecurity.csrf().disable();
		//Logger.info("in101");
		
	}

	public void configure(WebSecurity web) throws Exception 
	{
		web.ignoring().antMatchers(HttpMethod.POST, "/user/register/");
		web.ignoring().antMatchers(HttpMethod.POST, "/reset");
}
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	  auth.authenticationProvider(customAuthenticationProvider);
	}
}
