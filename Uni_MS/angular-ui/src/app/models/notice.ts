export interface Notice {
  id?: number;
  uniqueCode?: string;
  title: string;
  content: string;
  priority?: string;
  postedBy?: number;
  expiresAt?: string;
  targetAudience?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
