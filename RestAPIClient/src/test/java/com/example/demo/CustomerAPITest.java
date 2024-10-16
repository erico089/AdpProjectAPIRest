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

import java.util.Arrays;
import java.util.List;
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

    @Test
    public void testGetAllCustomers_AdminToken_Success() {
        List<Customer> customers = Arrays.asList(
            new Customer(1, "Eric Casanovas", "pass","eric.casanovas@example.com"),
            new Customer(2, "Steve Reece","pass", "steve.reece@example.com")
        );

        when(customerRepository.findAll()).thenReturn(customers);

        ResponseEntity<?> response = customerAPI.getAll(adminToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers, response.getBody());
    }

    @Test
    public void testGetAllCustomers_NonAdminToken_Success() {
        List<Customer> customers = Arrays.asList(
            new Customer(1, "Eric Casanovas", "pass","eric.casanovas@example.com"),
            new Customer(2, "Steve Reece","pass", "steve.reece@example.com")
        );

        when(customerRepository.findAll()).thenReturn(customers);

        ResponseEntity<?> response = customerAPI.getAll(userToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers, response.getBody());
    }

    @Test
    public void testGetAllCustomers_FakeUser_NoAuth() {
        List<Customer> customers = Arrays.asList(
            new Customer(1, "Eric Casanovas", "pass","eric.casanovas@example.com"),
            new Customer(2, "Steve Reece","pass", "steve.reece@example.com")
        );

        when(customerRepository.findAll()).thenReturn(customers);

        ResponseEntity<?> response = customerAPI.getAll(fakeToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetCustomerByName_Success() {
        String username = "Eric_Casanovas";
        Customer customer = new Customer(1, "Eric Casanovas", "pass","eric.casanovas@example.com");

        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = customerAPI.getCustomerByName(adminToken, username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    public void testGetCustomerByName_NotFound() {
        String username = "unknown_user";

        when(customerRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = customerAPI.getCustomerByName(adminToken, username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("A user with this username doesn't exists", response.getBody());
    }

    @Test
    public void testGetCustomerByName_NonAdminToken_Success() {
        
        String username = "Eric_Casanovas";
        Customer customer = new Customer(1, "Eric Casanovas", "pass","eric.casanovas@example.com");

        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = customerAPI.getCustomerByName(userToken, username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    public void testGetCustomerByName_FakeUser_NoAuth() {
        String username = "Eric Casanovas";
        Customer customer = new Customer(1, "Eric Casanovas", "pass","eric.casanovas@example.com");

        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(customer));


        ResponseEntity<?> response = customerAPI.getCustomerByName(fakeToken, username);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateCustomer_Success() {
        
        Long customerId = 1L;
        Customer existingCustomer = new Customer(1, "Eric Casanovas", "pass", "eric.casanovas@example.com");
        Customer updatedCustomer = new Customer(1, "Eric Casanovas", "newpass", "eric.new@example.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        ResponseEntity<?> response = customerAPI.updateCustomer(adminToken, customerId, updatedCustomer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateCustomer_NotFound() {
        Long customerId = 1L;
        Customer updatedCustomer = new Customer(1, "Eric Casanovas", "newpass", "eric.new@example.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = customerAPI.updateCustomer(adminToken, customerId, updatedCustomer);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with ID: " + customerId + " not found! 1", response.getBody());
    }

    @Test
    public void testUpdateCustomer_NonAdminToken_Success() {

        Long customerId = 1L;
        Customer existingCustomer = new Customer(1, "Eric Casanovas", "pass", "eric.casanovas@example.com");
        Customer updatedCustomer = new Customer(1, "Eric Casanovas", "newpass", "eric.new@example.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        ResponseEntity<?> response = customerAPI.updateCustomer(userToken, customerId, updatedCustomer);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateCustomer_FakeUser_NoAuth() {
        
        Long customerId = 1L;
        Customer existingCustomer = new Customer(1, "Eric Casanovas", "pass", "eric.casanovas@example.com");
        Customer updatedCustomer = new Customer(1, "Eric Casanovas", "newpass", "eric.new@example.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        ResponseEntity<?> response = customerAPI.updateCustomer(fakeToken, customerId, updatedCustomer);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeleteCustomer_Success() {
        Integer customerId = 1;
        Customer customer = new Customer(customerId, "Eric Casanovas", "pass", "eric.casanovas@example.com");

        when(customerRepository.findById((long) customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(customer);

        ResponseEntity<?> response = customerAPI.deleteCustomerById(adminToken, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    public void testDeleteCustomer_NotFound() {
        Integer customerId = 1;

        when(customerRepository.findById((long) customerId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = customerAPI.deleteCustomerById(adminToken, customerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteCustomer_NoAuth() {
        Integer customerId = 1;

        ResponseEntity<?> response = customerAPI.deleteCustomerById(userToken, customerId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}

