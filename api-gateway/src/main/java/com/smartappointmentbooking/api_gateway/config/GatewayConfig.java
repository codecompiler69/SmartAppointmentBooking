package com.smartappointmentbooking.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.addResponseHeader;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Slf4j
@Configuration
public class GatewayConfig {

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
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth-service")
                .route(RequestPredicates.path("/api/auth/**"), HandlerFunctions.http(authServiceUrl))
                .filter(addResponseHeader("X-Powered-By", "Smart-Appointment-Booking-Gateway"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("auth-service-cb", 
                        URI.create("forward:/fallback/auth")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return route("user-service")
                .route(RequestPredicates.path("/api/users/**"), HandlerFunctions.http(userServiceUrl))
                .filter(addResponseHeader("X-Powered-By", "Smart-Appointment-Booking-Gateway"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("user-service-cb", 
                        URI.create("forward:/fallback/user")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> appointmentServiceRoute() {
        return route("appointment-service")
                .route(RequestPredicates.path("/api/appointments/**"), HandlerFunctions.http(appointmentServiceUrl))
                .filter(addResponseHeader("X-Powered-By", "Smart-Appointment-Booking-Gateway"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("appointment-service-cb", 
                        URI.create("forward:/fallback/appointment")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> serviceCatalogServiceRoute() {
        return route("service-catalog-service")
                .route(RequestPredicates.path("/api/services/**"), HandlerFunctions.http(serviceCatalogServiceUrl))
                .filter(addResponseHeader("X-Powered-By", "Smart-Appointment-Booking-Gateway"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("service-catalog-cb", 
                        URI.create("forward:/fallback/service-catalog")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoute() {
        return route("notification-service")
                .route(RequestPredicates.path("/api/notifications/**"), HandlerFunctions.http(notificationServiceUrl))
                .filter(addResponseHeader("X-Powered-By", "Smart-Appointment-Booking-Gateway"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("notification-service-cb", 
                        URI.create("forward:/fallback/notification")))
                .build();
    }
}
