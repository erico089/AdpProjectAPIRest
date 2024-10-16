package com.example.APIAuth.API;

import java.util.Map;
import java.util.Optional;

import com.example.APIAuth.model.Customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import com.example.APIAuth.model.LoginRequest;

@RestController
@RequestMapping("/")
public class APIAuth {
    @GetMapping
	public ResponseEntity<?> basic() {
        return ResponseEntity.ok("Auth API currently working");
	}

    @PostMapping("/token")
    public ResponseEntity<?> logIn(@RequestBody LoginRequest loginRequest) {
        // Validar que los campos no estén vacíos
        if (loginRequest.getName() == null || loginRequest.getName().isEmpty() ||
            loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("You must complete name and password");
        }

        // Construir la URL
        String url = "http://localhost:8080/api/customers/byname/" + loginRequest.getName();

        // Realizar la llamada HTTP y obtener el cliente
        RestTemplate restTemplate = new RestTemplate(); // Instancia de RestTemplate
        ResponseEntity<Customer> customerResponse;
        
        try {
            // Llamada a la API y mapeo a la clase Customer
            customerResponse = restTemplate.getForEntity(url, Customer.class);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user data: " + e.getMessage());
        }

        // Verificar si el cuerpo de la respuesta es nulo
        if (customerResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Customer customer = customerResponse.getBody();
        return ResponseEntity.ok("Login successful for customer: " + customer.getName());
    }


}