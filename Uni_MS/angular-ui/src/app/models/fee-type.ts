export interface FeeType {
  id?: number;
  name: string;
  code: string;
  category: string;
  description?: string;
  defaultAmount?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
