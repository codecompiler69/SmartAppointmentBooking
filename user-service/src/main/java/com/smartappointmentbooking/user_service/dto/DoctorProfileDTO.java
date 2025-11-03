package com.smartappointmentbooking.user_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileDTO {
    private Long id;
    private Long userId;
    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private String qualifications;
    private String hospitalAffiliation;
    private Double rating;
    private Boolean isAvailable;
}
