export interface Program {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  description?: string;
  programType: string;
  durationYears: number;
  totalCredits?: number;
  departmentId: number;
  administrationDivisionId?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
