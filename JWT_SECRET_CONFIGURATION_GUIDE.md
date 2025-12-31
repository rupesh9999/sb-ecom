# JWT Secret Configuration Guide

**Document Created:** December 31, 2025 | **Timestamp:** 2025-12-31T00:00:00+05:30

---

## Table of Contents
1. [What is JWT Secret?](#what-is-jwt-secret)
2. [Why Do We Need JWT Secret?](#why-do-we-need-jwt-secret)
3. [How to Configure JWT Secret](#how-to-configure-jwt-secret)
4. [Configuration Examples](#configuration-examples)
5. [Using JWT Secret in Java Code](#using-jwt-secret-in-java-code)
6. [Security Best Practices](#security-best-practices)
7. [Common Issues & Solutions](#common-issues--solutions)

---

## What is JWT Secret?

**JWT (JSON Web Token)** is an open standard (RFC 7519) for securely transmitting information between parties as a JSON object. The **JWT Secret** is a cryptographic key used to:

- **Sign** the JWT token during creation
- **Verify** the token's authenticity during validation
- **Ensure** the token hasn't been tampered with

### JWT Token Structure
```
Header.Payload.Signature
```

Example:
```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczNTYyMDAwMH0.abc123signature
```

---

## Why Do We Need JWT Secret?

| Purpose | Description |
|---------|-------------|
| **Authentication** | Verifies user identity without storing session data on server |
| **Authorization** | Controls access to protected resources |
| **Stateless** | Server doesn't need to store session information |
| **Secure** | Tokens are cryptographically signed and can't be forged |
| **Scalable** | Works across multiple servers without session synchronization |

---

## How to Configure JWT Secret

### Step 1: Add Properties to `application.properties`

Open your `src/main/resources/application.properties` file and add:

```properties
# JWT Configuration
spring.app.jwtSecret=your-secret-key-here
spring.app.jwtExpirationMs=86400000
```

### Step 2: Understanding the Properties

| Property | Description | Example Value |
|----------|-------------|---------------|
| `spring.app.jwtSecret` | Secret key for signing tokens | 64+ character string |
| `spring.app.jwtExpirationMs` | Token validity period in milliseconds | 86400000 (24 hours) |

### Expiration Time Reference:
| Duration | Milliseconds |
|----------|--------------|
| 1 hour | 3600000 |
| 6 hours | 21600000 |
| 12 hours | 43200000 |
| 24 hours (1 day) | 86400000 |
| 7 days | 604800000 |
| 30 days | 2592000000 |

---

## Configuration Examples

### Example 1: Basic Configuration (Development)

```properties
# JWT Configuration - Development Environment
spring.app.jwtSecret=mySecretKey123456789012345678901234567890123456789012345678901234
spring.app.jwtExpirationMs=86400000
```

### Example 2: Using Environment Variables (Production - Recommended)

```properties
# JWT Configuration - Production Environment
spring.app.jwtSecret=${JWT_SECRET:defaultSecretKeyForDevelopmentOnly12345678901234567890}
spring.app.jwtExpirationMs=${JWT_EXPIRATION:86400000}
```

**Setting Environment Variables:**

**Windows (PowerShell):**
```powershell
$env:JWT_SECRET="your-super-secure-production-secret-key-here-minimum-64-characters"
$env:JWT_EXPIRATION="86400000"
```

**Windows (Command Prompt):**
```cmd
set JWT_SECRET=your-super-secure-production-secret-key-here-minimum-64-characters
set JWT_EXPIRATION=86400000
```

**Linux/Mac:**
```bash
export JWT_SECRET="your-super-secure-production-secret-key-here-minimum-64-characters"
export JWT_EXPIRATION="86400000"
```

### Example 3: Base64 Encoded Secret

```properties
# JWT Configuration - Base64 Encoded
spring.app.jwtSecret=bXlTdXBlclNlY3VyZUtleUZvckpXVFRva2VuU2lnbmluZzEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MA==
spring.app.jwtExpirationMs=86400000
```

---

## Using JWT Secret in Java Code

### Step 1: Create JWT Utility Class

```java
package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Inject JWT Secret from application.properties
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    // Inject JWT Expiration from application.properties
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Generate JWT Token for authenticated user
     */
    public String generateJwtToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get signing key from JWT Secret
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Alternative: If using plain text secret (not Base64 encoded)
    private Key keyFromPlainText() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Get username from JWT Token
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate JWT Token
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
```

### Step 2: Add Required Dependencies (pom.xml)

```xml
<!-- JWT Dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

---

## Security Best Practices

### ✅ DO's

| Practice | Description |
|----------|-------------|
| **Use Strong Keys** | Minimum 64 characters for HS512, 32 for HS256 |
| **Use Environment Variables** | Never hardcode secrets in source code |
| **Rotate Keys Periodically** | Change secrets every 30-90 days |
| **Use HTTPS** | Always transmit tokens over secure connections |
| **Set Appropriate Expiration** | Balance security and user experience |
| **Use Different Keys per Environment** | Dev, staging, and production should have different secrets |

### ❌ DON'Ts

| Anti-Pattern | Why It's Bad |
|--------------|--------------|
| **Short secrets** | Easy to brute force |
| **Committing secrets to Git** | Exposes secrets in version history |
| **Same secret everywhere** | Compromise in one env affects all |
| **Never rotating keys** | Prolonged exposure increases risk |
| **Logging JWT tokens** | Tokens in logs can be stolen |

### Generating Strong Secret Keys

**Using OpenSSL:**
```bash
openssl rand -base64 64
```

**Using Java:**
```java
import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[64];
        random.nextBytes(key);
        String secret = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated Secret: " + secret);
    }
}
```

**Using PowerShell:**
```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Maximum 256 }))
```

---

## Common Issues & Solutions

### Issue 1: "The specified key byte array is 0 bits"

**Cause:** Empty or null JWT secret

**Solution:**
```properties
# Ensure the secret is properly set
spring.app.jwtSecret=yourActualSecretKeyHere64CharactersMinimumForHS512Algorithm
```

### Issue 2: "JWT signature does not match"

**Cause:** Secret key mismatch between token generation and validation

**Solution:** Ensure the same secret is used across all services

### Issue 3: "JWT token is expired"

**Cause:** Token validity period has passed

**Solution:** 
- Increase expiration time if appropriate
- Implement token refresh mechanism

### Issue 4: "Could not resolve placeholder 'spring.app.jwtSecret'"

**Cause:** Property not found in application.properties

**Solution:**
```properties
# Add to application.properties
spring.app.jwtSecret=yourSecretKey
```

---

## Current Project Configuration

**File:** `src/main/resources/application.properties`

```properties
# JWT Configuration
spring.app.jwtSecret=mySecretKey123456789012345678901234567890123456789012345678901234
spring.app.jwtExpirationMs=86400000
```

| Setting | Current Value | Description |
|---------|---------------|-------------|
| JWT Secret | 64 character string | Signing key |
| Expiration | 86400000 ms | 24 hours validity |

---

## Quick Reference

### Property Format
```properties
spring.app.jwtSecret=<your-secret-key>
spring.app.jwtExpirationMs=<milliseconds>
```

### Java Usage
```java
@Value("${spring.app.jwtSecret}")
private String jwtSecret;

@Value("${spring.app.jwtExpirationMs}")
private int jwtExpirationMs;
```

### Token Generation Flow
```
User Login → Authenticate → Generate JWT → Return Token → Client Stores Token
```

### Token Validation Flow
```
Request with Token → Extract Token → Validate Signature → Check Expiration → Allow/Deny
```

---

## Document Information

| Field | Value |
|-------|-------|
| **Created Date** | December 31, 2025 |
| **Timestamp** | 2025-12-31T00:00:00+05:30 |
| **Author** | Development Team |
| **Version** | 1.0 |
| **Project** | sb-ecom |

---

*This document is part of the sb-ecom project documentation.*

