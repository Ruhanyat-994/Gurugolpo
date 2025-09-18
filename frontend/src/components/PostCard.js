import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { formatDistanceToNow } from 'date-fns';
import { useAuth } from '../contexts/AuthContext';
import { useModal } from '../contexts/ModalContext';
import { votingAPI, postsAPI } from '../services/api';
import './PostCard.css';

const PostCard = ({ post, onPostDeleted }) => {
  const [voteCounts, setVoteCounts] = useState({
    upvotes: post.upvotes || 0,
    downvotes: post.downvotes || 0,
    voteCount: post.voteCount || 0
  });
  const [userVote, setUserVote] = useState(null); // 'upvote', 'downvote', or null
  const [voting, setVoting] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const { isAuthenticated, user } = useAuth();
  const { showModal, hideModal } = useModal();
  const navigate = useNavigate();

  // Fetch vote counts when component mounts
  useEffect(() => {
    const fetchVoteCounts = async () => {
      try {
        const response = await votingAPI.getPostVoteCount(post.id);
        setVoteCounts(response.data);
      } catch (error) {
        console.error('Error fetching vote counts:', error);
        // Keep the initial values from post props
      }
    };

    fetchVoteCounts();
  }, [post.id]);

  // Reset user vote when user changes
  useEffect(() => {
    setUserVote(null);
  }, [user]);

  const formatDate = (dateString) => {
    try {
      return formatDistanceToNow(new Date(dateString), { addSuffix: true });
    } catch (error) {
      return 'Unknown time';
    }
  };

  const truncateContent = (content, maxLength = 200) => {
    // Strip HTML tags to get plain text for length calculation
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = content;
    const textContent = tempDiv.textContent || tempDiv.innerText || '';
    
    if (textContent.length <= maxLength) return content;
    
    // If content is too long, return a truncated version
    // We'll truncate at a reasonable point and add ellipsis
    return content.substring(0, maxLength * 1.5) + '...';
  };

  const getVoteColor = () => {
    const total = voteCounts.upvotes - voteCounts.downvotes;
    if (total > 0) return 'positive';
    if (total < 0) return 'negative';
    return 'neutral';
  };

  const handleVote = async (voteType) => {
    if (!isAuthenticated()) {
      showModal('authPopup', {
        mode: 'login',
        onSuccess: () => {
          // After successful login, the user can try voting again
        }
      });
      return;
    }

    if (voting) return; // Prevent multiple simultaneous votes

    setVoting(true);
    try {
      let response;
      if (voteType === 'upvote') {
        response = await votingAPI.upvotePost(post.id);
      } else {
        response = await votingAPI.downvotePost(post.id);
      }

      // Update vote counts after successful vote
      const voteCountResponse = await votingAPI.getPostVoteCount(post.id);
      setVoteCounts(voteCountResponse.data);

      // Update user's vote status based on the response message
      const message = response.data.message;
      if (message.includes('Vote withdrawn')) {
        setUserVote(null);
      } else if (message.includes('upvote') || message.includes('UPVOTE')) {
        setUserVote('upvote');
      } else if (message.includes('downvote') || message.includes('DOWNVOTE')) {
        setUserVote('downvote');
      }

    } catch (error) {
      console.error('Error voting:', error);
      // You could show a toast notification here
    } finally {
      setVoting(false);
    }
  };

  const handleRestrictedAction = () => {
    if (!isAuthenticated()) {
      showModal('authPopup', {
        mode: 'login',
        onSuccess: () => {
          // After successful login, the user can try the action again
        }
      });
      return;
    }
    // TODO: Implement actual action logic
  };

  const handleEdit = () => {
    if (!isAuthenticated()) {
      showModal('authPopup', {
        mode: 'login',
        onSuccess: () => {
          // After successful login, navigate to edit page
          navigate(`/edit-post/${post.id}`);
        }
      });
      return;
    }
    navigate(`/edit-post/${post.id}`);
  };

  const handleDeleteClick = () => {
    if (!isAuthenticated()) {
      showModal('authPopup', {
        mode: 'login',
        onSuccess: () => {
          // After successful login, show delete confirmation
          showModal('deleteConfirm', {
            title: 'Delete Post',
            message: 'Are you sure you want to delete this post?',
            confirmText: 'Delete Post',
            loading: deleting,
            onConfirm: handleDelete
          });
        }
      });
      return;
    }
    
    showModal('deleteConfirm', {
      title: 'Delete Post',
      message: 'Are you sure you want to delete this post?',
      confirmText: 'Delete Post',
      loading: deleting,
      onConfirm: handleDelete
    });
  };

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await postsAPI.deletePost(post.id);
      if (onPostDeleted) {
        onPostDeleted(post.id);
      }
      hideModal('deleteConfirm');
    } catch (error) {
      console.error('Error deleting post:', error);
      alert('Failed to delete post. Please try again.');
    } finally {
      setDeleting(false);
    }
  };

  const isPostAuthor = user && post.authorId === user.id;

  return (
    <article className="post-card">
      {/* Company/Role Header */}
      <div className="post-header">
        <div className="company-role">
          <span className="company-name">{post.university}</span>
          <span className="role-separator">by</span>
          <span className="author-role">{post.authorName}</span>
        </div>
        
        {/* Post Actions - Only show for post author */}
        {isPostAuthor && (
          <div className="post-author-actions">
            <button 
              className="action-btn edit-btn"
              onClick={handleEdit}
              title="Edit Post"
            >
              <i className="fas fa-edit"></i>
            </button>
            <button 
              className="action-btn delete-btn"
              onClick={handleDeleteClick}
              title="Delete Post"
            >
              <i className="fas fa-trash"></i>
            </button>
          </div>
        )}
      </div>

      {/* Post Content */}
      <div className="post-content">
        <h3 className="post-title">
          <Link to={`/post/${post.id}`} className="post-title-link">
            {post.title}
          </Link>
        </h3>
        
        <div className="post-excerpt">
          <div dangerouslySetInnerHTML={{ __html: truncateContent(post.content) }} />
        </div>
      </div>

      {/* Action Footer */}
      <div className="post-footer">
        <div className="post-actions">
          <Link to={`/post/${post.id}`} className="read-more-link">
            Read More
          </Link>
          
          <div className="vote-actions">
            <button 
              className={`vote-btn upvote ${userVote === 'upvote' ? 'voted' : ''} ${voting ? 'loading' : ''}`}
              onClick={() => handleVote('upvote')}
              disabled={voting}
            >
              <i className="fas fa-arrow-up"></i>
              <span>{voteCounts.upvotes}</span>
            </button>
            <button 
              className={`vote-btn downvote ${userVote === 'downvote' ? 'voted' : ''} ${voting ? 'loading' : ''}`}
              onClick={() => handleVote('downvote')}
              disabled={voting}
            >
              <i className="fas fa-arrow-down"></i>
              <span>{voteCounts.downvotes}</span>
            </button>
          </div>
        </div>
      </div>

    </article>
  );
};

export default PostCard;
