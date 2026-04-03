package com.mailservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public class MailRequest {

    @Email(message = "Invalid From Address")
    @NotBlank(message = "From address is required")
    private String from;

    @NotBlank(message = "To address is required")
    private String to;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;

    private String cc;
    private String bcc;
    private String replyTo;
    private Boolean confidential;
    private List<MultipartFile> attachments;

    // Getters and Setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
    }
}
