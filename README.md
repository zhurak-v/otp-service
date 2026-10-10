# OTP Service

Spring Boot microservice for creating, verifying and revoking one-time passwords. Codes are stored as keyed hashes, expire after a configurable lifetime, and are destroyed after too many failed attempts or one successful verification.

The service generates the code and returns it to the caller. Delivering the code to the user (SMS, email, messenger) is the caller's job.

## Features

- Cryptographically secure code generation (`SecureRandom`), configurable length and alphabet (digits, letters or both)
- Per-OTP lifetime and attempt limit
- Codes stored as HMAC-SHA256 hashes, compared in constant time
- Single use: a token is deleted after successful verification
- A token is deleted when its attempts run out or when it is found expired
- Per-id locking shared by the API and the cleaner, so concurrent verify requests cannot bypass the attempt limit (single instance)
- Background cleanup of expired tokens every 10 seconds, in batches

## Requirements

- Java 25
- Maven (wrapper included)

## Run

```bash
./mvnw spring-boot:run
```

The service starts on `http://localhost:<port>`.

## Configuration

| Property | Default | Description |
|---|---|---|
| `otp.hash-secret` | random per start | Secret key for HMAC hashing of codes. Set it explicitly if hashes must survive a restart |
| `server.port` | `8080` | HTTP port (Spring Boot default; override in `application.yml`) |

## API

Base path: `/otp`

### Create OTP

`POST /otp/create` → `201 Created`

```json
{
  "id": "login:user-42",
  "lifetime": "PT5M",
  "attempts": 3,
  "generateCodeReq": {
    "length": 6,
    "characterTypes": ["DIGITS"]
  }
}
```

| Field | Constraints |
|---|---|
| `id` | Not blank, up to 128 characters, unique among active OTPs |
| `lifetime` | ISO-8601 duration from `PT1S` to `PT24H` |
| `attempts` | 1 to 10 |
| `generateCodeReq.length` | 1 to 32 |
| `generateCodeReq.characterTypes` | Non-empty set of `DIGITS`, `LETTERS` |

Response:

```json
{ "password": "483920" }
```

### Verify OTP

`POST /otp/verify` → `200 OK`

```json
{ "id": "login:user-42", "password": "483920" }
```

Response:

```json
{ "verified": true, "attempts": 3 }
```

- Correct code: `verified: true`, the token is deleted.
- Wrong code: `verified: false` and the number of remaining attempts. When the last attempt is used, `attempts` is `0` and the token is deleted.

### Delete OTP

`DELETE /otp/delete/{id}` → `204 No Content`

## Errors

All errors share one format:

```json
{
  "timestamp": "2026-10-10T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "code": "<error code>",
  "message": "OTP not found",
  "errors": { "field": "validation message" }
}
```

`errors` is present only for validation failures.

| Status | Cause |
|---|---|
| 400 | Validation failed, malformed JSON, or the OTP is expired |
| 404 | No OTP with this `id` (never created, deleted, used up or cleaned) |
| 409 | An OTP with this `id` already exists |
| 500 | Unexpected server error |

## Limitations

- Storage is in memory: tokens are lost on restart and the service cannot be scaled horizontally. Locking is per JVM.
- The number of active OTPs is not capped, so an exposed `create` endpoint can exhaust memory.
- The create response contains the plain code, so the service must only be reachable by trusted backend clients. There is no built-in authentication.
- Attempt limiting applies to verification only. There is no rate limit on creating OTPs.

## Tests

```bash
./mvnw test
```

## License

Apache-2.0
