package com.webauditor.backend.CompanyService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class TurnstileService {

    private static final String SITEVERIFY_URL =
        "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    private final boolean enabled;
    private final String secretKey;
    private final RestClient restClient;

    public TurnstileService(
        @Value("${app.security.turnstile.enabled:false}") boolean enabled,
        @Value("${app.security.turnstile.secret-key:}") String secretKey
    ) {
        this.enabled = enabled;
        this.secretKey = secretKey;
        this.restClient = RestClient.create(SITEVERIFY_URL);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean validate(String token, HttpServletRequest request) {
        if (!enabled) {
            return true;
        }

        if (token == null || token.isBlank() || secretKey.isBlank()) {
            return false;
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey);
        body.add("response", token);
        body.add("remoteip", AdminAuditService.clientIp(request));

        try {
            TurnstileResponse response = restClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(TurnstileResponse.class);

            return response != null
                && response.success()
                && "internship_application".equals(response.action());
        } catch (Exception ignored) {
            // Fail closed: applications are not accepted if Cloudflare cannot
            // verify a token.
            return false;
        }
    }

    private record TurnstileResponse(boolean success, String action) {
    }
}
