export interface Curriculum {
  id?: number;
  uniqueCode?: string;
  programId: number;
  subjectId: number;
  semesterId: number;
  isRequired?: boolean;
  orderNo?: number;
  creditHours?: number;
  createdAt?: string;
  updatedAt?: string;
}
