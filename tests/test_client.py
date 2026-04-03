import hmac
import hashlib
import base64
import time
import requests

# Config (Matches clients.json)
API_KEY = "portfolio-api-key-123"
SECRET_KEY = "portfolio-secret-key-xyz"
USER_ID = "portfolio_contact_form"
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

files = {
    'attachments': open('test.txt', 'rb') 
}

payload = {
    "from": "dev.deloitte@gmail.com",
    "to": "hemanth.nitm@gmail.com",
    "subject": "Test Secure Mail",
    "body": "<h1>Hello Test2</h1><p>This is a secure trace test.</p>"
}

try:
    response = requests.post(URL, headers=headers, data=payload, files=files)
    print(f"Status: {response.status_code}")
    print(response.text)
except Exception as e:
    print(e)
