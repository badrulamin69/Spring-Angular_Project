export interface MedicalInfo {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  bloodGroup?: string;
  height?: number;
  weight?: number;
  allergies?: string;
  medications?: string;
  conditions?: string;
  emergencyContact?: string;
  emergencyPhone?: string;
  insuranceProvider?: string;
  insuranceNumber?: string;
  doctorName?: string;
  doctorPhone?: string;
  notes?: string;
}
