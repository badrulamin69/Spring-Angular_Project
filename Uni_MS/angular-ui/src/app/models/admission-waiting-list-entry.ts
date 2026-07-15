export interface AdmissionWaitingListEntry {
  id?: number;
  uniqueCode?: string;
  waitingListId: number;
  applicationId: number;
  rank: number;
  score?: number;
  status?: string;
  isPromoted?: boolean;
  isOffered?: boolean;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
