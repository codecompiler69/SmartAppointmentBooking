package com.smartappointmentbooking.user_service.repository;

import com.smartappointmentbooking.user_service.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUserId(Long userId);

    List<DoctorProfile> findBySpecialization(String specialization);

    List<DoctorProfile> findByVerifiedTrue();
}
