import React from 'react';
import { useModal } from '../contexts/ModalContext';
import DeleteConfirmModal from './DeleteConfirmModal';
import AuthPopup from './AuthPopup';

const GlobalModals = () => {
  const { modals, hideModal } = useModal();

  return (
    <>
      {/* Delete Confirmation Modal */}
      {modals.deleteConfirm && (
        <DeleteConfirmModal
          isOpen={true}
          onClose={() => hideModal('deleteConfirm')}
          onConfirm={modals.deleteConfirm.onConfirm}
          title={modals.deleteConfirm.title || "Delete Post"}
          message={modals.deleteConfirm.message || "Are you sure you want to delete this post?"}
          confirmText={modals.deleteConfirm.confirmText || "Delete Post"}
          loading={modals.deleteConfirm.loading || false}
        />
      )}

      {/* Auth Popup Modal */}
      {modals.authPopup && (
        <AuthPopup
          isOpen={true}
          onClose={() => hideModal('authPopup')}
          mode={modals.authPopup.mode || 'login'}
          onSuccess={(user) => {
            hideModal('authPopup');
            if (modals.authPopup.onSuccess) {
              modals.authPopup.onSuccess(user);
            }
          }}
        />
      )}
    </>
  );
};

export default GlobalModals;
