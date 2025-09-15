import React, { createContext, useContext, useState, useEffect } from 'react';

const ThemeContext = createContext();

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

export const ThemeProvider = ({ children }) => {
  const [isDarkMode, setIsDarkMode] = useState(() => {
    // Default to light mode for clean white backgrounds
    return false;
  });

  // Immediate initialization
  useEffect(() => {
    const root = document.documentElement;
    const html = document.documentElement;
    const body = document.body;
    
    root.classList.remove('dark');
    html.classList.remove('dark');
    body.classList.remove('dark');
    
    localStorage.setItem('theme', 'light');
    console.log('Initialized light mode on html, body, and root');
  }, []);

  useEffect(() => {
    // Update CSS custom properties
    const root = document.documentElement;
    const html = document.documentElement;
    const body = document.body;
    
    console.log('Theme changing to:', isDarkMode ? 'dark' : 'light');
    
    if (isDarkMode) {
      root.classList.add('dark');
      html.classList.add('dark');
      body.classList.add('dark');
      console.log('Added dark class to html, body, and root elements');
    } else {
      root.classList.remove('dark');
      html.classList.remove('dark');
      body.classList.remove('dark');
      console.log('Removed dark class from html, body, and root elements');
    }
    
    // Save to localStorage
    localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
    console.log('Saved theme to localStorage:', isDarkMode ? 'dark' : 'light');
  }, [isDarkMode]);

  const toggleTheme = () => {
    setIsDarkMode(prev => !prev);
  };

  const value = {
    isDarkMode,
    toggleTheme,
  };

  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  );
};
