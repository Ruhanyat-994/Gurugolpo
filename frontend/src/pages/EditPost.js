import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { postsAPI } from '../services/api';
import './EditPost.css';

const EditPost = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [post, setPost] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    isAnonymous: false,
  });

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      setLoading(true);
      const response = await postsAPI.getPostById(id);
      const postData = response.data;
      
      // Check if user is the author
      if (postData.authorId !== user.id) {
        setError('You can only edit your own posts');
        return;
      }
      
      setPost(postData);
      setFormData({
        title: postData.title,
        content: postData.content,
        isAnonymous: postData.isAnonymous || false,
      });
      setError('');
    } catch (error) {
      console.error('Error fetching post:', error);
      setError('Failed to load post');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    // Clear any previous error/success messages
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      setError('Title is required');
      return;
    }
    
    if (!formData.content.trim()) {
      setError('Content is required');
      return;
    }

    try {
      setSaving(true);
      setError('');
      
      const response = await postsAPI.updatePost(id, formData);
      
      setSuccess('Post updated successfully!');
      setTimeout(() => {
        navigate(`/post/${id}`);
      }, 1500);
      
    } catch (error) {
      console.error('Error updating post:', error);
      if (error.response?.data?.error) {
        setError(error.response.data.error);
      } else {
        setError('Failed to update post. Please try again.');
      }
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    navigate(`/post/${id}`);
  };

  if (loading) {
    return (
      <div className="edit-post-container">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading post...</p>
        </div>
      </div>
    );
  }

  if (error && !post) {
    return (
      <div className="edit-post-container">
        <div className="error-container">
          <i className="fas fa-exclamation-circle"></i>
          <h2>Error</h2>
          <p>{error}</p>
          <button onClick={() => navigate('/')} className="btn btn-primary">
            <i className="fas fa-home"></i>
            Go Home
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="edit-post-container">
      <div className="edit-post-header">
        <h1 className="edit-post-title">
          <i className="fas fa-edit"></i>
          Edit Post
        </h1>
        <p className="edit-post-subtitle">
          Update your post content and settings
        </p>
      </div>

      <div className="edit-post-form-container">
        <form onSubmit={handleSubmit} className="edit-post-form">
          {error && (
            <div className="alert alert-error">
              <i className="fas fa-exclamation-circle"></i>
              {error}
            </div>
          )}

          {success && (
            <div className="alert alert-success">
              <i className="fas fa-check-circle"></i>
              {success}
            </div>
          )}

          <div className="form-group">
            <label htmlFor="title" className="form-label">
              <i className="fas fa-heading"></i>
              Post Title
            </label>
            <input
              type="text"
              id="title"
              name="title"
              value={formData.title}
              onChange={(e) => handleChange('title', e.target.value)}
              className="form-input"
              placeholder="Enter a compelling title for your post"
              disabled={saving}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="content" className="form-label">
              <i className="fas fa-align-left"></i>
              Post Content
            </label>
            <textarea
              id="content"
              name="content"
              value={formData.content}
              onChange={(e) => handleChange('content', e.target.value)}
              className="form-input form-textarea"
              placeholder="Share your thoughts, ask questions, or start a discussion..."
              rows="8"
              disabled={saving}
              required
            />
          </div>

          <div className="form-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                name="isAnonymous"
                checked={formData.isAnonymous}
                onChange={(e) => handleChange('isAnonymous', e.target.checked)}
                disabled={saving}
              />
              <span className="checkbox-text">
                <i className="fas fa-user-secret"></i>
                Post anonymously (your name will be hidden)
              </span>
            </label>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={handleCancel}
              className="btn btn-outline"
              disabled={saving}
            >
              <i className="fas fa-times"></i>
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={saving}
            >
              {saving ? (
                <>
                  <div className="loading-spinner"></div>
                  Updating...
                </>
              ) : (
                <>
                  <i className="fas fa-save"></i>
                  Update Post
                </>
              )}
            </button>
          </div>
        </form>

        <div className="edit-guidelines">
          <h3>
            <i className="fas fa-lightbulb"></i>
            Editing Guidelines
          </h3>
          <ul>
            <li>Keep your title clear and descriptive</li>
            <li>Provide detailed content to encourage discussion</li>
            <li>Be respectful and constructive in your language</li>
            <li>Check for typos and formatting before saving</li>
            <li>Consider if anonymous posting is appropriate for your content</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default EditPost;
