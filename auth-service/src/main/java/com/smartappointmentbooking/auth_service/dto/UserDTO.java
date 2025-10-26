package com.smartappointmentbooking.auth_service.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean emailVerified;
    private LocalDateTime emailVerifiedAt;
    private Set<String> roles;
    private LocalDateTime createdAt;
}
