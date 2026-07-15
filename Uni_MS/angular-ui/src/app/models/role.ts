import { Permission } from './permission';

export interface Role {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  description?: string;
  active?: boolean;
  permissions?: Permission[];
  createdAt?: string;
  updatedAt?: string;
}
