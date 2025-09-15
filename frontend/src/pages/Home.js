import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { postsAPI } from '../services/api';
import PostCard from '../components/PostCard';
import './Home.css';

const Home = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedUniversity, setSelectedUniversity] = useState('');
  const [universities, setUniversities] = useState([]);

  const { isAuthenticated } = useAuth();

  useEffect(() => {
    fetchPosts();
    fetchUniversities();
  }, []);

  useEffect(() => {
    if (searchQuery.trim()) {
      searchPosts();
    } else {
      fetchPosts();
    }
  }, [searchQuery]);

  useEffect(() => {
    if (selectedUniversity) {
      fetchPostsByUniversity();
    } else if (!searchQuery.trim()) {
      fetchPosts();
    }
  }, [selectedUniversity]);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const response = await postsAPI.getAllPosts();
      setPosts(response.data);
      setError('');
    } catch (error) {
      setError('Failed to load posts');
      console.error('Error fetching posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchPostsByUniversity = async () => {
    try {
      setLoading(true);
      const response = await postsAPI.getPostsByUniversity(selectedUniversity);
      setPosts(response.data);
      setError('');
    } catch (error) {
      setError('Failed to load posts');
      console.error('Error fetching posts by university:', error);
    } finally {
      setLoading(false);
    }
  };

  const searchPosts = async () => {
    try {
      setLoading(true);
      const response = await postsAPI.searchPosts(searchQuery);
      setPosts(response.data);
      setError('');
    } catch (error) {
      setError('Failed to search posts');
      console.error('Error searching posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchUniversities = async () => {
    try {
      const response = await postsAPI.getAllPosts();
      const uniqueUniversities = [...new Set(response.data.map(post => post.university))];
      setUniversities(uniqueUniversities);
    } catch (error) {
      console.error('Error fetching universities:', error);
    }
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleUniversityChange = (e) => {
    setSelectedUniversity(e.target.value);
  };

  const clearFilters = () => {
    setSearchQuery('');
    setSelectedUniversity('');
    fetchPosts();
  };

  return (
    <div className="home-container">
      <div className="home-header">
        <div className="hero-section">
          <h1 className="hero-title">
            <i className="fas fa-graduation-cap"></i>
            Welcome to Gurugolpo
          </h1>
          <p className="hero-subtitle">
            Connect with students from universities across the country. 
            Share ideas, ask questions, and build your academic community.
          </p>
          
          {isAuthenticated() && (
            <Link to="/create-post" className="btn btn-primary btn-lg">
              <i className="fas fa-plus"></i>
              Create New Post
            </Link>
          )}
        </div>

        <div className="filters-section">
          <div className="search-bar">
            <div className="search-input-container">
              <i className="fas fa-search search-icon"></i>
              <input
                type="text"
                placeholder="Search posts..."
                value={searchQuery}
                onChange={handleSearchChange}
                className="search-input"
              />
              {searchQuery && (
                <button
                  onClick={() => setSearchQuery('')}
                  className="clear-search"
                  aria-label="Clear search"
                >
                  <i className="fas fa-times"></i>
                </button>
              )}
            </div>
          </div>

          <div className="filter-controls">
            <select
              value={selectedUniversity}
              onChange={handleUniversityChange}
              className="university-filter"
            >
              <option value="">All Universities</option>
              {universities.map((university, index) => (
                <option key={index} value={university}>
                  {university}
                </option>
              ))}
            </select>

            {(searchQuery || selectedUniversity) && (
              <button onClick={clearFilters} className="btn btn-outline btn-sm">
                <i className="fas fa-times"></i>
                Clear Filters
              </button>
            )}
          </div>
        </div>
      </div>

      <div className="posts-section">
        <div className="posts-header">
          <h2 className="posts-title">
            <i className="fas fa-comments"></i>
            Recent Discussions
            {posts.length > 0 && (
              <span className="posts-count">({posts.length} posts)</span>
            )}
          </h2>
        </div>

        {loading && (
          <div className="loading-container">
            <div className="loading-spinner"></div>
            <p>Loading posts...</p>
          </div>
        )}

        {error && (
          <div className="alert alert-error">
            <i className="fas fa-exclamation-circle"></i>
            {error}
          </div>
        )}

        {!loading && !error && posts.length === 0 && (
          <div className="empty-state">
            <i className="fas fa-comments empty-icon"></i>
            <h3>No posts found</h3>
            <p>
              {searchQuery || selectedUniversity
                ? 'Try adjusting your search or filters'
                : 'Be the first to start a discussion!'}
            </p>
            {isAuthenticated() && (
              <Link to="/create-post" className="btn btn-primary">
                <i className="fas fa-plus"></i>
                Create First Post
              </Link>
            )}
          </div>
        )}

        {!loading && !error && posts.length > 0 && (
          <div className="posts-grid">
            {posts.map((post) => (
              <PostCard key={post.id} post={post} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Home;
