export interface CreditRule {
  id?: number;
  uniqueCode?: string;
  programId: number;
  minCreditsPerSemester: number;
  maxCreditsPerSemester: number;
  totalRequiredCredits: number;
  maxTransferCredits?: number;
  maxElectiveCredits?: number;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}
