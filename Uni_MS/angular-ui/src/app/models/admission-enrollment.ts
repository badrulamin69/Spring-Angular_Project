export interface AdmissionEnrollment {
  id?: number;
  uniqueCode?: string;
  enrollmentNumber?: string;
  applicationId: number;
  studentId?: number;
  offerLetterId?: number;
  programId: number;
  semesterId?: number;
  batchId?: number;
  sectionId?: number;
  status: string;
  enrolledAt?: string;
  remarks?: string;
  isDocumentVerified?: boolean;
  isFeePaid?: boolean;
  totalFeePaid?: number;
  enrolledById?: number;
  createdAt?: string;
  updatedAt?: string;
}
