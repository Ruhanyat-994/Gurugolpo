// Moderator Dashboard JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const moderatorNavLinks = document.querySelectorAll('.moderator-nav-link');
    const moderatorSections = document.querySelectorAll('.moderator-section');
    const loadingSpinner = document.getElementById('loadingSpinner');

    let currentSection = 'dashboard';
    let dashboardData = null;

    // Initialize page
    init();

    async function init() {
        try {
            await checkModeratorAccess();
            await loadDashboardData();
            setupEventListeners();
            showSection('dashboard');
        } catch (error) {
            console.error('Failed to initialize moderator dashboard:', error);
            UIUtils.showError('Failed to load moderator dashboard. Please try again.');
        }
    }

    async function checkModeratorAccess() {
        try {
            const isAuthenticated = await api.checkAuth();
            if (!isAuthenticated) {
                window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
                return;
            }

            // Check if user is moderator or admin
            const userData = localStorage.getItem('userData');
            if (userData) {
                const user = JSON.parse(userData);
                if (user.role !== 'MODERATOR' && user.role !== 'ADMIN') {
                    UIUtils.showError('Access denied. Moderator privileges required.');
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 2000);
                    return;
                }
            }
        } catch (error) {
            console.error('Moderator access check failed:', error);
            window.location.href = '/login';
        }
    }

    async function loadDashboardData() {
        try {
            showLoading();
            dashboardData = await api.getModeratorDashboard();
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
        const totalPostsEl = document.getElementById('totalPosts');
        const totalCommentsEl = document.getElementById('totalComments');
        const totalUsersEl = document.getElementById('totalUsers');
        const reportedContentEl = document.getElementById('reportedContent');

        if (totalPostsEl) totalPostsEl.textContent = dashboardData.totalPosts || 0;
        if (totalCommentsEl) totalCommentsEl.textContent = dashboardData.totalComments || 0;
        if (totalUsersEl) totalUsersEl.textContent = dashboardData.totalUsers || 0;
        if (reportedContentEl) reportedContentEl.textContent = '0'; // Placeholder
    }

    function setupEventListeners() {
        // Navigation
        moderatorNavLinks.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const section = this.dataset.section;
                showSection(section);
            });
        });

        // Section-specific event listeners
        setupDashboardEvents();
        setupPostsEvents();
        setupCommentsEvents();
        setupUsersEvents();
    }

    function showSection(sectionName) {
        // Update navigation
        moderatorNavLinks.forEach(link => {
            link.classList.remove('active');
            if (link.dataset.section === sectionName) {
                link.classList.add('active');
            }
        });

        // Show section
        moderatorSections.forEach(section => {
            section.classList.remove('active');
            if (section.id === `${sectionName}-section`) {
                section.classList.add('active');
            }
        });

        currentSection = sectionName;

        // Load section data
        switch (sectionName) {
            case 'posts':
                loadPostsData();
                break;
            case 'comments':
                loadCommentsData();
                break;
            case 'users':
                loadUsersData();
                break;
        }
    }

    function setupDashboardEvents() {
        // Dashboard doesn't need specific event listeners
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

    function setupUsersEvents() {
        const refreshUsersBtn = document.getElementById('refreshUsers');
        if (refreshUsersBtn) {
            refreshUsersBtn.addEventListener('click', loadUsersData);
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
                        <button class="action-btn view" onclick="viewPost(${post.id})">
                            <i class="fas fa-eye"></i> View
                        </button>
                        <button class="action-btn delete" onclick="deletePostAsModerator(${post.id})">
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
                        <td colspan="6" class="text-center text-muted">
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

    // Global functions for table actions
    window.viewPost = function(postId) {
        window.open(`/post/${postId}`, '_blank');
    };

    window.deletePostAsModerator = async function(postId) {
        showConfirmModal(
            'Delete Post',
            'Are you sure you want to delete this post? This action cannot be undone.',
            async () => {
                try {
                    await api.deletePostAsModerator(postId);
                    UIUtils.showSuccess('Post deleted successfully');
                    loadPostsData(); // Refresh the table
                } catch (error) {
                    console.error('Failed to delete post:', error);
                    UIUtils.showError('Failed to delete post. Please try again.');
                }
            }
        );
    };

    window.deleteCommentAsModerator = async function(commentId) {
        showConfirmModal(
            'Delete Comment',
            'Are you sure you want to delete this comment? This action cannot be undone.',
            async () => {
                try {
                    await api.deleteCommentAsModerator(commentId);
                    UIUtils.showSuccess('Comment deleted successfully');
                    loadCommentsData(); // Refresh the table
                } catch (error) {
                    console.error('Failed to delete comment:', error);
                    UIUtils.showError('Failed to delete comment. Please try again.');
                }
            }
        );
    };

    window.banUser = async function(userId) {
        showConfirmModal(
            'Ban User',
            'Are you sure you want to ban this user? They will not be able to access the platform.',
            async () => {
                try {
                    await api.banUser(userId);
                    UIUtils.showSuccess('User banned successfully');
                    loadUsersData(); // Refresh the table
                } catch (error) {
                    console.error('Failed to ban user:', error);
                    UIUtils.showError('Failed to ban user. Please try again.');
                }
            }
        );
    };

    window.unbanUser = async function(userId) {
        showConfirmModal(
            'Unban User',
            'Are you sure you want to unban this user? They will regain access to the platform.',
            async () => {
                try {
                    await api.unbanUser(userId);
                    UIUtils.showSuccess('User unbanned successfully');
                    loadUsersData(); // Refresh the table
                } catch (error) {
                    console.error('Failed to unban user:', error);
                    UIUtils.showError('Failed to unban user. Please try again.');
                }
            }
        );
    };

    function showConfirmModal(title, message, onConfirm) {
        const modal = document.getElementById('confirmModal');
        const titleEl = document.getElementById('confirmModalTitle');
        const messageEl = document.getElementById('confirmModalMessage');
        const confirmBtn = document.getElementById('confirmAction');
        const cancelBtn = document.getElementById('cancelAction');

        if (!modal || !titleEl || !messageEl || !confirmBtn || !cancelBtn) return;

        // Set content
        titleEl.textContent = title;
        messageEl.textContent = message;

        // Show modal
        modal.style.display = 'flex';
        modal.classList.add('show');

        // Handle confirm
        confirmBtn.onclick = function() {
            modal.style.display = 'none';
            modal.classList.remove('show');
            onConfirm();
        };

        // Handle cancel
        cancelBtn.onclick = function() {
            modal.style.display = 'none';
            modal.classList.remove('show');
        };

        // Handle modal close
        const closeBtn = modal.querySelector('.close');
        if (closeBtn) {
            closeBtn.onclick = function() {
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
        // Ctrl/Cmd + 1-4 to switch sections
        if ((e.ctrlKey || e.metaKey) && e.key >= '1' && e.key <= '4') {
            e.preventDefault();
            const sections = ['dashboard', 'posts', 'comments', 'users'];
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
            console.log(`Moderator dashboard loaded in ${loadTime}ms`);
        });
    }
});
