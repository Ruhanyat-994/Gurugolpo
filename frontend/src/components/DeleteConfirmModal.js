import React from 'react';
import './DeleteConfirmModal.css';

const DeleteConfirmModal = ({ 
  isOpen, 
  onClose, 
  onConfirm, 
  title = "Delete Post",
  message = "Are you sure you want to delete this post?",
  confirmText = "Delete Post",
  loading = false 
}) => {
  if (!isOpen) return null;

  return (
    <div className="delete-confirm-overlay" onClick={onClose}>
      <div className="delete-confirm-modal" onClick={(e) => e.stopPropagation()}>
        <div className="delete-confirm-header">
          <h3>
            <i className="fas fa-exclamation-triangle"></i>
            {title}
          </h3>
        </div>
        <div className="delete-confirm-body">
          <p>{message}</p>
          <p className="delete-warning">
            <i className="fas fa-warning"></i>
            This action cannot be undone.
          </p>
        </div>
        <div className="delete-confirm-actions">
          <button 
            className="btn btn-outline"
            onClick={onClose}
            disabled={loading}
          >
            Cancel
          </button>
          <button 
            className="btn btn-danger"
            onClick={onConfirm}
            disabled={loading}
          >
            {loading ? (
              <>
                <div className="loading-spinner"></div>
                Deleting...
              </>
            ) : (
              <>
                <i className="fas fa-trash"></i>
                {confirmText}
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteConfirmModal;
