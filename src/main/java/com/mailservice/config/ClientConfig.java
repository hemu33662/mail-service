package com.mailservice.config;

import java.util.List;

public class ClientConfig {
    private String clientId;
    private String clientName;
    private String apiKey;
    private String secretKey;
    private String environment;
    private List<SenderConfig> senders;
    private SmtpConfig smtpConfig;
    private AuditDbConfig auditDbConfig;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public List<SenderConfig> getSenders() {
        return senders;
    }

    public void setSenders(List<SenderConfig> senders) {
        this.senders = senders;
    }

    public SmtpConfig getSmtpConfig() {
        return smtpConfig;
    }

    public void setSmtpConfig(SmtpConfig smtpConfig) {
        this.smtpConfig = smtpConfig;
    }

    public AuditDbConfig getAuditDbConfig() {
        return auditDbConfig;
    }

    public void setAuditDbConfig(AuditDbConfig auditDbConfig) {
        this.auditDbConfig = auditDbConfig;
    }
}
