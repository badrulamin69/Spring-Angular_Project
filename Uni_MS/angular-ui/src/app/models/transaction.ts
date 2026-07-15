export interface Transaction {
  id?: number;
  uniqueCode?: string;
  accountId: number;
  transactionType: string;
  amount: number;
  description?: string;
  referenceType?: string;
  referenceId?: number;
  transactionDate: string;
  createdAt?: string;
  updatedAt?: string;
}
