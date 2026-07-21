import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SemesterEnrollmentService } from '../../../services/semester-enrollment.service';
import { SemesterService } from '../../../services/semester.service';
import { EnrollmentDashboard } from '../../../models/semester-enrollment';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-enrollment-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Semester Enrollment Dashboard</h2>
        <p class="page-sub">Overview of enrollment statistics and metrics</p>
      </div>
      <div class="header-actions">
        <div class="form-group">
          <select [(ngModel)]="selectedSemesterId" name="semesterId" class="form-control" (change)="onSemesterChange()">
            <option value="">Select Semester</option>
            @for (sem of semesters; track sem.id) {
              <option [ngValue]="sem.id">{{ sem.name }}</option>
            }
          </select>
        </div>
      </div>
    </div>

    @if (loading) {
      <div class="card">
        <div class="loading-state">
          <div class="spinner"></div>
          <p>Loading dashboard data...</p>
        </div>
      </div>
    }

    @if (dashboard && !loading) {
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon total">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ dashboard.totalEnrollments }}</span>
            <span class="stat-label">Total Enrollments</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon pending">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ dashboard.pendingApprovals }}</span>
            <span class="stat-label">Pending Approvals</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon approved">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ dashboard.approvedEnrollments }}</span>
            <span class="stat-label">Approved</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon completed">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ dashboard.completedEnrollments }}</span>
            <span class="stat-label">Completed</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rejected">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="15" y1="9" x2="9" y2="15"></line>
              <line x1="9" y1="9" x2="15" y2="15"></line>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ dashboard.rejectedEnrollments }}</span>
            <span class="stat-label">Rejected</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon cancelled">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="15" y1="9" x2="9" y2="15"></line>
              <line x1="9" y1="9" x2="15" y2="15"></line>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ dashboard.cancelledEnrollments }}</span>
            <span class="stat-label">Cancelled</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon draft">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ dashboard.draftEnrollments }}</span>
            <span class="stat-label">Draft</span>
          </div>
        </div>
      </div>

      <div class="charts-row">
        <div class="card">
          <div class="card-header">
            <h3>Status Breakdown</h3>
          </div>
          <div class="card-body">
            @if (dashboard.statusBreakdown.length === 0) {
              <div class="empty-state">
                <p>No status data available</p>
              </div>
            } @else {
              <table class="data-table">
                <thead>
                  <tr>
                    <th>Status</th>
                    <th>Count</th>
                    <th>Percentage</th>
                  </tr>
                </thead>
                <tbody>
                  @for (item of dashboard.statusBreakdown; track item.status) {
                    <tr>
                      <td>
                        <span class="status-badge" [ngClass]="getStatusClass(item.status)">{{ item.status }}</span>
                      </td>
                      <td>{{ item.count }}</td>
                      <td>
                        <div class="progress-bar-container">
                          <div class="progress-bar" [style.width.%]="getPercentage(item.count, dashboard.totalEnrollments)"></div>
                          <span class="progress-text">{{ getPercentage(item.count, dashboard.totalEnrollments) | number:'1.0-1' }}%</span>
                        </div>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        </div>

        <div class="card">
          <div class="card-header">
            <h3>Department Breakdown</h3>
          </div>
          <div class="card-body">
            @if (dashboard.departmentBreakdown.length === 0) {
              <div class="empty-state">
                <p>No department data available</p>
              </div>
            } @else {
              <table class="data-table">
                <thead>
                  <tr>
                    <th>Department</th>
                    <th>Count</th>
                    <th>Percentage</th>
                  </tr>
                </thead>
                <tbody>
                  @for (item of dashboard.departmentBreakdown; track item.departmentId) {
                    <tr>
                      <td>{{ item.departmentName }}</td>
                      <td>{{ item.count }}</td>
                      <td>
                        <div class="progress-bar-container">
                          <div class="progress-bar dept" [style.width.%]="getPercentage(item.count, dashboard.totalEnrollments)"></div>
                          <span class="progress-text">{{ getPercentage(item.count, dashboard.totalEnrollments) | number:'1.0-1' }}%</span>
                        </div>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3>Recent Enrollments</h3>
        </div>
        <div class="card-body">
          @if (dashboard.recentEnrollments.length === 0) {
            <div class="empty-state">
              <p>No recent enrollments found</p>
            </div>
          } @else {
            <div class="table-responsive">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>Enrollment #</th>
                    <th>Student</th>
                    <th>Semester</th>
                    <th>Status</th>
                    <th>Credits</th>
                    <th>Advisor</th>
                    <th>Payment</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  @for (enrollment of dashboard.recentEnrollments; track enrollment.id) {
                    <tr>
                      <td class="enrollment-number">{{ enrollment.enrollmentNumber }}</td>
                      <td>{{ enrollment.studentName }}</td>
                      <td>{{ enrollment.semesterName }}</td>
                      <td>
                        <span class="status-badge" [ngClass]="getStatusClass(enrollment.status)">{{ enrollment.status }}</span>
                      </td>
                      <td>{{ enrollment.registeredCredits }}</td>
                      <td>
                        <span class="status-badge" [ngClass]="getAdvisorStatusClass(enrollment.advisorStatus)">{{ enrollment.advisorStatus || 'N/A' }}</span>
                      </td>
                      <td>
                        <span class="status-badge" [ngClass]="getPaymentStatusClass(enrollment.paymentStatus)">{{ enrollment.paymentStatus || 'N/A' }}</span>
                      </td>
                      <td>{{ enrollment.createdAt | date:'mediumDate' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
      </div>
    }

    @if (!loading && !dashboard && selectedSemesterId) {
      <div class="card">
        <div class="empty-state">
          <p>No dashboard data available for the selected semester</p>
        </div>
      </div>
    }

    @if (!selectedSemesterId && !loading) {
      <div class="card">
        <div class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon">
            <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
          </svg>
          <p>Select a semester to view enrollment dashboard</p>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; flex-wrap: wrap; gap: 12px; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .header-actions .form-group { min-width: 250px; }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; margin-bottom: 1.25rem; }
    .card-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid var(--border-color); }
    .card-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .card-body { padding: 16px 20px; }
    .loading-state { display: flex; flex-direction: column; align-items: center; padding: 40px 20px; gap: 12px; }
    .spinner { width: 32px; height: 32px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .loading-state p { color: var(--text-muted); font-size: 0.875rem; margin: 0; }
    .empty-state { display: flex; flex-direction: column; align-items: center; padding: 40px 20px; gap: 12px; }
    .empty-state p { color: var(--text-muted); font-size: 0.875rem; margin: 0; }
    .empty-icon { width: 48px; height: 48px; color: var(--text-muted); }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 1.25rem; }
    .stat-card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); padding: 20px; display: flex; align-items: center; gap: 16px; transition: transform 0.15s; }
    .stat-card:hover { transform: translateY(-2px); }
    .stat-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .stat-icon svg { width: 24px; height: 24px; }
    .stat-icon.total { background: #eff6ff; color: #2563eb; }
    .stat-icon.pending { background: #fffbeb; color: #d97706; }
    .stat-icon.approved { background: #f0fdf4; color: #16a34a; }
    .stat-icon.completed { background: #f0fdf4; color: #059669; }
    .stat-icon.rejected { background: #fef2f2; color: #dc2626; }
    .stat-icon.cancelled { background: #fef2f2; color: #991b1b; }
    .stat-icon.draft { background: #f5f3ff; color: #7c3aed; }
    .stat-info { display: flex; flex-direction: column; }
    .stat-number { font-size: 1.5rem; font-weight: 700; color: var(--text-primary); line-height: 1.2; }
    .stat-label { font-size: 0.8rem; color: var(--text-muted); margin-top: 2px; }
    .charts-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; margin-bottom: 1.25rem; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th { padding: 10px 12px; text-align: left; font-size: 0.8rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid var(--border-color); background: var(--bg-secondary); }
    .data-table td { padding: 10px 12px; font-size: 0.875rem; color: var(--text-primary); border-bottom: 1px solid var(--border-color); }
    .data-table tbody tr:hover { background: var(--bg-secondary); }
    .status-badge { padding: 3px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-draft { background: #f5f3ff; color: #7c3aed; }
    .badge-pending { background: #fffbeb; color: #d97706; }
    .badge-approved { background: #f0fdf4; color: #16a34a; }
    .badge-completed { background: #ecfdf5; color: #059669; }
    .badge-rejected { background: #fef2f2; color: #dc2626; }
    .badge-cancelled { background: #fee2e2; color: #991b1b; }
    .badge-paid { background: #f0fdf4; color: #16a34a; }
    .badge-unpaid { background: #fef2f2; color: #dc2626; }
    .badge-na { background: #e5e7eb; color: #6b7280; }
    .progress-bar-container { display: flex; align-items: center; gap: 8px; }
    .progress-bar { height: 6px; background: var(--brand-color); border-radius: 3px; min-width: 20px; max-width: 120px; }
    .progress-bar.dept { background: #7c3aed; }
    .progress-text { font-size: 0.8rem; color: var(--text-muted); white-space: nowrap; }
    .enrollment-number { font-weight: 600; color: var(--brand-color); }
    .table-responsive { overflow-x: auto; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    @media (max-width: 1200px) { .stats-grid { grid-template-columns: repeat(3, 1fr); } }
    @media (max-width: 900px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } .charts-row { grid-template-columns: 1fr; } }
    @media (max-width: 600px) { .stats-grid { grid-template-columns: 1fr; } }
  `]
})
export class DashboardComponent implements OnInit {
  semesters: any[] = [];
  selectedSemesterId = '';
  dashboard: EnrollmentDashboard | null = null;
  loading = false;

  constructor(
    private enrollmentService: SemesterEnrollmentService,
    private semesterService: SemesterService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadSemesters();
  }

  loadSemesters() {
    this.semesterService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.semesters = data.content || []; },
      error: () => {}
    });
  }

  onSemesterChange() {
    if (this.selectedSemesterId) {
      this.loadDashboard();
    } else {
      this.dashboard = null;
    }
  }

  loadDashboard() {
    if (!this.selectedSemesterId) return;
    this.loading = true;
    this.enrollmentService.getDashboardStats(parseInt(this.selectedSemesterId, 10)).subscribe({
      next: (data) => {
        this.dashboard = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load dashboard data');
      }
    });
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      'Draft': 'badge-draft',
      'Pending': 'badge-pending',
      'Approved': 'badge-approved',
      'Completed': 'badge-completed',
      'Rejected': 'badge-rejected',
      'Cancelled': 'badge-cancelled'
    };
    return map[status] || 'badge-na';
  }

  getAdvisorStatusClass(status: string | undefined): string {
    if (!status) return 'badge-na';
    const map: Record<string, string> = {
      'Pending': 'badge-pending',
      'Approved': 'badge-approved',
      'Rejected': 'badge-rejected'
    };
    return map[status] || 'badge-na';
  }

  getPaymentStatusClass(status: string | undefined): string {
    if (!status) return 'badge-na';
    const map: Record<string, string> = {
      'Paid': 'badge-paid',
      'Pending': 'badge-pending',
      'Unpaid': 'badge-unpaid',
      'Partial': 'badge-pending'
    };
    return map[status] || 'badge-na';
  }

  getPercentage(count: number, total: number): number {
    if (!total) return 0;
    return (count / total) * 100;
  }
}
