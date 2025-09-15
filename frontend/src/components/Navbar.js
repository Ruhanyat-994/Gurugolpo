import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useTheme } from '../contexts/ThemeContext';
import './Navbar.css';

const Navbar = () => {
  const { user, isAuthenticated, logout, isAdmin, isModerator } = useAuth();
  const { isDarkMode, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsMenuOpen(false);
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          <i className="fas fa-graduation-cap"></i>
          Gurugolpo
        </Link>

        <div className={`navbar-menu ${isMenuOpen ? 'active' : ''}`}>
          <div className="navbar-nav">
            <Link to="/" className="nav-link" onClick={() => setIsMenuOpen(false)}>
              <i className="fas fa-home"></i>
              Home
            </Link>

            {isAuthenticated() && (
              <>
                <Link to="/create-post" className="nav-link" onClick={() => setIsMenuOpen(false)}>
                  <i className="fas fa-plus"></i>
                  Create Post
                </Link>
                
                {isModerator() && (
                  <Link to="/moderator" className="nav-link" onClick={() => setIsMenuOpen(false)}>
                    <i className="fas fa-shield-alt"></i>
                    Moderator
                  </Link>
                )}
                
                {isAdmin() && (
                  <Link to="/admin" className="nav-link" onClick={() => setIsMenuOpen(false)}>
                    <i className="fas fa-crown"></i>
                    Admin
                  </Link>
                )}
              </>
            )}
          </div>

          <div className="navbar-auth">
            {isAuthenticated() ? (
              <div className="user-menu">
                <div className="user-info">
                  <span className="user-name">{user?.fullName}</span>
                  <span className="user-role">{user?.role}</span>
                </div>
                <div className="user-actions">
                  <Link to="/profile" className="btn btn-outline btn-sm">
                    <i className="fas fa-user"></i>
                    Profile
                  </Link>
                  <button onClick={handleLogout} className="btn btn-outline btn-sm">
                    <i className="fas fa-sign-out-alt"></i>
                    Logout
                  </button>
                </div>
              </div>
            ) : (
              <div className="auth-buttons">
                <Link to="/login" className="btn btn-outline btn-sm">
                  <i className="fas fa-sign-in-alt"></i>
                  Login
                </Link>
                <Link to="/register" className="btn btn-primary btn-sm">
                  <i className="fas fa-user-plus"></i>
                  Register
                </Link>
              </div>
            )}
          </div>
        </div>

        <div className="navbar-controls">
          <button 
            className="theme-toggle"
            onClick={toggleTheme}
            aria-label={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
            title={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
          >
            <i className={`fas ${isDarkMode ? 'fa-sun' : 'fa-moon'}`}></i>
          </button>
          
          <button 
            className="navbar-toggle"
            onClick={toggleMenu}
            aria-label="Toggle navigation menu"
          >
            <span className="hamburger"></span>
            <span className="hamburger"></span>
            <span className="hamburger"></span>
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
