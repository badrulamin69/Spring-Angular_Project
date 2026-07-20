export interface AdmissionConfirmation {
  id?: number;
  confirmationNumber?: string;
  allocationId?: number;
  allocation?: any;
  registrationId?: number;
  registration?: any;
  status?: string;
  documentsSubmitted?: boolean;
  documentsVerified?: boolean;
  documentsVerifiedBy?: number;
  documentsVerifiedAt?: string;
  documentRemarks?: string;
  feePaid?: boolean;
  feeAmount?: number;
  feePaymentMethod?: string;
  feeTransactionId?: string;
  feePaidAt?: string;
  confirmedAt?: string;
  confirmedBy?: number;
  remarks?: string;
  sessionId?: number;
  session?: any;
  createdAt?: string;
  updatedAt?: string;
}

export interface AdmissionDocument {
  id?: number;
  confirmationId?: number;
  confirmation?: any;
  documentType?: string;
  documentName?: string;
  fileUrl?: string;
  fileSize?: number;
  status?: string;
  verifiedBy?: number;
  verifiedAt?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ConfirmationStats {
  total: number;
  pending: number;
  documentsSubmitted: number;
  documentsVerified: number;
  documentsRejected: number;
  feePaid: number;
  confirmed: number;
  enrolled: number;
}
