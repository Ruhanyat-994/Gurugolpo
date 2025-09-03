// Admin Dashboard JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const adminNavLinks = document.querySelectorAll('.admin-nav-link');
    const adminSections = document.querySelectorAll('.admin-section');
    const loadingSpinner = document.getElementById('loadingSpinner');

    let currentSection = 'dashboard';
    let dashboardData = null;

    // Initialize page
    init();

    async function init() {
        try {
            await checkAdminAccess();
            await loadDashboardData();
            setupEventListeners();
            showSection('dashboard');
        } catch (error) {
            console.error('Failed to initialize admin dashboard:', error);
            UIUtils.showError('Failed to load admin dashboard. Please try again.');
        }
    }

    async function checkAdminAccess() {
        try {
            const isAuthenticated = await api.checkAuth();
            if (!isAuthenticated) {
                window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
                return;
            }

            // Check if user is admin
            const userData = localStorage.getItem('userData');
            if (userData) {
                const user = JSON.parse(userData);
                if (user.role !== 'ADMIN') {
                    UIUtils.showError('Access denied. Admin privileges required.');
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 2000);
                    return;
                }
            }
        } catch (error) {
            console.error('Admin access check failed:', error);
            window.location.href = '/login';
        }
    }

    async function loadDashboardData() {
        try {
            showLoading();
            dashboardData = await api.getAdminDashboard();
            renderDashboard();
        } catch (error) {
            console.error('Failed to load dashboard data:', error);
            throw error;
        } finally {
            hideLoading();
        }
    }

    function renderDashboard() {
        if (!dashboardData) return;

        // Update stats
        const totalUsersEl = document.getElementById('totalUsers');
        const totalPostsEl = document.getElementById('totalPosts');
        const totalCommentsEl = document.getElementById('totalComments');
        const totalModeratorsEl = document.getElementById('totalModerators');

        if (totalUsersEl) totalUsersEl.textContent = dashboardData.totalUsers || 0;
        if (totalPostsEl) totalPostsEl.textContent = dashboardData.totalPosts || 0;
        if (totalCommentsEl) totalCommentsEl.textContent = dashboardData.totalComments || 0;
        if (totalModeratorsEl) totalModeratorsEl.textContent = dashboardData.totalModerators || 0;
    }

    function setupEventListeners() {
        // Navigation
        adminNavLinks.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const section = this.dataset.section;
                showSection(section);
            });
        });

        // Section-specific event listeners
        setupDashboardEvents();
        setupUsersEvents();
        setupPostsEvents();
        setupCommentsEvents();
        setupSettingsEvents();
    }

    function showSection(sectionName) {
        // Update navigation
        adminNavLinks.forEach(link => {
            link.classList.remove('active');
            if (link.dataset.section === sectionName) {
                link.classList.add('active');
            }
        });

        // Show section
        adminSections.forEach(section => {
            section.classList.remove('active');
            if (section.id === `${sectionName}-section`) {
                section.classList.add('active');
            }
        });

        currentSection = sectionName;

        // Load section data
        switch (sectionName) {
            case 'users':
                loadUsersData();
                break;
            case 'posts':
                loadPostsData();
                break;
            case 'comments':
                loadCommentsData();
                break;
            case 'settings':
                loadSettingsData();
                break;
        }
    }

    function setupDashboardEvents() {
        // Dashboard doesn't need specific event listeners
    }

    function setupUsersEvents() {
        const refreshUsersBtn = document.getElementById('refreshUsers');
        if (refreshUsersBtn) {
            refreshUsersBtn.addEventListener('click', loadUsersData);
        }
    }

    function setupPostsEvents() {
        const refreshPostsBtn = document.getElementById('refreshPosts');
        if (refreshPostsBtn) {
            refreshPostsBtn.addEventListener('click', loadPostsData);
        }
    }

    function setupCommentsEvents() {
        const refreshCommentsBtn = document.getElementById('refreshComments');
        if (refreshCommentsBtn) {
            refreshCommentsBtn.addEventListener('click', loadCommentsData);
        }
    }

    function setupSettingsEvents() {
        const postManagementToggle = document.getElementById('postManagementEnabled');
        if (postManagementToggle) {
            postManagementToggle.addEventListener('change', handlePostManagementToggle);
        }
    }

    async function loadUsersData() {
        try {
            showLoading();
            // This would need to be implemented in the backend
            // const users = await api.getUsers();
            // renderUsersTable(users);
            
            // For now, show a placeholder
            const usersTableBody = document.getElementById('usersTableBody');
            if (usersTableBody) {
                usersTableBody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center text-muted">
                            User management functionality will be implemented
                        </td>
                    </tr>
                `;
            }
        } catch (error) {
            console.error('Failed to load users:', error);
            UIUtils.showError('Failed to load users data');
        } finally {
            hideLoading();
        }
    }

    async function loadPostsData() {
        try {
            showLoading();
            const posts = await api.getPosts();
            renderPostsTable(posts);
        } catch (error) {
            console.error('Failed to load posts:', error);
            UIUtils.showError('Failed to load posts data');
        } finally {
            hideLoading();
        }
    }

    function renderPostsTable(posts) {
        const postsTableBody = document.getElementById('postsTableBody');
        if (!postsTableBody) return;

        if (posts.length === 0) {
            postsTableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-muted">No posts found</td>
                </tr>
            `;
            return;
        }

        postsTableBody.innerHTML = posts.map(post => `
            <tr>
                <td>${post.id}</td>
                <td>${UIUtils.truncateText(post.title, 50)}</td>
                <td>${post.authorName || 'Anonymous'}</td>
                <td>${post.university || 'Unknown'}</td>
                <td>
                    <span class="status-badge ${post.sentiment ? post.sentiment.toLowerCase() : 'neutral'}">
                        ${post.sentiment || 'Neutral'}
                    </span>
                </td>
                <td>${UIUtils.formatDate(post.createdAt)}</td>
                <td>
                    <div class="action-buttons">
                        <button class="action-btn delete" onclick="deletePost(${post.id})">
                            <i class="fas fa-trash"></i> Delete
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    async function loadCommentsData() {
        try {
            showLoading();
            // This would need to be implemented in the backend
            // const comments = await api.getComments();
            // renderCommentsTable(comments);
            
            // For now, show a placeholder
            const commentsTableBody = document.getElementById('commentsTableBody');
            if (commentsTableBody) {
                commentsTableBody.innerHTML = `
                    <tr>
                        <td colspan="6" class="text-center text-muted">
                            Comment management functionality will be implemented
                        </td>
                    </tr>
                `;
            }
        } catch (error) {
            console.error('Failed to load comments:', error);
            UIUtils.showError('Failed to load comments data');
        } finally {
            hideLoading();
        }
    }

    async function loadSettingsData() {
        try {
            // Load current settings
            // This would need to be implemented in the backend
            const postManagementToggle = document.getElementById('postManagementEnabled');
            if (postManagementToggle) {
                postManagementToggle.checked = true; // Default value
            }
        } catch (error) {
            console.error('Failed to load settings:', error);
            UIUtils.showError('Failed to load settings');
        }
    }

    async function handlePostManagementToggle(event) {
        const enabled = event.target.checked;
        
        try {
            await api.togglePostManagement(enabled);
            UIUtils.showSuccess('Post management setting updated successfully');
        } catch (error) {
            console.error('Failed to update post management setting:', error);
            UIUtils.showError('Failed to update setting. Please try again.');
            // Revert the toggle
            event.target.checked = !enabled;
        }
    }

    // Global functions for table actions
    window.deletePost = async function(postId) {
        if (!confirm('Are you sure you want to delete this post? This action cannot be undone.')) {
            return;
        }

        try {
            await api.deletePostAsAdmin(postId);
            UIUtils.showSuccess('Post deleted successfully');
            loadPostsData(); // Refresh the table
        } catch (error) {
            console.error('Failed to delete post:', error);
            UIUtils.showError('Failed to delete post. Please try again.');
        }
    };

    window.deleteComment = async function(commentId) {
        if (!confirm('Are you sure you want to delete this comment? This action cannot be undone.')) {
            return;
        }

        try {
            await api.deleteCommentAsAdmin(commentId);
            UIUtils.showSuccess('Comment deleted successfully');
            loadCommentsData(); // Refresh the table
        } catch (error) {
            console.error('Failed to delete comment:', error);
            UIUtils.showError('Failed to delete comment. Please try again.');
        }
    };

    window.deleteUser = async function(userId) {
        if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
            return;
        }

        try {
            await api.deleteUser(userId);
            UIUtils.showSuccess('User deleted successfully');
            loadUsersData(); // Refresh the table
        } catch (error) {
            console.error('Failed to delete user:', error);
            UIUtils.showError('Failed to delete user. Please try again.');
        }
    };

    window.promoteUser = function(userId) {
        showPromoteUserModal(userId);
    };

    function showPromoteUserModal(userId) {
        const modal = document.getElementById('promoteUserModal');
        const form = document.getElementById('promoteUserForm');
        const universityInput = document.getElementById('promoteUniversity');

        if (!modal || !form || !universityInput) return;

        // Reset form
        form.reset();
        universityInput.value = '';

        // Show modal
        modal.style.display = 'flex';
        modal.classList.add('show');

        // Handle form submission
        form.onsubmit = async function(e) {
            e.preventDefault();
            
            const university = universityInput.value.trim();
            if (!university) {
                UIUtils.showError('University is required');
                return;
            }

            try {
                await api.promoteUserToModerator(userId, university);
                UIUtils.showSuccess('User promoted to moderator successfully');
                modal.style.display = 'none';
                modal.classList.remove('show');
                loadUsersData(); // Refresh the table
            } catch (error) {
                console.error('Failed to promote user:', error);
                UIUtils.showError('Failed to promote user. Please try again.');
            }
        };

        // Handle modal close
        const closeBtn = modal.querySelector('.close');
        const cancelBtn = document.getElementById('cancelPromote');
        
        if (closeBtn) {
            closeBtn.onclick = function() {
                modal.style.display = 'none';
                modal.classList.remove('show');
            };
        }
        
        if (cancelBtn) {
            cancelBtn.onclick = function() {
                modal.style.display = 'none';
                modal.classList.remove('show');
            };
        }

        // Close modal when clicking outside
        modal.onclick = function(e) {
            if (e.target === modal) {
                modal.style.display = 'none';
                modal.classList.remove('show');
            }
        };
    }

    function showLoading() {
        if (loadingSpinner) {
            loadingSpinner.style.display = 'flex';
        }
    }

    function hideLoading() {
        if (loadingSpinner) {
            loadingSpinner.style.display = 'none';
        }
    }

    // Auto-refresh dashboard data every 5 minutes
    setInterval(async () => {
        if (currentSection === 'dashboard') {
            try {
                await loadDashboardData();
            } catch (error) {
                console.error('Auto-refresh failed:', error);
            }
        }
    }, 300000); // 5 minutes

    // Handle browser back/forward navigation
    window.addEventListener('popstate', function(event) {
        if (event.state && event.state.section) {
            showSection(event.state.section);
        }
    });

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + 1-5 to switch sections
        if ((e.ctrlKey || e.metaKey) && e.key >= '1' && e.key <= '5') {
            e.preventDefault();
            const sections = ['dashboard', 'users', 'posts', 'comments', 'settings'];
            const sectionIndex = parseInt(e.key) - 1;
            if (sections[sectionIndex]) {
                showSection(sections[sectionIndex]);
            }
        }
    });

    // Performance monitoring
    if ('performance' in window) {
        window.addEventListener('load', function() {
            const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
            console.log(`Admin dashboard loaded in ${loadTime}ms`);
        });
    }
});
