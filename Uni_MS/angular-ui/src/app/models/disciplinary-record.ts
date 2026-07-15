export interface DisciplinaryRecord {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  incidentDate: string;
  category: string;
  severity: string;
  description: string;
  actionTaken?: string;
  reportedById?: number;
  status: string;
  remarks?: string;
}
