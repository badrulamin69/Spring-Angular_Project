export interface AdmitCard {
  id?: number;
  registrationId?: number;
  registration?: any;
  testId?: number;
  test?: any;
  admitCardNumber: string;
  rollNumber: string;
  seatNumber?: string;
  centerName?: string;
  buildingName?: string;
  roomName?: string;
  qrCode?: string;
  issuedAt?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}
