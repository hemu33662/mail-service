# Secure Mail Client SDK Examples

This directory contains ready-to-use examples for connecting to the Secure Mail Service.
Each example implements the required **HMAC Security** logic (Signature generation + Timestamping).

## Prerequisites
Your clients need the following credentials (provided by you):
- **Base URL**: e.g. `http://localhost:8080/api/v1`
- **API Key**: Unique key for the client
- **Secret Key**: Confidential key for signing requests
- **User ID**: ID of the user triggering the email

## 🐍 Python Example
1. Navigate to `python/`
2. Install dependencies: `pip install requests`
3. Run: `python mail_client.py`

## 🟢 Node.js Example
1. Navigate to `nodejs/`
2. Install dependencies: `npm install axios form-data`
3. Run: `node mail_client.js`

## ⚠️ Security Note
The **Secret Key** is used to sign requests and **MUST NEVER** be exposed on the frontend or shared publicly. These SDKs are intended for **Back-End** integration only.
