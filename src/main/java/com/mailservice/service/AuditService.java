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
        if (client.getAuditDbConfig() == null) {
            log.warn("No Audit DB Config configured for client {}. Skipping audit log.", client.getClientId());
            return;
        }

        try {
            AuditDbConfig dbConfig = client.getAuditDbConfig();
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(
                    Objects.requireNonNull(dbConfig.getDriverClassName(), "Driver class name is required"));
            dataSource.setUrl(Objects.requireNonNull(dbConfig.getJdbcUrl(), "JDBC URL is required"));
            dataSource.setUsername(Objects.requireNonNull(dbConfig.getUsername(), "Username is required"));
            dataSource.setPassword(Objects.requireNonNull(dbConfig.getPassword(), "Password is required"));

            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            // Removed repeated "CREATE TABLE" execution for performance in production
            // If needed, DB setup should be a separate initialization step.

            String insertSql = "INSERT INTO MAIL_AUDIT_LOG (ID, TIMESTAMP, CLIENT_ID, USER_ID, RECIPIENT, SUBJECT, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql,
                    UUID.randomUUID().toString(),
                    LocalDateTime.now(),
                    client.getClientId(),
                    userId != null ? userId : "UNKNOWN",
                    recipient != null ? recipient : "",
                    subject != null ? subject : "No Subject",
                    status);

            log.info("Audit Log written to client DB for Client={}", client.getClientId());

        } catch (Exception e) {
            log.error("Failed to write audit log to client DB for Client={}", client.getClientId(), e);
        }
    }
}
