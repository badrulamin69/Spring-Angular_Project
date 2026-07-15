export interface AdmissionOfferLetter {
  id?: number;
  uniqueCode?: string;
  letterNumber?: string;
  applicationId: number;
  meritListEntryId?: number;
  issuedAt?: string;
  validUntil?: string;
  status: string;
  letterContent?: string;
  conditions?: string;
  remarks?: string;
  issuedById?: number;
  acceptedAt?: string;
  declinedAt?: string;
  declineReason?: string;
  isDownloaded?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
