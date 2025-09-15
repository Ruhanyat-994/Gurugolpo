import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { adminAPI, postsAPI, commentsAPI } from '../services/api';
import './Dashboard.css';

const AdminDashboard = () => {
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
      const response = await adminAPI.getDashboard();
      setDashboardData(response.data);
      setError('');
    } catch (error) {
      setError('Failed to load dashboard data');
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleTogglePostManagement = async (enabled) => {
    try {
      await adminAPI.togglePostManagement(enabled);
      // Refresh dashboard data
      fetchDashboardData();
    } catch (error) {
      console.error('Error toggling post management:', error);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading admin dashboard...</p>
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
          <i className="fas fa-crown"></i>
          Admin Dashboard
        </h1>
        <p className="dashboard-subtitle">
          Welcome back, {user?.fullName}. Manage your platform.
        </p>
      </div>

      <div className="dashboard-content">
        <div className="stats-grid">
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
              <i className="fas fa-user-check"></i>
            </div>
            <div className="stat-content">
              <div className="stat-number">{dashboardData?.activeUsers || 0}</div>
              <div className="stat-label">Active Users</div>
            </div>
          </div>
        </div>

        <div className="dashboard-sections">
          <div className="dashboard-section">
            <div className="section-header">
              <h2 className="section-title">
                <i className="fas fa-cog"></i>
                System Settings
              </h2>
            </div>
            <div className="section-content">
              <div className="setting-item">
                <div className="setting-info">
                  <h3>Post Management</h3>
                  <p>Enable or disable post creation for users</p>
                </div>
                <div className="setting-control">
                  <label className="toggle-switch">
                    <input
                      type="checkbox"
                      onChange={(e) => handleTogglePostManagement(e.target.checked)}
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>
              </div>
            </div>
          </div>

          <div className="dashboard-section">
            <div className="section-header">
              <h2 className="section-title">
                <i className="fas fa-chart-bar"></i>
                Platform Overview
              </h2>
            </div>
            <div className="section-content">
              <div className="overview-grid">
                <div className="overview-item">
                  <div className="overview-label">Platform Status</div>
                  <div className="overview-value status-active">
                    <i className="fas fa-circle"></i>
                    Online
                  </div>
                </div>
                <div className="overview-item">
                  <div className="overview-label">Last Updated</div>
                  <div className="overview-value">
                    {new Date().toLocaleDateString()}
                  </div>
                </div>
                <div className="overview-item">
                  <div className="overview-label">Admin Role</div>
                  <div className="overview-value role-admin">
                    <i className="fas fa-crown"></i>
                    Administrator
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="dashboard-section">
            <div className="section-header">
              <h2 className="section-title">
                <i className="fas fa-tools"></i>
                Quick Actions
              </h2>
            </div>
            <div className="section-content">
              <div className="action-buttons">
                <button className="btn btn-primary">
                  <i className="fas fa-user-plus"></i>
                  Promote User
                </button>
                <button className="btn btn-secondary">
                  <i className="fas fa-trash"></i>
                  Delete Post
                </button>
                <button className="btn btn-secondary">
                  <i className="fas fa-ban"></i>
                  Ban User
                </button>
                <button className="btn btn-outline">
                  <i className="fas fa-download"></i>
                  Export Data
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
