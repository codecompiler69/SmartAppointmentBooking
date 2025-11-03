package com.smartappointmentbooking.api_gateway.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Service Catalog", description = "Medical services catalog endpoints (Proxied to Service Catalog Service)")
public class ServiceCatalogDocsController {

    @GetMapping
    @Operation(summary = "Get all services", description = "Retrieve a list of all available medical services", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "name": "General Consultation",
                        "description": "Basic health checkup and consultation",
                        "duration": 30,
                        "price": 100.00,
                        "category": "General",
                        "isActive": true
                      },
                      {
                        "id": 2,
                        "name": "Cardiology Consultation",
                        "description": "Specialized heart consultation",
                        "duration": 45,
                        "price": 200.00,
                        "category": "Cardiology",
                        "isActive": true
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getAllServices() {
        // Documentation only
    }

    @GetMapping("/public")
    @Operation(summary = "Get public services (No authentication required)", description = "Retrieve a list of publicly available medical services without authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public services retrieved successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "name": "General Consultation",
                        "description": "Basic health checkup",
                        "duration": 30,
                        "price": 100.00,
                        "category": "General"
                      }
                    ]
                    """)))
    })
    public void getPublicServices() {
        // Documentation only
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID", description = "Retrieve detailed information about a specific medical service", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "name": "General Consultation",
                      "description": "Basic health checkup and consultation",
                      "duration": 30,
                      "price": 100.00,
                      "category": "General",
                      "isActive": true,
                      "createdAt": "2025-11-01T10:00:00",
                      "updatedAt": "2025-11-01T10:00:00"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getServiceById(
            @Parameter(description = "Service ID") @PathVariable Long id) {
        // Documentation only
    }

    @PostMapping
    @Operation(summary = "Create new service (ADMIN only)", description = "Create a new medical service in the catalog (requires ADMIN role)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service created successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "id": 3,
                      "name": "Dermatology Consultation",
                      "description": "Skin and hair consultation",
                      "duration": 30,
                      "price": 150.00,
                      "category": "Dermatology",
                      "isActive": true
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    public void createService(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Service details", content = @Content(examples = @ExampleObject(value = """
                    {
                      "name": "Dermatology Consultation",
                      "description": "Skin and hair consultation",
                      "duration": 30,
                      "price": 150.00,
                      "category": "Dermatology",
                      "isActive": true
                    }
                    """))) Object request) {
        // Documentation only
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update service (ADMIN only)", description = "Update an existing medical service (requires ADMIN role)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    public void updateService(
            @Parameter(description = "Service ID") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated service details", content = @Content(examples = @ExampleObject(value = """
                    {
                      "name": "Updated Service Name",
                      "description": "Updated description",
                      "duration": 45,
                      "price": 175.00,
                      "isActive": true
                    }
                    """))) Object request) {
        // Documentation only
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete service (ADMIN only)", description = "Delete a medical service from the catalog (requires ADMIN role)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Service deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    public void deleteService(
            @Parameter(description = "Service ID") @PathVariable Long id) {
        // Documentation only
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get services by category", description = "Retrieve all services in a specific category", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getServicesByCategory(
            @Parameter(description = "Service category") @PathVariable String category) {
        // Documentation only
    }
}
