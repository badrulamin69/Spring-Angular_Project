export interface Result {
  id?: number;
  uniqueCode?: string;
  examId: number;
  studentId: number;
  totalMarksObtained: number;
  totalMarks: number;
  percentage?: number;
  grade?: string;
  resultStatus?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
