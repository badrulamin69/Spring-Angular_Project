export interface Payment {
  id?: number;
  paymentNumber?: string;
  invoiceId?: number;
  invoice?: any;
  studentId?: number;
  student?: any;
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
  payment?: any;
  studentId?: number;
  student?: any;
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
  student?: any;
  feeTypeId?: number;
  feeType?: any;
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
  student?: any;
  invoiceId?: number;
  invoice?: any;
  feeTypeId?: number;
  feeType?: any;
  amount?: number;
  reason?: string;
  issuedBy?: string;
  status?: string;
  issuedDate?: string;
  createdAt?: string;
}
