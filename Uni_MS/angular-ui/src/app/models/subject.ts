export interface Subject {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  credits: number;
  courseId: number;
  departmentId: number;
  createdAt?: string;
  updatedAt?: string;
}
