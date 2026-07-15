export interface Invoice {
  id?: number;
  uniqueCode?: string;
  invoiceNumber: string;
  studentId: number;
  issueDate: string;
  dueDate: string;
  totalAmount: number;
  paidAmount?: number;
  status?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}
