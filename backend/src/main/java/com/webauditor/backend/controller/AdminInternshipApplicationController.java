package com.webauditor.backend.controller;

import com.webauditor.backend.CompanyService.InternshipApplicationService;
import com.webauditor.backend.CompanyService.AdminAuditService;
import com.webauditor.backend.CompanyService.InternshipApplicationService.StoredCv;
import com.webauditor.backend.dto.AdminApplicationResponse;
import com.webauditor.backend.dto.ApplicationStatusUpdateRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/internship-applications")
public class AdminInternshipApplicationController {

    private final InternshipApplicationService applicationService;
    private final AdminAuditService auditService;

    public AdminInternshipApplicationController(
        InternshipApplicationService applicationService,
        AdminAuditService auditService
    ) {
        this.applicationService = applicationService;
        this.auditService = auditService;
    }

    @GetMapping
    public List<AdminApplicationResponse> getAllApplications() {
        return applicationService.getAllForAdmin();
    }

    @GetMapping("/count")
    public Map<String, Long> countApplications() {
        return Map.of("count", applicationService.countForAdmin());
    }

    @GetMapping("/{id}")
    public AdminApplicationResponse getApplication(
        @PathVariable Long id
    ) {
        return applicationService.getForAdmin(id);
    }

    @PatchMapping("/{id}/status")
    public AdminApplicationResponse updateStatus(
        @PathVariable Long id,
        @RequestBody ApplicationStatusUpdateRequest request,
        Authentication authentication,
        HttpServletRequest servletRequest
    ) {
        AdminApplicationResponse before = applicationService.getForAdmin(id);
        AdminApplicationResponse updated =
            applicationService.updateStatusForAdmin(id, request.status());
        auditService.record(
            authentication,
            servletRequest,
            "APPLICATION_STATUS_CHANGED",
            "INTERNSHIP_APPLICATION",
            id,
            before.applicationStatus() + " -> " + updated.applicationStatus()
        );
        return updated;
    }

    @GetMapping("/offer/{offerId}")
    public List<AdminApplicationResponse> getApplicationsByOffer(
        @PathVariable Long offerId
    ) {
        return applicationService.getByOfferForAdmin(offerId);
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<Resource> downloadCv(
        @PathVariable Long id
    ) throws MalformedURLException {
        StoredCv cv = applicationService.getCvForAdmin(id);
        Resource resource = new UrlResource(cv.path().toUri());

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                    .filename(cv.originalFileName(), StandardCharsets.UTF_8)
                    .build()
                    .toString()
            )
            .header("X-Content-Type-Options", "nosniff")
            .contentLength(cv.size())
            .body(resource);
    }
}
