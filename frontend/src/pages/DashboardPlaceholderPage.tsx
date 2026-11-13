import React from 'react';
import { ShieldCheck, User, Users, Building, CheckCircle2, ArrowLeft } from 'lucide-react';
import { UserSummary } from '../types';

interface DashboardPlaceholderPageProps {
  user: UserSummary;
  setCurrentView: (view: string) => void;
  onLogout: () => void;
}

export const DashboardPlaceholderPage: React.FC<DashboardPlaceholderPageProps> = ({
  user,
  setCurrentView,
  onLogout
}) => {
  const getRoleDetails = () => {
    switch (user.role) {
      case 'SUPER_ADMIN':
        return {
          title: 'Super Administrator Console',
          icon: <ShieldCheck size={32} color="var(--primary)" />,
          description: 'Global system overview, tenant approvals, queue performance telemetry and audit logs.',
          module2Notice: 'Module 2 will enable tenant approval queue management and system configuration.'
        };
      case 'ORGANIZATION_ADMIN':
        return {
          title: 'Organization Admin Portal',
          icon: <Building size={32} color="var(--primary)" />,
          description: `Organization: ${user.organizationName || 'Registered Tenant'}. Branch setup, staff roster, counters, and service desks.`,
          module2Notice: user.status === 'PENDING_APPROVAL'
            ? 'Account status is PENDING_APPROVAL. Once verified by the platform admin, organizational queue configuration will unlock in Module 2.'
            : 'Organizational queue setup, counter assignment and staff roles will unlock in Module 2.'
        };
      case 'STAFF':
        return {
          title: 'Service Staff Counter Station',
          icon: <Users size={32} color="var(--primary)" />,
          description: 'Desk terminal for calling tokens, marking service completed, or transferring tickets.',
          module2Notice: 'Live counter calling interface will be introduced in Module 2.'
        };
      case 'CUSTOMER':
      default:
        return {
          title: 'Customer Self-Service Hub',
          icon: <User size={32} color="var(--primary)" />,
          description: 'Take virtual tokens, view live queue positions, and track real-time queue progress.',
          module2Notice: 'Virtual token generation and mobile waitlist tracking will activate in Module 2.'
        };
    }
  };

  const details = getRoleDetails();

  return (
    <div className="container" style={{ padding: '3rem 1.5rem', minHeight: 'calc(100vh - 160px)' }}>
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        {/* User Card */}
        <div style={{ background: '#ffffff', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)', padding: '2.5rem', boxShadow: 'var(--shadow-md)' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <div style={{ width: '60px', height: '60px', borderRadius: 'var(--radius-md)', background: 'var(--primary-light)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                {details.icon}
              </div>
              <div>
                <h1 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--secondary)' }}>{details.title}</h1>
                <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginTop: '0.25rem' }}>
                  <span className="badge badge-neutral">{user.role.replace('_', ' ')}</span>
                  <span className={`badge ${user.status === 'ACTIVE' ? 'badge-success' : 'badge-warning'}`}>
                    {user.status.replace(/_/g, ' ')}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <p style={{ color: 'var(--text-muted)', fontSize: '1.05rem', marginBottom: '2rem' }}>
            {details.description}
          </p>

          {/* Account Profile details */}
          <div style={{ background: '#f8fafc', border: '1px solid var(--border)', borderRadius: 'var(--radius-md)', padding: '1.5rem', marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '1rem', color: 'var(--secondary)' }}>
              Verified Credentials
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', fontSize: '0.9rem' }}>
              <div>
                <div style={{ color: 'var(--text-muted)' }}>Full Name</div>
                <div style={{ fontWeight: 600 }}>{user.fullName}</div>
              </div>
              <div>
                <div style={{ color: 'var(--text-muted)' }}>Phone Number</div>
                <div style={{ fontWeight: 600, fontFamily: 'JetBrains Mono, monospace' }}>{user.phone}</div>
              </div>
              <div>
                <div style={{ color: 'var(--text-muted)' }}>Email Address</div>
                <div style={{ fontWeight: 600 }}>{user.email}</div>
              </div>
              {user.preferredOrgType && (
                <div>
                  <div style={{ color: 'var(--text-muted)' }}>Organization Category</div>
                  <div style={{ fontWeight: 600 }}>{user.preferredOrgType.replace(/_/g, ' ')}</div>
                </div>
              )}
            </div>
          </div>

          {/* Module 2 Roadmap Notice */}
          <div className="alert alert-info" style={{ display: 'flex', alignItems: 'flex-start', gap: '0.75rem' }}>
            <CheckCircle2 size={20} style={{ flexShrink: 0, marginTop: '2px' }} />
            <div>
              <strong>Module 1 Authentication Complete!</strong>
              <div style={{ marginTop: '0.25rem', fontSize: '0.875rem' }}>
                {details.module2Notice}
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '2rem' }}>
            <button className="btn btn-ghost" onClick={() => setCurrentView('home')}>
              <ArrowLeft size={16} />
              <span>Back to Home</span>
            </button>
            <button className="btn btn-outline" onClick={onLogout}>
              <span>Log Out</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
