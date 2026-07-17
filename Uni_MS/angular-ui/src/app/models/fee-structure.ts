export interface FeeStructure {
  id?: number;
  feeTypeId?: number;
  feeType?: any;
  programId?: number;
  program?: any;
  semesterId?: number;
  semester?: any;
  batchId?: number;
  batch?: any;
  amount: number;
  dueDays?: number;
  academicYear?: string;
  description?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
