import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { authGuard } from './core/guards/auth.guard';
import { permissionGuard } from './core/guards/permission.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./modules/landing/landing.component').then(m => m.LandingComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./modules/security/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () => import('./modules/security/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'reset-password',
    loadComponent: () => import('./modules/security/reset-password/reset-password.component').then(m => m.ResetPasswordComponent)
  },
  {
    path: 'access-denied',
    loadComponent: () => import('./modules/security/access-denied/access-denied.component').then(m => m.AccessDeniedComponent)
  },
  {
    path: '403',
    loadComponent: () => import('./modules/security/access-denied/access-denied.component').then(m => m.AccessDeniedComponent)
  },
  {
    path: 'pre-admission/register',
    loadComponent: () => import('./modules/admissions/pre-admission-register/pre-admission-register.component').then(m => m.PreAdmissionRegisterComponent)
  },
  {
    path: 'pre-admission/status',
    loadComponent: () => import('./modules/admissions/pre-admission-status/pre-admission-status.component').then(m => m.PreAdmissionStatusComponent)
  },
  {
    path: 'applicant/dashboard',
    loadComponent: () => import('./modules/applicant/applicant-dashboard/applicant-dashboard.component').then(m => m.ApplicantDashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'applicant/test',
    loadComponent: () => import('./modules/applicant/applicant-test/applicant-test.component').then(m => m.ApplicantTestComponent),
    canActivate: [authGuard]
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', loadComponent: () => import('./modules/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [permissionGuard], data: { permission: 'DASHBOARD_VIEW' } },

      // Security
      { path: 'security/users', loadComponent: () => import('./modules/security/users/users.component').then(m => m.UsersComponent), canActivate: [permissionGuard], data: { permission: 'USER_VIEW' } },
      { path: 'security/roles', loadComponent: () => import('./modules/security/roles/roles.component').then(m => m.RolesComponent), canActivate: [permissionGuard], data: { permission: 'ROLE_VIEW' } },
      { path: 'security/permissions', loadComponent: () => import('./modules/security/permissions/permissions.component').then(m => m.PermissionsComponent), canActivate: [permissionGuard], data: { permission: 'PERMISSION_VIEW' } },
      { path: 'security/role-permissions', loadComponent: () => import('./modules/security/role-permissions/role-permissions.component').then(m => m.RolePermissionsComponent), canActivate: [permissionGuard], data: { permission: 'PERMISSION_VIEW' } },
      { path: 'security/user-roles', loadComponent: () => import('./modules/security/user-roles/user-roles.component').then(m => m.UserRolesComponent), canActivate: [permissionGuard], data: { permission: 'ROLE_VIEW' } },
      { path: 'security/audit-logs', loadComponent: () => import('./modules/security/audit-logs/audit-logs.component').then(m => m.AuditLogsComponent), canActivate: [permissionGuard], data: { permission: 'AUDIT_VIEW' } },
      { path: 'security/menus', loadComponent: () => import('./modules/security/menus/menus.component').then(m => m.MenusComponent), canActivate: [permissionGuard], data: { permission: 'MENU_MANAGE' } },
      { path: 'security/role-permission-matrix', loadComponent: () => import('./modules/security/role-permission-matrix/role-permission-matrix.component').then(m => m.RolePermissionMatrixComponent), canActivate: [permissionGuard], data: { permission: 'PERMISSION_VIEW' } },
      { path: 'security/user-permission-override', loadComponent: () => import('./modules/security/user-permission-override/user-permission-override.component').then(m => m.UserPermissionOverrideComponent), canActivate: [permissionGuard], data: { permission: 'ROLE_VIEW' } },
      { path: 'security/menu-permission-mapping', loadComponent: () => import('./modules/security/menu-permission-mapping/menu-permission-mapping.component').then(m => m.MenuPermissionMappingComponent), canActivate: [permissionGuard], data: { permission: 'MENU_MANAGE' } },
      { path: 'security/login-sessions', loadComponent: () => import('./modules/security/login-sessions/login-sessions.component').then(m => m.LoginSessionsComponent), canActivate: [permissionGuard], data: { permission: 'ROLE_VIEW' } },
      { path: 'security/activity-logs', loadComponent: () => import('./modules/security/activity-logs/activity-logs.component').then(m => m.ActivityLogsComponent), canActivate: [permissionGuard], data: { permission: 'AUDIT_VIEW' } },
      { path: 'change-password', loadComponent: () => import('./modules/security/change-password/change-password.component').then(m => m.ChangePasswordComponent), canActivate: [permissionGuard], data: { permission: 'DASHBOARD_VIEW' } },
      { path: 'settings', loadComponent: () => import('./modules/security/settings/settings.component').then(m => m.SettingsComponent), canActivate: [permissionGuard], data: { permission: 'SETTINGS_VIEW' } },
      { path: 'security/settings', loadComponent: () => import('./modules/security/settings/settings.component').then(m => m.SettingsComponent), canActivate: [permissionGuard], data: { permission: 'SETTINGS_MANAGE' } },
      { path: 'security/dashboard', loadComponent: () => import('./modules/security/dashboard/dashboard.component').then(m => m.SecurityDashboardComponent), canActivate: [permissionGuard], data: { permission: 'DASHBOARD_VIEW' } },
      { path: 'security/route-management', loadComponent: () => import('./modules/security/route-management/route-management.component').then(m => m.RouteManagementComponent), canActivate: [permissionGuard], data: { permission: 'MENU_MANAGE' } },
      { path: 'security/password-policies', loadComponent: () => import('./modules/security/password-policies/password-policies.component').then(m => m.PasswordPoliciesComponent), canActivate: [permissionGuard], data: { permission: 'SETTINGS_MANAGE' } },
      { path: 'security/account-lock-rules', loadComponent: () => import('./modules/security/account-lock-rules/account-lock-rules.component').then(m => m.AccountLockRulesComponent), canActivate: [permissionGuard], data: { permission: 'SETTINGS_MANAGE' } },
      { path: 'security/two-factor', loadComponent: () => import('./modules/security/two-factor/two-factor.component').then(m => m.TwoFactorComponent), canActivate: [permissionGuard], data: { permission: 'SETTINGS_VIEW' } },
      { path: 'security/api-tokens', loadComponent: () => import('./modules/security/api-tokens/api-tokens.component').then(m => m.ApiTokensComponent), canActivate: [permissionGuard], data: { permission: 'SETTINGS_VIEW' } },
      { path: 'security/workflow-management', loadComponent: () => import('./modules/security/workflow-management/workflow-management.component').then(m => m.WorkflowManagementComponent), canActivate: [permissionGuard], data: { permission: 'SETTINGS_MANAGE' } },

      // Academic
      { path: 'academic/dashboard', loadComponent: () => import('./modules/academic/dashboard/dashboard.component').then(m => m.AcademicDashboardComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/university', loadComponent: () => import('./modules/academic/university/university.component').then(m => m.UniversityComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/campus', loadComponent: () => import('./modules/academic/campus/campus.component').then(m => m.CampusComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/faculty', loadComponent: () => import('./modules/academic/faculties/faculties.component').then(m => m.FacultiesComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/administration-divisions', loadComponent: () => import('./modules/academic/administration-divisions/administration-divisions.component').then(m => m.AdministrationDivisionsComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/departments', loadComponent: () => import('./modules/academic/departments/departments.component').then(m => m.DepartmentsComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/programs', loadComponent: () => import('./modules/academic/programs/programs.component').then(m => m.ProgramsComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/academic-sessions', loadComponent: () => import('./modules/academic/academic-sessions/academic-sessions.component').then(m => m.AcademicSessionsComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/semesters', loadComponent: () => import('./modules/academic/semesters/semesters.component').then(m => m.SemestersComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/batches', loadComponent: () => import('./modules/academic/batches/batches.component').then(m => m.BatchesComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/sections', loadComponent: () => import('./modules/academic/sections/sections.component').then(m => m.SectionsComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/courses', loadComponent: () => import('./modules/academic/courses/courses.component').then(m => m.CoursesComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/subjects', loadComponent: () => import('./modules/academic/subjects/subjects.component').then(m => m.SubjectsComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/curriculum', loadComponent: () => import('./modules/academic/curriculum/curriculum.component').then(m => m.CurriculumComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/credit-rules', loadComponent: () => import('./modules/academic/credit-rules/credit-rules.component').then(m => m.CreditRulesComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/prerequisites', loadComponent: () => import('./modules/academic/prerequisites/prerequisites.component').then(m => m.PrerequisitesComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/course-assignments', loadComponent: () => import('./modules/academic/course-assignments/course-assignments.component').then(m => m.CourseAssignmentsComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/academic-calendar', loadComponent: () => import('./modules/academic/academic-calendar/academic-calendar.component').then(m => m.AcademicCalendarComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/class-routines', loadComponent: () => import('./modules/academic/class-routines/class-routines.component').then(m => m.ClassRoutinesComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/semester-routines', loadComponent: () => import('./modules/academic/semester-routines/semester-routines.component').then(m => m.SemesterRoutinesComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },
      { path: 'academic/academic-policies', loadComponent: () => import('./modules/academic/academic-policies/academic-policies.component').then(m => m.AcademicPoliciesComponent), canActivate: [permissionGuard], data: { permission: 'ACADEMIC_VIEW' } },

      // Admissions
      { path: 'admissions/dashboard', loadComponent: () => import('./modules/admissions/dashboard/admission-dashboard.component').then(m => m.AdmissionDashboardComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/sessions', loadComponent: () => import('./modules/admissions/admission-sessions/admission-sessions.component').then(m => m.AdmissionSessionsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/circulars', loadComponent: () => import('./modules/admissions/circulars/circulars.component').then(m => m.CircularsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/applications', loadComponent: () => import('./modules/admissions/applications/applications.component').then(m => m.ApplicationsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/tests', loadComponent: () => import('./modules/admissions/tests/tests.component').then(m => m.TestsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/merit-lists', loadComponent: () => import('./modules/admissions/merit-lists/merit-lists.component').then(m => m.MeritListsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/waiting-lists', loadComponent: () => import('./modules/admissions/waiting-lists/waiting-lists.component').then(m => m.WaitingListsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/interviews', loadComponent: () => import('./modules/admissions/interviews/interviews.component').then(m => m.InterviewsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/document-verifications', loadComponent: () => import('./modules/admissions/document-verifications/document-verifications.component').then(m => m.DocumentVerificationsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/requirements', loadComponent: () => import('./modules/admissions/admission-requirements/admission-requirements.component').then(m => m.AdmissionRequirementsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/eligibility-criteria', loadComponent: () => import('./modules/admissions/eligibility-criteria/eligibility-criteria.component').then(m => m.EligibilityCriteriaComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/candidates', loadComponent: () => import('./modules/admissions/candidates/candidates.component').then(m => m.CandidatesComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/fee-collection', loadComponent: () => import('./modules/admissions/fee-collection/fee-collection.component').then(m => m.FeeCollectionComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/faculties-programs', loadComponent: () => import('./modules/admissions/faculties-programs/faculties-programs.component').then(m => m.FacultiesProgramsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/offer-letters', loadComponent: () => import('./modules/admissions/offer-letters/offer-letters.component').then(m => m.OfferLettersComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/enrollments', loadComponent: () => import('./modules/admissions/enrollments/enrollments.component').then(m => m.EnrollmentsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/student-id-generation', loadComponent: () => import('./modules/admissions/student-id-generation/student-id-generation.component').then(m => m.StudentIdGenerationComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/orientation', loadComponent: () => import('./modules/admissions/orientation/orientation.component').then(m => m.OrientationComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/analytics', loadComponent: () => import('./modules/admissions/admission-analytics/admission-analytics.component').then(m => m.AdmissionAnalyticsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/reports', loadComponent: () => import('./modules/admissions/admission-reports/admission-reports.component').then(m => m.AdmissionReportsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/pre-admissions', loadComponent: () => import('./modules/admissions/pre-admissions/pre-admissions.component').then(m => m.PreAdmissionsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/test-results', loadComponent: () => import('./modules/admissions/test-results/test-results.component').then(m => m.TestResultsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/merit-processing', loadComponent: () => import('./modules/admissions/merit-processing/merit-processing.component').then(m => m.MeritProcessingComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_MANAGE' } },
      { path: 'admissions/allocations', loadComponent: () => import('./modules/admissions/allocations/allocations.component').then(m => m.AllocationsComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },
      { path: 'admissions/question-bank', loadComponent: () => import('./modules/admissions/question-bank/question-bank.component').then(m => m.QuestionBankComponent), canActivate: [permissionGuard], data: { permission: 'ADMISSION_VIEW' } },

      // Students
      { path: 'students/list', loadComponent: () => import('./modules/students/list/list.component').then(m => m.StudentsListComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/enrollments', loadComponent: () => import('./modules/students/enrollments/enrollments.component').then(m => m.EnrollmentsComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/profiles', loadComponent: () => import('./modules/students/profiles/profiles.component').then(m => m.ProfilesComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/guardians', loadComponent: () => import('./modules/students/guardians/guardians.component').then(m => m.GuardiansComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/dashboard', loadComponent: () => import('./modules/students/student-dashboard/student-dashboard.component').then(m => m.StudentDashboardComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/attendance', loadComponent: () => import('./modules/students/attendance/student-attendance.component').then(m => m.StudentAttendanceComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/academic-history', loadComponent: () => import('./modules/students/academic-history/academic-history.component').then(m => m.AcademicHistoryComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/course-registration', loadComponent: () => import('./modules/students/course-registration/course-registration.component').then(m => m.CourseRegistrationComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/semester-registration', loadComponent: () => import('./modules/students/semester-registration/semester-registration.component').then(m => m.SemesterRegistrationComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/result', loadComponent: () => import('./modules/students/result/student-result.component').then(m => m.StudentResultComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/transcript', loadComponent: () => import('./modules/students/transcript/transcript.component').then(m => m.TranscriptComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/certificates', loadComponent: () => import('./modules/students/certificates/certificates.component').then(m => m.CertificatesComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/disciplinary-records', loadComponent: () => import('./modules/students/disciplinary-records/disciplinary-records.component').then(m => m.DisciplinaryRecordsComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/medical-info', loadComponent: () => import('./modules/students/medical-info/medical-info.component').then(m => m.MedicalInfoComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/documents', loadComponent: () => import('./modules/students/documents/documents.component').then(m => m.DocumentsComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/promotions', loadComponent: () => import('./modules/students/student-promotion/student-promotion.component').then(m => m.StudentPromotionComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },
      { path: 'students/alumni', loadComponent: () => import('./modules/students/alumni/alumni.component').then(m => m.AlumniComponent), canActivate: [permissionGuard], data: { permission: 'STUDENT_VIEW' } },

      // Teachers
      { path: 'teachers/dashboard', loadComponent: () => import('./modules/teachers/teachers-dashboard/teachers-dashboard.component').then(m => m.TeachersDashboardComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/list', loadComponent: () => import('./modules/teachers/teachers-list/teachers-list.component').then(m => m.TeachersListComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/documents', loadComponent: () => import('./modules/teachers/teacher-documents/teacher-documents.component').then(m => m.TeacherDocumentsComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/detail/:id', loadComponent: () => import('./modules/teachers/teacher-detail/teacher-detail.component').then(m => m.TeacherDetailComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/departments', loadComponent: () => import('./modules/teachers/teacher-departments/teacher-departments.component').then(m => m.TeacherDepartmentsComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/faculty-assignment', loadComponent: () => import('./modules/teachers/faculty-assignment/faculty-assignment.component').then(m => m.FacultyAssignmentComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/course-allocation', loadComponent: () => import('./modules/teachers/course-allocation/course-allocation.component').then(m => m.CourseAllocationComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/class-routine', loadComponent: () => import('./modules/teachers/class-routine/class-routine.component').then(m => m.ClassRoutineComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/attendance', loadComponent: () => import('./modules/teachers/teacher-attendance/teacher-attendance.component').then(m => m.TeacherAttendanceComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/research', loadComponent: () => import('./modules/teachers/research-publications/research-publications.component').then(m => m.ResearchPublicationsComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/awards', loadComponent: () => import('./modules/teachers/awards-achievements/awards-achievements.component').then(m => m.AwardsAchievementsComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/promotions', loadComponent: () => import('./modules/teachers/promotion-history/promotion-history.component').then(m => m.PromotionHistoryComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },
      { path: 'teachers/alumni', loadComponent: () => import('./modules/teachers/alumni-teachers/alumni-teachers.component').then(m => m.AlumniTeachersComponent), canActivate: [permissionGuard], data: { permission: 'TEACHER_VIEW' } },

      // Administration
      { path: 'administration/administrative-heads', loadComponent: () => import('./modules/administration/administrative-heads/administrative-heads.component').then(m => m.AdministrativeHeadsComponent), canActivate: [permissionGuard], data: { permission: 'ADMINISTRATION_VIEW' } },
      { path: 'administration/academic-heads', loadComponent: () => import('./modules/administration/academic-heads/academic-heads.component').then(m => m.AcademicHeadsComponent), canActivate: [permissionGuard], data: { permission: 'ADMINISTRATION_VIEW' } },
      { path: 'administration/head-of-offices', loadComponent: () => import('./modules/administration/head-of-offices/head-of-offices.component').then(m => m.HeadOfOfficesComponent), canActivate: [permissionGuard], data: { permission: 'ADMINISTRATION_VIEW' } },
      { path: 'administration/others', loadComponent: () => import('./modules/administration/others/others.component').then(m => m.OthersComponent), canActivate: [permissionGuard], data: { permission: 'ADMINISTRATION_VIEW' } },
      // HRM
      { path: 'hrm/employees', loadComponent: () => import('./modules/hrm/employees/employees.component').then(m => m.EmployeesComponent), canActivate: [permissionGuard], data: { permission: 'HRM_VIEW' } },
      { path: 'hrm/attendance', loadComponent: () => import('./modules/hrm/attendance/attendance.component').then(m => m.EmployeeAttendanceComponent), canActivate: [permissionGuard], data: { permission: 'HRM_VIEW' } },
      { path: 'hrm/leave-requests', loadComponent: () => import('./modules/hrm/leave-requests/leave-requests.component').then(m => m.LeaveRequestsComponent), canActivate: [permissionGuard], data: { permission: 'HRM_VIEW' } },
      { path: 'hrm/payrolls', loadComponent: () => import('./modules/hrm/payrolls/payrolls.component').then(m => m.PayrollsComponent), canActivate: [permissionGuard], data: { permission: 'HRM_VIEW' } },

      // Examination
      { path: 'examination/exams', loadComponent: () => import('./modules/examination/exams/exams.component').then(m => m.ExamsComponent), canActivate: [permissionGuard], data: { permission: 'EXAM_VIEW' } },
      { path: 'examination/schedules', loadComponent: () => import('./modules/examination/schedules/schedules.component').then(m => m.SchedulesComponent), canActivate: [permissionGuard], data: { permission: 'EXAM_VIEW' } },
      { path: 'examination/marks', loadComponent: () => import('./modules/examination/marks/marks.component').then(m => m.MarksComponent), canActivate: [permissionGuard], data: { permission: 'EXAM_VIEW' } },
      { path: 'examination/grade-rules', loadComponent: () => import('./modules/examination/grade-rules/grade-rules.component').then(m => m.GradeRulesComponent), canActivate: [permissionGuard], data: { permission: 'EXAM_VIEW' } },
      { path: 'examination/results', loadComponent: () => import('./modules/examination/results/results.component').then(m => m.ExamResultsComponent), canActivate: [permissionGuard], data: { permission: 'EXAM_VIEW' } },

      // LMS
      { path: 'lms/assignments', loadComponent: () => import('./modules/lms/assignments/assignments.component').then(m => m.LmsAssignmentsComponent), canActivate: [permissionGuard], data: { permission: 'LMS_VIEW' } },
      { path: 'lms/submissions', loadComponent: () => import('./modules/lms/submissions/submissions.component').then(m => m.SubmissionsComponent), canActivate: [permissionGuard], data: { permission: 'LMS_VIEW' } },
      { path: 'lms/materials', loadComponent: () => import('./modules/lms/materials/materials.component').then(m => m.MaterialsComponent), canActivate: [permissionGuard], data: { permission: 'LMS_VIEW' } },
      { path: 'lms/online-classes', loadComponent: () => import('./modules/lms/online-classes/online-classes.component').then(m => m.OnlineClassesComponent), canActivate: [permissionGuard], data: { permission: 'LMS_VIEW' } },

      // Finance
      { path: 'finance/fee-types', loadComponent: () => import('./modules/finance/fee-types/fee-types.component').then(m => m.FeeTypesComponent), canActivate: [permissionGuard], data: { permission: 'FINANCE_VIEW' } },
      { path: 'finance/student-fees', loadComponent: () => import('./modules/finance/student-fees/student-fees.component').then(m => m.StudentFeesComponent), canActivate: [permissionGuard], data: { permission: 'FINANCE_VIEW' } },
      { path: 'finance/invoices', loadComponent: () => import('./modules/finance/invoices/invoices.component').then(m => m.InvoicesComponent), canActivate: [permissionGuard], data: { permission: 'FINANCE_VIEW' } },
      { path: 'finance/payments', loadComponent: () => import('./modules/finance/payments/payments.component').then(m => m.PaymentsComponent), canActivate: [permissionGuard], data: { permission: 'FINANCE_VIEW' } },
      { path: 'finance/accounts', loadComponent: () => import('./modules/finance/accounts/accounts.component').then(m => m.AccountsComponent), canActivate: [permissionGuard], data: { permission: 'FINANCE_VIEW' } },
      { path: 'finance/transactions', loadComponent: () => import('./modules/finance/transactions/transactions.component').then(m => m.TransactionsComponent), canActivate: [permissionGuard], data: { permission: 'FINANCE_VIEW' } },

      // Library
      { path: 'library/books', loadComponent: () => import('./modules/library/books/books.component').then(m => m.BooksComponent), canActivate: [permissionGuard], data: { permission: 'LIBRARY_VIEW' } },
      { path: 'library/categories', loadComponent: () => import('./modules/library/categories/categories.component').then(m => m.BookCategoriesComponent), canActivate: [permissionGuard], data: { permission: 'LIBRARY_VIEW' } },
      { path: 'library/issues', loadComponent: () => import('./modules/library/issues/issues.component').then(m => m.IssuesComponent), canActivate: [permissionGuard], data: { permission: 'LIBRARY_VIEW' } },
      { path: 'library/returns', loadComponent: () => import('./modules/library/returns/returns.component').then(m => m.ReturnsComponent), canActivate: [permissionGuard], data: { permission: 'LIBRARY_VIEW' } },

      // Hostel
      { path: 'hostel/list', loadComponent: () => import('./modules/hostel/list/list.component').then(m => m.HostelsComponent), canActivate: [permissionGuard], data: { permission: 'HOSTEL_VIEW' } },
      { path: 'hostel/rooms', loadComponent: () => import('./modules/hostel/rooms/rooms.component').then(m => m.RoomsComponent), canActivate: [permissionGuard], data: { permission: 'HOSTEL_VIEW' } },
      { path: 'hostel/allocations', loadComponent: () => import('./modules/hostel/allocations/allocations.component').then(m => m.HostelAllocationsComponent), canActivate: [permissionGuard], data: { permission: 'HOSTEL_VIEW' } },

      // Transport
      { path: 'transport/vehicles', loadComponent: () => import('./modules/transport/vehicles/vehicles.component').then(m => m.VehiclesComponent), canActivate: [permissionGuard], data: { permission: 'TRANSPORT_VIEW' } },
      { path: 'transport/routes', loadComponent: () => import('./modules/transport/routes/routes.component').then(m => m.RoutesComponent), canActivate: [permissionGuard], data: { permission: 'TRANSPORT_VIEW' } },
      { path: 'transport/allocations', loadComponent: () => import('./modules/transport/allocations/allocations.component').then(m => m.TransportAllocationsComponent), canActivate: [permissionGuard], data: { permission: 'TRANSPORT_VIEW' } },

      // Communication
      { path: 'communication/notices', loadComponent: () => import('./modules/communication/notices/notices.component').then(m => m.NoticesComponent), canActivate: [permissionGuard], data: { permission: 'COMMUNICATION_VIEW' } },
      { path: 'communication/announcements', loadComponent: () => import('./modules/communication/announcements/announcements.component').then(m => m.AnnouncementsComponent), canActivate: [permissionGuard], data: { permission: 'COMMUNICATION_VIEW' } },
      { path: 'communication/messages', loadComponent: () => import('./modules/communication/messages/messages.component').then(m => m.MessagesComponent), canActivate: [permissionGuard], data: { permission: 'COMMUNICATION_VIEW' } },
      { path: 'communication/notifications', loadComponent: () => import('./modules/communication/notifications/notifications.component').then(m => m.NotificationsComponent), canActivate: [permissionGuard], data: { permission: 'COMMUNICATION_VIEW' } },

      // Activities
      { path: 'activities/clubs', loadComponent: () => import('./modules/activities/clubs/clubs.component').then(m => m.ClubsComponent), canActivate: [permissionGuard], data: { permission: 'ACTIVITY_VIEW' } },
      { path: 'activities/sports', loadComponent: () => import('./modules/activities/sports/sports.component').then(m => m.SportsComponent), canActivate: [permissionGuard], data: { permission: 'ACTIVITY_VIEW' } },
      { path: 'activities/events', loadComponent: () => import('./modules/activities/events/events.component').then(m => m.EventsComponent), canActivate: [permissionGuard], data: { permission: 'ACTIVITY_VIEW' } },
      { path: 'activities/registrations', loadComponent: () => import('./modules/activities/registrations/registrations.component').then(m => m.RegistrationsComponent), canActivate: [permissionGuard], data: { permission: 'ACTIVITY_VIEW' } },

      // Reports
      { path: 'reports/templates', loadComponent: () => import('./modules/reports/templates/templates.component').then(m => m.TemplatesComponent), canActivate: [permissionGuard], data: { permission: 'REPORT_VIEW' } },
      { path: 'reports/generated', loadComponent: () => import('./modules/reports/generated/generated.component').then(m => m.GeneratedComponent), canActivate: [permissionGuard], data: { permission: 'REPORT_VIEW' } },
    ]
  },
  { path: '**', redirectTo: '' }
];
