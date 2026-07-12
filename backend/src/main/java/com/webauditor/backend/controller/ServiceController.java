package com.webauditor.backend.controller;

import com.webauditor.backend.CompanyService.ServiceService;
import com.webauditor.backend.entity.Service;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public List<Service> getAllServices() {
        return serviceService.getAllServices();
    }

    @PostMapping
    public Service createService(@RequestBody Service service) {
        return serviceService.createService(service);
    }
}