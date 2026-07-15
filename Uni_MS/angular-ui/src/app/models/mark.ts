export interface Mark {
  id?: number;
  uniqueCode?: string;
  examId: number;
  studentId: number;
  subjectId: number;
  marksObtained: number;
  totalMarks: number;
  grade?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
