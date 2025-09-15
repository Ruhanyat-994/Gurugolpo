import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { postsAPI, commentsAPI, votingAPI } from '../services/api';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import Comment from '../components/Comment';
import './PostDetail.css';

const PostDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [commentContent, setCommentContent] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);
  const [sortBy, setSortBy] = useState('newest');

  useEffect(() => {
    fetchPost();
    fetchComments();
  }, [id]);

  const fetchPost = async () => {
    try {
      const response = await postsAPI.getPostById(id);
      setPost(response.data);
    } catch (error) {
      setError('Failed to load post');
      console.error('Error fetching post:', error);
    }
  };

  const fetchComments = async () => {
    try {
      const response = await commentsAPI.getCommentsForPost(id);
      setComments(response.data);
    } catch (error) {
      console.error('Error fetching comments:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleVote = async (type) => {
    if (!isAuthenticated()) {
      // Show login modal or redirect
      navigate('/login', { state: { from: { pathname: `/post/${id}` } } });
      return;
    }

    try {
      if (type === 'upvote') {
        await votingAPI.upvotePost(id);
      } else {
        await votingAPI.downvotePost(id);
      }
      // Refresh post data to get updated vote counts
      fetchPost();
    } catch (error) {
      console.error('Error voting:', error);
    }
  };

  const handleCommentVote = async (commentId, type) => {
    if (!isAuthenticated()) {
      navigate('/login', { state: { from: { pathname: `/post/${id}` } } });
      return;
    }

    try {
      if (type === 'upvote') {
        await votingAPI.upvoteComment(commentId);
      } else {
        await votingAPI.downvoteComment(commentId);
      }
      // Refresh comments to get updated vote counts
      fetchComments();
    } catch (error) {
      console.error('Error voting on comment:', error);
    }
  };

  const handleSubmitComment = async () => {
    if (!isAuthenticated()) {
      navigate('/login', { state: { from: { pathname: `/post/${id}` } } });
      return;
    }

    if (!commentContent.trim()) {
      setError('Please enter a comment');
      return;
    }

    setSubmittingComment(true);
    try {
      await commentsAPI.addComment(id, { content: commentContent });
      setCommentContent('');
      setError('');
      fetchComments();
    } catch (error) {
      setError('Failed to submit comment');
      console.error('Error submitting comment:', error);
    } finally {
      setSubmittingComment(false);
    }
  };

  const sortedComments = [...comments].sort((a, b) => {
    switch (sortBy) {
      case 'oldest':
        return new Date(a.createdAt) - new Date(b.createdAt);
      case 'most_voted':
        return (b.upvotes - b.downvotes) - (a.upvotes - a.downvotes);
      case 'newest':
      default:
        return new Date(b.createdAt) - new Date(a.createdAt);
    }
  });

  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      return 'Unknown date';
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading post...</p>
      </div>
    );
  }

  if (error && !post) {
    return (
      <div className="error-container">
        <div className="alert alert-error">
          <i className="fas fa-exclamation-circle"></i>
          {error}
        </div>
        <button onClick={() => navigate('/')} className="btn btn-primary">
          <i className="fas fa-home"></i>
          Back to Home
        </button>
      </div>
    );
  }

  return (
    <div className="post-detail-container">
      <div className="post-detail-header">
        <button onClick={() => navigate('/')} className="btn btn-outline btn-sm">
          <i className="fas fa-arrow-left"></i>
          Back to Home
        </button>
      </div>

      {post && (
        <article className="post-detail">
          <div className="post-header">
            <div className="post-meta">
              <div className="post-author">
                <i className="fas fa-user-circle"></i>
                <span className="author-name">{post.authorName}</span>
              </div>
              <div className="post-university">
                <i className="fas fa-university"></i>
                <span>{post.university}</span>
              </div>
              <div className="post-time">
                <i className="fas fa-clock"></i>
                <span>{formatDate(post.createdAt)}</span>
              </div>
            </div>
            
            <div className="post-status">
              <span className={`status-badge status-${post.status?.toLowerCase()}`}>
                {post.status}
              </span>
            </div>
          </div>

          <div className="post-content">
            <h1 className="post-title">{post.title}</h1>
            <div className="post-body">
              <div dangerouslySetInnerHTML={{ __html: post.content }} />
            </div>
          </div>

          <div className="post-footer">
            <div className="vote-section">
              <button
                onClick={() => handleVote('upvote')}
                className={`vote-btn ${isAuthenticated() ? '' : 'disabled'}`}
                disabled={!isAuthenticated()}
              >
                <i className="fas fa-arrow-up"></i>
                <span>{post.upvotes || 0}</span>
              </button>
              <button
                onClick={() => handleVote('downvote')}
                className={`vote-btn ${isAuthenticated() ? '' : 'disabled'}`}
                disabled={!isAuthenticated()}
              >
                <i className="fas fa-arrow-down"></i>
                <span>{post.downvotes || 0}</span>
              </button>
            </div>

            <div className="post-actions">
              <button
                onClick={() => {
                  if (!isAuthenticated()) {
                    navigate('/login', { state: { from: { pathname: `/post/${id}` } } });
                  }
                }}
                className="btn btn-outline btn-sm"
              >
                <i className="fas fa-share"></i>
                Share
              </button>
            </div>
          </div>
        </article>
      )}

      <div className="comments-section">
        <div className="comments-header">
          <h2 className="comments-title">
            <i className="fas fa-comments"></i>
            Comments ({comments.length})
          </h2>
          
          <div className="comments-controls">
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="sort-select"
            >
              <option value="newest">Newest First</option>
              <option value="oldest">Oldest First</option>
              <option value="most_voted">Most Voted</option>
            </select>
          </div>
        </div>

        {isAuthenticated() ? (
          <div className="comment-form">
            <div className="comment-editor">
              <ReactQuill
                value={commentContent}
                onChange={setCommentContent}
                placeholder="Share your thoughts..."
                modules={{
                  toolbar: [
                    ['bold', 'italic', 'underline'],
                    ['blockquote', 'code-block'],
                    [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                    ['link'],
                    ['clean']
                  ]
                }}
                theme="snow"
              />
            </div>
            
            {error && (
              <div className="alert alert-error">
                <i className="fas fa-exclamation-circle"></i>
                {error}
              </div>
            )}
            
            <div className="comment-form-actions">
              <button
                onClick={handleSubmitComment}
                disabled={submittingComment || !commentContent.trim()}
                className="btn btn-primary"
              >
                {submittingComment ? (
                  <>
                    <div className="loading-spinner"></div>
                    Posting...
                  </>
                ) : (
                  <>
                    <i className="fas fa-paper-plane"></i>
                    Post Comment
                  </>
                )}
              </button>
            </div>
          </div>
        ) : (
          <div className="login-prompt">
            <div className="alert alert-info">
              <i className="fas fa-info-circle"></i>
              Please <button onClick={() => navigate('/login')} className="auth-link">sign in</button> to post comments.
            </div>
          </div>
        )}

        <div className="comments-list">
          {sortedComments.length === 0 ? (
            <div className="empty-comments">
              <i className="fas fa-comments empty-icon"></i>
              <p>No comments yet. Be the first to comment!</p>
            </div>
          ) : (
            sortedComments.map((comment) => (
              <Comment
                key={comment.id}
                comment={comment}
                onVote={handleCommentVote}
                isAuthenticated={isAuthenticated()}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default PostDetail;
