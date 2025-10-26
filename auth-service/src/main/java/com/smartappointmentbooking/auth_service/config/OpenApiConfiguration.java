package com.smartappointmentbooking.auth_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Auth Service API", version = "1.0.0", description = "Smart Appointment Booking System - Authentication Service", contact = @Contact(name = "API Support", email = "support@appointmentbooking.com"), license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")), servers = {
        @Server(url = "http://localhost:8001", description = "Development Server"),
        @Server(url = "https://api.appointmentbooking.com", description = "Production Server")
})
@SecurityScheme(name = "bearer", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT token", in = SecuritySchemeIn.HEADER)
public class OpenApiConfiguration {
}
