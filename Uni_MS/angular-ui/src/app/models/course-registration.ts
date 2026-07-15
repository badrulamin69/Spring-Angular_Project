export interface CourseRegistration {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  courseId: number;
  semesterId: number;
  batchId?: number;
  status: string;
  registrationDate?: string;
  isSelected?: boolean;
  creditHours?: number;
  remarks?: string;
  approvedById?: number;
}
