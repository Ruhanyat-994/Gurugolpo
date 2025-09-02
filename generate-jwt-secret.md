# Generate Strong JWT Secret

## Current Issue
Your current JWT secret `3nF92kLp7TmZyVwBtXr4qH6sUv93PgAi` is only 32 characters long and may not provide sufficient security for JWT signing.

## Recommended Solution
Generate a stronger JWT secret with at least 64 characters.

## Method 1: Using Java (Recommended)
Add this method to your JwtUtil class temporarily:

```java
public static String generateStrongSecret() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[64]; // 512 bits
    random.nextBytes(bytes);
    return Base64.getEncoder().withoutPadding().encodeToString(bytes);
}
```

Then call it once to generate a new secret and update your `application.properties`.

## Method 2: Using Online Generator
Use a secure online JWT secret generator:
- https://generate-secret.vercel.app/64
- https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx

## Method 3: Using Command Line
```bash
# On Linux/Mac
openssl rand -base64 64

# On Windows PowerShell
[System.Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(64))
```

## Method 4: Using Node.js
```bash
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

## Update Application Properties
Replace your current JWT secret in `src/main/resources/application.properties`:

```properties
# Old (weak)
jwt.secret=3nF92kLp7TmZyVwBtXr4qH6sUv93PgAi

# New (strong) - example
jwt.secret=YourGeneratedSecretHereShouldBeAtLeast64CharactersLongAndContainRandomBytes
```

## Security Requirements
- **Minimum length**: 64 characters
- **Character set**: Base64 encoded random bytes
- **Entropy**: High randomness (cryptographically secure)
- **Storage**: Keep secret and never commit to version control

## After Updating
1. Restart your Spring Boot application
2. Test login to generate a new JWT token
3. Verify the new token has proper structure and length
4. Test the protected endpoints with the new token

## Expected Token Structure
A properly generated JWT token should:
- Have 3 parts separated by dots (header.payload.signature)
- Each part should be Base64 encoded
- The signature should be at least 64 characters long
- Timestamps should be current (not in 2025)
