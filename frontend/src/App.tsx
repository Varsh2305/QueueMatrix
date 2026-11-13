import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { HomePage } from './pages/HomePage';
import { SignUpPage } from './pages/SignUpPage';
import { LoginPage } from './pages/LoginPage';
import { DashboardPlaceholderPage } from './pages/DashboardPlaceholderPage';
import { AuthData, UserSummary } from './types';

export const App: React.FC = () => {
  const [currentView, setCurrentView] = useState<string>('home');
  const [currentUser, setCurrentUser] = useState<UserSummary | null>(null);

  useEffect(() => {
    const savedUser = localStorage.getItem('qm_user');
    const savedToken = localStorage.getItem('qm_token');
    if (savedUser && savedToken) {
      try {
        setCurrentUser(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('qm_user');
        localStorage.removeItem('qm_token');
      }
    }
  }, []);

  const handleLoginSuccess = (authData: AuthData) => {
    setCurrentUser(authData.user);
    // Role-based redirection: redirect to dashboard placeholder
    setCurrentView('dashboard');
  };

  const handleLogout = () => {
    localStorage.removeItem('qm_token');
    localStorage.removeItem('qm_user');
    setCurrentUser(null);
    setCurrentView('home');
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Navbar
        currentView={currentView}
        setCurrentView={setCurrentView}
        currentUser={currentUser}
        onLogout={handleLogout}
      />

      <main style={{ flex: 1 }}>
        {currentView === 'home' && <HomePage setCurrentView={setCurrentView} />}
        {currentView === 'signup' && <SignUpPage setCurrentView={setCurrentView} />}
        {currentView === 'login' && (
          <LoginPage
            setCurrentView={setCurrentView}
            onLoginSuccess={handleLoginSuccess}
          />
        )}
        {currentView === 'dashboard' && currentUser && (
          <DashboardPlaceholderPage
            user={currentUser}
            setCurrentView={setCurrentView}
            onLogout={handleLogout}
          />
        )}
      </main>

      <Footer />
    </div>
  );
};
