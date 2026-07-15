export interface StudentEnrollment {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  batchId: number;
  sectionId?: number;
  enrollmentDate: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}
