export interface Course {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  description?: string;
  durationYears: number;
  departmentId: number;
  createdAt?: string;
  updatedAt?: string;
}
