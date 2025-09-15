import React from 'react';
import { formatDistanceToNow } from 'date-fns';
import './Comment.css';

const Comment = ({ comment, onVote, isAuthenticated }) => {
  const formatDate = (dateString) => {
    try {
      return formatDistanceToNow(new Date(dateString), { addSuffix: true });
    } catch (error) {
      return 'Unknown time';
    }
  };

  const handleVote = (type) => {
    if (!isAuthenticated) {
      return;
    }
    onVote(comment.id, type);
  };

  const getVoteColor = (upvotes, downvotes) => {
    const total = upvotes - downvotes;
    if (total > 0) return 'positive';
    if (total < 0) return 'negative';
    return 'neutral';
  };

  return (
    <div className="comment">
      <div className="comment-header">
        <div className="comment-meta">
          <div className="comment-author">
            <i className="fas fa-user-circle"></i>
            <span className="author-name">{comment.authorName}</span>
          </div>
          <div className="comment-time">
            <i className="fas fa-clock"></i>
            <span>{formatDate(comment.createdAt)}</span>
          </div>
        </div>
      </div>

      <div className="comment-content">
        <div dangerouslySetInnerHTML={{ __html: comment.content }} />
      </div>

      <div className="comment-footer">
        <div className="comment-votes">
          <button
            onClick={() => handleVote('upvote')}
            className={`vote-btn ${isAuthenticated ? '' : 'disabled'}`}
            disabled={!isAuthenticated}
            title={isAuthenticated ? 'Upvote this comment' : 'Sign in to vote'}
          >
            <i className="fas fa-arrow-up"></i>
            <span>{comment.upvotes || 0}</span>
          </button>
          <button
            onClick={() => handleVote('downvote')}
            className={`vote-btn ${isAuthenticated ? '' : 'disabled'}`}
            disabled={!isAuthenticated}
            title={isAuthenticated ? 'Downvote this comment' : 'Sign in to vote'}
          >
            <i className="fas fa-arrow-down"></i>
            <span>{comment.downvotes || 0}</span>
          </button>
          <div className={`vote-total ${getVoteColor(comment.upvotes, comment.downvotes)}`}>
            {comment.upvotes - comment.downvotes}
          </div>
        </div>

        <div className="comment-actions">
          <button className="btn btn-outline btn-sm">
            <i className="fas fa-reply"></i>
            Reply
          </button>
        </div>
      </div>
    </div>
  );
};

export default Comment;
