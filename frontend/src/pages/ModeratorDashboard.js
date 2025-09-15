import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { moderatorAPI } from '../services/api';
import './Dashboard.css';

const ModeratorDashboard = () => {
  const { user } = useAuth();
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const response = await moderatorAPI.getDashboard();
      setDashboardData(response.data);
      setError('');
    } catch (error) {
      setError('Failed to load dashboard data');
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading moderator dashboard...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <div className="alert alert-error">
          <i className="fas fa-exclamation-circle"></i>
          {error}
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1 className="dashboard-title">
          <i className="fas fa-shield-alt"></i>
          Moderator Dashboard
        </h1>
        <p className="dashboard-subtitle">
          Welcome back, {user?.fullName}. Moderate your community.
        </p>
      </div>

      <div className="dashboard-content">
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon">
              <i className="fas fa-comments"></i>
            </div>
            <div className="stat-content">
              <div className="stat-number">{dashboardData?.totalPosts || 0}</div>
              <div className="stat-label">Total Posts</div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">
              <i className="fas fa-comment-dots"></i>
            </div>
            <div className="stat-content">
              <div className="stat-number">{dashboardData?.totalComments || 0}</div>
              <div className="stat-label">Total Comments</div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">
              <i className="fas fa-users"></i>
            </div>
            <div className="stat-content">
              <div className="stat-number">{dashboardData?.totalUsers || 0}</div>
              <div className="stat-label">Total Users</div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">
              <i className="fas fa-university"></i>
            </div>
            <div className="stat-content">
              <div className="stat-number">{user?.university}</div>
              <div className="stat-label">Your University</div>
            </div>
          </div>
        </div>

        <div className="dashboard-sections">
          <div className="dashboard-section">
            <div className="section-header">
              <h2 className="section-title">
                <i className="fas fa-tools"></i>
                Moderation Tools
              </h2>
            </div>
            <div className="section-content">
              <div className="action-buttons">
                <button className="btn btn-danger">
                  <i className="fas fa-trash"></i>
                  Delete Post
                </button>
                <button className="btn btn-danger">
                  <i className="fas fa-comment-slash"></i>
                  Delete Comment
                </button>
                <button className="btn btn-warning">
                  <i className="fas fa-ban"></i>
                  Ban User
                </button>
                <button className="btn btn-success">
                  <i className="fas fa-check"></i>
                  Unban User
                </button>
              </div>
            </div>
          </div>

          <div className="dashboard-section">
            <div className="section-header">
              <h2 className="section-title">
                <i className="fas fa-chart-bar"></i>
                Community Overview
              </h2>
            </div>
            <div className="section-content">
              <div className="overview-grid">
                <div className="overview-item">
                  <div className="overview-label">Moderation Status</div>
                  <div className="overview-value status-active">
                    <i className="fas fa-circle"></i>
                    Active
                  </div>
                </div>
                <div className="overview-item">
                  <div className="overview-label">University</div>
                  <div className="overview-value">
                    {user?.university}
                  </div>
                </div>
                <div className="overview-item">
                  <div className="overview-label">Moderator Role</div>
                  <div className="overview-value role-moderator">
                    <i className="fas fa-shield-alt"></i>
                    Moderator
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="dashboard-section">
            <div className="section-header">
              <h2 className="section-title">
                <i className="fas fa-info-circle"></i>
                Moderation Guidelines
              </h2>
            </div>
            <div className="section-content">
              <div className="guidelines-list">
                <div className="guideline-item">
                  <i className="fas fa-check-circle"></i>
                  <span>Remove inappropriate content promptly</span>
                </div>
                <div className="guideline-item">
                  <i className="fas fa-check-circle"></i>
                  <span>Warn users before banning for minor violations</span>
                </div>
                <div className="guideline-item">
                  <i className="fas fa-check-circle"></i>
                  <span>Maintain fair and consistent moderation</span>
                </div>
                <div className="guideline-item">
                  <i className="fas fa-check-circle"></i>
                  <span>Document moderation actions when necessary</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ModeratorDashboard;
