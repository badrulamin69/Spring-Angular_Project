export interface Transcript {
  id?: number;
  uniqueCode?: string;
  transcriptNumber?: string;
  studentId: number;
  programId?: number;
  semesterId?: number;
  issuedAt?: string;
  status: string;
  gpa?: number;
  totalCredits?: number;
  remarks?: string;
  issuedById?: number;
}
