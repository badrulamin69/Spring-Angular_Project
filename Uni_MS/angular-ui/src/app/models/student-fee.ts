export interface StudentFee {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  feeTypeId: number;
  amount: number;
  dueDate: string;
  paidAmount?: number;
  status?: string;
  academicYear?: string;
  createdAt?: string;
  updatedAt?: string;
}
