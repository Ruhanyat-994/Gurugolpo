import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { postsAPI } from '../services/api';
import PostCard from '../components/PostCard';
import './Profile.css';

const Profile = () => {
  const { user } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('posts');

  useEffect(() => {
    fetchUserPosts();
  }, []);

  const fetchUserPosts = async () => {
    try {
      setLoading(true);
      const response = await postsAPI.getAllPosts();
      // Filter posts by current user
      const userPosts = response.data.filter(post => post.authorId === user.id);
      setPosts(userPosts);
      setError('');
    } catch (error) {
      setError('Failed to load your posts');
      console.error('Error fetching user posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const getRoleBadgeColor = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'role-admin';
      case 'MODERATOR':
        return 'role-moderator';
      default:
        return 'role-user';
    }
  };

  const getRoleIcon = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'fas fa-crown';
      case 'MODERATOR':
        return 'fas fa-shield-alt';
      default:
        return 'fas fa-user';
    }
  };

  return (
    <div className="profile-container">
      <div className="profile-header">
        <div className="profile-info">
          <div className="profile-avatar">
            <i className="fas fa-user-circle"></i>
          </div>
          <div className="profile-details">
            <h1 className="profile-name">{user?.fullName}</h1>
            <div className="profile-meta">
              <div className="profile-email">
                <i className="fas fa-envelope"></i>
                <span>{user?.email}</span>
              </div>
              <div className="profile-university">
                <i className="fas fa-university"></i>
                <span>{user?.university}</span>
              </div>
              <div className={`profile-role ${getRoleBadgeColor(user?.role)}`}>
                <i className={getRoleIcon(user?.role)}></i>
                <span>{user?.role}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="profile-content">
        <div className="profile-tabs">
          <button
            className={`tab-button ${activeTab === 'posts' ? 'active' : ''}`}
            onClick={() => setActiveTab('posts')}
          >
            <i className="fas fa-comments"></i>
            My Posts ({posts.length})
          </button>
          <button
            className={`tab-button ${activeTab === 'activity' ? 'active' : ''}`}
            onClick={() => setActiveTab('activity')}
          >
            <i className="fas fa-chart-line"></i>
            Activity
          </button>
          <button
            className={`tab-button ${activeTab === 'settings' ? 'active' : ''}`}
            onClick={() => setActiveTab('settings')}
          >
            <i className="fas fa-cog"></i>
            Settings
          </button>
        </div>

        <div className="tab-content">
          {activeTab === 'posts' && (
            <div className="posts-tab">
              <div className="posts-header">
                <h2>Your Posts</h2>
                <p>Posts you've created in the community</p>
              </div>

              {loading && (
                <div className="loading-container">
                  <div className="loading-spinner"></div>
                  <p>Loading your posts...</p>
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
                  <h3>No posts yet</h3>
                  <p>You haven't created any posts yet. Start a discussion!</p>
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
          )}

          {activeTab === 'activity' && (
            <div className="activity-tab">
              <div className="activity-header">
                <h2>Activity Overview</h2>
                <p>Your engagement in the community</p>
              </div>

              <div className="activity-stats">
                <div className="stat-card">
                  <div className="stat-icon">
                    <i className="fas fa-comments"></i>
                  </div>
                  <div className="stat-content">
                    <div className="stat-number">{posts.length}</div>
                    <div className="stat-label">Posts Created</div>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon">
                    <i className="fas fa-thumbs-up"></i>
                  </div>
                  <div className="stat-content">
                    <div className="stat-number">
                      {posts.reduce((total, post) => total + (post.upvotes || 0), 0)}
                    </div>
                    <div className="stat-label">Total Upvotes</div>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon">
                    <i className="fas fa-eye"></i>
                  </div>
                  <div className="stat-content">
                    <div className="stat-number">
                      {posts.reduce((total, post) => total + (post.downvotes || 0), 0)}
                    </div>
                    <div className="stat-label">Total Downvotes</div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'settings' && (
            <div className="settings-tab">
              <div className="settings-header">
                <h2>Account Settings</h2>
                <p>Manage your account preferences</p>
              </div>

              <div className="settings-content">
                <div className="setting-group">
                  <h3>Account Information</h3>
                  <div className="setting-item">
                    <label>Email Address</label>
                    <div className="setting-value">{user?.email}</div>
                  </div>
                  <div className="setting-item">
                    <label>Full Name</label>
                    <div className="setting-value">{user?.fullName}</div>
                  </div>
                  <div className="setting-item">
                    <label>University</label>
                    <div className="setting-value">{user?.university}</div>
                  </div>
                  <div className="setting-item">
                    <label>Role</label>
                    <div className={`setting-value ${getRoleBadgeColor(user?.role)}`}>
                      <i className={getRoleIcon(user?.role)}></i>
                      {user?.role}
                    </div>
                  </div>
                </div>

                <div className="setting-group">
                  <h3>Account Status</h3>
                  <div className="setting-item">
                    <label>Account Status</label>
                    <div className={`setting-value ${user?.isActive ? 'status-active' : 'status-inactive'}`}>
                      <i className={`fas fa-circle ${user?.isActive ? 'active' : 'inactive'}`}></i>
                      {user?.isActive ? 'Active' : 'Inactive'}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Profile;
