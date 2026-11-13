import React from 'react';
import { Layers, UserPlus, LogIn, LogOut, User } from 'lucide-react';
import { UserSummary } from '../types';

interface NavbarProps {
  currentView: string;
  setCurrentView: (view: string) => void;
  currentUser: UserSummary | null;
  onLogout: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentView,
  setCurrentView,
  currentUser,
  onLogout
}) => {
  return (
    <header className="header">
      <div className="nav">
        {/* Far-Left: QueueMatrix logo and website name */}
        <div className="brand" onClick={() => setCurrentView('home')}>
          <div className="brand-icon">
            <Layers size={22} />
          </div>
          <span>QueueMatrix</span>
        </div>

        {/* Right side: Login and Sign Up buttons */}
        <div className="nav-actions">
          {currentUser ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.85rem' }}>
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  fontSize: '0.9rem',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
                onClick={() => setCurrentView('dashboard')}
              >
                <User size={18} color="var(--primary)" />
                <span>{currentUser.fullName}</span>
                <span className="badge badge-neutral" style={{ fontSize: '0.65rem' }}>
                  {currentUser.role.replace('_', ' ')}
                </span>
              </div>
              <button
                className="btn btn-outline"
                style={{ padding: '0.45rem 0.85rem', fontSize: '0.85rem' }}
                onClick={onLogout}
                title="Log Out"
              >
                <LogOut size={16} />
                <span>Logout</span>
              </button>
            </div>
          ) : (
            <>
              <button
                className={`btn ${currentView === 'login' ? 'btn-primary' : 'btn-ghost'}`}
                onClick={() => setCurrentView('login')}
              >
                <LogIn size={18} />
                <span>Login</span>
              </button>
              <button
                className={`btn ${currentView === 'signup' ? 'btn-primary' : 'btn-primary'}`}
                onClick={() => setCurrentView('signup')}
              >
                <UserPlus size={18} />
                <span>Sign Up</span>
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};
