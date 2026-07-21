import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SemesterEnrollmentService } from '../../../services/semester-enrollment.service';
import { SemesterService } from '../../../services/semester.service';
import { SemesterEnrollment } from '../../../models/semester-enrollment';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-advisor-approval',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>

    <div class="page-header">
      <div>
        <h2>Semester Enrollment - Advisor Approval</h2>
        <p class="page-sub">Review and approve pending student enrollments</p>
      </div>
    </div>

    <div class="card filter-card">
      <div class="filter-row">
        <div class="form-group">
          <label>Semester</label>
          <select [(ngModel)]="selectedSemesterId" name="semester" class="form-control" (change)="loadPendingApprovals()">
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
          <p>Loading pending enrollments...</p>
        </div>
      </div>
    }

    @if (!loading && enrollments.length === 0 && selectedSemesterId) {
      <div class="card empty-state">
        <div class="empty-icon">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/>
            <rect x="9" y="3" width="6" height="4" rx="1"/>
          </svg>
        </div>
        <p>No pending enrollments found</p>
      </div>
    }

    @if (enrollments.length > 0) {
      <div class="card table-card">
        <div class="card-header">
          <h3>Pending Approvals ({{ enrollments.length }})</h3>
        </div>
        <div class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>Enrollment #</th>
                <th>Student</th>
                <th>Credits</th>
                <th>Type</th>
                <th>Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (enrollment of enrollments; track enrollment.id) {
                <tr>
                  <td class="text-brand">{{ enrollment.enrollmentNumber }}</td>
                  <td>
                    <div class="student-info">
                      <span class="student-name">{{ enrollment.studentName }}</span>
                      <span class="student-code">{{ enrollment.studentCode }}</span>
                    </div>
                  </td>
                  <td>{{ enrollment.registeredCredits }}</td>
                  <td>
                    <span class="type-badge" [class]="'type-' + (enrollment.enrollmentType || 'regular').toLowerCase()">
                      {{ enrollment.enrollmentType || 'Regular' }}
                    </span>
                  </td>
                  <td>{{ enrollment.enrollmentDate | date:'mediumDate' }}</td>
                  <td>
                    <span class="badge" [class]="getStatusBadgeClass(enrollment.advisorStatus || enrollment.status)">
                      {{ enrollment.advisorStatus || enrollment.status }}
                    </span>
                  </td>
                  <td>
                    <div class="action-buttons">
                      <button class="btn btn-success btn-sm" (click)="openApprovalModal(enrollment, 'APPROVED')" [disabled]="processingId === enrollment.id">
                        Approve
                      </button>
                      <button class="btn btn-danger btn-sm" (click)="openApprovalModal(enrollment, 'REJECTED')" [disabled]="processingId === enrollment.id">
                        Reject
                      </button>
                    </div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    }

    @if (showApprovalModal) {
      <div class="modal-overlay" (click)="closeModal()">
        <div class="modal-box" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ approvalAction === 'APPROVED' ? 'Approve' : 'Reject' }} Enrollment</h3>
            <button class="modal-close" (click)="closeModal()">&times;</button>
          </div>
          <div class="modal-body">
            <div class="enrollment-summary">
              <div class="summary-row">
                <span class="label">Enrollment:</span>
                <span class="value">{{ selectedEnrollment?.enrollmentNumber }}</span>
              </div>
              <div class="summary-row">
                <span class="label">Student:</span>
                <span class="value">{{ selectedEnrollment?.studentName }} ({{ selectedEnrollment?.studentCode }})</span>
              </div>
              <div class="summary-row">
                <span class="label">Credits:</span>
                <span class="value">{{ selectedEnrollment?.registeredCredits }}</span>
              </div>
              <div class="summary-row">
                <span class="label">Type:</span>
                <span class="value">{{ selectedEnrollment?.enrollmentType || 'Regular' }}</span>
              </div>
            </div>
            <div class="form-group">
              <label>Comments *</label>
              <textarea [(ngModel)]="approvalComments" name="comments" class="form-control" rows="3"
                [placeholder]="approvalAction === 'APPROVED' ? 'Optional approval comments...' : 'Reason for rejection...'"></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" (click)="closeModal()">Cancel</button>
            <button class="btn" [class]="approvalAction === 'APPROVED' ? 'btn-success' : 'btn-danger'"
              (click)="submitApproval()" [disabled]="submittingApproval || !approvalComments.trim()">
              {{ submittingApproval ? 'Processing...' : (approvalAction === 'APPROVED' ? 'Confirm Approval' : 'Confirm Rejection') }}
            </button>
          </div>
        </div>
      </div>
    }

    <app-confirm-dialog [open]="showCancelConfirm" title="Confirm Action" [message]="'Are you sure you want to ' + pendingConfirmAction + ' this enrollment?'" [confirmText]="pendingConfirmAction === 'approve' ? 'Approve' : 'Reject'" [type]="pendingConfirmAction === 'approve' ? 'info' : 'danger'" (confirmed)="confirmAction()" (cancelled)="showCancelConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; margin-bottom: 1.25rem; }
    .filter-card { padding: 20px; }
    .filter-row { display: flex; gap: 16px; align-items: flex-end; }
    .filter-row .form-group { flex: 1; min-width: 200px; max-width: 300px; }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    textarea.form-control { resize: vertical; min-height: 60px; }
    .loading-state { display: flex; flex-direction: column; align-items: center; padding: 40px 20px; gap: 12px; }
    .spinner { width: 32px; height: 32px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .loading-state p { color: var(--text-muted); font-size: 0.875rem; margin: 0; }
    .empty-state { padding: 60px 20px; text-align: center; }
    .empty-icon { color: var(--text-muted); margin-bottom: 12px; }
    .empty-state p { color: var(--text-muted); font-size: 0.9375rem; margin: 0; }
    .table-card .card-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid var(--border-color); }
    .table-card .card-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .table-wrapper { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th { padding: 12px 16px; text-align: left; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; color: var(--text-muted); border-bottom: 1px solid var(--border-color); background: var(--bg-secondary); }
    .data-table td { padding: 12px 16px; font-size: 0.875rem; color: var(--text-primary); border-bottom: 1px solid var(--border-color); }
    .data-table tbody tr:hover { background: var(--bg-secondary); }
    .text-brand { color: var(--brand-color); font-weight: 500; }
    .student-info { display: flex; flex-direction: column; }
    .student-name { font-weight: 500; }
    .student-code { font-size: 0.75rem; color: var(--text-muted); }
    .type-badge { padding: 3px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 500; }
    .type-regular { background: #e0f2fe; color: #0369a1; }
    .type-late { background: #fef3c7; color: #92400e; }
    .type-special { background: #ede9fe; color: #5b21b6; }
    .badge { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #d1fae5; color: #065f46; }
    .badge-warning { background: #fef3c7; color: #92400e; }
    .badge-danger { background: #fee2e2; color: #991b1b; }
    .badge-secondary { background: #e5e7eb; color: #374151; }
    .action-buttons { display: flex; gap: 6px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-sm { padding: 5px 12px; font-size: 0.8125rem; }
    .btn-success { background: #10b981; color: #fff; }
    .btn-success:hover { background: #059669; }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-danger:hover { background: #dc2626; }
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .btn-secondary:hover { background: #d1d5db; }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000; backdrop-filter: blur(4px); }
    .modal-box { background: var(--card-bg); border-radius: 16px; width: 90%; max-width: 500px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); animation: popIn 0.2s ease-out; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid var(--border-color); }
    .modal-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .modal-close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--text-muted); padding: 0; line-height: 1; }
    .modal-close:hover { color: var(--text-primary); }
    .modal-body { padding: 24px; }
    .enrollment-summary { margin-bottom: 16px; background: var(--bg-secondary); border-radius: 8px; padding: 12px 16px; }
    .summary-row { display: flex; justify-content: space-between; padding: 6px 0; }
    .summary-row .label { font-size: 0.875rem; color: var(--text-muted); }
    .summary-row .value { font-size: 0.875rem; font-weight: 500; color: var(--text-primary); }
    .modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 24px; border-top: 1px solid var(--border-color); }
    @keyframes popIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
  `]
})
export class AdvisorApprovalComponent implements OnInit {
  semesters: any[] = [];
  enrollments: SemesterEnrollment[] = [];
  selectedSemesterId: string = '';
  loading = false;
  processingId: number | null = null;
  showApprovalModal = false;
  selectedEnrollment: SemesterEnrollment | null = null;
  approvalAction: string = '';
  approvalComments = '';
  submittingApproval = false;
  showCancelConfirm = false;
  pendingConfirmAction = '';

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

  loadPendingApprovals() {
    if (!this.selectedSemesterId) {
      this.enrollments = [];
      return;
    }

    this.loading = true;
    this.enrollmentService.getPendingApprovals(parseInt(this.selectedSemesterId, 10)).subscribe({
      next: (data) => {
        this.enrollments = (data || []).filter(e => (e.advisorStatus || '').toUpperCase() === 'PENDING' || (!e.advisorStatus && e.status === 'Pending'));
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load pending enrollments');
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'APPROVED': return 'badge badge-success';
      case 'PENDING': return 'badge badge-warning';
      case 'REJECTED': return 'badge badge-danger';
      default: return 'badge badge-secondary';
    }
  }

  openApprovalModal(enrollment: SemesterEnrollment, action: string) {
    this.selectedEnrollment = enrollment;
    this.approvalAction = action;
    this.approvalComments = '';
    this.showApprovalModal = true;
  }

  closeModal() {
    this.showApprovalModal = false;
    this.selectedEnrollment = null;
    this.approvalComments = '';
  }

  submitApproval() {
    if (!this.selectedEnrollment || !this.approvalComments.trim()) return;

    this.submittingApproval = true;

    const payload = {
      enrollmentId: this.selectedEnrollment.id!,
      action: this.approvalAction,
      comments: this.approvalComments
    };

    this.enrollmentService.processApproval(payload).subscribe({
      next: () => {
        this.toastService.success(`Enrollment ${this.approvalAction.toLowerCase()} successfully`);
        this.submittingApproval = false;
        this.closeModal();
        this.loadPendingApprovals();
      },
      error: (err) => {
        this.submittingApproval = false;
        this.toastService.error(err.error?.message || `Failed to ${this.approvalAction.toLowerCase()} enrollment`);
      }
    });
  }

  confirmAction() {
    this.showCancelConfirm = false;
  }
}
