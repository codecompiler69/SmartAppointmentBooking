package com.smartappointmentbooking.user_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorProfileRequest {
    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private String qualifications;
    private String hospitalAffiliation;
}
