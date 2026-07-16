export interface AdmissionTestResult {
  id?: number;
  writtenMarks?: number;
  mcqMarks?: number;
  vivaMarks?: number;
  writtenMax?: number;
  mcqMax?: number;
  vivaMax?: number;
  totalWeightedScore?: number;
  status?: string;
  remarks?: string;
  registrationId: number;
  testId?: number;
  createdAt?: string;
  updatedAt?: string;
}
