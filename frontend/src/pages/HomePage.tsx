import React, { useEffect, useState } from 'react';
import {
  Building2,
  UtensilsCrossed,
  Landmark,
  Building
} from 'lucide-react';
import { api } from '../services/api';
import { PublicHomeData } from '../types';

interface HomePageProps {
  setCurrentView?: (view: string) => void;
}

export const HomePage: React.FC<HomePageProps> = () => {
  const [homeData, setHomeData] = useState<PublicHomeData | null>(null);

  useEffect(() => {
    api.getPublicHome()
      .then(data => setHomeData(data))
      .catch(err => console.error('Failed to load public home data', err));
  }, []);

  const getCategoryIcon = (key: string) => {
    switch (key) {
      case 'HOSPITALS':
        return <Building2 size={24} />;
      case 'RESTAURANTS':
        return <UtensilsCrossed size={24} />;
      case 'BANKS':
        return <Landmark size={24} />;
      case 'GOVERNMENT_OFFICES':
        return <Building size={24} />;
      default:
        return <Building2 size={24} />;
    }
  };

  const defaultCategories = [
    {
      key: 'HOSPITALS',
      name: 'Hospitals',
      description: 'Outpatient clinics, diagnostic centers, and triage desks',
      icon: <Building2 size={24} />
    },
    {
      key: 'RESTAURANTS',
      name: 'Restaurants',
      description: 'Table seating waitlists, takeaway pickup, and host stations',
      icon: <UtensilsCrossed size={24} />
    },
    {
      key: 'BANKS',
      name: 'Banks',
      description: 'Teller counters, loan desks, and branch advisory services',
      icon: <Landmark size={24} />
    },
    {
      key: 'GOVERNMENT_OFFICES',
      name: 'Government Offices',
      description: 'Citizen service centers, licensing, and documentation desks',
      icon: <Building size={24} />
    }
  ];

  const allowedKeys = ['HOSPITALS', 'RESTAURANTS', 'BANKS', 'GOVERNMENT_OFFICES'];
  const displayedCategories = homeData?.supportedCategories
    ? homeData.supportedCategories
        .filter(cat => allowedKeys.includes(cat.key))
        .map(cat => ({
          key: cat.key,
          name: cat.name,
          description: cat.description,
          icon: getCategoryIcon(cat.key)
        }))
    : defaultCategories;

  return (
    <div>
      {/* Hero Section */}
      <section className="hero">
        <div className="container">
          <h1 className="hero-title">
            {homeData?.heading || 'Welcome to QueueMatrix'}
          </h1>

          <div className="hero-tagline">
            {homeData?.tagline || 'Smarter Queues. Better Experiences.'}
          </div>
        </div>
      </section>

      {/* Supported Categories Section (Directly below hero) */}
      <section className="section" style={{ background: '#ffffff', borderTop: '1px solid var(--border)' }}>
        <div className="container">
          <div className="category-grid">
            {displayedCategories.map(cat => (
              <div key={cat.key} className="category-card">
                <div className="category-icon-wrapper">
                  {cat.icon}
                </div>
                <h3 className="category-name">{cat.name}</h3>
                <p className="category-desc">{cat.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
};
