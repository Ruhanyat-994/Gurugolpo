// Create Post Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const createPostForm = document.getElementById('createPostForm');
    const vibeSelect = document.getElementById('vibe');
    const companyInput = document.getElementById('company');
    const designationInput = document.getElementById('designation');
    const titleInput = document.getElementById('title');
    const contentTextarea = document.getElementById('content');
    const termsCheckbox = document.getElementById('termsCheckbox');
    const shareBtn = document.querySelector('.share-btn');
    const loadingSpinner = document.getElementById('loadingSpinner');

    let isSubmitting = false;
    let autoSaveTimeout = null;

    // Initialize page
    init();

    function init() {
        setupEventListeners();
        initializeTinyMCE();
        checkAuthentication();
        loadDraftData();
    }

    async function checkAuthentication() {
        try {
            const isAuthenticated = await api.checkAuth();
            if (!isAuthenticated) {
                UIUtils.showError('Please login to create posts');
                setTimeout(() => {
                    window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
                }, 2000);
                return;
            }
        } catch (error) {
            console.error('Authentication check failed:', error);
            UIUtils.showError('Authentication failed. Please login again.');
            setTimeout(() => {
                window.location.href = '/login';
            }, 2000);
        }
    }

    function setupEventListeners() {
        // Form submission
        createPostForm.addEventListener('submit', handleFormSubmit);

        // Auto-save functionality
        const inputs = [vibeSelect, companyInput, designationInput, titleInput, contentTextarea];
        inputs.forEach(input => {
            input.addEventListener('input', handleAutoSave);
            input.addEventListener('change', handleAutoSave);
        });

        // Terms checkbox
        termsCheckbox.addEventListener('change', validateForm);

        // Character count for title
        titleInput.addEventListener('input', handleTitleInput);

        // Form validation
        inputs.forEach(input => {
            input.addEventListener('blur', validateField);
        });

        // Keyboard shortcuts
        document.addEventListener('keydown', handleKeyboardShortcuts);

        // Before unload warning
        window.addEventListener('beforeunload', handleBeforeUnload);
    }

    function initializeTinyMCE() {
        if (typeof tinymce !== 'undefined') {
            tinymce.init({
                selector: '#content',
                height: 300,
                menubar: false,
                plugins: [
                    'advlist', 'autolink', 'lists', 'link', 'image', 'charmap', 'preview',
                    'anchor', 'searchreplace', 'visualblocks', 'code', 'fullscreen',
                    'insertdatetime', 'media', 'table', 'help', 'wordcount'
                ],
                toolbar: 'undo redo | blocks | ' +
                    'bold italic forecolor | alignleft aligncenter ' +
                    'alignright alignjustify | bullist numlist outdent indent | ' +
                    'removeformat | help',
                content_style: 'body { font-family: -apple-system, BlinkMacSystemFont, San Francisco, Segoe UI, Roboto, Helvetica Neue, sans-serif; font-size: 14px; }',
                setup: function(editor) {
                    editor.on('change', function() {
                        handleAutoSave();
                    });
                }
            });
        }
    }

    async function handleFormSubmit(event) {
        event.preventDefault();

        if (isSubmitting) return;

        const formData = getFormData();
        
        if (!validateForm()) {
            return;
        }

        try {
            isSubmitting = true;
            setLoadingState(true);
            clearMessages();

            // Create post
            const response = await api.createPost(formData);

            // Clear draft data
            clearDraftData();

            // Show success message
            UIUtils.showSuccess('Post created successfully! Redirecting...');

            // Redirect to the new post
            setTimeout(() => {
                window.location.href = `/post/${response.id}`;
            }, 1500);

        } catch (error) {
            console.error('Failed to create post:', error);
            UIUtils.showError(error.message || 'Failed to create post. Please try again.');
        } finally {
            isSubmitting = false;
            setLoadingState(false);
        }
    }

    function getFormData() {
        const content = typeof tinymce !== 'undefined' && tinymce.get('content') 
            ? tinymce.get('content').getContent() 
            : contentTextarea.value;

        return {
            vibe: vibeSelect.value,
            company: companyInput.value.trim(),
            designation: designationInput.value.trim(),
            title: titleInput.value.trim(),
            content: content
        };
    }

    function validateForm() {
        let isValid = true;
        clearFieldErrors();

        // Validate vibe
        if (!vibeSelect.value) {
            showFieldError(vibeSelect, 'Please select a vibe');
            isValid = false;
        }

        // Validate company
        if (!companyInput.value.trim()) {
            showFieldError(companyInput, 'Company name is required');
            isValid = false;
        }

        // Validate designation
        if (!designationInput.value.trim()) {
            showFieldError(designationInput, 'Designation is required');
            isValid = false;
        }

        // Validate title
        if (!titleInput.value.trim()) {
            showFieldError(titleInput, 'Story title is required');
            isValid = false;
        } else if (titleInput.value.trim().length < 10) {
            showFieldError(titleInput, 'Title must be at least 10 characters');
            isValid = false;
        }

        // Validate content
        const content = typeof tinymce !== 'undefined' && tinymce.get('content') 
            ? tinymce.get('content').getContent() 
            : contentTextarea.value;
        
        if (!content.trim()) {
            showFieldError(contentTextarea, 'Story content is required');
            isValid = false;
        } else if (content.trim().length < 50) {
            showFieldError(contentTextarea, 'Story must be at least 50 characters');
            isValid = false;
        }

        // Validate terms
        if (!termsCheckbox.checked) {
            showFieldError(termsCheckbox, 'You must accept the terms and conditions');
            isValid = false;
        }

        return isValid;
    }

    function validateField(event) {
        const field = event.target;
        clearFieldError(field);

        switch (field.id) {
            case 'vibe':
                if (!field.value) {
                    showFieldError(field, 'Please select a vibe');
                }
                break;
            case 'company':
                if (!field.value.trim()) {
                    showFieldError(field, 'Company name is required');
                }
                break;
            case 'designation':
                if (!field.value.trim()) {
                    showFieldError(field, 'Designation is required');
                }
                break;
            case 'title':
                if (!field.value.trim()) {
                    showFieldError(field, 'Story title is required');
                } else if (field.value.trim().length < 10) {
                    showFieldError(field, 'Title must be at least 10 characters');
                }
                break;
            case 'content':
                const content = typeof tinymce !== 'undefined' && tinymce.get('content') 
                    ? tinymce.get('content').getContent() 
                    : field.value;
                if (!content.trim()) {
                    showFieldError(field, 'Story content is required');
                } else if (content.trim().length < 50) {
                    showFieldError(field, 'Story must be at least 50 characters');
                }
                break;
        }
    }

    function handleTitleInput(event) {
        const title = event.target.value;
        const maxLength = 200;
        
        if (title.length > maxLength) {
            event.target.value = title.substring(0, maxLength);
        }

        // Update character count
        updateTitleCharacterCount(title.length, maxLength);
    }

    function updateTitleCharacterCount(current, max) {
        // You could add a character counter element here
        // For now, we'll just handle the length limit
    }

    function handleAutoSave() {
        // Clear existing timeout
        if (autoSaveTimeout) {
            clearTimeout(autoSaveTimeout);
        }

        // Set new timeout
        autoSaveTimeout = setTimeout(() => {
            saveDraftData();
        }, 2000); // Auto-save after 2 seconds of inactivity
    }

    function saveDraftData() {
        const formData = getFormData();
        const draftData = {
            ...formData,
            timestamp: Date.now()
        };
        
        localStorage.setItem('postDraft', JSON.stringify(draftData));
        showAutoSaveIndicator('saved');
    }

    function loadDraftData() {
        const draftData = localStorage.getItem('postDraft');
        if (draftData) {
            try {
                const draft = JSON.parse(draftData);
                
                // Check if draft is recent (within 24 hours)
                const isRecent = Date.now() - draft.timestamp < 24 * 60 * 60 * 1000;
                
                if (isRecent && confirm('You have a saved draft. Would you like to restore it?')) {
                    vibeSelect.value = draft.vibe || '';
                    companyInput.value = draft.company || '';
                    designationInput.value = draft.designation || '';
                    titleInput.value = draft.title || '';
                    
                    if (typeof tinymce !== 'undefined' && tinymce.get('content')) {
                        tinymce.get('content').setContent(draft.content || '');
                    } else {
                        contentTextarea.value = draft.content || '';
                    }
                    
                    showAutoSaveIndicator('restored');
                }
            } catch (error) {
                console.error('Failed to load draft data:', error);
            }
        }
    }

    function clearDraftData() {
        localStorage.removeItem('postDraft');
    }

    function showAutoSaveIndicator(status) {
        // You could add an auto-save indicator element here
        console.log(`Auto-save: ${status}`);
    }

    function showFieldError(field, message) {
        field.classList.add('error');
        
        // Remove existing error message
        const existingError = field.parentNode.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }

        // Add new error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
        field.parentNode.appendChild(errorDiv);
    }

    function clearFieldError(field) {
        field.classList.remove('error');
        const errorMessage = field.parentNode.querySelector('.error-message');
        if (errorMessage) {
            errorMessage.remove();
        }
    }

    function clearFieldErrors() {
        const fields = [vibeSelect, companyInput, designationInput, titleInput, contentTextarea, termsCheckbox];
        fields.forEach(field => clearFieldError(field));
    }

    function setLoadingState(loading) {
        if (loading) {
            shareBtn.classList.add('loading');
            shareBtn.disabled = true;
            createPostForm.querySelectorAll('input, select, textarea').forEach(field => {
                field.disabled = true;
            });
        } else {
            shareBtn.classList.remove('loading');
            shareBtn.disabled = false;
            createPostForm.querySelectorAll('input, select, textarea').forEach(field => {
                field.disabled = false;
            });
        }
    }

    function clearMessages() {
        const messageContainer = document.getElementById('messageContainer');
        if (messageContainer) {
            messageContainer.style.display = 'none';
        }
    }

    function handleKeyboardShortcuts(event) {
        // Ctrl/Cmd + S to save draft
        if ((event.ctrlKey || event.metaKey) && event.key === 's') {
            event.preventDefault();
            saveDraftData();
            UIUtils.showSuccess('Draft saved!');
        }
        
        // Ctrl/Cmd + Enter to submit
        if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
            event.preventDefault();
            if (!isSubmitting) {
                handleFormSubmit(event);
            }
        }
    }

    function handleBeforeUnload(event) {
        const formData = getFormData();
        const hasContent = formData.title || formData.content;
        
        if (hasContent && !isSubmitting) {
            event.preventDefault();
            event.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
            return event.returnValue;
        }
    }

    // Handle form reset
    const resetBtn = document.querySelector('.reset-btn');
    if (resetBtn) {
        resetBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('Are you sure you want to reset the form? All unsaved changes will be lost.')) {
                createPostForm.reset();
                if (typeof tinymce !== 'undefined' && tinymce.get('content')) {
                    tinymce.get('content').setContent('');
                }
                clearFieldErrors();
                clearDraftData();
            }
        });
    }

    // Handle guidelines link clicks
    const guidelinesLinks = document.querySelectorAll('.terms-link');
    guidelinesLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            UIUtils.showError('Terms and conditions page is not yet implemented.');
        });
    });

    // Performance monitoring
    if ('performance' in window) {
        window.addEventListener('load', function() {
            const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
            console.log(`Create post page loaded in ${loadTime}ms`);
        });
    }
});
