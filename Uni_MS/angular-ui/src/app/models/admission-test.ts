export interface AdmissionTest {
  id?: number;
  uniqueCode?: string;
  name: string;
  testDate: string;
  totalMarks: number;
  passingMarks: number;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}
