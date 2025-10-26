package com.smartappointmentbooking.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

    @Column
    private String description;

    public enum RoleEnum {
        ROLE_ADMIN,
        ROLE_DOCTOR,
        ROLE_PATIENT
    }
}
