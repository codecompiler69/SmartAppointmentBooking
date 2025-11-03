package com.smartappointmentbooking.api_gateway.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Hidden // Hide fallback endpoints from Swagger UI
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> authServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Authentication Service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, String>> userServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User Service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/appointment")
    public ResponseEntity<Map<String, String>> appointmentServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Appointment Service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/service-catalog")
    public ResponseEntity<Map<String, String>> serviceCatalogFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Service Catalog is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, String>> notificationServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification Service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
