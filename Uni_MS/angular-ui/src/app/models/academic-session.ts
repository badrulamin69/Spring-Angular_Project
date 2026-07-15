export interface AcademicSession {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  startDate: string;
  endDate: string;
  isActive?: boolean;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}
