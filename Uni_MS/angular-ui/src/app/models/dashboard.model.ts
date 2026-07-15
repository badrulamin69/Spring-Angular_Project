export interface KpiCard {
  title: string;
  value: number | string;
  icon: string;
  color: string;
  change: number;
  changeLabel: string;
  trend: 'up' | 'down' | 'neutral';
  lastUpdated: string;
}

export interface ChartData {
  labels: string[];
  datasets: number[];
}

export interface ActivityItem {
  id: number;
  type: string;
  title: string;
  description: string;
  timestamp: string;
  icon: string;
  color: string;
  user: string;
}

export interface NotificationItem {
  id: number;
  type: 'info' | 'warning' | 'success' | 'danger';
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
  icon: string;
}

export interface CalendarEvent {
  id: number;
  title: string;
  date: string;
  type: 'exam' | 'holiday' | 'meeting' | 'class' | 'event';
  color: string;
}

export interface ApprovalItem {
  id: number;
  type: string;
  title: string;
  requester: string;
  date: string;
  status: 'pending' | 'approved' | 'rejected';
  priority: 'low' | 'medium' | 'high';
}

export interface SystemHealth {
  usersOnline: number;
  dbStatus: string;
  apiStatus: string;
  storageUsed: number;
  storageTotal: number;
  memoryUsed: number;
  memoryTotal: number;
  lastBackup: string;
}

export interface DashboardStats {
  students: { total: number; active: number; male: number; female: number; international: number; alumni: number; suspended: number; dropout: number; transferred: number };
  administration: { total: number; active: number };
  staff: { total: number; active: number };
  departments: number;
  programs: number;
  courses: number;
  semesters: { total: number; active: number };
  enrollments: { current: number; pending: number };
  admissions: { today: number; pending: number; approved: number; rejected: number; waitlisted: number; rate: number };
  classes: { active: number };
  exams: { upcoming: number; running: number; completed: number };
  results: { published: number; pending: number; avgGpa: number; avgCgpa: number };
  library: { books: number; borrowed: number; returned: number; overdue: number; reserved: number; fineCollection: number };
  hostel: { occupied: number; available: number; students: number; visitors: number; pendingRequests: number };
  transport: { vehicles: number; routes: number; drivers: number; registeredStudents: number; tripsToday: number; fuelStatus: number };
  finance: { todayRevenue: number; monthlyRevenue: number; outstandingFees: number; scholarships: number; waivers: number; pendingPayments: number };
  attendance: { studentRate: number; facultyRate: number; staffRate: number; todayAbsent: number; todayPresent: number };
  lms: { courses: number; lessons: number; assignments: number; submissions: number; quizzes: number; discussions: number; liveClasses: number; recordedVideos: number; progress: number };
  hrm: { employees: number; attendance: number; leaveRequests: number; payroll: number; performance: number };
  recentActivities: ActivityItem[];
  notifications: NotificationItem[];
  calendarEvents: CalendarEvent[];
  pendingApprovals: ApprovalItem[];
  systemHealth: SystemHealth;
}
