import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { postsAPI } from '../services/api';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import './CreatePost.css';

const CreatePost = () => {
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    isAnonymous: false,
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { user } = useAuth();
  const navigate = useNavigate();

  const handleChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    if (error) setError('');
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

    setLoading(true);
    setError('');

    try {
      const response = await postsAPI.createPost(formData);
      navigate(`/post/${response.data.id}`);
    } catch (error) {
      setError(error.response?.data?.error || 'Failed to create post');
      console.error('Error creating post:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/');
  };

  return (
    <div className="create-post-container">
      <div className="create-post-header">
        <h1 className="create-post-title">
          <i className="fas fa-plus"></i>
          Create New Post
        </h1>
        <p className="create-post-subtitle">
          Share your thoughts with the {user?.university} community
        </p>
      </div>

      <div className="create-post-form-container">
        <form onSubmit={handleSubmit} className="create-post-form">
          {error && (
            <div className="alert alert-error">
              <i className="fas fa-exclamation-circle"></i>
              {error}
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
              value={formData.title}
              onChange={(e) => handleChange('title', e.target.value)}
              className="form-input"
              placeholder="What's your post about?"
              maxLength={500}
              disabled={loading}
              required
            />
            <div className="character-count">
              {formData.title.length}/500 characters
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="content" className="form-label">
              <i className="fas fa-edit"></i>
              Post Content
            </label>
            <div className="content-editor">
              <ReactQuill
                value={formData.content}
                onChange={(value) => handleChange('content', value)}
                placeholder="Share your thoughts, ask questions, or start a discussion..."
                modules={{
                  toolbar: [
                    [{ 'header': [1, 2, 3, false] }],
                    ['bold', 'italic', 'underline', 'strike'],
                    [{ 'color': [] }, { 'background': [] }],
                    [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                    [{ 'indent': '-1'}, { 'indent': '+1' }],
                    ['blockquote', 'code-block'],
                    ['link', 'image'],
                    ['clean']
                  ]
                }}
                theme="snow"
                readOnly={loading}
              />
            </div>
            <div className="character-count">
              {formData.content.replace(/<[^>]*>/g, '').length}/10000 characters
            </div>
          </div>

          <div className="form-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                name="isAnonymous"
                checked={formData.isAnonymous}
                onChange={(e) => handleChange('isAnonymous', e.target.checked)}
                disabled={loading}
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
              disabled={loading}
            >
              <i className="fas fa-times"></i>
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading || !formData.title.trim() || !formData.content.trim()}
            >
              {loading ? (
                <>
                  <div className="loading-spinner"></div>
                  Creating Post...
                </>
              ) : (
                <>
                  <i className="fas fa-paper-plane"></i>
                  Create Post
                </>
              )}
            </button>
          </div>
        </form>

        <div className="posting-guidelines">
          <h3 className="guidelines-title">
            <i className="fas fa-lightbulb"></i>
            Posting Guidelines
          </h3>
          <ul className="guidelines-list">
            <li>
              <i className="fas fa-check"></i>
              Be respectful and constructive in your discussions
            </li>
            <li>
              <i className="fas fa-check"></i>
              Use clear, descriptive titles that summarize your post
            </li>
            <li>
              <i className="fas fa-check"></i>
              Provide context and details to help others understand your point
            </li>
            <li>
              <i className="fas fa-check"></i>
              Use appropriate formatting to make your post easy to read
            </li>
            <li>
              <i className="fas fa-check"></i>
              Stay on topic and relevant to your university community
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default CreatePost;
