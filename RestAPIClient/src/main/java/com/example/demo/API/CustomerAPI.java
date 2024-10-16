package com.example.demo.API;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.example.demo.JWTHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;


import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;

@RestController
@RequestMapping("/customers")
public class CustomerAPI {

	@Autowired
	private CustomerRepository customerRepository;

	@GetMapping
	public ResponseEntity<?> getAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

		token = fixHeaderToken(token);
		if (!JWTHelper.verifyToken(token)){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		try {
			Iterable<Customer> customers = customerRepository.findAll();
			return ResponseEntity.ok( customers );
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}


	@GetMapping("/{customerId}")
	public ResponseEntity<?> getCustomerById(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,@PathVariable("customerId") long id) {
		
		token = fixHeaderToken(token);
		if (!JWTHelper.verifyToken(token)){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
		return customerRepository.findById(id)
		.map(ResponseEntity::ok)
		.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/byname/{username}")
	public ResponseEntity<?> getCustomerByName(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,@PathVariable("username") String username) {
		
		token = fixHeaderToken(token);
		if (!JWTHelper.verifyToken(token)){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
		try {
			Optional<Customer> customerOptional = customerRepository.findByUsername(username);

			if (customerOptional.isPresent()) {
				return ResponseEntity.ok(customerOptional.get());
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("A user with this username doesn't exists");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("A user with this username doesn't exists");
		}
	}

	@PostMapping
    public ResponseEntity<?> createCustomer(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,@RequestBody Customer customer) {
		
		token = fixHeaderToken(token);
		if (!JWTHelper.verifyToken(token)){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		if(!isAdmin(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user for this operation");
		}

		if (customer == null || customer.getName() == null || customer.getName().isEmpty() || 
			customer.getEmail() == null || customer.getEmail().isEmpty() || customer.getPassword() == null || customer.getPassword().isEmpty()) {
			
			return ResponseEntity.badRequest().build();
		}

		try {
			Optional<Integer> oldestId = customerRepository.findOldestCustomerId();
		
			if (oldestId.isPresent()) {
				customer.setId(oldestId.get() + 1);
			} else {
				customer.setId(1);
			}

			try {
				customerRepository.save(customer);
				return ResponseEntity.status(HttpStatus.CREATED).body(customer);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

    }

	@DeleteMapping("/{customerId}")
	public ResponseEntity<?> deleteCustomerById(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,@PathVariable("customerId") long id) {
		
		token = fixHeaderToken(token);
		if (!JWTHelper.verifyToken(token)){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		if(!isAdmin(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user for this operation");
		}

		try {
			Optional<Customer> deletedCustomerOptional = customerRepository.findById(id);

			if (deletedCustomerOptional.isPresent()) {

				try {
					Customer deletedCustomer = deletedCustomerOptional.get();
					customerRepository.delete(deletedCustomer);
					return ResponseEntity.ok(deletedCustomer);

				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Customer deletion error");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,@PathVariable("id") Long id, @RequestBody Customer updatedCustomer) {
		
		token = fixHeaderToken(token);
		if (!JWTHelper.verifyToken(token)){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
		if(!isAdmin(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user for this operation");
		}

		if (updatedCustomer.getEmail().isEmpty() && updatedCustomer.getName().isEmpty()) {
			return ResponseEntity.badRequest().body("Both email and name are empty");
		}
		try {
			Optional<Customer> customerOptional = customerRepository.findById(id);

			if(customerOptional.isPresent()) {
				
				Customer customer = customerOptional.get();

				if (customer.getEmail().equals(updatedCustomer.getEmail()) && customer.getName().equals(updatedCustomer.getName())) {
					return ResponseEntity.badRequest().body("Both email and name are the same, no update needed!"); 
				}

				if (!updatedCustomer.getName().isEmpty()) {
					customer.setName(updatedCustomer.getName());
				}
				if (!updatedCustomer.getEmail().isEmpty()) {
					customer.setEmail(updatedCustomer.getEmail());
				}

				try {
					customerRepository.save(customer);
					return ResponseEntity.ok(customer);
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User with ID: "+id+" cannot be update!");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID: "+id+" not found! 1");
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID: "+id+" not found! 2");
		}
    }

	private String fixHeaderToken(String token) {
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7); 
		}
		return token;
	}

	private boolean isAdmin(String token){
		Map<String,Claim> claims = JWTHelper.getClaims(token);
		if (claims == null) {
            return false;
        }
		
        Claim scopeClaim = claims.get("scopes"); 
        if (scopeClaim == null || !"com.webage.auth.apis".equals(scopeClaim.asString())) {
            return false;
        }
		return true;
	}

}
