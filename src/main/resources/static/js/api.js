// API Utility Functions
class API {
    constructor() {
        this.baseURL = window.location.origin;
        this.token = localStorage.getItem('authToken');
    }

    // Set authentication token
    setToken(token) {
        this.token = token;
        localStorage.setItem('authToken', token);
    }

    // Clear authentication token
    clearToken() {
        this.token = null;
        localStorage.removeItem('authToken');
    }

    // Get headers with authentication
    getHeaders() {
        const headers = {
            'Content-Type': 'application/json',
        };
        
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        
        return headers;
    }

    // Generic request method
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            headers: this.getHeaders(),
            ...options
        };

        try {
            const response = await fetch(url, config);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || `HTTP error! status: ${response.status}`);
            }

            return data;
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    // GET request
    async get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }

    // POST request
    async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    // PUT request
    async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    // DELETE request
    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }

    // Authentication methods
    async login(email, password) {
        const response = await this.post('/api/auth/login', { email, password });
        if (response.token) {
            this.setToken(response.token);
        }
        return response;
    }

    async logout() {
        this.clearToken();
        window.location.href = '/login';
    }

    // Post methods
    async getPosts() {
        return this.get('/api/posts');
    }

    async getPost(id) {
        return this.get(`/api/posts/${id}`);
    }

    async createPost(postData) {
        return this.post('/api/posts/create', postData);
    }

    async updatePost(id, postData) {
        return this.put(`/api/posts/${id}`, postData);
    }

    async deletePost(id) {
        return this.delete(`/api/posts/${id}`);
    }

    async searchPosts(query) {
        return this.get(`/api/posts/search?q=${encodeURIComponent(query)}`);
    }

    async getPostsByUniversity(university) {
        return this.get(`/api/posts/university/${encodeURIComponent(university)}`);
    }

    // Comment methods
    async getComments(postId) {
        return this.get(`/api/comments/${postId}`);
    }

    async addComment(postId, commentData) {
        return this.post(`/api/comments/${postId}`, commentData);
    }

    async updateComment(commentId, commentData) {
        return this.put(`/api/comments/${commentId}`, commentData);
    }

    async deleteComment(commentId) {
        return this.delete(`/api/comments/${commentId}`);
    }

    // Voting methods
    async upvotePost(postId) {
        return this.post(`/api/votes/posts/${postId}/upvote`);
    }

    async downvotePost(postId) {
        return this.post(`/api/votes/posts/${postId}/downvote`);
    }

    async upvoteComment(commentId) {
        return this.post(`/api/votes/comments/${commentId}/upvote`);
    }

    async downvoteComment(commentId) {
        return this.post(`/api/votes/comments/${commentId}/downvote`);
    }

    async getPostVoteCount(postId) {
        return this.get(`/api/votes/posts/${postId}/count`);
    }

    async getCommentVoteCount(commentId) {
        return this.get(`/api/votes/comments/${commentId}/count`);
    }

    // Admin methods
    async getAdminDashboard() {
        return this.get('/api/admin/dashboard');
    }

    async togglePostManagement(enabled) {
        return this.post('/api/admin/settings/post-management', { enabled });
    }

    async promoteUserToModerator(userId, university) {
        return this.post(`/api/admin/users/${userId}/promote`, { university });
    }

    async deleteUser(userId) {
        return this.delete(`/api/admin/users/${userId}`);
    }

    async deletePostAsAdmin(postId) {
        return this.delete(`/api/admin/posts/${postId}`);
    }

    async deleteCommentAsAdmin(commentId) {
        return this.delete(`/api/admin/comments/${commentId}`);
    }

    // Moderator methods
    async getModeratorDashboard() {
        return this.get('/api/moderator/dashboard');
    }

    async deletePostAsModerator(postId) {
        return this.delete(`/api/moderator/posts/${postId}`);
    }

    async deleteCommentAsModerator(commentId) {
        return this.delete(`/api/moderator/comments/${commentId}`);
    }

    async banUser(userId) {
        return this.post(`/api/moderator/users/${userId}/ban`);
    }

    async unbanUser(userId) {
        return this.post(`/api/moderator/users/${userId}/unban`);
    }

    // User methods
    async getCurrentUser() {
        if (!this.token) return null;
        try {
            return await this.get('/api/auth/me');
        } catch (error) {
            this.clearToken();
            return null;
        }
    }

    // Utility methods
    async checkAuth() {
        if (!this.token) return false;
        try {
            await this.get('/api/auth/me');
            return true;
        } catch (error) {
            this.clearToken();
            return false;
        }
    }

    // File upload (if needed)
    async uploadFile(file, endpoint) {
        const formData = new FormData();
        formData.append('file', file);

        const headers = {};
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        const response = await fetch(`${this.baseURL}${endpoint}`, {
            method: 'POST',
            headers,
            body: formData
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || `HTTP error! status: ${response.status}`);
        }

        return response.json();
    }
}

// Create global API instance
window.api = new API();

// Utility functions for UI
class UIUtils {
    static showMessage(message, type = 'success') {
        const container = document.getElementById('messageContainer');
        const content = document.getElementById('messageContent');
        
        if (!container || !content) return;

        content.textContent = message;
        content.className = `message-content ${type}`;
        container.style.display = 'block';

        // Auto hide after 5 seconds
        setTimeout(() => {
            container.style.display = 'none';
        }, 5000);
    }

    static showError(message) {
        this.showMessage(message, 'error');
    }

    static showSuccess(message) {
        this.showMessage(message, 'success');
    }

    static showLoading(element) {
        if (element) {
            element.classList.add('loading');
            element.disabled = true;
        }
    }

    static hideLoading(element) {
        if (element) {
            element.classList.remove('loading');
            element.disabled = false;
        }
    }

    static formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    static truncateText(text, maxLength = 100) {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    }

    static sanitizeHtml(html) {
        const div = document.createElement('div');
        div.textContent = html;
        return div.innerHTML;
    }

    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    static throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    }
}

// Make UIUtils globally available
window.UIUtils = UIUtils;
