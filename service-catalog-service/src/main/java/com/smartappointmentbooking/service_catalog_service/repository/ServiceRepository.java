package com.smartappointmentbooking.service_catalog_service.repository;

import com.smartappointmentbooking.service_catalog_service.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByIsActiveTrue();
}
