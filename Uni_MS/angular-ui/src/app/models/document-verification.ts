export interface DocumentVerification {
  id?: number;
  uniqueCode?: string;
  admissionCandidateId: number;
  documentType: string;
  documentNumber?: string;
  isVerified?: boolean;
  verifiedBy?: number;
  verificationDate?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
