export interface BookIssue {
  id?: number;
  uniqueCode?: string;
  bookId: number;
  studentId: number;
  issueDate: string;
  dueDate: string;
  returnDate?: string;
  status?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
