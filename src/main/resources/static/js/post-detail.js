// Post Detail Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const postId = getPostIdFromUrl();
    const commentTextarea = document.getElementById('commentText');
    const postCommentBtn = document.getElementById('postCommentBtn');
    const commentsList = document.getElementById('commentsList');
    const sortSelect = document.getElementById('sortSelect');
    const loadingSpinner = document.getElementById('loadingSpinner');

    let currentPost = null;
    let currentComments = [];
    let sortOrder = 'newest';

    // Initialize page
    init();

    async function init() {
        if (!postId) {
            UIUtils.showError('Invalid post ID');
            return;
        }

        try {
            showLoading();
            await loadPost();
            await loadComments();
            setupEventListeners();
        } catch (error) {
            console.error('Failed to initialize post detail page:', error);
            UIUtils.showError('Failed to load post. Please try again.');
        } finally {
            hideLoading();
        }
    }

    function getPostIdFromUrl() {
        const pathParts = window.location.pathname.split('/');
        return pathParts[pathParts.length - 1];
    }

    async function loadPost() {
        try {
            currentPost = await api.getPost(postId);
            renderPost();
        } catch (error) {
            console.error('Failed to load post:', error);
            throw error;
        }
    }

    function renderPost() {
        if (!currentPost) return;

        // Update page title
        document.title = `${currentPost.title} - Gurugolpo`;

        // Update post content (assuming the template already has the post data)
        // This would be used if we're dynamically loading the post
        updateVoteCounts();
    }

    async function loadComments() {
        try {
            currentComments = await api.getComments(postId);
            renderComments();
        } catch (error) {
            console.error('Failed to load comments:', error);
            throw error;
        }
    }

    function renderComments() {
        if (!commentsList) return;

        const sortedComments = sortComments([...currentComments]);
        
        if (sortedComments.length === 0) {
            commentsList.innerHTML = `
                <div class="no-comments">
                    <p>No comments yet. Be the first to share your thoughts!</p>
                </div>
            `;
            return;
        }

        commentsList.innerHTML = sortedComments.map(comment => createCommentElement(comment)).join('');
    }

    function createCommentElement(comment) {
        const createdAt = UIUtils.formatDate(comment.createdAt);
        
        return `
            <div class="comment-item" data-comment-id="${comment.id}">
                <div class="comment-header">
                    <div class="comment-author">
                        <div class="author-avatar">
                            <i class="fas fa-user"></i>
                        </div>
                        <span class="author-name">${UIUtils.sanitizeHtml(comment.authorName || 'Anonymous')}</span>
                    </div>
                    <span class="comment-date">${createdAt}</span>
                </div>
                <div class="comment-content">
                    <p>${UIUtils.sanitizeHtml(comment.content)}</p>
                </div>
                <div class="comment-actions">
                    <button class="comment-vote-btn upvote" data-comment-id="${comment.id}">
                        <i class="fas fa-arrow-up"></i>
                        <span class="vote-count">${comment.upvotes || 0}</span>
                    </button>
                    <button class="comment-vote-btn downvote" data-comment-id="${comment.id}">
                        <i class="fas fa-arrow-down"></i>
                        <span class="vote-count">${comment.downvotes || 0}</span>
                    </button>
                </div>
            </div>
        `;
    }

    function sortComments(comments) {
        switch (sortOrder) {
            case 'oldest':
                return comments.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
            case 'popular':
                return comments.sort((a, b) => (b.upvotes || 0) - (a.upvotes || 0));
            case 'newest':
            default:
                return comments.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        }
    }

    function setupEventListeners() {
        // Post comment
        if (postCommentBtn) {
            postCommentBtn.addEventListener('click', handlePostComment);
        }

        // Comment textarea
        if (commentTextarea) {
            commentTextarea.addEventListener('keydown', handleCommentKeydown);
            commentTextarea.addEventListener('input', handleCommentInput);
        }

        // Sort comments
        if (sortSelect) {
            sortSelect.addEventListener('change', handleSortChange);
        }

        // Vote buttons
        document.addEventListener('click', handleVoteClick);

        // Back button
        const backBtn = document.querySelector('.back-btn');
        if (backBtn) {
            backBtn.addEventListener('click', function(e) {
                e.preventDefault();
                window.history.back();
            });
        }
    }

    async function handlePostComment() {
        if (!commentTextarea || !postCommentBtn) return;

        const content = commentTextarea.value.trim();
        
        if (!content) {
            UIUtils.showError('Please enter a comment');
            commentTextarea.focus();
            return;
        }

        try {
            // Check authentication
            const isAuthenticated = await api.checkAuth();
            if (!isAuthenticated) {
                UIUtils.showError('Please login to post comments');
                return;
            }

            // Show loading state
            postCommentBtn.disabled = true;
            postCommentBtn.textContent = 'Posting...';

            // Post comment
            const newComment = await api.addComment(postId, { content });

            // Clear textarea
            commentTextarea.value = '';

            // Reload comments
            await loadComments();

            // Show success message
            UIUtils.showSuccess('Comment posted successfully!');

            // Scroll to new comment
            setTimeout(() => {
                const commentElement = document.querySelector(`[data-comment-id="${newComment.id}"]`);
                if (commentElement) {
                    commentElement.scrollIntoView({ behavior: 'smooth' });
                }
            }, 100);

        } catch (error) {
            console.error('Failed to post comment:', error);
            UIUtils.showError(error.message || 'Failed to post comment. Please try again.');
        } finally {
            postCommentBtn.disabled = false;
            postCommentBtn.textContent = 'Post Comment';
        }
    }

    function handleCommentKeydown(event) {
        // Ctrl/Cmd + Enter to submit
        if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
            event.preventDefault();
            handlePostComment();
        }
    }

    function handleCommentInput(event) {
        const textarea = event.target;
        const maxLength = 1000; // Set maximum comment length
        
        if (textarea.value.length > maxLength) {
            textarea.value = textarea.value.substring(0, maxLength);
        }

        // Update character count if needed
        updateCharacterCount(textarea);
    }

    function updateCharacterCount(textarea) {
        const maxLength = 1000;
        const currentLength = textarea.value.length;
        
        // You could add a character counter element here
        // For now, we'll just handle the length limit
    }

    function handleSortChange(event) {
        sortOrder = event.target.value;
        renderComments();
    }

    async function handleVoteClick(event) {
        const voteBtn = event.target.closest('.vote-btn, .comment-vote-btn');
        if (!voteBtn) return;

        const isCommentVote = voteBtn.classList.contains('comment-vote-btn');
        const isUpvote = voteBtn.classList.contains('upvote');
        const targetId = voteBtn.dataset.postId || voteBtn.dataset.commentId;

        if (!targetId) return;

        try {
            // Check authentication
            const isAuthenticated = await api.checkAuth();
            if (!isAuthenticated) {
                UIUtils.showError('Please login to vote');
                return;
            }

            // Disable button temporarily
            voteBtn.disabled = true;

            // Perform vote
            if (isCommentVote) {
                if (isUpvote) {
                    await api.upvoteComment(targetId);
                } else {
                    await api.downvoteComment(targetId);
                }
                await updateCommentVoteCounts(targetId);
            } else {
                if (isUpvote) {
                    await api.upvotePost(targetId);
                } else {
                    await api.downvotePost(targetId);
                }
                await updatePostVoteCounts(targetId);
            }

            UIUtils.showSuccess(isUpvote ? 'Upvoted successfully!' : 'Downvoted successfully!');

        } catch (error) {
            console.error('Vote failed:', error);
            UIUtils.showError(error.message || 'Failed to vote. Please try again.');
        } finally {
            voteBtn.disabled = false;
        }
    }

    async function updatePostVoteCounts(postId) {
        try {
            const voteCounts = await api.getPostVoteCount(postId);
            
            // Update post vote counts
            const agreeBtn = document.querySelector('.vote-btn.agree');
            const disagreeBtn = document.querySelector('.vote-btn.disagree');
            
            if (agreeBtn) {
                const countSpan = agreeBtn.querySelector('.vote-count');
                if (countSpan) countSpan.textContent = voteCounts.upvotes || 0;
            }
            
            if (disagreeBtn) {
                const countSpan = disagreeBtn.querySelector('.vote-count');
                if (countSpan) countSpan.textContent = voteCounts.downvotes || 0;
            }
        } catch (error) {
            console.error('Failed to update post vote counts:', error);
        }
    }

    async function updateCommentVoteCounts(commentId) {
        try {
            const voteCounts = await api.getCommentVoteCount(commentId);
            const commentElement = document.querySelector(`[data-comment-id="${commentId}"]`);
            
            if (commentElement) {
                const upvoteCount = commentElement.querySelector('.upvote .vote-count');
                const downvoteCount = commentElement.querySelector('.downvote .vote-count');
                
                if (upvoteCount) upvoteCount.textContent = voteCounts.upvotes || 0;
                if (downvoteCount) downvoteCount.textContent = voteCounts.downvotes || 0;
            }
        } catch (error) {
            console.error('Failed to update comment vote counts:', error);
        }
    }

    function updateVoteCounts() {
        if (currentPost) {
            updatePostVoteCounts(currentPost.id);
        }
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

    // Auto-refresh comments every 2 minutes
    setInterval(async () => {
        try {
            await loadComments();
        } catch (error) {
            console.error('Auto-refresh comments failed:', error);
        }
    }, 120000); // 2 minutes

    // Handle browser back/forward navigation
    window.addEventListener('popstate', function(event) {
        // Reload comments if needed
        loadComments();
    });

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Escape to clear comment
        if (e.key === 'Escape' && document.activeElement === commentTextarea) {
            commentTextarea.value = '';
        }
        
        // Ctrl/Cmd + / to focus comment box
        if ((e.ctrlKey || e.metaKey) && e.key === '/') {
            e.preventDefault();
            commentTextarea.focus();
        }
    });

    // Handle comment textarea auto-resize
    if (commentTextarea) {
        commentTextarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
        });
    }

    // Handle comment interactions
    commentsList.addEventListener('mouseenter', function(e) {
        const commentItem = e.target.closest('.comment-item');
        if (commentItem) {
            commentItem.classList.add('hovered');
        }
    });

    commentsList.addEventListener('mouseleave', function(e) {
        const commentItem = e.target.closest('.comment-item');
        if (commentItem) {
            commentItem.classList.remove('hovered');
        }
    });

    // Performance monitoring
    if ('performance' in window) {
        window.addEventListener('load', function() {
            const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
            console.log(`Post detail page loaded in ${loadTime}ms`);
        });
    }
});
