export interface Invoice {
  id?: number;
  invoiceNumber?: string;
  studentId?: number;
  student?: any;
  academicYear?: string;
  semesterId?: number;
  semester?: any;
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
  feeType?: any;
  description?: string;
  amount?: number;
  discountAmount?: number;
  netAmount?: number;
}
