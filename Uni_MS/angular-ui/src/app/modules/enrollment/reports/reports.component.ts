import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SemesterEnrollmentService } from '../../../services/semester-enrollment.service';
import { SemesterService } from '../../../services/semester.service';
import { FacultyService } from '../../../services/faculty.service';
import { DepartmentService } from '../../../services/department.service';
import { ProgramService } from '../../../services/program.service';
import { SemesterEnrollment } from '../../../models/semester-enrollment';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-enrollment-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Semester Enrollment Reports</h2>
        <p class="page-sub">Generate and export enrollment reports</p>
      </div>
    </div>

    <div class="card filter-card">
      <div class="card-header">
        <h3>Report Filters</h3>
      </div>
      <div class="filter-body">
        <div class="filter-grid">
          <div class="form-group">
            <label>Semester</label>
            <select [(ngModel)]="filters.semesterId" name="semesterId" class="form-control">
              <option value="">All Semesters</option>
              @for (sem of semesters; track sem.id) {
                <option [ngValue]="sem.id">{{ sem.name }}</option>
              }
            </select>
          </div>
          <div class="form-group">
            <label>Faculty</label>
            <select [(ngModel)]="filters.facultyId" name="facultyId" class="form-control">
              <option value="">All Faculties</option>
              @for (fac of faculties; track fac.id) {
                <option [ngValue]="fac.id">{{ fac.name }}</option>
              }
            </select>
          </div>
          <div class="form-group">
            <label>Department</label>
            <select [(ngModel)]="filters.departmentId" name="departmentId" class="form-control">
              <option value="">All Departments</option>
              @for (dept of departments; track dept.id) {
                <option [ngValue]="dept.id">{{ dept.name }}</option>
              }
            </select>
          </div>
          <div class="form-group">
            <label>Program</label>
            <select [(ngModel)]="filters.programId" name="programId" class="form-control">
              <option value="">All Programs</option>
              @for (prog of programs; track prog.id) {
                <option [ngValue]="prog.id">{{ prog.name }}</option>
              }
            </select>
          </div>
          <div class="form-group">
            <label>Status</label>
            <select [(ngModel)]="filters.status" name="status" class="form-control">
              <option value="">All Statuses</option>
              <option value="Draft">Draft</option>
              <option value="Pending">Pending</option>
              <option value="Approved">Approved</option>
              <option value="Completed">Completed</option>
              <option value="Rejected">Rejected</option>
              <option value="Cancelled">Cancelled</option>
            </select>
          </div>
          <div class="form-group btn-group-align">
            <button class="btn btn-primary" (click)="generateReport()" [disabled]="generating">
              {{ generating ? 'Generating...' : 'Generate Report' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    @if (generating) {
      <div class="card">
        <div class="loading-state">
          <div class="spinner"></div>
          <p>Generating report...</p>
        </div>
      </div>
    }

    @if (reportGenerated && !generating) {
      <div class="summary-grid">
        <div class="summary-card total">
          <span class="summary-number">{{ enrollments.length }}</span>
          <span class="summary-label">Total</span>
        </div>
        <div class="summary-card approved">
          <span class="summary-number">{{ getCountByStatus('Approved') }}</span>
          <span class="summary-label">Approved</span>
        </div>
        <div class="summary-card pending">
          <span class="summary-number">{{ getCountByStatus('Pending') }}</span>
          <span class="summary-label">Pending</span>
        </div>
        <div class="summary-card rejected">
          <span class="summary-number">{{ getCountByStatus('Rejected') }}</span>
          <span class="summary-label">Rejected</span>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3>Report Results ({{ enrollments.length }} records)</h3>
          <div class="export-actions">
            <button class="btn btn-secondary" (click)="printReport()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <polyline points="6 9 6 2 18 2 18 9"></polyline>
                <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                <rect x="6" y="14" width="12" height="8"></rect>
              </svg>
              Print
            </button>
            <button class="btn btn-secondary" (click)="downloadPDF()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
              Download PDF
            </button>
            <button class="btn btn-secondary" (click)="downloadExcel()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="8" y1="13" x2="16" y2="13"></line>
                <line x1="8" y1="17" x2="16" y2="17"></line>
              </svg>
              Download Excel
            </button>
          </div>
        </div>
        <div class="card-body">
          @if (enrollments.length === 0) {
            <div class="empty-state">
              <p>No enrollment records found for the selected filters</p>
            </div>
          } @else {
            <div class="table-responsive">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>Enrollment #</th>
                    <th>Student Name</th>
                    <th>Student Code</th>
                    <th>Program</th>
                    <th>Department</th>
                    <th>Faculty</th>
                    <th>Status</th>
                    <th>Advisor Status</th>
                    <th>Payment Status</th>
                    <th>Credits</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  @for (e of enrollments; track e.id) {
                    <tr>
                      <td class="enrollment-number">{{ e.enrollmentNumber }}</td>
                      <td>{{ e.studentName }}</td>
                      <td>{{ e.studentCode }}</td>
                      <td>{{ e.programName }}</td>
                      <td>{{ e.departmentName }}</td>
                      <td>{{ e.facultyName }}</td>
                      <td>
                        <span class="status-badge" [ngClass]="getStatusClass(e.status)">{{ e.status }}</span>
                      </td>
                      <td>
                        <span class="status-badge" [ngClass]="getAdvisorStatusClass(e.advisorStatus)">{{ e.advisorStatus || 'N/A' }}</span>
                      </td>
                      <td>
                        <span class="status-badge" [ngClass]="getPaymentStatusClass(e.paymentStatus)">{{ e.paymentStatus || 'N/A' }}</span>
                      </td>
                      <td>{{ e.registeredCredits }}</td>
                      <td>{{ e.createdAt | date:'mediumDate' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; flex-wrap: wrap; gap: 12px; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; margin-bottom: 1.25rem; }
    .card-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid var(--border-color); }
    .card-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .card-body { padding: 16px 20px; }
    .filter-body { padding: 20px; }
    .filter-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; align-items: flex-end; }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    .btn-group-align { display: flex; align-items: flex-end; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .btn-secondary:hover { background: #d1d5db; }
    .loading-state { display: flex; flex-direction: column; align-items: center; padding: 40px 20px; gap: 12px; }
    .spinner { width: 32px; height: 32px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .loading-state p { color: var(--text-muted); font-size: 0.875rem; margin: 0; }
    .empty-state { display: flex; flex-direction: column; align-items: center; padding: 40px 20px; gap: 12px; }
    .empty-state p { color: var(--text-muted); font-size: 0.875rem; margin: 0; }
    .summary-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 1.25rem; }
    .summary-card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); padding: 20px; text-align: center; }
    .summary-card.total { border-left: 4px solid var(--brand-color); }
    .summary-card.approved { border-left: 4px solid #16a34a; }
    .summary-card.pending { border-left: 4px solid #d97706; }
    .summary-card.rejected { border-left: 4px solid #dc2626; }
    .summary-number { display: block; font-size: 1.75rem; font-weight: 700; color: var(--text-primary); line-height: 1.2; }
    .summary-label { font-size: 0.8rem; color: var(--text-muted); margin-top: 4px; }
    .export-actions { display: flex; gap: 8px; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th { padding: 10px 12px; text-align: left; font-size: 0.8rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid var(--border-color); background: var(--bg-secondary); white-space: nowrap; }
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
    .enrollment-number { font-weight: 600; color: var(--brand-color); }
    .table-responsive { overflow-x: auto; }
    @media (max-width: 1200px) { .filter-grid { grid-template-columns: repeat(2, 1fr); } .summary-grid { grid-template-columns: repeat(2, 1fr); } }
    @media (max-width: 600px) { .filter-grid { grid-template-columns: 1fr; } .summary-grid { grid-template-columns: 1fr; } }
    @media print {
      .page-header, .filter-card, .export-actions, .btn { display: none !important; }
      .card { box-shadow: none; border: 1px solid #ddd; }
      .card-body { padding: 0; }
      .data-table th { background: #f5f5f5 !important; }
      body { background: #fff; }
    }
  `]
})
export class ReportsComponent implements OnInit {
  semesters: any[] = [];
  faculties: any[] = [];
  departments: any[] = [];
  programs: any[] = [];
  enrollments: SemesterEnrollment[] = [];
  generating = false;
  reportGenerated = false;

  filters = {
    semesterId: '' as string | number,
    facultyId: '' as string | number,
    departmentId: '' as string | number,
    programId: '' as string | number,
    status: ''
  };

  constructor(
    private enrollmentService: SemesterEnrollmentService,
    private semesterService: SemesterService,
    private facultyService: FacultyService,
    private departmentService: DepartmentService,
    private programService: ProgramService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadDropdowns();
  }

  loadDropdowns() {
    this.semesterService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.semesters = data.content || []; }
    });
    this.facultyService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.faculties = data.content || []; }
    });
    this.departmentService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.departments = data.content || []; }
    });
    this.programService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.programs = data.content || []; }
    });
  }

  generateReport() {
    this.generating = true;
    this.reportGenerated = false;

    const filters: any = {};
    if (this.filters.semesterId) filters.semesterId = parseInt(String(this.filters.semesterId), 10);
    if (this.filters.facultyId) filters.facultyId = parseInt(String(this.filters.facultyId), 10);
    if (this.filters.departmentId) filters.departmentId = parseInt(String(this.filters.departmentId), 10);
    if (this.filters.programId) filters.programId = parseInt(String(this.filters.programId), 10);
    if (this.filters.status) filters.status = this.filters.status;

    this.enrollmentService.findAll({ page: 0, size: 500, sortBy: 'id', sortDir: 'desc' }, filters).subscribe({
      next: (data) => {
        this.enrollments = data.content || [];
        this.generating = false;
        this.reportGenerated = true;
        this.toastService.success('Report generated successfully');
      },
      error: () => {
        this.generating = false;
        this.toastService.error('Failed to generate report');
      }
    });
  }

  getCountByStatus(status: string): number {
    return this.enrollments.filter(e => e.status === status).length;
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

  printReport() {
    window.print();
  }

  downloadPDF() {
    this.toastService.info('PDF export will be available soon');
  }

  downloadExcel() {
    this.toastService.info('Excel export will be available soon');
  }
}
