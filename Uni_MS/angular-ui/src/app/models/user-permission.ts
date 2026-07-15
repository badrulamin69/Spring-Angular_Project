export interface UserPermission {
  id?: number;
  uniqueCode?: string;
  userId: number;
  permissionId: number;
  granted: boolean;
  notes?: string;
  overriddenById?: number;
  expiresAt?: string;
  createdAt?: string;
  updatedAt?: string;
}
