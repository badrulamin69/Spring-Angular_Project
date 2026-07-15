export interface AdmissionApplication {
  id?: number;
  uniqueCode?: string;
  applicationNumber?: string;
  candidateId: number;
  sessionId: number;
  programId: number;
  departmentId?: number;
  campusId?: number;
  status: string;
  remarks?: string;
  submittedAt?: string;
  isSubmitted?: boolean;
  isVerified?: boolean;
  examId?: number;
  testScore?: number;
  meritScore?: number;
  meritPosition?: number;
  createdAt?: string;
  updatedAt?: string;
}
