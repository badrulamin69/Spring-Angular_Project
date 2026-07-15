export interface GradeRule {
  id?: number;
  uniqueCode?: string;
  courseId: number;
  grade: string;
  minPercentage: number;
  maxPercentage: number;
  gradePoint: number;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}
