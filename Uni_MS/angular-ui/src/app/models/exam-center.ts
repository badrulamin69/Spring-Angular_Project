export interface ExamCenter {
  id?: number;
  name: string;
  code: string;
  address?: string;
  city?: string;
  totalCapacity?: number;
  contactPerson?: string;
  contactPhone?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
