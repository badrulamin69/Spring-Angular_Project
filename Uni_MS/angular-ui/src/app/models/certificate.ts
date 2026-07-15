export interface Certificate {
  id?: number;
  uniqueCode?: string;
  certificateNumber?: string;
  studentId: number;
  certificateType: string;
  issuedAt?: string;
  validUntil?: string;
  status: string;
  purpose?: string;
  issuedById?: number;
  isDownloaded?: boolean;
}
