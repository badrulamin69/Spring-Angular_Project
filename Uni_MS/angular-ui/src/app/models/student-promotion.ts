export interface StudentPromotion {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  fromSemesterId?: number;
  toSemesterId?: number;
  fromBatchId?: number;
  toBatchId?: number;
  promotionDate?: string;
  status: string;
  remarks?: string;
  approvedById?: number;
}
