export interface SemesterRegistration {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  semesterId: number;
  batchId?: number;
  registrationDate?: string;
  status: string;
  remarks?: string;
  approvedById?: number;
}
