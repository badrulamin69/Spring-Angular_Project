export interface Payment {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  invoiceId?: number;
  amount: number;
  paymentDate: string;
  paymentMethod?: string;
  transactionReference?: string;
  status?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}
