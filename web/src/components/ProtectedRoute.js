import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../features/auth/AuthContext';

const ProtectedRoute = ({ allowedRoles, element }) => {
  const { isLoggedIn } = useAuth();
  const role = localStorage.getItem('role');

  if (!isLoggedIn) {
    return <Navigate to="/login" replace />;
  }

  if (!allowedRoles.includes(role)) {
    return <Navigate to="/403" replace />;
  }

  return element;
};

export default ProtectedRoute;
