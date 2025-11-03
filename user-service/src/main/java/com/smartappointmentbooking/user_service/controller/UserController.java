package com.smartappointmentbooking.user_service.controller;

import com.smartappointmentbooking.user_service.dto.*;
import com.smartappointmentbooking.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management endpoints")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile (from auth context)")
    public ResponseEntity<UserProfileDTO> getUserProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null)
            userId = 1L; // Default for testing
        UserProfileDTO profile = userService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody UpdateUserProfileRequest request) {
        if (userId == null)
            userId = 1L; // Default for testing
        UserProfileDTO profile = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long id) {
        UserProfileDTO profile = userService.getUserProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctors")
    @Operation(summary = "Get all doctors")
    public ResponseEntity<List<DoctorProfileDTO>> getAllDoctors() {
        List<DoctorProfileDTO> doctors = userService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/doctors/specialization/{specialization}")
    @Operation(summary = "Get doctors by specialization")
    public ResponseEntity<List<DoctorProfileDTO>> getDoctorsBySpecialization(@PathVariable String specialization) {
        List<DoctorProfileDTO> doctors = userService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    @PostMapping("/doctors")
    @Operation(summary = "Create doctor profile")
    public ResponseEntity<DoctorProfileDTO> createDoctorProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody CreateDoctorProfileRequest request) {
        if (userId == null)
            userId = 1L; // Default for testing
        DoctorProfileDTO profile = userService.createDoctorProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/doctors/profile")
    @Operation(summary = "Get doctor profile of current user")
    public ResponseEntity<DoctorProfileDTO> getDoctorProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null)
            userId = 1L; // Default for testing
        DoctorProfileDTO profile = userService.getDoctorProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/doctors/{id}")
    @Operation(summary = "Get doctor profile by ID")
    public ResponseEntity<DoctorProfileDTO> getDoctorById(@PathVariable Long id) {
        DoctorProfileDTO profile = userService.getDoctorProfile(id);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/doctors/{id}")
    @Operation(summary = "Update doctor profile")
    public ResponseEntity<DoctorProfileDTO> updateDoctorProfile(
            @PathVariable Long id,
            @RequestBody CreateDoctorProfileRequest request) {
        DoctorProfileDTO profile = userService.updateDoctorProfile(id, request);
        return ResponseEntity.ok(profile);
    }

    @PostMapping
    @Operation(summary = "Create new user profile")
    public ResponseEntity<UserProfileDTO> createUser(@RequestBody UpdateUserProfileRequest request) {
        UserProfileDTO profile = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User service is running");
    }
}
