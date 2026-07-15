export interface Notification {
  id?: number;
  uniqueCode?: string;
  userId: number;
  title: string;
  message: string;
  type?: string;
  isRead?: boolean;
  referenceType?: string;
  referenceId?: number;
  createdAt?: string;
  updatedAt?: string;
}
