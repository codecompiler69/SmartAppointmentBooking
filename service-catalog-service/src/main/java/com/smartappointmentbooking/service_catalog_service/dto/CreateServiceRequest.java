package com.smartappointmentbooking.service_catalog_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceRequest {
    private String name;
    private String description;
    private String category;
    private Integer durationMinutes;
    private Double basePrice;
    private String iconUrl;
    private String notes;
}
