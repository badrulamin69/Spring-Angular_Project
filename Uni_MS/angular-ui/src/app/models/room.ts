export interface Room {
  id?: number;
  uniqueCode?: string;
  roomNumber: string;
  floor?: number;
  capacity: number;
  currentOccupancy?: number;
  roomType: string;
  monthlyRent?: number;
  isAvailable?: boolean;
  hostelId: number;
  createdAt?: string;
  updatedAt?: string;
}
