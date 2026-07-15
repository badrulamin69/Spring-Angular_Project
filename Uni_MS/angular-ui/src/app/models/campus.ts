export interface Campus {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  address?: string;
  phone?: string;
  email?: string;
  campusType: string;
  latitude?: number;
  longitude?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
