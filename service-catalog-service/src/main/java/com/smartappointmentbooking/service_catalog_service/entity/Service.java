package com.smartappointmentbooking.service_catalog_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Column
    private Integer durationMinutes;

    @Column(nullable = false)
    private Double basePrice;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private String iconUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
}
