# Secure Mail Service - Project Walkthrough

## Overview
A secure, multi-tenant mail API built with **Java 17** and **Spring Boot**.
Designed for "Zero Persistence" (Stateless) with **BYOD Audit Logging** and **HMAC Security**.

## 🚀 Quick Start (Windows)
1. **Update Configuration**:  Edit `src/main/resources/clients.json` with **valid SMTP credentials** (Mailtrap, SendGrid, etc.). The default credentials are placeholders and will cause authentication errors.
2. **Run Application**: Double-click `run_app.bat` or run it from the command line.
3. **Verify**: The server will start on port 8080.


## 📡 API Reference

**Endpoint**: `POST /api/v1/mail`
**Content-Type**: `multipart/form-data`

### Headers
| Header | Description |
| :--- | :--- |
| `X-API-KEY` | Your Client API Key (from `clients.json`) |
| `X-USER-ID` | ID of the user sending the mail |
| `X-TIMESTAMP` | Current Unix timestamp (ms) |
| `X-SIGNATURE` | HMAC_SHA256(`apiKey` + `userId` + `timestamp`, `secretKey`) |

### Payload (Multipart Fields)
| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `from` | Text | Yes | Sender email (must be authorized in `clients.json`) |
| `to` | Text | Yes | Recipient email address(es) |
| `subject` | Text | Yes | Email subject |
| `body` | Text | Yes | Email body (HTML supported) |
| `attachments` | File | No | One or more files to attach |
| `cc` | Text | No | Carbon Copy recipients |
| `bcc` | Text | No | Blind Carbon Copy recipients |

### Sample Payload (Python Dictionary equivalent)
```python
payload = {
    "from": "dev-support@deloitte.com",
    "to": "recipient@example.com",
    "subject": "Test Secure Mail",
    "body": "<h1>Hello</h1><p>This is a secure trace test.</p>"
}
files = {
    "attachments": open("invoice.pdf", "rb")
}
```

```

### cURL (PowerShell Generator)
Since the API requires a **valid timestamp** and **HMAC signature**, you cannot use a static cURL command.
Use the provided PowerShell script to generate a valid command for the next 5 minutes:
```powershell
./generate_curl.ps1
```
This will output a command like:
```bash
curl.exe -X POST "http://localhost:8080/api/v1/mail" \
  -H "X-API-KEY: dlt-dev-api-key-123" \
  -H "X-USER-ID: employee_test_01" \
  -H "X-TIMESTAMP: 1705763000000" \
  -H "X-SIGNATURE: sOmEhAsH..." \
  -F "from=dev-support@deloitte.com" \
  -F "to=recipient@example.com" \
  -F "subject=Test via CURL" \
  -F "body=<h1>Hello from PowerShell CURL generator</h1>"
```
> [!NOTE]
> On Windows PowerShell, `curl` is an alias for `Invoke-WebRequest`. Always use **`curl.exe`** to use the native cURL tool.

## 🚀 Key Features Implemented

1.  **Multi-Tenant Architecture** (`ClientConfigurationService`)
    - Configurable via `clients.json`.
    - Dynamic SMTP credentials per client.
    - Environment isolation (SANDBOX vs PROD).

2.  **Enterprise Security** (`ApiKeyAuthFilter`)
    - **Header Auth**: `X-API-KEY`, `X-USER-ID`.
    - **HMAC Signatures**: `X-SIGNATURE` verifies `HMAC_SHA256(Secret, ApiKey+UserId+Timestamp)`.
    - **Replay Protection**: `X-TIMESTAMP` validation (5-minute window).

3.  **Manager Oversight** (`MailService`)
    - Automatically maps "From" addresses to Managers.
    - **Auto-BCC**: Every email sent is blind-copied to the manager for immediate oversight.

4.  **BYOD Audit Logging** (`AuditService`)
    - Connects dynamically to the **Client's Database** (configured in `clients.json`).
    - Writes `MAIL_AUDIT_LOG` directly to their DB.
    - No sensitive Log data stored in the Service itself.

## 🛠️ Setup & Run

### Prerequisites
- Java 17+ installed.
- Maven installed (or run via IDE).
- Python 3 (for the test script).

### Running the Application
1.  Open the project in **IntelliJ IDEA** or **Eclipse**.
2.  Import as a **Maven Project**.
3.  Run `MailServiceApplication.java`.
4.  Server starts on `http://localhost:8080`.

## 🧪 Testing with HMAC

Since security is enabled, you cannot validly test with simple CURL (unless you calculate the HMAC manually).
Use the provided `test_client.py` script.

1.  **Install dependencies**: `pip install requests`
2.  **Run**: `python test_client.py`

This script will:
- Generate a valid Timestamp.
- Sign the request with the `clients.json` secret key (`dlt-dev-secret-key-xyz`).
- Send a request to `localhost:8080`.

## 📂 Configuration
Edit `src/main/resources/clients.json` to add new clients or change SMTP settings.
```json
{
  "clientId": "your_client",
  "apiKey": "your_key",
  "secretKey": "your_secret", 
  "senders": [ ... ]
}
```
