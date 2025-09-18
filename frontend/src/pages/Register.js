import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import AuthPopup from '../components/AuthPopup';

const Register = () => {
  const [showAuthPopup, setShowAuthPopup] = useState(false);
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    // If user is already authenticated, redirect them to home
    if (isAuthenticated()) {
      navigate('/', { replace: true });
    } else {
      // Show the auth popup immediately
      setShowAuthPopup(true);
    }
  }, [isAuthenticated, navigate]);

  const handleAuthSuccess = (user) => {
    setShowAuthPopup(false);
    navigate('/', { replace: true });
  };

  const handleClose = () => {
    setShowAuthPopup(false);
    navigate('/', { replace: true });
  };

  return (
    <div style={{ 
      minHeight: '100vh', 
      background: 'var(--bg-primary)', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center' 
    }}>
      <AuthPopup
        isOpen={showAuthPopup}
        onClose={handleClose}
        mode="register"
        onSuccess={handleAuthSuccess}
      />
    </div>
  );
};

export default Register;
