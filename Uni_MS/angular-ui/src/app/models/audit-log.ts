import { User } from './user';

export interface AuditLog {
  id?: number;
  uniqueCode?: string;
  user?: User;
  action: string;
  entityType?: string;
  entityId?: string;
  oldValue?: string;
  newValue?: string;
  ipAddress?: string;
  createdAt?: string;
  updatedAt?: string;
}
