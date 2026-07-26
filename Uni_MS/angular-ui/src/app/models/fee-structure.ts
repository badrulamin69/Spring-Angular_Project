import { FeeType } from './fee-type';
import { Program } from './program';
import { Semester } from './semester';
import { Batch } from './batch';

export interface FeeStructure {
  id?: number;
  feeTypeId?: number;
  feeType?: FeeType;
  programId?: number;
  program?: Program;
  semesterId?: number;
  semester?: Semester;
  batchId?: number;
  batch?: Batch;
  amount: number;
  dueDays?: number;
  academicYear?: string;
  description?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
