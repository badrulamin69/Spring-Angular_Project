export interface AdmissionWaitingList {
  id?: number;
  name: string;
  description?: string;
  academicYear?: string;
  sessionId?: number;
  session?: any;
  facultyId?: number;
  faculty?: any;
  departmentId?: number;
  department?: any;
  programId?: number;
  program?: any;
  shift?: string;
  testId?: number;
  test?: any;
  status: string;
  totalSlots?: number;
  totalApplicants?: number;
  cutoffScore?: number;
  publishedAt?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
