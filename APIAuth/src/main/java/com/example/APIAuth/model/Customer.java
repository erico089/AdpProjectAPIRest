package com.example.APIAuth.model;

public class Customer {

	Integer id;

	String name;

	String password;

	String email;

	public Customer() {
    }
	
	public Customer(Integer id, String name, String password, String email) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.email = email;
	}

	public String toJSON(){
		return "{\"id\":" + id + ", \"name\":\"" + name + "\", \"password\":\"" + password + "\", \"email\":\"" + email + "\" }";
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}

