export interface AdmissionMeritList {
  id?: number;
  uniqueCode?: string;
  name: string;
  description?: string;
  sessionId: number;
  programId: number;
  campusId?: number;
  status: string;
  publishedAt?: string;
  totalSeats?: number;
  filledSeats?: number;
  waitingCount?: number;
  cutoffScore?: number;
  approvedById?: number;
  createdAt?: string;
  updatedAt?: string;
}
