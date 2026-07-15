export interface AdmissionInterview {
  id?: number;
  uniqueCode?: string;
  applicationId: number;
  interviewerId?: number;
  scheduledAt: string;
  completedAt?: string;
  interviewType?: string;
  status: string;
  remarks?: string;
  score?: number;
  maxScore?: number;
  strengths?: string;
  weaknesses?: string;
  isRecommended?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
