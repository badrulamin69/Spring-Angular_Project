export interface Account {
  id?: number;
  uniqueCode?: string;
  accountName: string;
  accountNumber: string;
  accountType: string;
  balance?: number;
  description?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
