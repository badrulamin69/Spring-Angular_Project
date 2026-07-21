export interface RegistrationConfig {
  id?: number;
  semesterId: number;
  semesterName?: string;
  startDate: string;
  endDate: string;
  minCredits: number;
  maxCredits: number;
  allowAddDrop: boolean;
  addDropDeadline?: string;
  advisorApprovalRequired: boolean;
  paymentRequired: boolean;
  isActive: boolean;
  isClosed?: boolean;
  status?: string;
  remarks?: string;
  createdAt?: string;
}

export interface CourseRegistrationRequest {
  studentId: number;
  subjectId: number;
  semesterId: number;
  batchId?: number;
  registrationType?: string;
  remarks?: string;
}

export interface AdvisorApprovalRequest {
  registrationIds: number[];
  action: string;
  comments?: string;
}

export interface PaymentValidationRequest {
  registrationId: number;
  paymentReference: string;
  paymentAmount: number;
}

export interface RegistrationSummary {
  studentId: number;
  studentName: string;
  studentCode: string;
  semesterId: number;
  semesterName: string;
  batchId?: number;
  batchName?: string;
  totalCreditsRegistered: number;
  minCreditsRequired: number;
  maxCreditsAllowed: number;
  registrationStatus: string;
  advisorApprovalStatus: string;
  paymentStatus: string;
  isFinalized: boolean;
  registeredCourses: RegisteredCourseItem[];
  errors: string[];
  lastUpdated: string;
}

export interface RegisteredCourseItem {
  registrationId: number;
  subjectId: number;
  subjectName: string;
  subjectCode: string;
  creditHours: number;
  status: string;
  advisorStatus: string;
  paymentStatus: string;
}

export interface EligibilityCheck {
  studentId: number;
  studentName: string;
  semesterId: number;
  semesterName: string;
  eligible: boolean;
  totalCreditsRegistered: number;
  minCreditsRequired: number;
  maxCreditsAllowed: number;
  status: string;
  errors: string[];
  warnings: string[];
  checkedAt: string;
}

export interface RegistrationDashboard {
  totalRegistrations: number;
  pendingApprovals: number;
  approvedRegistrations: number;
  registeredStudents: number;
  droppedRegistrations: number;
  statusBreakdown: RegistrationStatsByStatus[];
  recentRegistrations: RecentRegistration[];
}

export interface RegistrationStatsByStatus {
  status: string;
  count: number;
}

export interface RecentRegistration {
  id: number;
  studentName: string;
  studentCode: string;
  courseName: string;
  semesterName: string;
  status: string;
  creditHours: number;
  registrationDate: string;
}

export interface RegistrationHistory {
  id: number;
  studentId: number;
  courseId: number;
  semesterId: number;
  courseRegistrationId: number;
  action: string;
  details: string;
  performedById: number;
  ipAddress: string;
  createdAt: string;
}
