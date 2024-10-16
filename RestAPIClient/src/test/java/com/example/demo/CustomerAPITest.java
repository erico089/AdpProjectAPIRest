package com.example.demo;

import com.example.demo.API.CustomerAPI;
import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerAPITest {

    @InjectMocks
    private CustomerAPI customerAPI;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    public static String adminToken;
    public static String userToken;


    static {
        adminToken = JWTHelper.createToken("com.webage.auth.apis");
        userToken = JWTHelper.createToken("com.webage.data.apis"); 
    }

    @Test
    public void testGetCustomerById_Success() {
        // Arrange
        Integer customerId = 1;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");

        when(customerRepository.findById((long) customerId)).thenReturn(Optional.of(customer));

        // Act
        ResponseEntity<Customer> response = customerAPI.getCustomerById("Bearer token", customerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    public void testGetCustomerById_NotFound() {
        // Arrange
        long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Customer> response = customerAPI.getCustomerById("Bearer token", customerId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateCustomer_Success() {
        // Arrange
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane.doe@example.com");
        customer.setPassword("password");

        when(customerRepository.findOldestCustomerId()).thenReturn(Optional.of(1));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        ResponseEntity<?> response = customerAPI.createCustomer(JWTHelper.createToken("com.webage.auth.apis"), customer);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    public void testCreateCustomer_BadRequest() {
        // Arrange
        Customer customer = new Customer(); // Missing required fields

        // Act
        ResponseEntity<?> response = customerAPI.createCustomer("Bearer token", customer);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

