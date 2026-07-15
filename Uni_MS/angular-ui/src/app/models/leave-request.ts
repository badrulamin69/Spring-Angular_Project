export interface LeaveRequest {
  id?: number;
  uniqueCode?: string;
  employeeId: number;
  leaveType: string;
  startDate: string;
  endDate: string;
  reason: string;
  status?: string;
  approvedBy?: number;
  approvedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}
