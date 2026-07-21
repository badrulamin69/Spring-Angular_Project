export interface EnrollmentConfig {
  id?: number;
  semesterId: number;
  semesterName?: string;
  academicSessionId?: number;
  academicSessionName?: string;
  startDate: string;
  endDate: string;
  lateEnrollmentDate?: string;
  minCredits: number;
  maxCredits: number;
  enrollmentStatus: string;
  isActive: boolean;
  isClosed?: boolean;
  requiresAdvisorApproval: boolean;
  requiresPayment: boolean;
  allowLateEnrollment: boolean;
  remarks?: string;
  createdAt?: string;
}

export interface SemesterEnrollment {
  id?: number;
  enrollmentNumber?: string;
  studentId: number;
  studentName?: string;
  studentCode?: string;
  studentEmail?: string;
  semesterId: number;
  semesterName?: string;
  batchId?: number;
  batchName?: string;
  programId?: number;
  programName?: string;
  facultyId?: number;
  facultyName?: string;
  departmentId?: number;
  departmentName?: string;
  advisorId?: number;
  advisorName?: string;
  enrollmentDate?: string;
  status: string;
  registeredCredits?: number;
  minCredits?: number;
  maxCredits?: number;
  advisorStatus?: string;
  advisorComments?: string;
  advisorApprovedAt?: string;
  paymentStatus?: string;
  paymentAmount?: number;
  paymentReference?: string;
  paymentDate?: string;
  isFinalized?: boolean;
  finalizedAt?: string;
  remarks?: string;
  isActive?: boolean;
  isLateEnrollment?: boolean;
  enrollmentType?: string;
  cancelledAt?: string;
  cancellationReason?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface EnrollmentEligibility {
  studentId: number;
  studentName?: string;
  semesterId: number;
  semesterName?: string;
  eligible: boolean;
  errors: string[];
  warnings: string[];
  hasActiveEnrollment: boolean;
  hasAcademicHold: boolean;
  hasFinancialHold: boolean;
  registrationCompleted: boolean;
  feesPaid: boolean;
  currentOutstandingBalance?: number;
}

export interface EnrollmentApproval {
  id?: number;
  enrollmentId: number;
  enrollmentNumber?: string;
  studentId?: number;
  studentName?: string;
  studentCode?: string;
  semesterId?: number;
  semesterName?: string;
  advisorId?: number;
  advisorName?: string;
  action: string;
  comments?: string;
  createdAt?: string;
}

export interface EnrollmentDashboard {
  totalEnrollments: number;
  pendingApprovals: number;
  approvedEnrollments: number;
  completedEnrollments: number;
  rejectedEnrollments: number;
  cancelledEnrollments: number;
  draftEnrollments: number;
  statusBreakdown: EnrollmentStatsByStatus[];
  departmentBreakdown: EnrollmentStatsByDepartment[];
  recentEnrollments: RecentEnrollment[];
}

export interface EnrollmentStatsByStatus {
  status: string;
  count: number;
}

export interface EnrollmentStatsByDepartment {
  departmentId: number;
  departmentName: string;
  count: number;
}

export interface RecentEnrollment {
  id: number;
  enrollmentNumber: string;
  studentName: string;
  studentCode: string;
  semesterName: string;
  status: string;
  registeredCredits: number;
  advisorStatus: string;
  paymentStatus: string;
  createdAt: string;
}

export interface EnrollmentHistory {
  id: number;
  studentId: number;
  semesterId: number;
  semesterEnrollmentId: number;
  action: string;
  details: string;
  performedById: number;
  ipAddress: string;
  createdAt: string;
}
