import { Invoice } from './invoice';
import { Student } from './student';
import { FeeType } from './fee-type';

export interface Payment {
  id?: number;
  paymentNumber?: string;
  invoiceId?: number;
  invoice?: Invoice;
  studentId?: number;
  student?: Student;
  amount?: number;
  paymentMethod?: string;
  paymentStatus?: string;
  transactionId?: string;
  gatewayResponse?: string;
  paymentDate?: string;
  createdBy?: string;
  notes?: string;
  receiptUrl?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Refund {
  id?: number;
  refundNumber?: string;
  paymentId?: number;
  payment?: Payment;
  studentId?: number;
  student?: Student;
  amount?: number;
  reason?: string;
  status?: string;
  approvedBy?: string;
  approvedAt?: string;
  rejectionReason?: string;
  createdAt?: string;
}

export interface Discount {
  id?: number;
  studentId?: number;
  student?: Student;
  feeTypeId?: number;
  feeType?: FeeType;
  discountType: string;
  discountValue: number;
  description?: string;
  validFrom?: string;
  validTo?: string;
  isActive?: boolean;
  createdAt?: string;
}

export interface Fine {
  id?: number;
  studentId?: number;
  student?: Student;
  invoiceId?: number;
  invoice?: Invoice;
  feeTypeId?: number;
  feeType?: FeeType;
  amount?: number;
  reason?: string;
  issuedBy?: string;
  status?: string;
  issuedDate?: string;
  createdAt?: string;
}
