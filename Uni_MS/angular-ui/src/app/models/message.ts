export interface Message {
  id?: number;
  uniqueCode?: string;
  senderId: number;
  receiverId: number;
  subject?: string;
  body: string;
  isRead?: boolean;
  readAt?: string;
  createdAt?: string;
  updatedAt?: string;
}
