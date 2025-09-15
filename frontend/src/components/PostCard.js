import React from 'react';
import { Link } from 'react-router-dom';
import { formatDistanceToNow } from 'date-fns';
import './PostCard.css';

const PostCard = ({ post }) => {
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

  const getVoteColor = (upvotes, downvotes) => {
    const total = upvotes - downvotes;
    if (total > 0) return 'positive';
    if (total < 0) return 'negative';
    return 'neutral';
  };

  return (
    <article className="post-card">
      {/* Company/Role Header */}
      <div className="post-header">
        <div className="company-role">
          <span className="company-name">{post.university}</span>
          <span className="role-separator">by</span>
          <span className="author-role">{post.authorName}</span>
        </div>
        
        {/* Badges */}
        <div className="post-badges">
          <span className={`sentiment-badge ${getVoteColor(post.upvotes, post.downvotes)}`}>
            {getVoteColor(post.upvotes, post.downvotes) === 'positive' ? 'Positive' : 
             getVoteColor(post.upvotes, post.downvotes) === 'negative' ? 'Negative' : 'Neutral'}
          </span>
          <span className="verification-badge not-verified">
            Not Verified
          </span>
        </div>
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
            <button className="vote-btn upvote">
              <i className="fas fa-arrow-up"></i>
              <span>{post.upvotes || 0}</span>
            </button>
            <button className="vote-btn downvote">
              <i className="fas fa-arrow-down"></i>
              <span>{post.downvotes || 0}</span>
            </button>
          </div>
          
          <div className="comment-action">
            <button className="comment-btn">
              <i className="fas fa-comments"></i>
              <span>0</span>
            </button>
          </div>
          
          <button className="conversation-btn">
            Start Conversation
          </button>
        </div>
      </div>
    </article>
  );
};

export default PostCard;
