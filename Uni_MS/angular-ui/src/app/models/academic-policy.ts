export interface AcademicPolicy {
  id?: number;
  uniqueCode?: string;
  name: string;
  description: string;
  policyType: string;
  policyValue?: string;
  programId?: number;
  isActive?: boolean;
  effectiveFrom?: string;
  effectiveTo?: string;
  createdAt?: string;
  updatedAt?: string;
}
