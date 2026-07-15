export interface ExamSchedule {
  id?: number;
  uniqueCode?: string;
  examId: number;
  startTime: string;
  endTime: string;
  venue?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}
