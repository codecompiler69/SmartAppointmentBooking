package com.smartappointmentbooking.service_catalog_service.controller;

import com.smartappointmentbooking.service_catalog_service.dto.ServiceDTO;
import com.smartappointmentbooking.service_catalog_service.dto.CreateServiceRequest;
import com.smartappointmentbooking.service_catalog_service.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Service catalog management endpoints")
public class ServiceCatalogController {
    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    @Operation(summary = "Get all services")
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        List<ServiceDTO> services = serviceCatalogService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active services")
    public ResponseEntity<List<ServiceDTO>> getActiveServices() {
        List<ServiceDTO> services = serviceCatalogService.getActiveServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
        ServiceDTO service = serviceCatalogService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PostMapping
    @Operation(summary = "Create new service")
    public ResponseEntity<ServiceDTO> createService(@RequestBody CreateServiceRequest request) {
        ServiceDTO service = serviceCatalogService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update service")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable Long id,
            @RequestBody CreateServiceRequest request) {
        ServiceDTO service = serviceCatalogService.updateService(id, request);
        return ResponseEntity.ok(service);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete service")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceCatalogService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service catalog service is running");
    }
}
