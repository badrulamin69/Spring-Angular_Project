export interface AdmissionWaitingList {
  id?: number;
  uniqueCode?: string;
  name: string;
  description?: string;
  sessionId: number;
  programId: number;
  campusId?: number;
  status: string;
  totalSlots?: number;
  filledSlots?: number;
  approvedById?: number;
  createdAt?: string;
  updatedAt?: string;
}
