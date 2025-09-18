import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import AuthPopup from '../components/AuthPopup';

const Login = () => {
  const [showAuthPopup, setShowAuthPopup] = useState(false);
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/';

  useEffect(() => {
    // If user is already authenticated, redirect them
    if (isAuthenticated()) {
      navigate(from, { replace: true });
    } else {
      // Show the auth popup immediately
      setShowAuthPopup(true);
    }
  }, [isAuthenticated, navigate, from]);

  const handleAuthSuccess = (user) => {
    setShowAuthPopup(false);
    navigate(from, { replace: true });
  };

  const handleClose = () => {
    setShowAuthPopup(false);
    navigate(from, { replace: true });
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
        mode="login"
        onSuccess={handleAuthSuccess}
      />
    </div>
  );
};

export default Login;
