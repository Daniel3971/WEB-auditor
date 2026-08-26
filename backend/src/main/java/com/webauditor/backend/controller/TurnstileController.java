package com.webauditor.backend.controller;

import com.webauditor.backend.CompanyService.TurnstileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/turnstile")
public class TurnstileController {

    private final TurnstileService turnstileService;
    private final String siteKey;

    public TurnstileController(
        TurnstileService turnstileService,
        @Value("${app.security.turnstile.site-key:}") String siteKey
    ) {
        this.turnstileService = turnstileService;
        this.siteKey = siteKey;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        boolean enabled = turnstileService.isEnabled() && !siteKey.isBlank();
        return Map.of("enabled", enabled, "siteKey", enabled ? siteKey : "");
    }
}
