package com.example.demo.API;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;

@RestController
@RequestMapping("/customers")
public class CustomerAPI {

	@Autowired
	private CustomerRepository customerRepository;

	@GetMapping
	public ResponseEntity<Iterable<Customer>> getAll() {
		try {
			Iterable<Customer> customers = customerRepository.findAll();
			return ResponseEntity.ok(customers);
		} catch (Exception e) {
			// Aquí puedes registrar el error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}


	@GetMapping("/{customerId}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable("customerId") long id) {
		return customerRepository.findById(id)
		.map(ResponseEntity::ok)
		.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
		if (customer == null || customer.getName() == null || customer.getName().isEmpty() || 
			customer.getEmail() == null || customer.getEmail().isEmpty() || customer.getPassword() == null || customer.getPassword().isEmpty()) {
			

			return ResponseEntity.badRequest().build();
		}

		// Optional<Long> oldestId = customerRepository.findOldestCustomerId();
    
		// if (oldestId.isPresent()) {
		// 	customer.setId(oldestId.get());
		// } else {
		// 	customer.setId(0);
		// }

        customerRepository.save(customer);

		return ResponseEntity.status(HttpStatus.CREATED).body(customer);

    }

	@DeleteMapping("/{customerId}")
	public String deleteCustomerById(@PathVariable("customerId") long id) {
		
		// get a user from the db

		// delete it from the db

		return "User with id "+ id + "succesfully deleted!";
	}
	
	// @PutMapping("/{id}")
    // public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
    //     Customer customer = getCustomerById(id);
    //     if (customer != null) {
    //         customer.setName(updatedCustomer.getName());
    //         customer.setEmail(updatedCustomer.getEmail());
    //     }
    //     return customer;
    // }

}
