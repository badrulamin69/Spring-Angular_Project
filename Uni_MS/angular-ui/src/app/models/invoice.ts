import { Student } from './student';
import { Semester } from './semester';
import { FeeType } from './fee-type';

export interface Invoice {
  id?: number;
  invoiceNumber?: string;
  studentId?: number;
  student?: Student;
  academicYear?: string;
  semesterId?: number;
  semester?: Semester;
  totalAmount?: number;
  paidAmount?: number;
  dueAmount?: number;
  discountAmount?: number;
  fineAmount?: number;
  status?: string;
  dueDate?: string;
  notes?: string;
  items?: InvoiceItem[];
  createdAt?: string;
  updatedAt?: string;
}

export interface InvoiceItem {
  id?: number;
  invoiceId?: number;
  feeTypeId?: number;
  feeType?: FeeType;
  description?: string;
  amount?: number;
  discountAmount?: number;
  netAmount?: number;
}
