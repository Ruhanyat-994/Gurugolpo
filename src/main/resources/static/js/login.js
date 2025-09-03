// Login Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const loginBtn = document.querySelector('.login-btn');
    const googleBtn = document.getElementById('googleLogin');
    const messageContainer = document.getElementById('messageContainer');
    const messageContent = document.getElementById('messageContent');

    // Initialize page
    init();

    function init() {
        setupEventListeners();
        checkForRedirect();
        focusFirstInput();
    }

    function setupEventListeners() {
        // Form submission
        loginForm.addEventListener('submit', handleLogin);

        // Google login
        googleBtn.addEventListener('click', handleGoogleLogin);

        // Input validation
        emailInput.addEventListener('blur', validateEmail);
        passwordInput.addEventListener('blur', validatePassword);

        // Real-time validation
        emailInput.addEventListener('input', clearFieldError);
        passwordInput.addEventListener('input', clearFieldError);

        // Enter key handling
        passwordInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                handleLogin(e);
            }
        });

        // Auto-focus management
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Tab') {
                // Handle tab navigation
                const focusableElements = loginForm.querySelectorAll('input, button, a');
                const firstElement = focusableElements[0];
                const lastElement = focusableElements[focusableElements.length - 1];

                if (e.shiftKey && document.activeElement === firstElement) {
                    e.preventDefault();
                    lastElement.focus();
                } else if (!e.shiftKey && document.activeElement === lastElement) {
                    e.preventDefault();
                    firstElement.focus();
                }
            }
        });
    }

    async function handleLogin(event) {
        event.preventDefault();

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        // Validate inputs
        if (!validateForm(email, password)) {
            return;
        }

        try {
            // Show loading state
            setLoadingState(true);
            clearMessages();

            // Attempt login
            const response = await api.login(email, password);

            // Store user data
            if (response.user) {
                localStorage.setItem('userData', JSON.stringify(response.user));
            }

            // Show success message
            showMessage('Login successful! Redirecting...', 'success');

            // Redirect based on user role
            setTimeout(() => {
                redirectAfterLogin(response.user);
            }, 1500);

        } catch (error) {
            console.error('Login failed:', error);
            showMessage(error.message || 'Login failed. Please check your credentials.', 'error');
        } finally {
            setLoadingState(false);
        }
    }

    function handleGoogleLogin() {
        // For now, show a message that Google login is not implemented
        showMessage('Google login is not yet implemented. Please use email and password.', 'error');
        
        // In a real implementation, you would:
        // 1. Initialize Google OAuth
        // 2. Handle the OAuth flow
        // 3. Send the Google token to your backend
    }

    function validateForm(email, password) {
        let isValid = true;

        // Clear previous errors
        clearFieldErrors();

        // Validate email
        if (!email) {
            showFieldError(emailInput, 'Email is required');
            isValid = false;
        } else if (!isValidEmail(email)) {
            showFieldError(emailInput, 'Please enter a valid email address');
            isValid = false;
        }

        // Validate password
        if (!password) {
            showFieldError(passwordInput, 'Password is required');
            isValid = false;
        } else if (password.length < 6) {
            showFieldError(passwordInput, 'Password must be at least 6 characters');
            isValid = false;
        }

        return isValid;
    }

    function validateEmail() {
        const email = emailInput.value.trim();
        if (email && !isValidEmail(email)) {
            showFieldError(emailInput, 'Please enter a valid email address');
            return false;
        }
        clearFieldError(emailInput);
        return true;
    }

    function validatePassword() {
        const password = passwordInput.value;
        if (password && password.length < 6) {
            showFieldError(passwordInput, 'Password must be at least 6 characters');
            return false;
        }
        clearFieldError(passwordInput);
        return true;
    }

    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    function showFieldError(input, message) {
        input.classList.add('error');
        
        // Remove existing error message
        const existingError = input.parentNode.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }

        // Add new error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
        input.parentNode.appendChild(errorDiv);
    }

    function clearFieldError(input) {
        input.classList.remove('error');
        const errorMessage = input.parentNode.querySelector('.error-message');
        if (errorMessage) {
            errorMessage.remove();
        }
    }

    function clearFieldErrors() {
        const inputs = [emailInput, passwordInput];
        inputs.forEach(input => clearFieldError(input));
    }

    function setLoadingState(loading) {
        if (loading) {
            loginBtn.classList.add('loading');
            loginBtn.disabled = true;
            emailInput.disabled = true;
            passwordInput.disabled = true;
            googleBtn.disabled = true;
        } else {
            loginBtn.classList.remove('loading');
            loginBtn.disabled = false;
            emailInput.disabled = false;
            passwordInput.disabled = false;
            googleBtn.disabled = false;
        }
    }

    function showMessage(message, type) {
        if (!messageContainer || !messageContent) return;

        messageContent.textContent = message;
        messageContent.className = `message-content ${type}`;
        messageContainer.style.display = 'block';

        // Auto hide after 5 seconds for success messages
        if (type === 'success') {
            setTimeout(() => {
                messageContainer.style.display = 'none';
            }, 5000);
        }
    }

    function clearMessages() {
        if (messageContainer) {
            messageContainer.style.display = 'none';
        }
    }

    function redirectAfterLogin(user) {
        if (!user) {
            window.location.href = '/';
            return;
        }

        // Redirect based on user role
        switch (user.role) {
            case 'ADMIN':
                window.location.href = '/admin/dashboard';
                break;
            case 'MODERATOR':
                window.location.href = '/moderator/dashboard';
                break;
            default:
                window.location.href = '/';
                break;
        }
    }

    function checkForRedirect() {
        // Check if user is already logged in
        const token = localStorage.getItem('authToken');
        if (token) {
            // Verify token is still valid
            api.checkAuth().then(isValid => {
                if (isValid) {
                    // User is already logged in, redirect to appropriate page
                    const userData = localStorage.getItem('userData');
                    if (userData) {
                        const user = JSON.parse(userData);
                        redirectAfterLogin(user);
                    } else {
                        window.location.href = '/';
                    }
                }
            }).catch(() => {
                // Token is invalid, clear it
                api.clearToken();
                localStorage.removeItem('userData');
            });
        }

        // Check for redirect parameter
        const urlParams = new URLSearchParams(window.location.search);
        const redirect = urlParams.get('redirect');
        if (redirect) {
            // Store redirect URL for after login
            sessionStorage.setItem('redirectAfterLogin', redirect);
        }
    }

    function focusFirstInput() {
        // Focus the first empty input or email input
        if (!emailInput.value) {
            emailInput.focus();
        } else if (!passwordInput.value) {
            passwordInput.focus();
        }
    }

    // Handle forgot password link
    const forgotPasswordLink = document.querySelector('.forgot-password');
    if (forgotPasswordLink) {
        forgotPasswordLink.addEventListener('click', function(e) {
            e.preventDefault();
            showMessage('Password reset functionality is not yet implemented.', 'error');
        });
    }

    // Handle create account link
    const createAccountLink = document.querySelector('.create-account');
    if (createAccountLink) {
        createAccountLink.addEventListener('click', function(e) {
            e.preventDefault();
            showMessage('Account creation functionality is not yet implemented.', 'error');
        });
    }

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Escape to clear form
        if (e.key === 'Escape') {
            loginForm.reset();
            clearFieldErrors();
            clearMessages();
            emailInput.focus();
        }
    });

    // Form accessibility improvements
    loginForm.addEventListener('keydown', function(e) {
        // Handle form navigation with arrow keys
        if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
            e.preventDefault();
            const focusableElements = Array.from(loginForm.querySelectorAll('input, button, a'));
            const currentIndex = focusableElements.indexOf(document.activeElement);
            
            if (e.key === 'ArrowDown') {
                const nextIndex = (currentIndex + 1) % focusableElements.length;
                focusableElements[nextIndex].focus();
            } else {
                const prevIndex = currentIndex === 0 ? focusableElements.length - 1 : currentIndex - 1;
                focusableElements[prevIndex].focus();
            }
        }
    });

    // Auto-save email (for convenience)
    emailInput.addEventListener('blur', function() {
        if (this.value && isValidEmail(this.value)) {
            localStorage.setItem('lastLoginEmail', this.value);
        }
    });

    // Auto-fill last used email
    const lastEmail = localStorage.getItem('lastLoginEmail');
    if (lastEmail && !emailInput.value) {
        emailInput.value = lastEmail;
        passwordInput.focus();
    }

    // Handle browser back button
    window.addEventListener('popstate', function() {
        // Clear any error messages when navigating back
        clearMessages();
        clearFieldErrors();
    });

    // Performance monitoring
    if ('performance' in window) {
        window.addEventListener('load', function() {
            const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
            console.log(`Login page loaded in ${loadTime}ms`);
        });
    }
});
