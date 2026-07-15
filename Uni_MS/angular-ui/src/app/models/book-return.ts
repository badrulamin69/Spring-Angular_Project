export interface BookReturn {
  id?: number;
  uniqueCode?: string;
  bookIssueId: number;
  returnDate: string;
  fineAmount?: number;
  finePaid?: boolean;
  conditionAtReturn?: string;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}
