import hmac
import hashlib
import base64
import time
import requests
import os

class SecureMailClient:
    def __init__(self, base_url, api_key, secret_key, user_id):
        self.base_url = base_url
        self.api_key = api_key
        self.secret_key = secret_key
        self.user_id = user_id

    def _generate_signature(self, timestamp):
        # Format: apiKey + userId + timestamp
        data_to_sign = f"{self.api_key}{self.user_id}{timestamp}"
        
        # HMAC-SHA256
        signature = base64.b64encode(
            hmac.new(
                self.secret_key.encode('utf-8'),
                data_to_sign.encode('utf-8'),
                hashlib.sha256
            ).digest()
        ).decode('utf-8')
        return signature

    def send_mail(self, to, subject, body, from_addr, attachments=None):
        timestamp = str(int(time.time() * 1000))
        signature = self._generate_signature(timestamp)

        headers = {
            "X-API-KEY": self.api_key,
            "X-USER-ID": self.user_id,
            "X-SIGNATURE": signature,
            "X-TIMESTAMP": timestamp
        }

        payload = {
            "from": from_addr,
            "to": to,
            "subject": subject,
            "body": body
        }

        files = []
        if attachments:
            for path in attachments:
                if os.path.exists(path):
                    files.append(('attachments', open(path, 'rb')))

        try:
            url = f"{self.base_url}/mail"
            print(f"Sending request to {url}...")
            response = requests.post(url, headers=headers, data=payload, files=files)
            
            # Close files
            for _, f in files:
                f.close()

            print(f"Response Status: {response.status_code}")
            print(f"Response Body: {response.text}")
            return response.json()
        except Exception as e:
            print(f"Error: {e}")
            return None

# Usage Example
if __name__ == "__main__":
    # Replace with your provided credentials
    CLIENT_API_KEY = "dlt-dev-api-key-123"
    CLIENT_SECRET_KEY = "dlt-dev-secret-key-xyz"
    CLIENT_USER_ID = "employee_test_01"
    
    client = SecureMailClient(
        base_url="http://localhost:8080/api/v1",
        api_key=CLIENT_API_KEY, 
        secret_key=CLIENT_SECRET_KEY, 
        user_id=CLIENT_USER_ID
    )

    # Send an email
    client.send_mail(
        to="recipient@example.com",
        subject="Hello from Python SDK",
        body="<h1>This is a secure test</h1>",
        from_addr="dev-support@deloitte.com"
        # attachments=["document.pdf"]
    )
