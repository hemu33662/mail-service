package com.mailservice.service;

import com.mailservice.config.ClientConfig;
import com.mailservice.config.SenderConfig;
import com.mailservice.config.SmtpConfig;
import com.mailservice.model.MailRequest;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final AuditService auditService;

    // Manual Constructor injection (Replaces @RequiredArgsConstructor)
    public MailService(AuditService auditService) {
        this.auditService = auditService;
    }

    @SuppressWarnings("null")
    public void processAndSendMail(MailRequest request, ClientConfig client, String userId) {
        log.info("Processing mail request from User={} Client={}", userId, client.getClientId());

        SenderConfig senderConfig = validateAndGetSender(request.getFrom(), client);
        JavaMailSender mailSender = createJavaMailSender(client.getSmtpConfig());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            String requestedFrom = Objects.requireNonNull(request.getFrom(), "From address is required");
            helper.setFrom(client.getSmtpConfig().getUsername(), requestedFrom);
            helper.setTo(Objects.requireNonNull(request.getTo(), "To address is required").split(","));
            helper.setSubject(Objects.requireNonNull(request.getSubject(), "Subject is required"));
            helper.setText(Objects.requireNonNull(request.getBody(), "Body is required"), true);

            String cc = request.getCc();
            if (cc != null && !cc.isBlank()) {
                helper.setCc(cc.split(","));
            }

            String replyTo = request.getReplyTo();
            if (replyTo != null && !replyTo.isBlank()) {
                helper.setReplyTo(replyTo);
            }

            if (request.getAttachments() != null) {
                for (MultipartFile file : request.getAttachments()) {
                    if (!file.isEmpty()) {
                        helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
                    }
                }
            }

            if (senderConfig.getManager() != null && !senderConfig.getManager().isBlank()) {
                String managerEmail = senderConfig.getManager();
                log.info("Auto-BCCing Manager: {}", managerEmail);
                helper.addBcc(managerEmail);
            }

            String bcc = request.getBcc();
            if (bcc != null && !bcc.isBlank()) {
                String[] bccList = bcc.split(",");
                for (String bccEmail : bccList) {
                    helper.addBcc(bccEmail.trim());
                }
            }

            mailSender.send(message);
            log.info("Mail sent successfully to {}", request.getTo());

            auditService.logMailTransaction(client, userId, request.getTo(), request.getSubject(), "SUCCESS");

        } catch (Exception e) {
            log.error("Failed to send mail (Suppressing error)", e);
            auditService.logMailTransaction(client, userId, request.getTo(), request.getSubject(),
                    "FAILED (Suppressed): " + e.getMessage());
            // throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private SenderConfig validateAndGetSender(String fromEmail, ClientConfig client) {
        Optional<SenderConfig> config = client.getSenders().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(fromEmail))
                .findFirst();

        if (config.isEmpty()) {
            throw new IllegalArgumentException("Sender address '" + fromEmail + "' is not authorized for this client.");
        }
        return config.get();
    }

    private JavaMailSender createJavaMailSender(SmtpConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");

        if (config.isAuth()) {
            props.put("mail.smtp.auth", "true");
        }
        if (config.isStarttls()) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }
}
