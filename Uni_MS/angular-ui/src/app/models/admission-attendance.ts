export interface AdmissionAttendance {
  id?: number;
  testId?: number;
  test?: any;
  registrationId?: number;
  registration?: any;
  attemptId?: number;
  attempt?: any;
  status: string;
  checkInTime?: string;
  checkOutTime?: string;
  markedById?: number;
  markedBy?: any;
  remarks?: string;
  qrScanned?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
