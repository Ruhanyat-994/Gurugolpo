# End-to-End University Affaires Blogging System 

A comprehensive backend system for a university affaires and teacher evaluation forum built with Spring Boot, featuring role-based access control, post management, voting system, and admin/moderator panels.

## Features

### Core Features (User-Side)
- **Registration & Login**: Students register with university name, email, username, and password
- **Posts**: Create, edit, delete posts with title and content
- **Comments**: Comment on posts with edit/delete capabilities
- **Voting**: Upvote/downvote posts and comments (Reddit-style ranking)
- **Search & Filtering**: Search by keywords and filter by university

### Admin & Moderator Features
- **Admin Panel**: 
  - Register moderators and assign to universities
  - Dashboard with top-voted posts and statistics
  - Toggle post management (approval required)
  - Delete any post or comment
- **Moderator Panel**:
  - Moderate posts from assigned university
  - Approve/reject posts
  - Dashboard with moderation queue

### Security Features
- **OWASP Top 10 Protection**:
  - SQL Injection prevention (parameterized queries)
  - XSS protection (input sanitization)
  - CSRF protection
  - Secure password hashing (BCrypt)
  - Role-based access control
- **Rate Limiting**: Prevents abuse with configurable limits
- **Input Validation**: Comprehensive validation and sanitization
- **Security Headers**: Protection against common web vulnerabilities

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.5.3
- **Security**: Spring Security with JWT
- **Database**: MySQL 8 with JDBC (no ORM)
- **Architecture**: Monolithic
- **Containerization**: Docker & Docker Compose
- **Documentation**: Swagger/OpenAPI 3

## API Endpoints

### Public Endpoints
- `POST /api/public/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/posts` - Get all posts
- `GET /api/posts/{id}` - Get post by ID
- `GET /api/posts/search?q={query}` - Search posts
- `GET /api/posts/university/{university}` - Get posts by university

### Authenticated Endpoints
- `POST /api/posts/create` - Create post
- `PUT /api/posts/{id}` - Update post
- `DELETE /api/posts/{id}` - Delete post
- `POST /api/comments/{postId}` - Add comment
- `PUT /api/comments/{commentId}` - Update comment
- `DELETE /api/comments/{commentId}` - Delete comment
- `POST /api/votes/posts/{postId}/upvote` - Upvote post
- `POST /api/votes/posts/{postId}/downvote` - Downvote post
- `POST /api/votes/comments/{commentId}/upvote` - Upvote comment
- `POST /api/votes/comments/{commentId}/downvote` - Downvote comment

### Admin Endpoints
- `GET /api/admin/dashboard` - Admin dashboard
- `POST /api/admin/settings/post-management` - Toggle post management
- `POST /api/admin/users/{userId}/promote` - Promote user to moderator
- `DELETE /api/admin/users/{userId}` - Delete user
- `DELETE /api/admin/posts/{postId}` - Delete any post
- `DELETE /api/admin/comments/{commentId}` - Delete any comment

### Moderator Endpoints
- `GET /api/moderator/dashboard` - Moderator dashboard
- `GET /api/moderator/pending-posts` - Get pending posts
- `POST /api/moderator/posts/{postId}/approve` - Approve post
- `POST /api/moderator/posts/{postId}/reject` - Reject post
- `DELETE /api/moderator/posts/{postId}` - Delete post

## Database Schema

### Tables
- `users` - User accounts with roles (STUDENT, MODERATOR, ADMIN)
- `posts` - Forum posts with approval status
- `comments` - Comments on posts
- `post_votes` - Votes on posts
- `comment_votes` - Votes on comments
- `system_settings` - System configuration

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+
- Docker & Docker Compose (optional)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd university-evaluation-forum
   ```

2. **Set up MySQL database**
   ```sql
   CREATE DATABASE university_forum;
   CREATE USER 'forumuser'@'localhost' IDENTIFIED BY 'forumpass';
   GRANT ALL PRIVILEGES ON university_forum.* TO 'forumuser'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure environment variables**
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/university_forum
   export SPRING_DATASOURCE_USERNAME=forumuser
   export SPRING_DATASOURCE_PASSWORD=forumpass
   export JWT_SECRET=your-secret-key
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

### Docker Deployment

1. **Build and run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

2. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/docs
   - Health Check: http://localhost:8080/actuator/health

## Configuration

### Environment Variables
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `SPRING_PROFILES_ACTIVE` - Active profile (dev/prod)

### Rate Limiting
- Posts per day: 5
- Comments per hour: 20
- Login attempts per hour: 5
- Votes per hour: 50

## Security Considerations

### Implemented Security Measures
1. **Input Validation**: All inputs are validated and sanitized
2. **SQL Injection Prevention**: Parameterized queries only
3. **XSS Protection**: Input sanitization and output encoding
4. **CSRF Protection**: Spring Security CSRF tokens
5. **Authentication**: JWT-based stateless authentication
6. **Authorization**: Role-based access control
7. **Rate Limiting**: Prevents abuse and DoS attacks
8. **Security Headers**: Comprehensive security headers
9. **Password Security**: BCrypt hashing with salt

### Security Headers
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security`
- `Content-Security-Policy`
- `Referrer-Policy`

## API Documentation

The API is documented using Swagger/OpenAPI 3. Access the interactive documentation at:
- Development: http://localhost:8080/docs
- Production: https://your-domain.com/docs

## Monitoring

### Health Checks
- Application health: `/actuator/health`
- Database health: Included in application health
- Custom health indicators for critical components

### Metrics
- Application metrics: `/actuator/metrics`
- JVM metrics, HTTP requests, database connections
- Custom business metrics

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Check the API documentation
- Review the security guidelines

## Changelog

### Version 1.0.0
- Initial release
- Core forum functionality
- Admin and moderator panels
- Security implementation
- Docker support
