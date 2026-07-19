export interface AdmissionWaitingListEntry {
  id?: number;
  waitingListId?: number;
  waitingList?: any;
  registrationId?: number;
  registration?: any;
  rank: number;
  rollNumber?: string;
  applicationNumber?: string;
  applicantName?: string;
  score?: number;
  testMarks?: number;
  totalWeightedScore?: number;
  status?: string;
  isPromoted?: boolean;
  isOffered?: boolean;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
