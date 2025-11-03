package com.smartappointmentbooking.user_service.service;

import com.smartappointmentbooking.user_service.dto.*;
import com.smartappointmentbooking.user_service.entity.User;
import com.smartappointmentbooking.user_service.entity.DoctorProfile;
import com.smartappointmentbooking.user_service.repository.UserRepository;
import com.smartappointmentbooking.user_service.repository.DoctorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public UserProfileDTO getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });
        return mapToUserProfileDTO(user);
    }

    public UserProfileDTO updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        user = userRepository.save(user);
        log.info("User profile updated: {}", userId);
        return mapToUserProfileDTO(user);
    }

    public DoctorProfileDTO createDoctorProfile(Long userId, CreateDoctorProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DoctorProfile doctorProfile = DoctorProfile.builder()
                .user(user)
                .specialization(request.getSpecialization())
                .medicalLicenseNumber(request.getLicenseNumber())
                .experienceYears(request.getYearsOfExperience())
                .qualification(request.getQualifications())
                .availabilityStatus("AVAILABLE")
                .verified(false)
                .isDeleted(false)
                .build();

        doctorProfile = doctorProfileRepository.save(doctorProfile);
        log.info("Doctor profile created for user: {}", userId);
        return mapToDoctorProfileDTO(doctorProfile);
    }

    public DoctorProfileDTO getDoctorProfile(Long doctorId) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        return mapToDoctorProfileDTO(doctorProfile);
    }

    public DoctorProfileDTO getDoctorProfileByUserId(Long userId) {
        DoctorProfile doctorProfile = doctorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        return mapToDoctorProfileDTO(doctorProfile);
    }

    public DoctorProfileDTO updateDoctorProfile(Long doctorId, CreateDoctorProfileRequest request) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        if (request.getSpecialization() != null) {
            doctorProfile.setSpecialization(request.getSpecialization());
        }
        if (request.getYearsOfExperience() != null) {
            doctorProfile.setExperienceYears(request.getYearsOfExperience());
        }
        if (request.getQualifications() != null) {
            doctorProfile.setQualification(request.getQualifications());
        }

        doctorProfile = doctorProfileRepository.save(doctorProfile);
        log.info("Doctor profile updated: {}", doctorId);
        return mapToDoctorProfileDTO(doctorProfile);
    }

    public List<DoctorProfileDTO> getAllDoctors() {
        return doctorProfileRepository.findAll().stream()
                .filter(d -> !d.getIsDeleted())
                .map(this::mapToDoctorProfileDTO)
                .collect(Collectors.toList());
    }

    public List<DoctorProfileDTO> getDoctorsBySpecialization(String specialization) {
        return doctorProfileRepository.findBySpecialization(specialization).stream()
                .filter(d -> !d.getIsDeleted())
                .map(this::mapToDoctorProfileDTO)
                .collect(Collectors.toList());
    }

    public void deleteUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsDeleted(true);
        userRepository.save(user);
        log.info("User account deleted: {}", userId);
    }

    private UserProfileDTO mapToUserProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }

    private DoctorProfileDTO mapToDoctorProfileDTO(DoctorProfile doctorProfile) {
        return DoctorProfileDTO.builder()
                .id(doctorProfile.getId())
                .userId(doctorProfile.getUser().getId())
                .specialization(doctorProfile.getSpecialization())
                .licenseNumber(doctorProfile.getMedicalLicenseNumber())
                .yearsOfExperience(doctorProfile.getExperienceYears())
                .qualifications(doctorProfile.getQualification())
                .hospitalAffiliation(doctorProfile.getQualification())
                .isAvailable(doctorProfile.getAvailabilityStatus().equals("AVAILABLE"))
                .build();
    }

    public UserProfileDTO createUser(UpdateUserProfileRequest request) {
        User user = User.builder()
                .email(request.getEmail() != null ? request.getEmail()
                        : "user" + System.currentTimeMillis() + "@example.com")
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .role(request.getRole() != null ? request.getRole() : "ROLE_PATIENT")
                .isDeleted(false)
                .build();

        user = userRepository.save(user);
        log.info("User created: {} with ID: {}", user.getEmail(), user.getId());
        return mapToUserProfileDTO(user);
    }
}
