export interface ApplicationReview {
  id?: number;
  uniqueCode?: string;
  applicationId: number;
  reviewerId?: number;
  status: string;
  comments?: string;
  rejectionReason?: string;
  score?: number;
  reviewedAt?: string;
  isRecommended?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
