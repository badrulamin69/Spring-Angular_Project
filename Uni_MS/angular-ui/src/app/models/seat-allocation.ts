export interface SeatAllocation {
  id?: number;
  testId?: number;
  test?: any;
  registrationId?: number;
  registration?: any;
  centerId?: number;
  center?: any;
  centerName?: string;
  buildingName?: string;
  roomName?: string;
  seatNumber: string;
  rollNumber: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SeatAllocationConfig {
  id?: number;
  sessionId?: number;
  session?: any;
  academicYear?: string;
  allocationRound?: number;
  autoAllocation?: boolean;
  manualAllocation?: boolean;
  allocationStartDate?: string;
  allocationEndDate?: string;
  acceptDeadlineHours?: number;
  lockAfterPublish?: boolean;
  enableQuota?: boolean;
  enableReservedSeats?: boolean;
  status?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProgramSeatConfig {
  id?: number;
  configId?: number;
  config?: any;
  facultyId?: number;
  faculty?: any;
  departmentId?: number;
  department?: any;
  programId?: number;
  program?: any;
  shift?: string;
  totalSeats?: number;
  generalSeats?: number;
  quotaSeats?: number;
  reservedSeats?: number;
  allocatedSeats?: number;
  waitingSeats?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProgramSeatAllocation {
  id?: number;
  allocationNumber?: string;
  configId?: number;
  config?: any;
  allocationRound?: number;
  choiceNumber?: number;
  allocatedFacultyId?: number;
  allocatedFaculty?: any;
  shift?: string;
  meritRank?: number;
  totalScore?: number;
  status?: string;
  allocatedAt?: string;
  acceptedAt?: string;
  declinedAt?: string;
  deadline?: string;
  confirmedAt?: string;
  isWaiting?: boolean;
  waitingRank?: number;
  remarks?: string;
  registrationId?: number;
  registration?: any;
  allocatedProgramId?: number;
  allocatedProgram?: any;
  allocatedDepartmentId?: number;
  allocatedDepartment?: any;
  createdAt?: string;
  updatedAt?: string;
}

export interface AllocationStats {
  total: number;
  allocated: number;
  confirmed: number;
  declined: number;
  cancelled: number;
  expired: number;
  waiting: number;
  notAllocated: number;
  totalSeats: number;
  allocatedSeats: number;
  remainingSeats: number;
  utilizationPercent: number;
}

export interface AllocationResult {
  totalProcessed: number;
  allocated: number;
  waiting: number;
  notAllocated: number;
  round: number;
}
