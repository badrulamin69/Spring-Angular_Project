export interface FeeType {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  description?: string;
  amount: number;
  isMandatory?: boolean;
  frequency?: string;
  createdAt?: string;
  updatedAt?: string;
}
