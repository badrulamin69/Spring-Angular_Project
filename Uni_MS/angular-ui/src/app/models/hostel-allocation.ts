export interface HostelAllocation {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  roomId: number;
  allocationDate: string;
  endDate?: string;
  status?: string;
  monthlyRent?: number;
  createdAt?: string;
  updatedAt?: string;
}
