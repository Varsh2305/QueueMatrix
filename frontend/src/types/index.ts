export type Role = 'SUPER_ADMIN' | 'ORGANIZATION_ADMIN' | 'STAFF' | 'CUSTOMER';

export type UserStatus = 'ACTIVE' | 'PENDING_APPROVAL' | 'PENDING_INVITE_APPROVAL' | 'INACTIVE' | 'SUSPENDED';

export type OrganizationType = 'HOSPITAL' | 'RESTAURANT' | 'BANK' | 'GOVERNMENT_OFFICE' | 'UNIVERSITY' | 'OTHER';

export interface UserSummary {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  role: Role;
  status: UserStatus;
  preferredOrgType?: OrganizationType;
  organizationName?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface AuthData {
  token: string;
  tokenType: string;
  user: UserSummary;
}

export interface CategoryInfo {
  key: string;
  name: string;
  description: string;
  icon: string;
}

export interface PublicHomeData {
  appName: string;
  heading: string;
  tagline: string;
  description: string;
  supportedCategories: CategoryInfo[];
  features: string[];
}
