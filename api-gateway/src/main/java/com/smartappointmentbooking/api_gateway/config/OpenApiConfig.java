package com.smartappointmentbooking.api_gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Smart Appointment Booking - API Gateway", version = "1.0.0", description = """
        API Gateway for Smart Appointment Booking System

        This gateway provides a unified entry point to all microservices:
        - **Auth Service**: User authentication and authorization
        - **User Service**: User and doctor profile management
        - **Appointment Service**: Appointment booking and management
        - **Service Catalog**: Medical services catalog
        - **Notification Service**: Email and SMS notifications

        ## Authentication
        Most endpoints require JWT authentication. Use the `/api/auth/login` endpoint to obtain a token,
        then include it in the `Authorization` header as `Bearer <token>` for subsequent requests.

        ## Rate Limiting
        Rate limiting is applied to prevent abuse. Contact support if you need higher limits.
        """, contact = @Contact(name = "Smart Appointment Booking Support", email = "support@smartappointmentbooking.com", url = "https://smartappointmentbooking.com"), license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")), servers = {
        @Server(url = "http://localhost:8000", description = "Local Development Server"),
        @Server(url = "https://api.smartappointmentbooking.com", description = "Production Server")
}, security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", description = "JWT Bearer Token Authentication. Obtain token from /api/auth/login endpoint.", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

    @Value("${auth-service.url}")
    private String authServiceUrl;

    @Value("${user-service.url}")
    private String userServiceUrl;

    @Value("${appointment-service.url}")
    private String appointmentServiceUrl;

    @Value("${service-catalog-service.url}")
    private String serviceCatalogServiceUrl;

    @Value("${notification-service.url}")
    private String notificationServiceUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .tags(Arrays.asList(
                        new Tag()
                                .name("Gateway")
                                .description("API Gateway health and information endpoints"),
                        new Tag()
                                .name("Authentication")
                                .description(
                                        "User authentication and authorization endpoints (Auth Service - Port 8001)")
                                .externalDocs(new io.swagger.v3.oas.models.ExternalDocumentation()
                                        .description("Auth Service Swagger")
                                        .url(authServiceUrl + "/swagger-ui.html")),
                        new Tag()
                                .name("Users")
                                .description("User and doctor profile management endpoints (User Service - Port 8002)")
                                .externalDocs(new io.swagger.v3.oas.models.ExternalDocumentation()
                                        .description("User Service Swagger")
                                        .url(userServiceUrl + "/swagger-ui.html")),
                        new Tag()
                                .name("Appointments")
                                .description(
                                        "Appointment booking and management endpoints (Appointment Service - Port 8003)")
                                .externalDocs(new io.swagger.v3.oas.models.ExternalDocumentation()
                                        .description("Appointment Service Swagger")
                                        .url(appointmentServiceUrl + "/swagger-ui.html")),
                        new Tag()
                                .name("Service Catalog")
                                .description("Medical services catalog endpoints (Service Catalog - Port 8004)")
                                .externalDocs(new io.swagger.v3.oas.models.ExternalDocumentation()
                                        .description("Service Catalog Swagger")
                                        .url(serviceCatalogServiceUrl + "/swagger-ui.html")),
                        new Tag()
                                .name("Notifications")
                                .description("Email and SMS notification endpoints (Notification Service - Port 8005)")
                                .externalDocs(new io.swagger.v3.oas.models.ExternalDocumentation()
                                        .description("Notification Service Swagger")
                                        .url(notificationServiceUrl + "/swagger-ui.html"))));
    }
}
