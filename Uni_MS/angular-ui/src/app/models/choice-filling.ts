export interface ChoiceFillingConfig {
  id?: number;
  sessionId?: number;
  session?: any;
  choiceStartDate?: string;
  choiceEndDate?: string;
  maxChoices?: number;
  minChoices?: number;
  allowEditingBeforeDeadline?: boolean;
  autoLockAfterDeadline?: boolean;
  includeWaitingList?: boolean;
  status?: string;
  remarks?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface ApplicantChoiceSubmission {
  id?: number;
  registrationId?: number;
  registration?: any;
  configId?: number;
  config?: any;
  meritListEntryId?: number;
  meritListEntry?: any;
  submissionId?: string;
  totalChoices?: number;
  status?: string;
  submittedAt?: string;
  lockedAt?: string;
  applicantName?: string;
  meritRank?: number;
  meritScore?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface ApplicantChoice {
  id?: number;
  submissionId?: number;
  submission?: any;
  priority?: number;
  facultyId?: number;
  faculty?: any;
  departmentId?: number;
  department?: any;
  programId?: number;
  program?: any;
  facultyName?: string;
  departmentName?: string;
  programName?: string;
  shift?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface AvailableProgram {
  programId?: number;
  programName?: string;
  programCode?: string;
  programType?: string;
  durationYears?: number;
  totalCredits?: number;
  departmentId?: number;
  departmentName?: string;
  facultyId?: number;
  facultyName?: string;
  currentDemand?: number;
}
