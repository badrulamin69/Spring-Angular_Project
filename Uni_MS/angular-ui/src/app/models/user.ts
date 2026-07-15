import { Role } from './role';

export interface User {
  id?: number;
  uniqueCode?: string;
  username: string;
  email: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  avatar?: string;
  role?: Role;
  active?: boolean;
  lastLoginAt?: string;
  lastLoginIp?: string;
  createdAt?: string;
  updatedAt?: string;
}
