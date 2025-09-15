# Gurugoppo Frontend

A modern React frontend application for the Gurugoppo university discussion platform.

## Features

- 🔐 **Authentication System** - JWT-based login/register with role-based access
- 📝 **Post Management** - Create, read, update, and delete posts
- 💬 **Comment System** - Rich text commenting with voting
- 👍 **Voting System** - Upvote/downvote posts and comments
- 👑 **Admin Dashboard** - Complete platform management
- 🛡️ **Moderator Tools** - Content moderation capabilities
- 📱 **Responsive Design** - Mobile-first, works on all devices
- 🎨 **Modern UI** - Clean, intuitive interface with Tailwind-inspired styling

## Tech Stack

- **React 18** - Modern React with hooks
- **React Router 6** - Client-side routing
- **Axios** - HTTP client for API communication
- **React Quill** - Rich text editor
- **date-fns** - Date manipulation
- **CSS3** - Custom styling with modern features

## Getting Started

### Prerequisites

- Node.js 16+ 
- npm or yarn
- Running Gurugoppo backend server

### Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

4. Open [http://localhost:3000](http://localhost:3000) in your browser

### Backend Connection

The frontend is configured to connect to the backend at `http://localhost:8080`. Make sure your Spring Boot backend is running on this port.

## Project Structure

```
frontend/
├── public/
│   ├── index.html
│   └── manifest.json
├── src/
│   ├── components/
│   │   ├── Navbar.js
│   │   ├── PostCard.js
│   │   └── Comment.js
│   ├── contexts/
│   │   └── AuthContext.js
│   ├── pages/
│   │   ├── Home.js
│   │   ├── Login.js
│   │   ├── Register.js
│   │   ├── PostDetail.js
│   │   ├── CreatePost.js
│   │   ├── Profile.js
│   │   ├── AdminDashboard.js
│   │   └── ModeratorDashboard.js
│   ├── services/
│   │   └── api.js
│   ├── App.js
│   ├── App.css
│   ├── index.js
│   └── index.css
├── package.json
└── README.md
```

## API Integration

The frontend communicates with the backend through a comprehensive API service (`src/services/api.js`) that includes:

- **Authentication API** - Login, register, token management
- **Posts API** - CRUD operations for posts
- **Comments API** - Comment management
- **Voting API** - Vote operations
- **Admin API** - Administrative functions
- **Moderator API** - Moderation tools
- **Universities API** - University management

## Authentication Flow

1. User logs in with email/password
2. Backend returns JWT token and user data
3. Token stored in localStorage
4. Token automatically included in API requests
5. Protected routes check authentication status
6. Token refresh handled automatically

## Demo Accounts

The application includes demo accounts for testing:

- **Admin**: admin@gurugoppo.com / admin
- **Moderator**: moderator@gurugoppo.com / moderator  
- **User**: user@gurugoppo.com / user123

## Available Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run tests
- `npm eject` - Eject from Create React App

## Deployment

1. Build the application:
```bash
npm run build
```

2. Deploy the `build` folder to your web server

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is part of the Gurugoppo platform.
