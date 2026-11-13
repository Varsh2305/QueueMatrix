import React, { useState } from 'react';
import {
  User,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  ArrowRight,
  Info
} from 'lucide-react';
import { api } from '../services/api';
import { OrganizationType, Role, UserSummary } from '../types';

interface SignUpPageProps {
  setCurrentView: (view: string) => void;
  onRegisteredSuccess?: (user: UserSummary) => void;
}

export const SignUpPage: React.FC<SignUpPageProps> = ({ setCurrentView, onRegisteredSuccess }) => {
  // Form Fields
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [countryCode, setCountryCode] = useState('+91');
  const [orgType, setOrgType] = useState<OrganizationType>('HOSPITAL');
  // Permitted public roles: CUSTOMER or STAFF
  const [role, setRole] = useState<Role>('CUSTOMER');

  // UI state
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [registeredUser, setRegisteredUser] = useState<UserSummary | null>(null);

  const validateForm = (): boolean => {
    if (!fullName.trim()) {
      setError('Full Name is required.');
      return false;
    }
    if (!email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError('Please provide a valid email address.');
      return false;
    }
    const cleanPhone = phone.trim().replace(/[\s-]/g, '');
    if (!cleanPhone || cleanPhone.length < 10 || !/^[0-9]+$/.test(cleanPhone)) {
      setError('Please enter a valid 10-digit mobile phone number.');
      return false;
    }
    if (!password || password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return false;
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match. Please re-enter.');
      return false;
    }
    return true;
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!validateForm()) return;

    setLoading(true);
    try {
      const fullPhone = `${countryCode}${phone.trim().replace(/[\s-]/g, '').replace(/^0+/, '')}`;
      const user = await api.register({
        fullName: fullName.trim(),
        email: email.trim().toLowerCase(),
        phone: fullPhone,
        password: password,
        organizationType: orgType,
        role: role
      });

      setRegisteredUser(user);

      if (user.role === 'CUSTOMER') {
        setSuccessMessage('Registration completed successfully! Your customer account is active.');
      } else if (user.role === 'STAFF') {
        setSuccessMessage('Registration submitted! As a Staff member, your account is awaiting authorized approval.');
      } else {
        setSuccessMessage('Registration successful! You can now log in.');
      }

      if (onRegisteredSuccess) {
        onRegisteredSuccess(user);
      }
    } catch (err: any) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        {registeredUser ? (
          <div>
            <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
              <div
                style={{
                  width: '64px',
                  height: '64px',
                  borderRadius: '50%',
                  background: 'var(--success-bg)',
                  color: 'var(--success)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  margin: '0 auto 1rem'
                }}
              >
                <CheckCircle2 size={36} />
              </div>
              <h2 className="auth-title">Welcome to QueueMatrix!</h2>
              <p className="auth-subtitle">{successMessage}</p>
            </div>

            <div style={{ background: '#f8fafc', border: '1px solid var(--border)', borderRadius: 'var(--radius-md)', padding: '1.25rem', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.4rem 0' }}>
                <span style={{ color: 'var(--text-muted)' }}>Full Name:</span>
                <strong>{registeredUser.fullName}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.4rem 0' }}>
                <span style={{ color: 'var(--text-muted)' }}>Email:</span>
                <strong>{registeredUser.email}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.4rem 0' }}>
                <span style={{ color: 'var(--text-muted)' }}>Phone Number:</span>
                <strong>{registeredUser.phone}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.4rem 0' }}>
                <span style={{ color: 'var(--text-muted)' }}>Role:</span>
                <span className="badge badge-neutral">{registeredUser.role.replace('_', ' ')}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.4rem 0' }}>
                <span style={{ color: 'var(--text-muted)' }}>Status:</span>
                <span className={`badge ${registeredUser.status === 'ACTIVE' ? 'badge-success' : 'badge-warning'}`}>
                  {registeredUser.status.replace(/_/g, ' ')}
                </span>
              </div>
            </div>

            {registeredUser.status === 'ACTIVE' ? (
              <button
                className="btn btn-primary"
                style={{ width: '100%', padding: '0.75rem' }}
                onClick={() => setCurrentView('login')}
              >
                <span>Proceed to Login</span>
                <ArrowRight size={18} />
              </button>
            ) : (
              <button
                className="btn btn-outline"
                style={{ width: '100%', padding: '0.75rem' }}
                onClick={() => setCurrentView('home')}
              >
                <span>Return to Homepage</span>
              </button>
            )}
          </div>
        ) : (
          <div>
            <div className="auth-header">
              <h2 className="auth-title">Create QueueMatrix Account</h2>
            </div>

            {error && (
              <div className="alert alert-error">
                <AlertCircle size={18} />
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleRegister}>
              {/* Full Name */}
              <div className="form-group">
                <label className="form-label">Full Name *</label>
                <input
                  type="text"
                  className="form-input"
                  value={fullName}
                  onChange={e => setFullName(e.target.value)}
                  required
                />
              </div>

              {/* Email Address */}
              <div className="form-group">
                <label className="form-label">Email Address *</label>
                <input
                  type="email"
                  className="form-input"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  required
                />
              </div>

              {/* Password */}
              <div className="form-group">
                <label className="form-label">Password *</label>
                <input
                  type="password"
                  className="form-input"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                />
              </div>

              {/* Confirm Password */}
              <div className="form-group">
                <label className="form-label">Confirm Password *</label>
                <input
                  type="password"
                  className="form-input"
                  value={confirmPassword}
                  onChange={e => setConfirmPassword(e.target.value)}
                  required
                />
              </div>

              {/* Phone Number with Country Code */}
              <div className="form-group">
                <label className="form-label">Mobile Phone Number *</label>
                <div className="phone-input-group">
                  <select
                    className="form-select country-code-select"
                    value={countryCode}
                    onChange={e => setCountryCode(e.target.value)}
                  >
                    <option value="+91">IN (+91)</option>
                    <option value="+1">US (+1)</option>
                    <option value="+44">UK (+44)</option>
                    <option value="+971">UAE (+971)</option>
                  </select>
                  <input
                    type="tel"
                    className="form-input"
                    value={phone}
                    onChange={e => setPhone(e.target.value)}
                    required
                  />
                </div>
              </div>

              {/* Organization Type dropdown */}
              <div className="form-group">
                <label className="form-label">Organization Category *</label>
                <select
                  className="form-select"
                  value={orgType}
                  onChange={e => setOrgType(e.target.value as OrganizationType)}
                >
                  <option value="HOSPITAL">Hospital / Clinic</option>
                  <option value="RESTAURANT">Restaurant / Food Service</option>
                  <option value="BANK">Bank / Financial Institution</option>
                  <option value="GOVERNMENT_OFFICE">Government Office / Public Sector</option>
                </select>
              </div>

              {/* Role dropdown (Only permitted public roles) */}
              <div className="form-group">
                <label className="form-label">User Role *</label>
                <select
                  className="form-select"
                  value={role}
                  onChange={e => setRole(e.target.value as Role)}
                >
                  <option value="CUSTOMER">Customer</option>
                  <option value="STAFF">Staff</option>
                </select>
                {role === 'STAFF' && (
                  <div className="alert alert-warning" style={{ marginTop: '0.65rem', padding: '0.55rem 0.75rem', fontSize: '0.8rem' }}>
                    <Info size={16} />
                    <span>Staff accounts require organizational authorization before active queue handling.</span>
                  </div>
                )}
              </div>

              <button
                type="submit"
                className="btn btn-primary"
                style={{ width: '100%', padding: '0.75rem', marginTop: '1rem' }}
                disabled={loading}
              >
                {loading ? (
                  <>
                    <RefreshCw size={18} className="spin" />
                    <span>Creating Account...</span>
                  </>
                ) : (
                  <>
                    <User size={18} />
                    <span>Register Account</span>
                  </>
                )}
              </button>
            </form>

            <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.9rem' }}>
              Already registered?{' '}
              <button
                className="btn btn-ghost"
                style={{ padding: '0.2rem 0.5rem', color: 'var(--primary)', fontWeight: 600 }}
                onClick={() => setCurrentView('login')}
              >
                Log In Here
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
