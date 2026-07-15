export interface Semester {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  academicSessionId: number;
  orderNo: number;
  startDate: string;
  endDate: string;
  registrationDeadline?: string;
  status: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
