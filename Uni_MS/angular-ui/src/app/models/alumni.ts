export interface Alumni {
  id?: number;
  uniqueCode?: string;
  studentId: number;
  graduationDate?: string;
  degree?: string;
  programId?: number;
  departmentId?: number;
  currentCompany?: string;
  currentDesignation?: string;
  currentLocation?: string;
  email?: string;
  phone?: string;
  linkedInProfile?: string;
  isAvailableForMentoring?: boolean;
  isAvailableForRecruitment?: boolean;
  remarks?: string;
}
