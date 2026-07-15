export interface AssignmentSubmission {
  id?: number;
  uniqueCode?: string;
  assignmentId: number;
  studentId: number;
  submissionDate: string;
  fileUrl?: string;
  notes?: string;
  marksObtained?: number;
  feedback?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}
