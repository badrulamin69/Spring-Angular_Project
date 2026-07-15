export interface AdmissionMeritListEntry {
  id?: number;
  uniqueCode?: string;
  meritListId: number;
  applicationId: number;
  rank: number;
  score?: number;
  testScore?: number;
  academicScore?: number;
  interviewScore?: number;
  totalWeightedScore?: number;
  status?: string;
  isOffered?: boolean;
  isEnrolled?: boolean;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
