import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance for authenticated requests
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create axios instance for public requests (no auth token)
const publicApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  getCurrentUser: () => api.get('/auth/me'),
  testAuth: () => api.get('/auth/test-auth'),
};

// Posts API
export const postsAPI = {
  getAllPosts: () => publicApi.get('/posts'),
  getPostById: (id) => publicApi.get(`/posts/${id}`),
  createPost: (postData) => api.post('/posts/create', postData),
  updatePost: (id, postData) => api.put(`/posts/${id}`, postData),
  deletePost: (id) => api.delete(`/posts/${id}`),
  getPostsByUniversity: (university) => publicApi.get(`/posts/university/${university}`),
  searchPosts: (query) => publicApi.get(`/posts/search?q=${query}`),
  getPostVotes: (id) => publicApi.get(`/posts/${id}/votes`),
};

// Comments API
export const commentsAPI = {
  getCommentsForPost: (postId) => publicApi.get(`/comments/${postId}`),
  addComment: (postId, commentData) => api.post(`/comments/${postId}`, commentData),
  updateComment: (commentId, commentData) => api.put(`/comments/${commentId}`, commentData),
  deleteComment: (commentId) => api.delete(`/comments/${commentId}`),
  getCommentVotes: (commentId) => publicApi.get(`/comments/${commentId}/votes`),
};

// Voting API
export const votingAPI = {
  upvotePost: (postId) => api.post(`/votes/posts/${postId}/upvote`),
  downvotePost: (postId) => api.post(`/votes/posts/${postId}/downvote`),
  upvoteComment: (commentId) => api.post(`/votes/comments/${commentId}/upvote`),
  downvoteComment: (commentId) => api.post(`/votes/comments/${commentId}/downvote`),
  getPostVoteCount: (postId) => api.get(`/votes/posts/${postId}/count`),
  getCommentVoteCount: (commentId) => api.get(`/votes/comments/${commentId}/count`),
};

// Universities API
export const universitiesAPI = {
  getAllUniversities: () => api.get('/universities'),
  getUniversityById: (id) => api.get(`/universities/${id}`),
  createUniversity: (universityData) => api.post('/universities', universityData),
  updateUniversity: (id, universityData) => api.put(`/universities/${id}`, universityData),
  deleteUniversity: (id) => api.delete(`/universities/${id}`),
};

// Admin API
export const adminAPI = {
  getDashboard: () => api.get('/admin/dashboard'),
  togglePostManagement: (enabled) => api.post('/admin/settings/post-management', { enabled }),
  promoteUser: (userId, university) => api.post(`/admin/users/${userId}/promote`, { university }),
  deleteUser: (userId) => api.delete(`/admin/users/${userId}`),
  deletePost: (postId) => api.delete(`/admin/posts/${postId}`),
  deleteComment: (commentId) => api.delete(`/admin/comments/${commentId}`),
};

// Moderator API
export const moderatorAPI = {
  getDashboard: () => api.get('/moderator/dashboard'),
  deletePost: (postId) => api.delete(`/moderator/posts/${postId}`),
  deleteComment: (commentId) => api.delete(`/moderator/comments/${commentId}`),
  banUser: (userId) => api.post(`/moderator/users/${userId}/ban`),
  unbanUser: (userId) => api.post(`/moderator/users/${userId}/unban`),
};

export default api;
