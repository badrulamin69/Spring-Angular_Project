export interface ActivityLog {
  id?: number;
  uniqueCode?: string;
  userId?: number;
  username?: string;
  action: string;
  module?: string;
  description?: string;
  entityType?: string;
  entityId?: string;
  ipAddress?: string;
  userAgent?: string;
  metadata?: string;
  createdAt?: string;
  updatedAt?: string;
}
