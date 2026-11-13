import {
  ApiResponse,
  AuthData,
  PublicHomeData,
  Role,
  OrganizationType,
  UserSummary
} from '../types';

const API_BASE = '/api';

async function request<T>(endpoint: string, options?: RequestInit): Promise<T> {
  const token = localStorage.getItem('qm_token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options?.headers as Record<string, string>)
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers
  });

  const body: ApiResponse<T> = await response.json();

  if (!response.ok || !body.success) {
    throw new Error(body.message || `Request failed with status ${response.status}`);
  }

  return body.data;
}

export const api = {
  getPublicHome: () => request<PublicHomeData>('/public/home'),

  register: (payload: {
    fullName: string;
    email: string;
    phone: string;
    password: string;
    organizationType?: OrganizationType;
    role: Role;
  }) =>
    request<UserSummary>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload)
    }),

  login: (payload: { email: string; password: string }) =>
    request<AuthData>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
};
