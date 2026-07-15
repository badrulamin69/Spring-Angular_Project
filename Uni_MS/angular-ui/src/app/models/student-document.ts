export interface StudentDocument {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  documentType: string;
  documentName: string;
  fileUrl?: string;
  fileSize?: number;
  uploadedAt?: string;
  status: string;
  verifiedById?: number;
  verifiedAt?: string;
  remarks?: string;
}
