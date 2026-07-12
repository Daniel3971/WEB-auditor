package com.webauditor.backend.repository;

import com.webauditor.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}