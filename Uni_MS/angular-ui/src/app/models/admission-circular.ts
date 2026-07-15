export interface AdmissionCircular {
  id?: number;
  uniqueCode?: string;
  title: string;
  description?: string;
  eligibility?: string;
  requiredDocuments?: string;
  admissionProcess?: string;
  publishDate: string;
  validUntil?: string;
  status: string;
  attachmentUrl?: string;
  isPublished?: boolean;
  sessionId?: number;
  programId?: number;
  createdAt?: string;
  updatedAt?: string;
}
