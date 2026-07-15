export interface TransportAllocation {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  routeId: number;
  vehicleId: number;
  pickupPoint?: string;
  dropPoint?: string;
  monthlyFee?: number;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}
