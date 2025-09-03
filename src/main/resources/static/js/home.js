// Home Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const vibeFilter = document.getElementById('vibeFilter');
    const searchBtn = document.getElementById('searchBtn');
    const postsList = document.getElementById('postsList');
    const noPostsMessage = document.getElementById('noPostsMessage');
    const loadingSpinner = document.getElementById('loadingSpinner');

    let currentPosts = [];
    let filteredPosts = [];

    // Initialize page
    init();

    async function init() {
        try {
            // Get posts from the DOM (already rendered by Mustache template)
            currentPosts = extractPostsFromDOM();
            filteredPosts = [...currentPosts];
            
            setupEventListeners();
            loadSearchState();
        } catch (error) {
            console.error('Failed to initialize home page:', error);
            UIUtils.showError('Failed to initialize page. Please refresh.');
        }
    }

    function extractPostsFromDOM() {
        const postCards = document.querySelectorAll('.post-card');
        const posts = [];
        
        postCards.forEach(card => {
            const postId = card.dataset.postId;
            const title = card.querySelector('.post-title')?.textContent || '';
            const content = card.querySelector('.post-body p')?.textContent || '';
            const university = card.querySelector('.university-name')?.textContent || '';
            const authorName = card.querySelector('.author')?.textContent?.replace('by ', '') || '';
            const upvotes = parseInt(card.querySelector('.upvote .vote-count')?.textContent) || 0;
            const downvotes = parseInt(card.querySelector('.downvote .vote-count')?.textContent) || 0;
            const commentCount = parseInt(card.querySelector('.comment-count span')?.textContent) || 0;
            
            // Extract sentiment from badge class
            const sentimentBadge = card.querySelector('.sentiment-badge');
            const sentiment = sentimentBadge ? sentimentBadge.className.split(' ').find(cls => 
                ['positive', 'negative', 'neutral'].includes(cls)) || 'neutral' : 'neutral';
            
            // Extract verification status
            const verificationBadge = card.querySelector('.verification-badge');
            const verified = verificationBadge ? verificationBadge.classList.contains('verified') : false;
            
            posts.push({
                id: postId,
                title,
                content,
                university,
                authorName,
                upvotes,
                downvotes,
                commentCount,
                sentiment,
                verified,
                aiRewritten: content.includes('[Rewritten using AI')
            });
        });
        
        return posts;
    }

    function renderPosts() {
        if (filteredPosts.length === 0) {
            postsList.style.display = 'none';
            noPostsMessage.style.display = 'block';
            return;
        }

        postsList.style.display = 'block';
        noPostsMessage.style.display = 'none';

        postsList.innerHTML = filteredPosts.map(post => createPostCard(post)).join('');
        
        // Re-attach event listeners to new elements
        setupVoteEventListeners();
    }

    function createPostCard(post) {
        const sentimentClass = post.sentiment ? post.sentiment.toLowerCase() : 'neutral';
        const verifiedClass = post.verified ? 'verified' : 'not-verified';
        const aiRewritten = post.content && post.content.includes('[Rewritten using AI');
        
        return `
            <div class="post-card" data-post-id="${post.id}">
                <div class="post-header">
                    <div class="post-meta">
                        <span class="university-name">${UIUtils.sanitizeHtml(post.university || 'Unknown')}</span>
                        <span class="author">by ${UIUtils.sanitizeHtml(post.authorName || 'Anonymous')}</span>
                    </div>
                    <div class="post-badges">
                        <span class="sentiment-badge ${sentimentClass}">
                            <i class="fas fa-thumbs-${sentimentClass === 'positive' ? 'up' : 'down'}"></i>
                            ${sentimentClass}
                        </span>
                        <span class="verification-badge ${verifiedClass}">
                            <i class="fas fa-${verifiedClass === 'verified' ? 'check' : 'times'}"></i>
                            ${verifiedClass === 'verified' ? 'Verified' : 'Not Verified'}
                        </span>
                    </div>
                </div>
                
                <div class="post-content">
                    <h3 class="post-title">${UIUtils.sanitizeHtml(post.title)}</h3>
                    <div class="post-body">
                        <p>${UIUtils.truncateText(UIUtils.sanitizeHtml(post.content), 200)}</p>
                        ${aiRewritten ? '<div class="ai-note">[Rewritten using AI for obvious reasons]</div>' : ''}
                    </div>
                </div>
                
                <div class="post-footer">
                    <a href="/post/${post.id}" class="read-more">Read More</a>
                    <div class="post-stats">
                        <div class="vote-section">
                            <button class="vote-btn upvote" data-post-id="${post.id}">
                                <i class="fas fa-arrow-up"></i>
                                <span class="vote-count">${post.upvotes || 0}</span>
                            </button>
                            <button class="vote-btn downvote" data-post-id="${post.id}">
                                <i class="fas fa-arrow-down"></i>
                                <span class="vote-count">${post.downvotes || 0}</span>
                            </button>
                        </div>
                        <div class="comment-count">
                            <i class="fas fa-comment"></i>
                            <span>${post.commentCount || 0}</span>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    function setupEventListeners() {
        // Search functionality
        const debouncedSearch = UIUtils.debounce(performSearch, 300);
        searchInput.addEventListener('input', debouncedSearch);
        vibeFilter.addEventListener('change', performSearch);
        searchBtn.addEventListener('click', performSearch);

        // Vote buttons
        setupVoteEventListeners();

        // Enter key search
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }

    function setupVoteEventListeners() {
        // Remove existing listeners to avoid duplicates
        postsList.removeEventListener('click', handleVoteClick);
        // Add new listeners
        postsList.addEventListener('click', handleVoteClick);
    }

    function performSearch() {
        const searchTerm = searchInput.value.trim().toLowerCase();
        const selectedVibe = vibeFilter.value;

        filteredPosts = currentPosts.filter(post => {
            const matchesSearch = !searchTerm || 
                post.title.toLowerCase().includes(searchTerm) ||
                post.content.toLowerCase().includes(searchTerm) ||
                (post.university && post.university.toLowerCase().includes(searchTerm));

            const matchesVibe = !selectedVibe || 
                (post.sentiment && post.sentiment.toLowerCase() === selectedVibe);

            return matchesSearch && matchesVibe;
        });

        renderPosts();
        saveSearchState();
    }

    async function handleVoteClick(event) {
        const voteBtn = event.target.closest('.vote-btn');
        if (!voteBtn) return;

        const postId = voteBtn.dataset.postId;
        const isUpvote = voteBtn.classList.contains('upvote');

        if (!postId) return;

        try {
            // Check if user is authenticated
            const isAuthenticated = await api.checkAuth();
            if (!isAuthenticated) {
                UIUtils.showError('Please login to vote on posts.');
                return;
            }

            // Disable button temporarily
            voteBtn.disabled = true;
            voteBtn.classList.add('loading');

            // Perform vote
            if (isUpvote) {
                await api.upvotePost(postId);
            } else {
                await api.downvotePost(postId);
            }

            // Update vote count
            await updateVoteCounts(postId);

            UIUtils.showSuccess(isUpvote ? 'Upvoted successfully!' : 'Downvoted successfully!');

        } catch (error) {
            console.error('Vote failed:', error);
            UIUtils.showError(error.message || 'Failed to vote. Please try again.');
        } finally {
            voteBtn.disabled = false;
            voteBtn.classList.remove('loading');
        }
    }

    async function updateVoteCounts(postId) {
        try {
            const voteCounts = await api.getPostVoteCount(postId);
            const postCard = document.querySelector(`[data-post-id="${postId}"]`);
            
            if (postCard) {
                const upvoteCount = postCard.querySelector('.upvote .vote-count');
                const downvoteCount = postCard.querySelector('.downvote .vote-count');
                
                if (upvoteCount) upvoteCount.textContent = voteCounts.upvotes || 0;
                if (downvoteCount) downvoteCount.textContent = voteCounts.downvotes || 0;
            }
        } catch (error) {
            console.error('Failed to update vote counts:', error);
        }
    }

    // Handle browser back/forward navigation
    window.addEventListener('popstate', function(event) {
        if (event.state && event.state.searchTerm) {
            searchInput.value = event.state.searchTerm;
            performSearch();
        }
    });



    // Handle infinite scroll (if needed)
    let isLoadingMore = false;
    const loadMoreThreshold = 100; // pixels from bottom

    window.addEventListener('scroll', UIUtils.throttle(async function() {
        if (isLoadingMore) return;

        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        const windowHeight = window.innerHeight;
        const documentHeight = document.documentElement.scrollHeight;

        if (scrollTop + windowHeight >= documentHeight - loadMoreThreshold) {
            // Could implement pagination here
            console.log('Reached bottom - could load more posts');
        }
    }, 200));

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + K to focus search
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            searchInput.focus();
        }
        
        // Escape to clear search
        if (e.key === 'Escape' && document.activeElement === searchInput) {
            searchInput.value = '';
            performSearch();
        }
    });

    // Add focus indicator for accessibility
    searchInput.addEventListener('focus', function() {
        this.parentElement.classList.add('focused');
    });

    searchInput.addEventListener('blur', function() {
        this.parentElement.classList.remove('focused');
    });

    // Handle post card interactions
    postsList.addEventListener('mouseenter', function(e) {
        const postCard = e.target.closest('.post-card');
        if (postCard) {
            postCard.classList.add('hovered');
        }
    });

    postsList.addEventListener('mouseleave', function(e) {
        const postCard = e.target.closest('.post-card');
        if (postCard) {
            postCard.classList.remove('hovered');
        }
    });

    // Performance monitoring
    if ('performance' in window) {
        window.addEventListener('load', function() {
            const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
            console.log(`Page loaded in ${loadTime}ms`);
        });
    }
});

