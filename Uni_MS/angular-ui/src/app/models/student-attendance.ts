export interface StudentAttendance {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  courseId?: number;
  semesterId?: number;
  attendanceDate: string;
  status: string;
  remarks?: string;
  checkInTime?: string;
  checkOutTime?: string;
  recordedById?: number;
}
