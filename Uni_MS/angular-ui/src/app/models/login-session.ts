export interface LoginSession {
  id?: number;
  uniqueCode?: string;
  userId?: number;
  sessionToken?: string;
  ipAddress?: string;
  browser?: string;
  operatingSystem?: string;
  deviceType?: string;
  loginTime?: string;
  lastActivityTime?: string;
  logoutTime?: string;
  isActive?: boolean;
  expired?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
