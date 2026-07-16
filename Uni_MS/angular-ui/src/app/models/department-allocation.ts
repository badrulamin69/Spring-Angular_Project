export interface DepartmentAllocation {
  id?: number;
  allocationNumber?: string;
  meritRank?: number;
  totalScore?: number;
  status?: string;
  allocatedAt?: string;
  confirmedAt?: string;
  remarks?: string;
  registrationId: number;
  allocatedProgramId?: number;
  allocatedDepartmentId?: number;
  allocatedBatchId?: number;
  allocatedSectionId?: number;
  semesterId?: number;
  allocatedById?: number;
  createdAt?: string;
  updatedAt?: string;
}
