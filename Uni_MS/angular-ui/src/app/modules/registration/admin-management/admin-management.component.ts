import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RegistrationService } from '../../../services/registration.service';
import { SemesterService } from '../../../services/semester.service';
import { RegistrationDashboard } from '../../../models/registration';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-admin-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Registration Management</h2>
        <p class="page-sub">Admin panel for managing course registrations</p>
      </div>
    </div>

    <div class="filter-bar card">
      <div class="form-row">
        <div class="form-group">
          <label>Semester *</label>
          <select [(ngModel)]="semesterId" class="form-control" (change)="loadDashboard()">
            <option value="">Select Semester</option>
            @for (sem of semesters; track sem.id) {
              <option [ngValue]="sem.id">{{ sem.name }}</option>
            }
          </select>
        </div>
      </div>
    </div>

    @if (dashboard) {
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon" style="background: #dbeafe; color: #2563eb;">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
          </div>
          <div class="stat-info">
            <div class="stat-label">Total Registrations</div>
            <div class="stat-value">{{ dashboard.totalRegistrations }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: #fef3c7; color: #d97706;">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="stat-info">
            <div class="stat-label">Pending Approvals</div>
            <div class="stat-value">{{ dashboard.pendingApprovals }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: #d1fae5; color: #059669;">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
          </div>
          <div class="stat-info">
            <div class="stat-label">Approved</div>
            <div class="stat-value">{{ dashboard.approvedRegistrations }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: #fee2e2; color: #dc2626;">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/></svg>
          </div>
          <div class="stat-info">
            <div class="stat-label">Dropped</div>
            <div class="stat-value">{{ dashboard.droppedRegistrations }}</div>
          </div>
        </div>
      </div>

      <div class="content-grid">
        <div class="card">
          <div class="card-header"><h3>Status Breakdown</h3></div>
          <div class="card-body">
            @for (stat of dashboard.statusBreakdown; track stat.status) {
              <div class="breakdown-item">
                <span class="breakdown-label">{{ stat.status }}</span>
                <div class="breakdown-bar">
                  <div class="breakdown-fill" [style.width]="getBarWidth(stat.count) + '%'"></div>
                </div>
                <span class="breakdown-count">{{ stat.count }}</span>
              </div>
            }
          </div>
        </div>

        <div class="card">
          <div class="card-header"><h3>Recent Registrations</h3></div>
          <div class="table-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Student</th>
                  <th>Course</th>
                  <th>Credits</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                @for (reg of dashboard.recentRegistrations; track reg.id) {
                  <tr>
                    <td>
                      <div>{{ reg.studentName }}</div>
                      <div class="text-muted text-sm">{{ reg.studentCode }}</div>
                    </td>
                    <td>{{ reg.courseName }}</td>
                    <td>{{ reg.creditHours }}</td>
                    <td><span class="badge badge-{{ getStatusColor(reg.status) }}">{{ reg.status }}</span></td>
                  </tr>
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
    .card-header { padding: 12px 16px; border-bottom: 1px solid var(--border-color); }
    .card-header h3 { margin: 0; font-size: 1rem; color: var(--text-primary); }
    .card-body { padding: 16px; }
    .filter-bar { padding: 16px; }
    .form-row { display: flex; gap: 16px; align-items: flex-end; }
    .form-group { display: flex; flex-direction: column; gap: 4px; flex: 1; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 1rem; }
    .stat-card { background: var(--card-bg); border-radius: 12px; padding: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); display: flex; align-items: center; gap: 12px; }
    .stat-icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
    .stat-label { font-size: 0.875rem; color: var(--text-muted); }
    .stat-value { font-size: 1.5rem; font-weight: 700; color: var(--text-primary); }
    .content-grid { display: grid; grid-template-columns: 1fr 2fr; gap: 1rem; }
    .breakdown-item { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
    .breakdown-label { width: 100px; font-size: 0.875rem; color: var(--text-secondary); }
    .breakdown-bar { flex: 1; height: 8px; background: var(--bg-secondary); border-radius: 4px; overflow: hidden; }
    .breakdown-fill { height: 100%; background: var(--brand-color); border-radius: 4px; transition: width 0.3s; }
    .breakdown-count { font-size: 0.875rem; font-weight: 600; color: var(--text-primary); width: 40px; text-align: right; }
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
    .text-muted { color: var(--text-muted); }
    .text-sm { font-size: 0.75rem; }
  `]
})
export class AdminManagementComponent implements OnInit {
  semesters: any[] = [];
  semesterId: number | null = null;
  dashboard: RegistrationDashboard | null = null;

  constructor(
    private registrationService: RegistrationService,
    private semesterService: SemesterService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.semesterService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.semesters = data.content || []; },
      error: () => this.toastService.error('Operation failed. Please try again.')
    });
  }

  loadDashboard() {
    if (!this.semesterId) return;
    this.registrationService.getDashboardStats(this.semesterId).subscribe({
      next: (data) => { this.dashboard = data; },
      error: () => this.toastService.error('Failed to load dashboard')
    });
  }

  getBarWidth(count: number): number {
    if (!this.dashboard || this.dashboard.totalRegistrations === 0) return 0;
    return (count / this.dashboard.totalRegistrations) * 100;
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
}
