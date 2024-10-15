package com.example.demo.repository;

import java.util.Optional;
import com.example.demo.model.Customer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @Query("SELECT MAX(c.id) FROM Customer c")
    Optional<Integer> findOldestCustomerId();

    @Query("SELECT c FROM Customer c WHERE c.name = ?1")
    Optional<Customer> findByUsername(String username);
}
