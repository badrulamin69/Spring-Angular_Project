export interface Guardian {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  firstName: string;
  lastName: string;
  relationship: string;
  email?: string;
  phone: string;
  occupation?: string;
  address?: string;
  isPrimary?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
