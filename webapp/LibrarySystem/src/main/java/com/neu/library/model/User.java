package com.neu.library.model;

import javax.persistence.*;
@Entity
public class User {
	@Id
	@GeneratedValue
	private long id;
	
	@Column(name= "username")
	private String username;
	
	@Column
	private String password;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	

}
