package com.example.demo.model;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "CUSTOMERS")
public class Customer {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(name="CUSTOMER_NAME")
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

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                Objects.equals(name, customer.name) &&
                Objects.equals(password, customer.password) &&
                Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, email);
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

