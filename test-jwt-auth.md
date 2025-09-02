# JWT Authentication Debugging Guide

## Issue Description
The endpoint `/api/posts/create` returns 403 Forbidden when called with a JWT token in the Authorization header.

## Debugging Steps

### 1. Test JWT Token Generation
First, test if the login endpoint is working correctly:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-email@example.com",
    "password": "your-password"
  }'
```

This should return a JWT token. Copy the token from the response.

### 2. Test JWT Token Validation
Use the debug endpoint to validate your JWT token:

```bash
curl -X GET http://localhost:8080/api/auth/debug \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

This will show you:
- The extracted email from the token
- Whether the token is valid
- Any validation errors

### 3. Test Protected Endpoint
Try calling the protected endpoint with your JWT token:

```bash
curl -X POST http://localhost:8080/api/posts/create \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Post",
    "content": "This is a test post",
    "university": "Test University"
  }'
```

### 4. Check Application Logs
Look at your application logs for any error messages. The enhanced logging should show:
- JWT extraction attempts
- User details loading
- Authentication context setting
- Any validation failures

## Common Issues and Solutions

### Issue 1: JWT Secret Mismatch
- Ensure the JWT secret in `application.properties` matches what was used to generate the token
- Check if the secret contains special characters that might cause encoding issues

### Issue 2: User Role Issues
- Verify the user has the correct role in the database
- Check if the user account is active (`isActive = true`)

### Issue 3: Token Expiration
- JWT tokens expire after 10 hours (as configured in JwtUtil)
- Generate a new token if the current one is expired

### Issue 4: Database Connection
- Ensure the database is running and accessible
- Check if the user exists in the database

## Expected Behavior

1. **Login**: Should return a JWT token and user details
2. **Debug**: Should return token validation information
3. **Create Post**: Should return the created post or an error message (not 403)

## If Still Getting 403

1. Check the application logs for detailed error messages
2. Verify the JWT token format (should start with "Bearer ")
3. Ensure the user exists and is active in the database
4. Check if there are any CORS issues
5. Verify the request is reaching the correct endpoint

## Testing with Postman

If using Postman:
1. Set the Authorization header to: `Bearer YOUR_JWT_TOKEN_HERE`
2. Ensure the Content-Type is set to `application/json`
3. Check the response status and body for error details
