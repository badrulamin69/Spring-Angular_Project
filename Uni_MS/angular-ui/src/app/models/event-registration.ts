export interface EventRegistration {
  id?: number;
  uniqueCode?: string;
  eventId: number;
  studentId: number;
  registrationDate: string;
  status?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
