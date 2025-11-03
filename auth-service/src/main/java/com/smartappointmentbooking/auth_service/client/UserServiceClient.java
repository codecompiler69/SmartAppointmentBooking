package com.smartappointmentbooking.auth_service.client;

import com.smartappointmentbooking.auth_service.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {
    private final RestTemplate restTemplate;

    @Value("${user-service.url:http://localhost:8002}")
    private String userServiceUrl;

    public void createUserInUserService(RegisterRequest registerRequest, Long userId) {
        try {
            Map<String, Object> userPayload = new HashMap<>();
            userPayload.put("email", registerRequest.getEmail());
            userPayload.put("firstName", registerRequest.getFirstName());
            userPayload.put("lastName", registerRequest.getLastName());
            userPayload.put("phoneNumber", registerRequest.getPhoneNumber());
            userPayload.put("role", "ROLE_" + registerRequest.getRole());
            userPayload.put("address", "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(userPayload, headers);
            String url = userServiceUrl + "/api/users";

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("User created in user-service with email: {}", registerRequest.getEmail());
            } else {
                log.warn("Failed to create user in user-service. Status: {}", response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("Error calling user-service to create user: {}", registerRequest.getEmail(), e);
            // Don't throw exception - let auth service registration succeed even if user-service call fails
            // This prevents cascading failures between services
        } catch (Exception e) {
            log.error("Unexpected error while creating user in user-service", e);
        }
    }
}
