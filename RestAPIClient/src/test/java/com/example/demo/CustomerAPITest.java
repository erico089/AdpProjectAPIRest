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
    public static String fakeToken;


    static {
        adminToken = JWTHelper.createToken("com.webage.auth.apis");
        userToken = JWTHelper.createToken("com.webage.data.apis"); 
        fakeToken = "SillyToken";
    }

    @Test
    public void testGetCustomerById_AdminToken_Success() {
        Integer customerId = 1;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Eric Casanovas");
        customer.setEmail("eric.casanovas@example.com");

        when(customerRepository.findById((long) customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = customerAPI.getCustomerById(adminToken, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    public void testGetCustomerById_FakeToken_NotAuthorizated() {
        Integer customerId = 1;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Eric Casanovas");
        customer.setEmail("eric.casanovas@example.com");

        when(customerRepository.findById((long) customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = customerAPI.getCustomerById(fakeToken, customerId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetCustomerById_NonAdminToken_Succes() {
        Integer customerId = 1;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Eric Casanovas");
        customer.setEmail("eric.casanovas@example.com");

        when(customerRepository.findById((long) customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = customerAPI.getCustomerById(userToken, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    public void testGetCustomerById_NotFound() {
        
        long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = customerAPI.getCustomerById(adminToken, customerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateCustomer_AdminToken_Success() {

        Customer customer = new Customer();
        customer.setName("Eric Casanovas");
        customer.setEmail("eric.casanovas@example.com");
        customer.setPassword("password");

        when(customerRepository.findOldestCustomerId()).thenReturn(Optional.of(1));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
   
        ResponseEntity<?> response = customerAPI.createCustomer(adminToken, customer);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    public void testCreateCustomer_FakeToken_NoAuth() {

        Customer customer = new Customer();
        customer.setName("Eric Casanovas");
        customer.setEmail("eric.casanovas@example.com");
        customer.setPassword("password");

        when(customerRepository.findOldestCustomerId()).thenReturn(Optional.of(1));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
   
        ResponseEntity<?> response = customerAPI.createCustomer(fakeToken, customer);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testCreateCustomer_NonAdminToken_NoAuth() {

        Customer customer = new Customer();
        customer.setName("Eric Casanovas");
        customer.setEmail("eric.casanovas@example.com");
        customer.setPassword("password");

        when(customerRepository.findOldestCustomerId()).thenReturn(Optional.of(1));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
   
        ResponseEntity<?> response = customerAPI.createCustomer(userToken, customer);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

    @Test
    public void testCreateCustomer_BadRequest() {
        
        Customer customer = new Customer();

        ResponseEntity<?> response = customerAPI.createCustomer(adminToken, customer);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

