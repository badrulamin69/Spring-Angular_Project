import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/dashboard`;

  getDashboard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/my`).pipe(
      map(response => {
        const payload = response?.data || response;
        if (!payload) return {};

        const result: any = {};

        if (payload.summary) {
          Object.assign(result, payload.summary);
        }

        if (Array.isArray(payload.cards)) {
          for (const card of payload.cards) {
            const key = this.cardToKey(card.title);
            result[key] = card.value;
          }
        }

        if (Array.isArray(payload.quickActions)) {
          result.quickActions = payload.quickActions.map((a: any) => ({
            label: a.title || a.label,
            route: a.route,
            icon: a.icon,
            color: '#6366f1',
          }));
        }

        if (Array.isArray(payload.recentActivities)) {
          result.recentActivities = payload.recentActivities.length;
        }

        result.systemHealth = result.systemHealth || 'UP';
        result.cards = payload.cards;
        result.quickActionsList = payload.quickActions;

        return result;
      })
    );
  }

  private cardToKey(title: string): string {
    const map: Record<string, string> = {
      'Total Students': 'totalStudents',
      'Total Courses': 'totalCourses',
      'Total Employees': 'totalEmployees',
      'Total Administration': 'totalAdministration',
      'Total Users': 'totalUsers',
      'Total Roles': 'totalRoles',
      'Permissions': 'totalPermissions',
      'Active Sessions': 'activeSessions',
      'Total Faculties': 'totalFaculties',
      'Total Departments': 'totalDepartments',
      'Active Exams': 'upcomingExams',
      'My Department Students': 'totalStudents',
      'My Courses': 'assignedCourses',
      'Pending Assignments': 'pendingAssignments',
      'Upcoming Exams': 'upcomingExams',
      'Total Invoices': 'totalInvoices',
      'Total Books': 'totalBooks',
      'Assigned Courses': 'assignedCourses',
      'Total Applications': 'totalApplications',
      'Pending Review': 'pendingReview',
      'Approved': 'approved',
      'Rejected': 'rejected',
      'Paid': 'paid',
      'Pending': 'pending',
      'Overdue': 'overdue',
      'Borrowed': 'borrowed',
      'Available': 'available',
      'Leave Requests': 'pendingLeaveRequests',
      'Payrolls': 'totalPayrolls',
      'Pending Approvals': 'pendingApprovals',
      'Recent Logins': 'recentLogins',
      'Security Alerts': 'securityAlerts',
      'Active Enrollments': 'activeEnrollments',
      'Registered Courses': 'registeredCourses',
      'Pending Fees': 'pendingFees',
      'Total Faculty': 'totalFaculty',
      'Total Alumni': 'totalAlumni',
      'Recent Submissions': 'recentSubmissions',
      'Recent Activities': 'recentActivities',
    };
    return map[title] || title.replace(/\s+/g, '').toLowerCase();
  }
}
