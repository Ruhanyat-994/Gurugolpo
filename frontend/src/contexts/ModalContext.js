import React, { createContext, useContext, useState } from 'react';

const ModalContext = createContext();

export const useModal = () => {
  const context = useContext(ModalContext);
  if (!context) {
    throw new Error('useModal must be used within a ModalProvider');
  }
  return context;
};

export const ModalProvider = ({ children }) => {
  const [modals, setModals] = useState({});

  const showModal = (modalId, modalData) => {
    setModals(prev => ({
      ...prev,
      [modalId]: modalData
    }));
  };

  const hideModal = (modalId) => {
    setModals(prev => {
      const newModals = { ...prev };
      delete newModals[modalId];
      return newModals;
    });
  };

  const hideAllModals = () => {
    setModals({});
  };

  return (
    <ModalContext.Provider value={{
      modals,
      showModal,
      hideModal,
      hideAllModals
    }}>
      {children}
    </ModalContext.Provider>
  );
};
