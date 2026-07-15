export interface AdmissionCandidate {
  id?: number;
  uniqueCode?: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  dateOfBirth?: string;
  gender?: string;
  address?: string;
  applicationNumber: string;
  status?: string;
  appliedCourseId?: number;
  createdAt?: string;
  updatedAt?: string;
}
