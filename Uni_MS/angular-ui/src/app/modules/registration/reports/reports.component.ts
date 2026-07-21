import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RegistrationService } from '../../../services/registration.service';
import { SemesterService } from '../../../services/semester.service';
import { RegistrationConfigService } from '../../../services/registration-config.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-registration-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Registration Reports</h2>
        <p class="page-sub">View registration statistics and reports</p>
      </div>
    </div>

    <div class="filter-bar card">
      <div class="form-row">
        <div class="form-group">
          <label>Semester *</label>
          <select [(ngModel)]="semesterId" class="form-control" (change)="loadData()">
            <option value="">Select Semester</option>
            @for (sem of semesters; track sem.id) {
              <option [ngValue]="sem.id">{{ sem.name }}</option>
            }
          </select>
        </div>
      </div>
    </div>

    @if (dashboard) {
      <div class="report-grid">
        <div class="card report-card">
          <div class="report-header">
            <h3>Registration Overview</h3>
          </div>
          <div class="report-body">
            <div class="report-row">
              <span class="report-label">Total Registrations</span>
              <span class="report-value">{{ dashboard.totalRegistrations }}</span>
            </div>
            <div class="report-row">
              <span class="report-label">Pending Approvals</span>
              <span class="report-value text-warning">{{ dashboard.pendingApprovals }}</span>
            </div>
            <div class="report-row">
              <span class="report-label">Approved</span>
              <span class="report-value text-success">{{ dashboard.approvedRegistrations }}</span>
            </div>
            <div class="report-row">
              <span class="report-label">Registered Students</span>
              <span class="report-value">{{ dashboard.registeredStudents }}</span>
            </div>
            <div class="report-row">
              <span class="report-label">Dropped</span>
              <span class="report-value text-danger">{{ dashboard.droppedRegistrations }}</span>
            </div>
          </div>
        </div>

        <div class="card report-card">
          <div class="report-header">
            <h3>Status Distribution</h3>
          </div>
          <div class="report-body">
            @for (stat of dashboard.statusBreakdown; track stat.status) {
              <div class="dist-item">
                <div class="dist-label">
                  <span class="badge badge-{{ getStatusColor(stat.status) }}">{{ stat.status }}</span>
                  <span class="dist-count">{{ stat.count }}</span>
                </div>
                <div class="dist-bar">
                  <div class="dist-fill" [style.width]="getBarWidth(stat.count) + '%'" [style.background]="getBarColor(stat.status)"></div>
                </div>
                <span class="dist-percent">{{ getPercent(stat.count) }}%</span>
              </div>
            }
          </div>
        </div>

        <div class="card report-card full-width">
          <div class="report-header">
            <h3>Recent Activity</h3>
          </div>
          <div class="table-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Student</th>
                  <th>Student Code</th>
                  <th>Course</th>
                  <th>Semester</th>
                  <th>Credits</th>
                  <th>Status</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                @for (reg of dashboard.recentRegistrations; track reg.id) {
                  <tr>
                    <td>{{ reg.id }}</td>
                    <td>{{ reg.studentName }}</td>
                    <td>{{ reg.studentCode }}</td>
                    <td>{{ reg.courseName }}</td>
                    <td>{{ reg.semesterName }}</td>
                    <td>{{ reg.creditHours }}</td>
                    <td><span class="badge badge-{{ getStatusColor(reg.status) }}">{{ reg.status }}</span></td>
                    <td>{{ reg.registrationDate | date:'short' }}</td>
                  </tr>
                } @empty {
                  <tr><td colspan="8" class="text-center text-muted">No recent registrations</td></tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; margin-bottom: 1rem; }
    .filter-bar { padding: 16px; }
    .form-row { display: flex; gap: 16px; align-items: flex-end; }
    .form-group { display: flex; flex-direction: column; gap: 4px; flex: 1; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); }
    .report-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .full-width { grid-column: 1 / -1; }
    .report-header { padding: 12px 16px; border-bottom: 1px solid var(--border-color); }
    .report-header h3 { margin: 0; font-size: 1rem; color: var(--text-primary); }
    .report-body { padding: 16px; }
    .report-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-color); }
    .report-row:last-child { border-bottom: none; }
    .report-label { font-size: 0.875rem; color: var(--text-secondary); }
    .report-value { font-size: 1rem; font-weight: 600; color: var(--text-primary); }
    .text-warning { color: #d97706; }
    .text-success { color: #059669; }
    .text-danger { color: #dc2626; }
    .dist-item { margin-bottom: 12px; }
    .dist-label { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
    .dist-count { font-size: 0.875rem; font-weight: 600; color: var(--text-primary); }
    .dist-bar { height: 8px; background: var(--bg-secondary); border-radius: 4px; overflow: hidden; }
    .dist-fill { height: 100%; border-radius: 4px; transition: width 0.3s; }
    .dist-percent { font-size: 0.75rem; color: var(--text-muted); }
    .table-responsive { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid var(--border-color); font-size: 0.875rem; }
    .data-table th { background: var(--bg-secondary); font-weight: 600; color: var(--text-secondary); }
    .data-table tr:hover { background: var(--bg-secondary); }
    .badge { padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #d1fae5; color: #065f46; }
    .badge-warning { background: #fef3c7; color: #92400e; }
    .badge-danger { background: #fee2e2; color: #991b1b; }
    .badge-secondary { background: #e5e7eb; color: #374151; }
    .text-center { text-align: center; }
    .text-muted { color: var(--text-muted); }
  `]
})
export class RegistrationReportsComponent implements OnInit {
  semesters: any[] = [];
  semesterId: number | null = null;
  dashboard: any = null;

  constructor(
    private registrationService: RegistrationService,
    private semesterService: SemesterService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.semesterService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.semesters = data.content || []; },
      error: () => {}
    });
  }

  loadData() {
    if (!this.semesterId) return;
    this.registrationService.getDashboardStats(this.semesterId).subscribe({
      next: (data) => { this.dashboard = data; },
      error: () => this.toastService.error('Failed to load report data')
    });
  }

  getBarWidth(count: number): number {
    if (!this.dashboard || this.dashboard.totalRegistrations === 0) return 0;
    return (count / this.dashboard.totalRegistrations) * 100;
  }

  getPercent(count: number): number {
    if (!this.dashboard || this.dashboard.totalRegistrations === 0) return 0;
    return Math.round((count / this.dashboard.totalRegistrations) * 100);
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'REGISTERED': return 'success';
      case 'APPROVED': return 'success';
      case 'SELECTED': return 'warning';
      case 'PENDING': return 'warning';
      case 'DROPPED': return 'danger';
      default: return 'secondary';
    }
  }

  getBarColor(status: string): string {
    switch (status) {
      case 'REGISTERED': return '#059669';
      case 'APPROVED': return '#10b981';
      case 'SELECTED': return '#f59e0b';
      case 'PENDING': return '#d97706';
      case 'DROPPED': return '#ef4444';
      default: return '#6b7280';
    }
  }
}
