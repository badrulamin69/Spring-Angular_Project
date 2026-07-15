export interface AdmissionResult {
  id?: number;
  uniqueCode?: string;
  admissionCandidateId: number;
  admissionTestId: number;
  marksObtained: number;
  rank?: number;
  status?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
