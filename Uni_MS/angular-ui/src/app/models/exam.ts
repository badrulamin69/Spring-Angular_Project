export interface Exam {
  id?: number;
  uniqueCode?: string;
  name: string;
  examType: string;
  courseId: number;
  subjectId: number;
  totalMarks: number;
  passingMarks: number;
  examDate?: string;
  durationMinutes?: number;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}
