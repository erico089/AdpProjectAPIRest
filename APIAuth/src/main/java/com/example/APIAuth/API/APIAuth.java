package com.example.APIAuth.API;

import java.util.Map;
import java.util.Optional;

import com.auth0.jwt.interfaces.Claim;
import com.example.APIAuth.JWTHelper;
import com.example.APIAuth.model.Customer;
import com.example.APIAuth.model.Token;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
        
        if (loginRequest.getName() == null || loginRequest.getName().isEmpty() ||
            loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("You must complete name and password");
        }

        String url = "http://localhost:8080/api/customers/byname/" + loginRequest.getName();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(createToken("admin","admin").getToken()); // Método para agregar el Bearer token

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate(); 
        ResponseEntity<Customer> customerResponse;

        try {
            customerResponse = restTemplate.exchange(url, HttpMethod.GET, entity, Customer.class);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user data: " + e.getMessage());
        }

        if (customerResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Customer customer = customerResponse.getBody();

        Token token = createToken(customer.getName(),customer.getPassword());
        return ResponseEntity.ok(token);	
    }

    @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody Customer customer) {

            String url = "http://localhost:8080/api/customers";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // Establecer el tipo de contenido a JSON
            headers.setBearerAuth(createToken("admin", "admin").getToken()); // Método para agregar el Bearer token

            HttpEntity<Customer> entity = new HttpEntity<>(customer, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Customer> customerResponse;

            try {
                // Llamada a la API con la entidad que contiene el cuerpo y los encabezados
                customerResponse = restTemplate.exchange(url, HttpMethod.POST, entity, Customer.class);
                
            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error retrieving user data: " + e.getMessage());
            }

            Customer createdCustomer = customerResponse.getBody();
            Token userToken = createToken(createdCustomer.getName(), "");

            return ResponseEntity.ok(userToken);
        }

    private static Token createToken(String username, String password) {
    	String scopes = "com.webage.data.apis";
    	// special case for application user
    	if( username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin") ) {
    		scopes = "com.webage.auth.apis";
    	}
    	String token_string = JWTHelper.createToken(scopes);
    
    	return new Token(token_string);
    }


}