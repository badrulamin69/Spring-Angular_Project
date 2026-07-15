export interface Batch {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  startYear: number;
  endYear: number;
  courseId: number;
  createdAt?: string;
  updatedAt?: string;
}
