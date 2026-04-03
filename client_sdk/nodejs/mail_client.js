const axios = require('axios');
const crypto = require('crypto');
const FormData = require('form-data');
const fs = require('fs');

class SecureMailClient {
    constructor(baseUrl, apiKey, secretKey, userId) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.userId = userId;
    }

    generateSignature(timestamp) {
        // Format: apiKey + userId + timestamp
        const dataToSign = `${this.apiKey}${this.userId}${timestamp}`;

        // HMAC-SHA256
        return crypto.createHmac('sha256', this.secretKey)
            .update(dataToSign)
            .digest('base64');
    }

    async sendMail({ to, subject, body, from, attachments = [] }) {
        const timestamp = Date.now().toString();
        const signature = this.generateSignature(timestamp);

        const form = new FormData();
        form.append('from', from);
        form.append('to', to);
        form.append('subject', subject);
        form.append('body', body);

        attachments.forEach(filePath => {
            if (fs.existsSync(filePath)) {
                form.append('attachments', fs.createReadStream(filePath));
            }
        });

        const headers = {
            'X-API-KEY': this.apiKey,
            'X-USER-ID': this.userId,
            'X-TIMESTAMP': timestamp,
            'X-SIGNATURE': signature,
            ...form.getHeaders()
        };

        try {
            const url = `${this.baseUrl}/mail`;
            console.log(`Sending request to ${url}...`);

            const response = await axios.post(url, form, { headers });

            console.log('Response Status:', response.status);
            console.log('Response Body:', response.data);
            return response.data;
        } catch (error) {
            if (error.response) {
                console.error('Error Status:', error.response.status);
                console.error('Error Data:', error.response.data);
            } else {
                console.error('Error:', error.message);
            }
        }
    }
}

// Usage Example
(async () => {
    // Replace with your provided credentials
    const client = new SecureMailClient(
        'http://localhost:8080/api/v1',
        'dlt-dev-api-key-123',
        'dlt-dev-secret-key-xyz',
        'employee_test_01'
    );

    await client.sendMail({
        to: 'recipient@example.com',
        from: 'dev-support@deloitte.com',
        subject: 'Hello from Node.js SDK',
        body: '<h1>This is a secure test</h1>'
        // attachments: ['document.pdf']
    });
})();
