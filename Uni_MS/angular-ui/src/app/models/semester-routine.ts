export interface SemesterRoutine {
  id?: number;
  uniqueCode?: string;
  semesterId: number;
  programId: number;
  batchId: number;
  description?: string;
  totalWeeks?: number;
  midtermWeek?: number;
  finalExamWeek?: number;
  startDate?: string;
  endDate?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
