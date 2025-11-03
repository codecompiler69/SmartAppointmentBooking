package com.smartappointmentbooking.service_catalog_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Integer durationMinutes;
    private Double basePrice;
    private Boolean isActive;
    private String iconUrl;
    private LocalDateTime createdAt;
}
