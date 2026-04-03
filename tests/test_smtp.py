import smtplib
from email.mime.text import MIMEText

SMTP_SERVER = "smtp.gmail.com"
SMTP_PORT = 587
USERNAME = "generatedmailservice@gmail.com"
PASSWORD = "dtpy duox uuoe guzg"

msg = MIMEText("Test body")
msg['Subject'] = "SMTP Test"
msg['From'] = USERNAME # Try from the registered username first
msg['To'] = "hemanth.nitm@gmail.com"

print(f"Connecting to {SMTP_SERVER}:{SMTP_PORT}...")
try:
    server = smtplib.SMTP(SMTP_SERVER, SMTP_PORT)
    server.set_debuglevel(1)
    server.starttls()
    print("Logging in...")
    server.login(USERNAME, PASSWORD)
    print("Sending mail...")
    server.send_message(msg)
    server.quit()
    print("Success!")
except Exception as e:
    print(f"Failed: {e}")
