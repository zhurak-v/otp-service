# OTP Service

A lightweight, secure, and high-performance microservice designed for generating, managing, and validating One-Time Passwords (OTPs). Built using modern architecture principles and fast in-memory storage, it provides an out-of-the-box security layer for any application requiring user verification.

---

## 🚀 Key Functionality & Features

* **Secure Code Generation:** Instantly creates unique temporary verification codes with customizable expiration times (lifetimes).
* **Cryptographic Hashing:** Passwords are never stored in plain text. The service uses secure hashing algorithms to guarantee that even internal data access cannot expose raw codes.
* **Attempt Tracking & Rate Limiting:** Protects against brute-force attacks by limiting the number of allowed verification attempts. If a user exceeds the limit, the token is instantly invalidated and destroyed.
* **Automated Expiry & Cleanup:** Expired codes are automatically purged from memory by a background worker, ensuring optimal resource usage and preventing data clutter.
* **Instant In-Memory Performance:** Operates entirely in memory for lightning-fast response times, eliminating heavy database bottlenecks for short-lived verification tokens.

---

## 💡 Where and Why It Is Useful (Use Cases)

This service can be easily i[pom.xml](pom.xml)ntegrated as an independent module or microservice into various systems where identity confirmation or transaction security is critical:

1. **Two-Factor Authentication (2FA):**
    * *Scenario:* When a user logs into a web dashboard or mobile app, the system generates a secondary verification code sent via SMS, email, or a messenger. The OTP service securely handles the creation, attempt limits, and validation of this code.
2. **Financial Transactions & E-commerce Checkouts:**
    * *Scenario:* Before a user transfers money, changes billing details, or confirms a high-value purchase in an online store, the platform requires a confirmation code. The service ensures the code is single-use, expires quickly, and locks out attackers after multiple failed tries.
3. **Password Resets & Account Recovery:**
    * *Scenario:* When a user forgets their password, they request a reset link or code. The OTP service generates a temporary, highly secure token that becomes invalid immediately after use or after a short time window.
4. **Contact Verification (Email / Phone Number):**
    * *Scenario:* During user registration, platforms often require confirming that an email or phone number belongs to the user. This service manages the short-lived verification codes entered during the signup flow.

---

## 🔌 API Overview

The service exposes a clean and straightforward REST API:
* **Create OTP:** Generates a new verification token with specific security rules and returns the code.
* **Verify OTP:** Validates the user's input against the stored token, automatically clearing successful or exhausted tokens.
* **Delete OTP:** Manually revokes or clears a token when needed.