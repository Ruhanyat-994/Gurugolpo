import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { universitiesAPI } from '../services/api';
import './AuthPopup.css';

const AuthPopup = ({ isOpen, onClose, mode = 'login', onSuccess }) => {
  const [currentMode, setCurrentMode] = useState(mode);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    fullName: '',
    confirmPassword: '',
    university: '',
    terms: false,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [universities, setUniversities] = useState([]);

  const { login, register } = useAuth();

  React.useEffect(() => {
    if (currentMode === 'register') {
      const fetchUniversities = async () => {
        try {
          const response = await universitiesAPI.getAllUniversities();
          setUniversities(response.data);
        } catch (error) {
          console.error('Failed to fetch universities:', error);
        }
      };
      fetchUniversities();
    }
  }, [currentMode]);

  React.useEffect(() => {
    if (isOpen) {
      setCurrentMode(mode);
      setFormData({
        email: '',
        password: '',
        fullName: '',
        confirmPassword: '',
        university: '',
        terms: false,
      });
      setError('');
      setSuccess('');
    }
  }, [isOpen, mode]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    if (error) setError('');
    if (success) setSuccess('');
  };

  const validateForm = () => {
    if (currentMode === 'register') {
      if (formData.password !== formData.confirmPassword) {
        setError('Passwords do not match');
        return false;
      }
      if (formData.password.length < 6) {
        setError('Password must be at least 6 characters long');
        return false;
      }
      if (!formData.terms) {
        setError('You must accept the terms and conditions');
        return false;
      }
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    if (!validateForm()) {
      setLoading(false);
      return;
    }

    try {
      if (currentMode === 'login') {
        const result = await login({
          email: formData.email,
          password: formData.password
        });
        
        if (result.success) {
          setSuccess('Login successful!');
          setTimeout(() => {
            onSuccess && onSuccess(result.user);
            onClose();
          }, 1000);
        } else {
          setError(result.error);
        }
      } else {
        const { confirmPassword, terms, ...userData } = formData;
        const result = await register(userData);
        
        if (result.success) {
          setSuccess('Registration successful! You can now sign in.');
          setTimeout(() => {
            setCurrentMode('login');
            setFormData({
              email: formData.email,
              password: '',
              fullName: '',
              confirmPassword: '',
              university: '',
              terms: false,
            });
          }, 2000);
        } else {
          setError(result.error);
        }
      }
    } catch (error) {
      setError('An unexpected error occurred');
    }
    
    setLoading(false);
  };

  const switchMode = () => {
    setCurrentMode(currentMode === 'login' ? 'register' : 'login');
    setFormData({
      email: '',
      password: '',
      fullName: '',
      confirmPassword: '',
      university: '',
      terms: false,
    });
    setError('');
    setSuccess('');
  };

  if (!isOpen) return null;

  return (
    <div className="auth-popup-overlay" onClick={onClose}>
      <div className="auth-popup" onClick={(e) => e.stopPropagation()}>
        <div className="auth-popup-header">
          <h2 className="auth-popup-title">
            <i className={`fas ${currentMode === 'login' ? 'fa-sign-in-alt' : 'fa-user-plus'}`}></i>
            {currentMode === 'login' ? 'Sign In' : 'Create Account'}
          </h2>
          <button className="auth-popup-close" onClick={onClose}>
            <i className="fas fa-times"></i>
          </button>
        </div>

        <form onSubmit={handleSubmit} className="auth-popup-form">
          {error && (
            <div className="alert alert-error">
              <i className="fas fa-exclamation-circle"></i>
              {error}
            </div>
          )}

          {success && (
            <div className="alert alert-success">
              <i className="fas fa-check-circle"></i>
              {success}
            </div>
          )}

          <div className="form-group">
            <label htmlFor="email" className="form-label">
              <i className="fas fa-envelope"></i>
              Email Address
            </label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="form-input"
              placeholder="Enter your email"
              required
              disabled={loading}
            />
          </div>

          {currentMode === 'register' && (
            <>
              <div className="form-group">
                <label htmlFor="fullName" className="form-label">
                  <i className="fas fa-user"></i>
                  Full Name
                </label>
                <input
                  type="text"
                  id="fullName"
                  name="fullName"
                  value={formData.fullName}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="Enter your full name"
                  required
                  disabled={loading}
                />
              </div>

              <div className="form-group">
                <label htmlFor="university" className="form-label">
                  <i className="fas fa-university"></i>
                  University
                </label>
                <select
                  id="university"
                  name="university"
                  value={formData.university}
                  onChange={handleChange}
                  className="form-input"
                  required
                  disabled={loading}
                >
                  <option value="">Select your university</option>
                  {universities.map((uni) => (
                    <option key={uni.id} value={uni.name}>
                      {uni.name}
                    </option>
                  ))}
                </select>
              </div>
            </>
          )}

          <div className="form-group">
            <label htmlFor="password" className="form-label">
              <i className="fas fa-lock"></i>
              Password
            </label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="form-input"
              placeholder={currentMode === 'login' ? 'Enter your password' : 'Create a password (min 6 characters)'}
              required
              disabled={loading}
            />
          </div>

          {currentMode === 'register' && (
            <>
              <div className="form-group">
                <label htmlFor="confirmPassword" className="form-label">
                  <i className="fas fa-lock"></i>
                  Confirm Password
                </label>
                <input
                  type="password"
                  id="confirmPassword"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="Confirm your password"
                  required
                  disabled={loading}
                />
              </div>

              <div className="form-group">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="terms"
                    checked={formData.terms}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  <span className="checkbox-text">
                    I agree to the Terms and Conditions and Privacy Policy
                  </span>
                </label>
              </div>
            </>
          )}

          <button
            type="submit"
            className="btn btn-primary w-full"
            disabled={loading}
          >
            {loading ? (
              <>
                <div className="loading-spinner"></div>
                {currentMode === 'login' ? 'Signing In...' : 'Creating Account...'}
              </>
            ) : (
              <>
                <i className={`fas ${currentMode === 'login' ? 'fa-sign-in-alt' : 'fa-user-plus'}`}></i>
                {currentMode === 'login' ? 'Sign In' : 'Create Account'}
              </>
            )}
          </button>
        </form>

        <div className="auth-popup-footer">
          <p>
            {currentMode === 'login' ? "Don't have an account?" : "Already have an account?"}{' '}
            <button onClick={switchMode} className="auth-link">
              {currentMode === 'login' ? 'Create one here' : 'Sign in here'}
            </button>
          </p>
        </div>

        {currentMode === 'login' && (
          <div className="auth-popup-demo">
            <h4>Demo Accounts</h4>
            <div className="demo-accounts">
              <div className="demo-account">
                <strong>Admin:</strong> admin@gurugoppo.com / admin
              </div>
              <div className="demo-account">
                <strong>Moderator:</strong> moderator@gurugoppo.com / moderator
              </div>
              <div className="demo-account">
                <strong>User:</strong> user@gurugoppo.com / user123
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuthPopup;
