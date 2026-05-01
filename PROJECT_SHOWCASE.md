# 🛡️ Secure Mail Service Project Showcase

## 🌟 Overview
The **Secure Mail Service** is a robust, enterprise-grade backend solution designed to provide secure and authenticated email delivery for multiple clients (SaaS ready). It acts as a centralized mail gateway that applications can use to send emails without managing their own SMTP server configurations.

The project focuses on **Security-First Architecture**, implementing HMAC signing to prevent unauthorized access and replay attacks.

---

## 🚀 Key Features

### 1. Multi-Tenant Architecture
- Supports multiple clients with unique configurations.
- Each client has their own set of authorized senders and credentials.
- Client configurations are managed via a JSON-based registry (`clients.json`).

### 2. Enterprise Security (HMAC-SHA256)
- **Signature-Based Auth**: Instead of sending static passwords, clients sign every request using a secret key.
- **Request Integrity**: The signature covers the `API Key`, `User ID`, and `Timestamp`, ensuring the request hasn't been tampered with.
- **Timestamp Validation**: Prevents "Replay Attacks" by rejecting any request older than 5 minutes.
- **No Stored Secrets on Client**: The secret key is used only for signing and is never transmitted over the network.

### 3. Comprehensive Payload Support
- **Rich Text Emails**: Supports HTML bodies for professional branding.
- **Attachments**: Built-in support for multiple file attachments via `multipart/form-data`.
- **Sender Spoof Prevention**: Only pre-authorized sender addresses can be used by a specific client.

### 4. Developer-Friendly SDKs
- Includes ready-to-use **Python** and **Node.js** client libraries.
- Standardized error responses with `traceId` for easy debugging.

---

## 🛠️ Tech Stack

| Technology | Purpose |
| :--- | :--- |
| **Java 17 / 21** | Core programming language. |
| **Spring Boot 3.x** | Application framework. |
| **Spring Security** | Customized filter-based HMAC authentication. |
| **Jakarta Mail** | Underlying SMTP communication. |
| **Maven** | Dependency management and build tool. |
| **Logback/SLF4J** | Structured logging for audits. |

---

## 🏗️ Technical Architecture

### 1. Authentication Flow
1. **Client** generates a timestamp and signs a string (`apiKey + userId + timestamp`) using their `Secret Key`.
2. **Client** sends request with headers: `X-API-KEY`, `X-USER-ID`, `X-TIMESTAMP`, `X-SIGNATURE`.
3. **Server** (`ApiKeyAuthFilter`) intercepts the request.
4. **Server** validates the timestamp (within 5 min window).
5. **Server** looks up the client's secret and regenerates the signature locally.
6. **Server** compares signatures; if they match, the request is authenticated.

### 2. Project Structure
```text
mail-service/
├── src/main/java/             # Backend logic
│   ├── config/                # Bean and client config
│   ├── controller/            # REST Endpoints (/api/v1/mail)
│   ├── model/                 # Data transfer objects
│   ├── security/              # HMAC Filter implementation
│   └── service/               # Mail and Audit business logic
├── client_sdk/                # Integration tools for users
│   ├── python/                # Python class library
│   └── nodejs/                # Node.js/Axios library
├── docs/                      # Technical diagrams and documentation
├── tools/                     # Dev utility scripts
└── pom.xml                    # Build configuration
```

---

## 📝 Usage Example (cURL)

```bash
curl -X POST http://localhost:8080/api/v1/mail \
  -H "X-API-KEY: YOUR_KEY" \
  -H "X-USER-ID: dev_test" \
  -H "X-SIGNATURE: BASE64_HMAC" \
  -H "X-TIMESTAMP: 1712056000" \
  -F "to=recipient@example.com" \
  -F "subject=Safety Report" \
  -F "body=<h1>System Alert</h1>"
```

---

## 🔮 Future Roadmap
- **Database Integration**: Migrate `clients.json` to PostgreSQL/MySQL for dynamic client management.
- **Web Dashboard**: A UI for clients to view their delivery logs and manage keys.
- **Webhooks**: Notify client applications when an email is successfully delivered or fails.
- **Rate Limiting**: Tiered API limits for different clients.
