export interface EmployeeAttendance {
  id?: number;
  uniqueCode?: string;
  employeeId: number;
  attendanceDate: string;
  status: string;
  checkIn?: string;
  checkOut?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
