package com.smartappointmentbooking.service_catalog_service.service;

import com.smartappointmentbooking.service_catalog_service.dto.ServiceDTO;
import com.smartappointmentbooking.service_catalog_service.dto.CreateServiceRequest;
import com.smartappointmentbooking.service_catalog_service.entity.Service;
import com.smartappointmentbooking.service_catalog_service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceCatalogService {
    private final ServiceRepository serviceRepository;

    public ServiceDTO createService(CreateServiceRequest request) {
        Service service = Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .durationMinutes(request.getDurationMinutes())
                .basePrice(request.getBasePrice())
                .iconUrl(request.getIconUrl())
                .notes(request.getNotes())
                .isActive(true)
                .build();

        service = serviceRepository.save(service);
        log.info("Service created: {}", service.getId());
        return mapToDTO(service);
    }

    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceDTO> getActiveServices() {
        return serviceRepository.findByIsActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ServiceDTO getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return mapToDTO(service);
    }

    public ServiceDTO updateService(Long id, CreateServiceRequest request) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (request.getName() != null) {
            service.setName(request.getName());
        }
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            service.setCategory(request.getCategory());
        }
        if (request.getDurationMinutes() != null) {
            service.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getBasePrice() != null) {
            service.setBasePrice(request.getBasePrice());
        }
        if (request.getIconUrl() != null) {
            service.setIconUrl(request.getIconUrl());
        }

        service.setUpdatedAt(LocalDateTime.now());
        service = serviceRepository.save(service);
        log.info("Service updated: {}", id);
        return mapToDTO(service);
    }

    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        service.setIsActive(false);
        serviceRepository.save(service);
        log.info("Service deleted (soft): {}", id);
    }

    private ServiceDTO mapToDTO(Service service) {
        return ServiceDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .category(service.getCategory())
                .durationMinutes(service.getDurationMinutes())
                .basePrice(service.getBasePrice())
                .isActive(service.getIsActive())
                .iconUrl(service.getIconUrl())
                .createdAt(service.getCreatedAt())
                .build();
    }
}
