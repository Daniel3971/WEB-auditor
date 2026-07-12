package com.webauditor.backend.CompanyService;

import com.webauditor.backend.entity.Service;
import com.webauditor.backend.repository.ServiceRepository;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Service createService(Service service) {
        service.setId(null);
        return serviceRepository.save(service);
    }
}