export interface Vehicle {
  id?: number;
  uniqueCode?: string;
  vehicleNumber: string;
  vehicleType: string;
  capacity: number;
  driverName?: string;
  driverPhone?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
