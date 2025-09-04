// Register Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const messageContainer = document.getElementById('messageContainer');
    const messageContent = document.getElementById('messageContent');
    const googleBtn = document.getElementById('googleRegister');

    // Form submission handler
    registerForm.addEventListener('submit', async function(event) {
        event.preventDefault();
        
        const formData = new FormData(registerForm);
        const registrationData = {
            fullName: formData.get('fullName'),
            email: formData.get('email'),
            university: formData.get('university'),
            password: formData.get('password'),
            confirmPassword: formData.get('confirmPassword'),
            terms: formData.get('terms')
        };

        // Validate form
        if (!validateForm(registrationData)) {
            return;
        }

        try {
            showLoading(true);
            hideMessage();

            const response = await api.register(registrationData);
            
            if (response.success) {
                showMessage('Account created successfully! Please login with your credentials.', 'success');
                // Clear form
                registerForm.reset();
                // Redirect to login after 2 seconds
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
            } else {
                showMessage(response.message || 'Registration failed. Please try again.', 'error');
            }
        } catch (error) {
            console.error('Registration error:', error);
            showMessage('Registration failed. Please try again.', 'error');
        } finally {
            showLoading(false);
        }
    });

    // Google registration handler
    googleBtn.addEventListener('click', function() {
        showMessage('Google registration not implemented yet', 'error');
    });

    // Form validation
    function validateForm(data) {
        // Check if passwords match
        if (data.password !== data.confirmPassword) {
            showMessage('Passwords do not match', 'error');
            return false;
        }

        // Check password strength
        if (data.password.length < 6) {
            showMessage('Password must be at least 6 characters long', 'error');
            return false;
        }

        // Check if terms are accepted
        if (!data.terms) {
            showMessage('Please accept the Terms & Conditions', 'error');
            return false;
        }

        // Check if university is selected
        if (!data.university) {
            showMessage('Please select your university', 'error');
            return false;
        }

        return true;
    }

    // Show/hide loading spinner
    function showLoading(show) {
        if (show) {
            loadingSpinner.style.display = 'flex';
            registerForm.style.opacity = '0.6';
            registerForm.style.pointerEvents = 'none';
        } else {
            loadingSpinner.style.display = 'none';
            registerForm.style.opacity = '1';
            registerForm.style.pointerEvents = 'auto';
        }
    }

    // Show message
    function showMessage(message, type) {
        messageContent.textContent = message;
        messageContent.className = `message-content ${type}`;
        messageContainer.style.display = 'block';
        
        // Auto-hide success messages
        if (type === 'success') {
            setTimeout(() => {
                hideMessage();
            }, 5000);
        }
    }

    // Hide message
    function hideMessage() {
        messageContainer.style.display = 'none';
    }

    // Real-time password confirmation validation
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    function validatePasswordMatch() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        
        if (confirmPassword && password !== confirmPassword) {
            confirmPasswordInput.style.borderColor = '#dc3545';
        } else {
            confirmPasswordInput.style.borderColor = '#e9ecef';
        }
    }

    confirmPasswordInput.addEventListener('input', validatePasswordMatch);
    passwordInput.addEventListener('input', validatePasswordMatch);

    // Email validation
    const emailInput = document.getElementById('email');
    emailInput.addEventListener('blur', function() {
        const email = this.value;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        if (email && !emailRegex.test(email)) {
            this.style.borderColor = '#dc3545';
            showMessage('Please enter a valid email address', 'error');
        } else {
            this.style.borderColor = '#e9ecef';
            hideMessage();
        }
    });

    // University selection validation
    const universitySelect = document.getElementById('university');
    universitySelect.addEventListener('change', function() {
        if (this.value) {
            this.style.borderColor = '#28a745';
        } else {
            this.style.borderColor = '#e9ecef';
        }
    });
});
