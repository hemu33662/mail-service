import hmac
import hashlib
import base64
import time
import requests

# Config (Matches clients.json)
API_KEY = "test-mail-service-api-key-123"
SECRET_KEY = "test-mail-service-secret-key-xyz"
USER_ID = "test_user"
URL = "http://localhost:8080/api/v1/mail"

# 1. Generate Timestamp
timestamp = str(int(time.time() * 1000))

# 2. Create Signature
# Format: apiKey + userId + timestamp
data_to_sign = f"{API_KEY}{USER_ID}{timestamp}"
signature = base64.b64encode(
    hmac.new(
        SECRET_KEY.encode('utf-8'),
        data_to_sign.encode('utf-8'),
        hashlib.sha256
    ).digest()
).decode('utf-8')

print(f"Timestamp: {timestamp}")
print(f"Signature: {signature}")

# 3. Send Request
headers = {
    "X-API-KEY": API_KEY,
    "X-USER-ID": USER_ID,
    "X-SIGNATURE": signature,
    "X-TIMESTAMP": timestamp
}

payload = {
    "from": "generatedmailservice@gmail.com",
    "to": "hemanth.nitm@gmail.com",
    "subject": "System Test - Secure Mail Service",
    "body": """
    <div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9;">
        <h1 style="color: #4A90E2;">Mail Service Test Successful!</h1>
        <p>This is a test email sent using the <b>Test Mail Service</b> client configuration.</p>
        <hr style="border: 0; border-top: 1px solid #eee;">
        <p style="font-size: 0.9em; color: #666;">
            <b>Client ID:</b> test_mail_service<br>
            <b>Status:</b> Verified<br>
            <b>Environment:</b> SANDBOX
        </p>
    </div>
    """
}

# Attachments (optional)
files = {
    'attachments': ('test.txt', 'This is a test attachment content.'.encode('utf-8'))
}

print(f"Sending test email to {payload['to']}...")
try:
    response = requests.post(URL, headers=headers, data=payload, files=files)
    print(f"Status Code: {response.status_code}")
    print("Response Content:")
    print(response.text)
except requests.exceptions.ConnectionError:
    print("Error: Could not connect to the server at http://localhost:8080.")
    print("Please make sure the Secure Mail Service is running.")
except Exception as e:
    print(f"An error occurred: {e}")
