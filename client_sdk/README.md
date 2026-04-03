# 🚀 Secure Mail Service: Client SDK Examples

This directory provides pre-built integration examples for the Secure Mail Service. These examples handle the complex **HMAC Signature** logic, ensuring your requests are authenticated and secure.

---

## 🛠️ How to Integrate

To connect your application to the Secure Mail Service, you need to implement **HMAC-SHA256 signature generation**. You can use the provided classes in your backend:

### 1. Python Integration
Perfect for Flask, Django, or data processing scripts.
- **Location**: `python/mail_client.py`
- **Setup**: `pip install requests`
- **Usage**: Copy the `SecureMailClient` class into your project and initialize with your credentials.

### 2. Node.js Integration
Ideal for Express, NestJS, or Next.js backends.
- **Location**: `nodejs/mail_client.js`
- **Setup**: `npm install axios form-data`
- **Usage**: Import the `SecureMailClient` class and use it in your service layer.

---

## 🔑 Your Credentials

Clients need the following information to authenticate:

| Field | Description |
| :--- | :--- |
| **Base URL** | The API endpoint (e.g., `http://api.yourdomain.com/v1`) |
| **API Key** | Your unique public identification key. |
| **Secret Key** | **CONFIDENTIAL**. Used for signing. Never share this! |
| **User ID** | The unique ID of the sender/system user. |

---

## 🔒 Security Best Practices

1. **Backend Only**: Never use these SDKs in the browser (frontend). Your **Secret Key** will be exposed to users, compromising your entire security.
2. **Environment Variables**: Store your keys in `.env` files, not directly in the code.
3. **Clock Sync**: Ensure your server's time is accurate. Requests with a timestamp older than 5 minutes will be rejected for security (anti-replay).

---

## ⚡ Quick Test
To verify your connection, you can run the examples directly after entering your credentials in the `Usage Example` section at the bottom of each file.
