import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RegistrationService } from '../../../services/registration.service';
import { RegistrationConfigService } from '../../../services/registration-config.service';
import { SemesterService } from '../../../services/semester.service';
import { StudentService } from '../../../services/student.service';
import { SubjectService } from '../../../services/subject.service';
import { RegistrationSummary, RegisteredCourseItem, RegistrationConfig } from '../../../models/registration';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-course-selection',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Course Selection</h2>
        <p class="page-sub">Select courses for registration</p>
      </div>
    </div>

    <div class="selection-form card">
      <div class="form-row">
        <div class="form-group">
          <label>Student ID *</label>
          <input type="number" [(ngModel)]="studentId" class="form-control" placeholder="Enter Student ID">
        </div>
        <div class="form-group">
          <label>Semester *</label>
          <select [(ngModel)]="semesterId" class="form-control">
            <option value="">Select Semester</option>
            @for (sem of semesters; track sem.id) {
              <option [ngValue]="sem.id">{{ sem.name }}</option>
            }
          </select>
        </div>
        <div class="form-group" style="justify-content: flex-end;">
          <button class="btn btn-primary" (click)="loadSummary()" [disabled]="!studentId || !semesterId">
            Load Summary
          </button>
        </div>
      </div>
    </div>

    @if (summary) {
      <div class="summary-cards">
        <div class="stat-card">
          <div class="stat-label">Credits Registered</div>
          <div class="stat-value">{{ summary.totalCreditsRegistered }} / {{ summary.maxCreditsAllowed }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">Status</div>
          <div class="stat-value">
            <span class="badge badge-{{ summary.registrationStatus === 'REGISTERED' ? 'success' : 'warning' }}">
              {{ summary.registrationStatus || 'N/A' }}
            </span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-label">Advisor Approval</div>
          <div class="stat-value">
            <span class="badge badge-{{ summary.advisorApprovalStatus === 'APPROVED' ? 'success' : 'warning' }}">
              {{ summary.advisorApprovalStatus || 'Pending' }}
            </span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-label">Payment</div>
          <div class="stat-value">
            <span class="badge badge-{{ summary.paymentStatus === 'PAID' ? 'success' : 'danger' }}">
              {{ summary.paymentStatus || 'Pending' }}
            </span>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3>Selected Courses</h3>
        </div>
        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Name</th>
                <th>Credits</th>
                <th>Status</th>
                <th>Advisor</th>
                <th>Payment</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (course of summary.registeredCourses; track course.registrationId) {
                <tr>
                  <td>{{ course.subjectCode }}</td>
                  <td>{{ course.subjectName }}</td>
                  <td>{{ course.creditHours }}</td>
                  <td><span class="badge badge-{{ getStatusColor(course.status) }}">{{ course.status }}</span></td>
                  <td><span class="badge badge-{{ getAdvisorColor(course.advisorStatus) }}">{{ course.advisorStatus || 'Pending' }}</span></td>
                  <td><span class="badge badge-{{ course.paymentStatus === 'PAID' ? 'success' : 'danger' }}">{{ course.paymentStatus || 'Pending' }}</span></td>
                  <td>
                    @if (course.status === 'SELECTED' || course.status === 'PENDING') {
                      <button class="btn btn-danger btn-sm" (click)="dropCourse(course.registrationId)">Drop</button>
                    }
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="7" class="text-center text-muted">No courses selected yet</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>

      @if (summary.errors.length) {
        <div class="errors card">
          <h4>Errors</h4>
          @for (error of summary.errors; track $index) {
            <div class="error-item">{{ error }}</div>
          }
        </div>
      }
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-sm { padding: 4px 10px; font-size: 0.75rem; }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; margin-bottom: 1rem; }
    .card-header { padding: 12px 16px; border-bottom: 1px solid var(--border-color); }
    .card-header h3 { margin: 0; font-size: 1rem; color: var(--text-primary); }
    .selection-form { padding: 16px; }
    .form-row { display: flex; gap: 16px; align-items: flex-end; }
    .form-group { display: flex; flex-direction: column; gap: 4px; flex: 1; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); }
    .summary-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 1rem; }
    .stat-card { background: var(--card-bg); border-radius: 12px; padding: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
    .stat-label { font-size: 0.875rem; color: var(--text-muted); margin-bottom: 4px; }
    .stat-value { font-size: 1.25rem; font-weight: 700; color: var(--text-primary); }
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
    .errors { padding: 16px; }
    .errors h4 { margin: 0 0 8px; color: #dc2626; }
    .error-item { padding: 8px; background: #fef2f2; border-radius: 6px; margin-bottom: 4px; font-size: 0.875rem; color: #991b1b; }
  `]
})
export class CourseSelectionComponent implements OnInit {
  studentId: number | null = null;
  semesterId: number | null = null;
  summary: RegistrationSummary | null = null;
  semesters: any[] = [];
  loading = false;

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

  loadSummary() {
    if (!this.studentId || !this.semesterId) return;
    this.loading = true;
    this.registrationService.getRegistrationSummary(this.studentId, this.semesterId).subscribe({
      next: (data) => { this.summary = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load summary'); }
    });
  }

  dropCourse(registrationId: number) {
    if (!confirm('Are you sure you want to drop this course?')) return;
    this.registrationService.dropCourse(registrationId).subscribe({
      next: () => { this.loadSummary(); this.toastService.success('Course dropped'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to drop course')
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'REGISTERED': return 'success';
      case 'APPROVED': return 'success';
      case 'SELECTED': return 'warning';
      case 'PENDING': return 'warning';
      case 'DROPPED': return 'danger';
      case 'REJECTED': return 'danger';
      default: return 'secondary';
    }
  }

  getAdvisorColor(status: string): string {
    switch (status) {
      case 'APPROVED': return 'success';
      case 'REJECTED': return 'danger';
      default: return 'warning';
    }
  }
}
