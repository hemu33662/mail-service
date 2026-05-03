package com.mailservice.service;

import com.mailservice.config.AuditDbConfig;
import com.mailservice.config.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    public void logMailTransaction(ClientConfig client, String userId, String recipient, String subject,
            String status) {
        // Database auditing disabled as requested. Logging to console only.
        log.info("[AUDIT] Mail Transaction: Client={}, User={}, Recipient={}, Subject={}, Status={}",
                client.getClientId(),
                userId != null ? userId : "UNKNOWN",
                recipient != null ? recipient : "No Recipient",
                subject != null ? subject : "No Subject",
                status);
    }
}
