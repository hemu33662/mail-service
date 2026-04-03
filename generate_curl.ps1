# Config (Matches clients.json)
$ApiKey = "test-mail-service-api-key-123"
$SecretKey = "test-mail-service-secret-key-xyz"
$UserId = "employee_test_01"
$Url = "http://localhost:8080/api/v1/mail"

# 1. Generate Timestamp
$Timestamp = [int64](([DateTimeOffset]::UtcNow).ToUnixTimeMilliseconds())

# 2. Create Signature (HMAC-SHA256)
$DataToSign = "$ApiKey$UserId$Timestamp"
$HMACSHA256 = New-Object System.Security.Cryptography.HMACSHA256
$HMACSHA256.Key = [Text.Encoding]::UTF8.GetBytes($SecretKey)
$SignatureBytes = $HMACSHA256.ComputeHash([Text.Encoding]::UTF8.GetBytes($DataToSign))
$Signature = [Convert]::ToBase64String($SignatureBytes)

# 3. Construct CURL Command
$CurlCmd = "curl.exe -X POST ""$Url"" `
  -H ""X-API-KEY: $ApiKey"" `
  -H ""X-USER-ID: $UserId"" `
  -H ""X-TIMESTAMP: $Timestamp"" `
  -H ""X-SIGNATURE: $Signature"" `
  -F ""from=dev.test@gmail.com"" `
  -F ""to=hemanth.nitm@gmail.com"" `
  -F ""subject=Test via CURL"" `
  -F ""body=<h1>Hello from PowerShell CURL generator</h1>"""

Write-Host "`n=== Valid CURL Command (Valid for 5 mins) ===`n" -ForegroundColor Green
Write-Host $CurlCmd -ForegroundColor Yellow
Write-Host "`n==============================================`n"
