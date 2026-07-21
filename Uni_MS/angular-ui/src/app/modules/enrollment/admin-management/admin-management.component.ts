import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SemesterEnrollmentService } from '../../../services/semester-enrollment.service';
import { SemesterService } from '../../../services/semester.service';
import { FacultyService } from '../../../services/faculty.service';
import { DepartmentService } from '../../../services/department.service';
import { ProgramService } from '../../../services/program.service';
import { SemesterEnrollment } from '../../../models/semester-enrollment';
import { PagedResponse, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>

    <div class="page-header">
      <div>
        <h2>Semester Enrollment - Admin Management</h2>
        <p class="page-sub">Manage all semester enrollments across departments</p>
      </div>
      <button class="btn btn-primary" (click)="showForceEnrollForm = true">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Force Enroll
      </button>
    </div>

    <div class="card filter-card">
      <div class="filter-grid">
        <div class="form-group">
          <label>Semester</label>
          <select [(ngModel)]="filters.semesterId" name="semester" class="form-control">
            <option value="">All Semesters</option>
            @for (sem of semesters; track sem.id) {
              <option [ngValue]="sem.id">{{ sem.name }}</option>
            }
          </select>
        </div>
        <div class="form-group">
          <label>Faculty</label>
          <select [(ngModel)]="filters.facultyId" name="faculty" class="form-control" (change)="onFacultyChange()">
            <option value="">All Faculties</option>
            @for (fac of faculties; track fac.id) {
              <option [ngValue]="fac.id">{{ fac.name }}</option>
            }
          </select>
        </div>
        <div class="form-group">
          <label>Department</label>
          <select [(ngModel)]="filters.departmentId" name="department" class="form-control">
            <option value="">All Departments</option>
            @for (dept of departments; track dept.id) {
              <option [ngValue]="dept.id">{{ dept.name }}</option>
            }
          </select>
        </div>
        <div class="form-group">
          <label>Program</label>
          <select [(ngModel)]="filters.programId" name="program" class="form-control">
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
            <option value="Rejected">Rejected</option>
            <option value="Cancelled">Cancelled</option>
            <option value="Completed">Completed</option>
          </select>
        </div>
        <div class="form-group btn-group-align">
          <button class="btn btn-primary" (click)="loadEnrollments()" [disabled]="loading">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            Search
          </button>
        </div>
      </div>
    </div>

    @if (loading) {
      <div class="card">
        <div class="loading-state">
          <div class="spinner"></div>
          <p>Loading enrollments...</p>
        </div>
      </div>
    }

    @if (!loading && pagedData && pagedData.content.length === 0) {
      <div class="card empty-state">
        <div class="empty-icon">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/>
            <rect x="9" y="3" width="6" height="4" rx="1"/>
          </svg>
        </div>
        <p>No enrollments found matching your filters</p>
      </div>
    }

    @if (pagedData && pagedData.content.length > 0) {
      <div class="card table-card">
        <div class="card-header">
          <h3>Enrollments ({{ pagedData.totalElements }})</h3>
          <div class="header-info">
            <span class="page-info">Page {{ pagedData.page + 1 }} of {{ pagedData.totalPages }}</span>
          </div>
        </div>
        <div class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>Enrollment #</th>
                <th>Student</th>
                <th>Semester</th>
                <th>Credits</th>
                <th>Status</th>
                <th>Advisor Status</th>
                <th>Payment Status</th>
                <th>Type</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (enrollment of pagedData.content; track enrollment.id) {
                <tr>
                  <td class="text-brand">{{ enrollment.enrollmentNumber }}</td>
                  <td>
                    <div class="student-info">
                      <span class="student-name">{{ enrollment.studentName }}</span>
                      <span class="student-code">{{ enrollment.studentCode }}</span>
                    </div>
                  </td>
                  <td>{{ enrollment.semesterName }}</td>
                  <td>{{ enrollment.registeredCredits }}</td>
                  <td>
                    <span class="badge" [class]="getStatusBadgeClass(enrollment.status)">{{ enrollment.status }}</span>
                  </td>
                  <td>
                    <span class="badge" [class]="getStatusBadgeClass(enrollment.advisorStatus || 'N/A')">
                      {{ enrollment.advisorStatus || 'N/A' }}
                    </span>
                  </td>
                  <td>
                    <span class="badge" [class]="getPaymentBadgeClass(enrollment.paymentStatus || 'N/A')">
                      {{ enrollment.paymentStatus || 'N/A' }}
                    </span>
                  </td>
                  <td>
                    <span class="type-badge" [class]="'type-' + (enrollment.enrollmentType || 'regular').toLowerCase()">
                      {{ enrollment.enrollmentType || 'Regular' }}
                    </span>
                  </td>
                  <td>
                    <div class="action-buttons">
                      <button class="btn btn-icon" title="View Details" (click)="openDetailsModal(enrollment)">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                      </button>
                      @if (enrollment.status !== 'Cancelled' && enrollment.status !== 'Completed') {
                        <button class="btn btn-icon btn-icon-danger" title="Cancel" (click)="cancelEnrollment(enrollment)">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/></svg>
                        </button>
                      }
                      @if (enrollment.status === 'Cancelled') {
                        <button class="btn btn-icon btn-icon-warning" title="Reopen" (click)="reopenEnrollment(enrollment)">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
        <div class="pagination-bar">
          <button class="btn btn-secondary btn-sm" (click)="prevPage()" [disabled]="pagedData.first">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
            Prev
          </button>
          <span class="page-indicator">{{ pagedData.page + 1 }} / {{ pagedData.totalPages }}</span>
          <button class="btn btn-secondary btn-sm" (click)="nextPage()" [disabled]="pagedData.last">
            Next
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </button>
        </div>
      </div>
    }

    @if (showDetailsModal && selectedEnrollment) {
      <div class="modal-overlay" (click)="closeDetailsModal()">
        <div class="modal-box modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Enrollment Details</h3>
            <button class="modal-close" (click)="closeDetailsModal()">&times;</button>
          </div>
          <div class="modal-body">
            <div class="details-grid">
              <div class="detail-item">
                <span class="label">Enrollment Number</span>
                <span class="value text-brand">{{ selectedEnrollment.enrollmentNumber }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Status</span>
                <span class="badge" [class]="getStatusBadgeClass(selectedEnrollment.status)">{{ selectedEnrollment.status }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Student</span>
                <span class="value">{{ selectedEnrollment.studentName }} ({{ selectedEnrollment.studentCode }})</span>
              </div>
              <div class="detail-item">
                <span class="label">Email</span>
                <span class="value">{{ selectedEnrollment.studentEmail || 'N/A' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Semester</span>
                <span class="value">{{ selectedEnrollment.semesterName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Enrollment Date</span>
                <span class="value">{{ selectedEnrollment.enrollmentDate | date:'medium' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Program</span>
                <span class="value">{{ selectedEnrollment.programName || 'N/A' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Faculty</span>
                <span class="value">{{ selectedEnrollment.facultyName || 'N/A' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Department</span>
                <span class="value">{{ selectedEnrollment.departmentName || 'N/A' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Advisor</span>
                <span class="value">{{ selectedEnrollment.advisorName || 'N/A' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Batch</span>
                <span class="value">{{ selectedEnrollment.batchName || 'N/A' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Credits</span>
                <span class="value">{{ selectedEnrollment.registeredCredits }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Enrollment Type</span>
                <span class="type-badge" [class]="'type-' + (selectedEnrollment.enrollmentType || 'regular').toLowerCase()">
                  {{ selectedEnrollment.enrollmentType || 'Regular' }}
                </span>
              </div>
              <div class="detail-item">
                <span class="label">Late Enrollment</span>
                <span class="value">{{ selectedEnrollment.isLateEnrollment ? 'Yes' : 'No' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Advisor Status</span>
                <span class="badge" [class]="getStatusBadgeClass(selectedEnrollment.advisorStatus || 'N/A')">
                  {{ selectedEnrollment.advisorStatus || 'N/A' }}
                </span>
              </div>
              <div class="detail-item">
                <span class="label">Payment Status</span>
                <span class="badge" [class]="getPaymentBadgeClass(selectedEnrollment.paymentStatus || 'N/A')">
                  {{ selectedEnrollment.paymentStatus || 'N/A' }}
                </span>
              </div>
              @if (selectedEnrollment.paymentAmount) {
                <div class="detail-item">
                  <span class="label">Payment Amount</span>
                  <span class="value">{{ selectedEnrollment.paymentAmount | number:'1.2-2' }}</span>
                </div>
              }
              @if (selectedEnrollment.advisorComments) {
                <div class="detail-item full-width">
                  <span class="label">Advisor Comments</span>
                  <span class="value">{{ selectedEnrollment.advisorComments }}</span>
                </div>
              }
              @if (selectedEnrollment.remarks) {
                <div class="detail-item full-width">
                  <span class="label">Remarks</span>
                  <span class="value">{{ selectedEnrollment.remarks }}</span>
                </div>
              }
              @if (selectedEnrollment.cancellationReason) {
                <div class="detail-item full-width">
                  <span class="label">Cancellation Reason</span>
                  <span class="value text-danger">{{ selectedEnrollment.cancellationReason }}</span>
                </div>
              }
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" (click)="closeDetailsModal()">Close</button>
          </div>
        </div>
      </div>
    }

    @if (showCancelModal && enrollmentToCancel) {
      <div class="modal-overlay" (click)="closeCancelModal()">
        <div class="modal-box" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Cancel Enrollment</h3>
            <button class="modal-close" (click)="closeCancelModal()">&times;</button>
          </div>
          <div class="modal-body">
            <div class="enrollment-summary">
              <div class="summary-row">
                <span class="label">Enrollment:</span>
                <span class="value">{{ enrollmentToCancel.enrollmentNumber }}</span>
              </div>
              <div class="summary-row">
                <span class="label">Student:</span>
                <span class="value">{{ enrollmentToCancel.studentName }}</span>
              </div>
            </div>
            <div class="form-group">
              <label>Cancellation Reason *</label>
              <textarea [(ngModel)]="cancelReason" name="cancelReason" class="form-control" rows="3" placeholder="Enter reason for cancellation..."></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" (click)="closeCancelModal()">Close</button>
            <button class="btn btn-danger" (click)="confirmCancel()" [disabled]="submittingCancel || !cancelReason.trim()">
              {{ submittingCancel ? 'Cancelling...' : 'Confirm Cancel' }}
            </button>
          </div>
        </div>
      </div>
    }

    @if (showForceEnrollForm) {
      <div class="modal-overlay" (click)="closeForceEnrollForm()">
        <div class="modal-box modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Force Enroll Student</h3>
            <button class="modal-close" (click)="closeForceEnrollForm()">&times;</button>
          </div>
          <div class="modal-body">
            <form (ngSubmit)="submitForceEnroll()">
              <div class="form-grid">
                <div class="form-group">
                  <label>Student ID *</label>
                  <input type="number" [(ngModel)]="forceEnrollForm.studentId" name="studentId" class="form-control" required placeholder="Enter student ID">
                </div>
                <div class="form-group">
                  <label>Semester *</label>
                  <select [(ngModel)]="forceEnrollForm.semesterId" name="semesterId" class="form-control" required>
                    <option value="">Select Semester</option>
                    @for (sem of semesters; track sem.id) {
                      <option [ngValue]="sem.id">{{ sem.name }}</option>
                    }
                  </select>
                </div>
                <div class="form-group">
                  <label>Faculty *</label>
                  <select [(ngModel)]="forceEnrollForm.facultyId" name="facultyId" class="form-control" required (change)="onForceEnrollFacultyChange()">
                    <option value="">Select Faculty</option>
                    @for (fac of faculties; track fac.id) {
                      <option [ngValue]="fac.id">{{ fac.name }}</option>
                    }
                  </select>
                </div>
                <div class="form-group">
                  <label>Department *</label>
                  <select [(ngModel)]="forceEnrollForm.departmentId" name="departmentId" class="form-control" required>
                    <option value="">Select Department</option>
                    @for (dept of departments; track dept.id) {
                      <option [ngValue]="dept.id">{{ dept.name }}</option>
                    }
                  </select>
                </div>
                <div class="form-group">
                  <label>Program *</label>
                  <select [(ngModel)]="forceEnrollForm.programId" name="programId" class="form-control" required>
                    <option value="">Select Program</option>
                    @for (prog of programs; track prog.id) {
                      <option [ngValue]="prog.id">{{ prog.name }}</option>
                    }
                  </select>
                </div>
                <div class="form-group">
                  <label>Advisor</label>
                  <input type="text" [(ngModel)]="forceEnrollForm.advisorName" name="advisorName" class="form-control" placeholder="Advisor name (optional)">
                </div>
                <div class="form-group">
                  <label>Credits *</label>
                  <input type="number" [(ngModel)]="forceEnrollForm.registeredCredits" name="registeredCredits" class="form-control" required min="1" max="30" placeholder="Enter credits">
                </div>
                <div class="form-group">
                  <label>Enrollment Type</label>
                  <select [(ngModel)]="forceEnrollForm.enrollmentType" name="enrollmentType" class="form-control">
                    <option value="Regular">Regular</option>
                    <option value="Late">Late</option>
                    <option value="Special">Special</option>
                  </select>
                </div>
                <div class="form-group full-width">
                  <label>Remarks</label>
                  <textarea [(ngModel)]="forceEnrollForm.remarks" name="remarks" class="form-control" rows="2" placeholder="Optional remarks"></textarea>
                </div>
              </div>
              <div class="form-actions">
                <button type="button" class="btn btn-secondary" (click)="closeForceEnrollForm()">Cancel</button>
                <button type="submit" class="btn btn-primary" [disabled]="submittingForceEnroll">
                  {{ submittingForceEnroll ? 'Processing...' : 'Force Enroll' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    }

    <app-confirm-dialog [open]="showReopenConfirm" title="Reopen Enrollment" message="Are you sure you want to reopen this cancelled enrollment?" confirmText="Reopen" type="warning" (confirmed)="confirmReopen()" (cancelled)="showReopenConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; margin-bottom: 1.25rem; }
    .filter-card { padding: 20px; }
    .filter-grid { display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap; }
    .filter-grid .form-group { flex: 1; min-width: 160px; }
    .btn-group-align { display: flex; align-items: flex-end; }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    textarea.form-control { resize: vertical; }
    .loading-state { display: flex; flex-direction: column; align-items: center; padding: 40px 20px; gap: 12px; }
    .spinner { width: 32px; height: 32px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .loading-state p { color: var(--text-muted); font-size: 0.875rem; margin: 0; }
    .empty-state { padding: 60px 20px; text-align: center; }
    .empty-icon { color: var(--text-muted); margin-bottom: 12px; }
    .empty-state p { color: var(--text-muted); font-size: 0.9375rem; margin: 0; }
    .table-card .card-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid var(--border-color); }
    .table-card .card-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .page-info { font-size: 0.8125rem; color: var(--text-muted); }
    .table-wrapper { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th { padding: 12px 16px; text-align: left; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; color: var(--text-muted); border-bottom: 1px solid var(--border-color); background: var(--bg-secondary); }
    .data-table td { padding: 12px 16px; font-size: 0.875rem; color: var(--text-primary); border-bottom: 1px solid var(--border-color); }
    .data-table tbody tr:hover { background: var(--bg-secondary); }
    .text-brand { color: var(--brand-color); font-weight: 500; }
    .text-danger { color: #dc2626; }
    .student-info { display: flex; flex-direction: column; }
    .student-name { font-weight: 500; }
    .student-code { font-size: 0.75rem; color: var(--text-muted); }
    .badge { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #d1fae5; color: #065f46; }
    .badge-warning { background: #fef3c7; color: #92400e; }
    .badge-danger { background: #fee2e2; color: #991b1b; }
    .badge-secondary { background: #e5e7eb; color: #374151; }
    .badge-info { background: #dbeafe; color: #1e40af; }
    .type-badge { padding: 3px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 500; }
    .type-regular { background: #e0f2fe; color: #0369a1; }
    .type-late { background: #fef3c7; color: #92400e; }
    .type-special { background: #ede9fe; color: #5b21b6; }
    .action-buttons { display: flex; gap: 4px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .btn-secondary:hover { background: #d1d5db; }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-danger:hover { background: #dc2626; }
    .btn-sm { padding: 5px 12px; font-size: 0.8125rem; }
    .btn-icon { padding: 6px; background: var(--bg-secondary); color: var(--text-secondary); border-radius: 6px; }
    .btn-icon:hover { background: var(--border-color); color: var(--text-primary); }
    .btn-icon-danger:hover { background: #fee2e2; color: #dc2626; }
    .btn-icon-warning:hover { background: #fef3c7; color: #92400e; }
    .pagination-bar { display: flex; justify-content: center; align-items: center; gap: 16px; padding: 16px 20px; border-top: 1px solid var(--border-color); }
    .page-indicator { font-size: 0.875rem; color: var(--text-muted); }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000; backdrop-filter: blur(4px); }
    .modal-box { background: var(--card-bg); border-radius: 16px; width: 90%; max-width: 500px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); animation: popIn 0.2s ease-out; }
    .modal-box.modal-lg { max-width: 700px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid var(--border-color); }
    .modal-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .modal-close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--text-muted); padding: 0; line-height: 1; }
    .modal-close:hover { color: var(--text-primary); }
    .modal-body { padding: 24px; max-height: 60vh; overflow-y: auto; }
    .modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 24px; border-top: 1px solid var(--border-color); }
    .enrollment-summary { margin-bottom: 16px; background: var(--bg-secondary); border-radius: 8px; padding: 12px 16px; }
    .summary-row { display: flex; justify-content: space-between; padding: 6px 0; }
    .summary-row .label { font-size: 0.875rem; color: var(--text-muted); }
    .summary-row .value { font-size: 0.875rem; font-weight: 500; color: var(--text-primary); }
    .details-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .detail-item { display: flex; flex-direction: column; gap: 4px; padding: 10px; background: var(--bg-secondary); border-radius: 8px; }
    .detail-item.full-width { grid-column: 1 / -1; }
    .detail-item .label { font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; color: var(--text-muted); }
    .detail-item .value { font-size: 0.875rem; font-weight: 500; color: var(--text-primary); }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .form-group.full-width { grid-column: 1 / -1; }
    .form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 20px; padding-top: 16px; border-top: 1px solid var(--border-color); }
    @keyframes popIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
  `]
})
export class AdminManagementComponent implements OnInit {
  semesters: any[] = [];
  faculties: any[] = [];
  departments: any[] = [];
  programs: any[] = [];
  pagedData: PagedResponse<SemesterEnrollment> | null = null;
  loading = false;
  currentPage = 0;
  pageSize = 20;

  filters = {
    semesterId: '',
    facultyId: '',
    departmentId: '',
    programId: '',
    status: ''
  };

  showDetailsModal = false;
  selectedEnrollment: SemesterEnrollment | null = null;

  showCancelModal = false;
  enrollmentToCancel: SemesterEnrollment | null = null;
  cancelReason = '';
  submittingCancel = false;

  showReopenConfirm = false;
  enrollmentToReopen: SemesterEnrollment | null = null;

  showForceEnrollForm = false;
  submittingForceEnroll = false;
  forceEnrollForm: Partial<SemesterEnrollment> = {
    enrollmentType: 'Regular',
    registeredCredits: 12
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
    this.loadEnrollments();
  }

  loadDropdowns() {
    this.semesterService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.semesters = data.content || []; }
    });
    this.facultyService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (data) => { this.faculties = data.content || []; }
    });
    this.departmentService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (data) => { this.departments = data.content || []; }
    });
    this.programService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (data) => { this.programs = data.content || []; }
    });
  }

  loadEnrollments() {
    this.loading = true;
    const params = { ...DEFAULT_PAGE_PARAMS, page: this.currentPage, size: this.pageSize };
    const filterParams: any = {};
    if (this.filters.semesterId) filterParams.semesterId = parseInt(this.filters.semesterId, 10);
    if (this.filters.facultyId) filterParams.facultyId = parseInt(this.filters.facultyId, 10);
    if (this.filters.departmentId) filterParams.departmentId = parseInt(this.filters.departmentId, 10);
    if (this.filters.programId) filterParams.programId = parseInt(this.filters.programId, 10);
    if (this.filters.status) filterParams.status = this.filters.status;

    this.enrollmentService.findAll(params, filterParams).subscribe({
      next: (data) => {
        this.pagedData = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load enrollments');
      }
    });
  }

  onFacultyChange() {
    this.filters.departmentId = '';
    this.filters.programId = '';
  }

  onForceEnrollFacultyChange() {
    this.forceEnrollForm.departmentId = undefined;
    this.forceEnrollForm.programId = undefined;
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadEnrollments();
    }
  }

  nextPage() {
    if (this.pagedData && !this.pagedData.last) {
      this.currentPage++;
      this.loadEnrollments();
    }
  }

  getStatusBadgeClass(status: string): string {
    if (!status || status === 'N/A') return 'badge badge-secondary';
    switch (status.toUpperCase()) {
      case 'APPROVED': return 'badge badge-success';
      case 'COMPLETED': return 'badge badge-success';
      case 'PENDING': return 'badge badge-warning';
      case 'DRAFT': return 'badge badge-info';
      case 'REJECTED': return 'badge badge-danger';
      case 'CANCELLED': return 'badge badge-danger';
      default: return 'badge badge-secondary';
    }
  }

  getPaymentBadgeClass(status: string): string {
    if (!status || status === 'N/A') return 'badge badge-secondary';
    switch (status.toUpperCase()) {
      case 'PAID': return 'badge badge-success';
      case 'PARTIAL': return 'badge badge-warning';
      case 'PENDING': return 'badge badge-warning';
      case 'UNPAID': return 'badge badge-danger';
      case 'WAIVED': return 'badge badge-info';
      default: return 'badge badge-secondary';
    }
  }

  openDetailsModal(enrollment: SemesterEnrollment) {
    this.selectedEnrollment = enrollment;
    this.showDetailsModal = true;
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedEnrollment = null;
  }

  cancelEnrollment(enrollment: SemesterEnrollment) {
    this.enrollmentToCancel = enrollment;
    this.cancelReason = '';
    this.showCancelModal = true;
  }

  closeCancelModal() {
    this.showCancelModal = false;
    this.enrollmentToCancel = null;
    this.cancelReason = '';
  }

  confirmCancel() {
    if (!this.enrollmentToCancel || !this.cancelReason.trim()) return;

    this.submittingCancel = true;
    this.enrollmentService.cancelEnrollment(this.enrollmentToCancel.id!, this.cancelReason).subscribe({
      next: () => {
        this.toastService.success('Enrollment cancelled successfully');
        this.submittingCancel = false;
        this.closeCancelModal();
        this.loadEnrollments();
      },
      error: (err) => {
        this.submittingCancel = false;
        this.toastService.error(err.error?.message || 'Failed to cancel enrollment');
      }
    });
  }

  reopenEnrollment(enrollment: SemesterEnrollment) {
    this.enrollmentToReopen = enrollment;
    this.showReopenConfirm = true;
  }

  confirmReopen() {
    if (!this.enrollmentToReopen) return;

    this.enrollmentService.reopenEnrollment(this.enrollmentToReopen.id!).subscribe({
      next: () => {
        this.toastService.success('Enrollment reopened successfully');
        this.showReopenConfirm = false;
        this.enrollmentToReopen = null;
        this.loadEnrollments();
      },
      error: (err) => {
        this.showReopenConfirm = false;
        this.enrollmentToReopen = null;
        this.toastService.error(err.error?.message || 'Failed to reopen enrollment');
      }
    });
  }

  submitForceEnroll() {
    if (!this.forceEnrollForm.studentId || !this.forceEnrollForm.semesterId || !this.forceEnrollForm.registeredCredits) {
      this.toastService.error('Please fill all required fields');
      return;
    }

    this.submittingForceEnroll = true;

    const payload: SemesterEnrollment = {
      studentId: this.forceEnrollForm.studentId!,
      semesterId: this.forceEnrollForm.semesterId!,
      programId: this.forceEnrollForm.programId,
      facultyId: this.forceEnrollForm.facultyId,
      departmentId: this.forceEnrollForm.departmentId,
      advisorName: this.forceEnrollForm.advisorName,
      registeredCredits: this.forceEnrollForm.registeredCredits,
      enrollmentType: this.forceEnrollForm.enrollmentType || 'Regular',
      remarks: this.forceEnrollForm.remarks,
      status: 'Pending'
    };

    this.enrollmentService.forceEnroll(payload).subscribe({
      next: () => {
        this.toastService.success('Student force enrolled successfully');
        this.submittingForceEnroll = false;
        this.closeForceEnrollForm();
        this.loadEnrollments();
      },
      error: (err) => {
        this.submittingForceEnroll = false;
        this.toastService.error(err.error?.message || 'Failed to force enroll student');
      }
    });
  }

  closeForceEnrollForm() {
    this.showForceEnrollForm = false;
    this.forceEnrollForm = { enrollmentType: 'Regular', registeredCredits: 12 };
  }
}
