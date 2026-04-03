package com.mailservice.controller;

import com.mailservice.config.ClientConfig;
import com.mailservice.model.MailRequest;
import com.mailservice.security.ApiKeyAuthenticationToken;
import com.mailservice.service.MailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController {

    private static final Logger log = LoggerFactory.getLogger(MailController.class);
    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> sendMail(@Valid @ModelAttribute MailRequest request) {
        ApiKeyAuthenticationToken auth = (ApiKeyAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        ClientConfig client = (ClientConfig) auth.getPrincipal();
        String userId = auth.getUserId();

        try {
            mailService.processAndSendMail(request, client, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Mail queued/sent successfully",
                    "traceId", UUID.randomUUID().toString()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", "Internal Server Error"));
        }
    }

    @GetMapping("/trigger")
    public ResponseEntity<?> triggerMail() {
        ApiKeyAuthenticationToken auth = (ApiKeyAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        ClientConfig client = (ClientConfig) auth.getPrincipal();
        String userId = auth.getUserId();

        if (client.getSenders().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "No authorized senders for this client."));
        }

        String fromEmail = client.getSenders().get(0).getEmail();

        MailRequest request = new MailRequest();
        request.setFrom(fromEmail);
        request.setTo(fromEmail); // Self test
        request.setSubject("Trigger Test: " + client.getClientName());
        request.setBody("<h1>Trigger Test Success</h1><p>This email was triggered via the <b>/trigger</b> endpoint.</p>");

        try {
            mailService.processAndSendMail(request, client, userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Test mail triggered successfully to " + fromEmail,
                    "traceId", UUID.randomUUID().toString()));
        } catch (Exception e) {
            log.error("Trigger failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", "Trigger failed: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Mail Service OK");
    }
}
