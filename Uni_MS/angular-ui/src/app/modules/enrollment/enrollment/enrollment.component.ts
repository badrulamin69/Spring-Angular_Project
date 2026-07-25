import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SemesterEnrollmentService } from '../../../services/semester-enrollment.service';
import { EnrollmentConfigService } from '../../../services/enrollment-config.service';
import { SemesterService } from '../../../services/semester.service';
import { SemesterEnrollment, EnrollmentEligibility } from '../../../models/semester-enrollment';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-enrollment',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Student Semester Enrollment</h2>
        <p class="page-sub">Enroll students for the current semester</p>
      </div>
    </div>

    <div class="card search-card">
      <div class="search-form">
        <div class="form-group">
          <label>Student Code *</label>
          <input type="text" [(ngModel)]="studentCode" name="studentCode" class="form-control" placeholder="Enter student code" (keyup.enter)="searchStudent()">
        </div>
        <div class="form-group">
          <label>Semester *</label>
          <select [(ngModel)]="semesterId" name="semesterId" class="form-control">
            <option value="">Select Semester</option>
            @for (sem of semesters; track sem.id) {
              <option [ngValue]="sem.id">{{ sem.name }}</option>
            }
          </select>
        </div>
        <div class="form-group btn-group-align">
          <button class="btn btn-primary" (click)="checkEligibility()" [disabled]="checkingEligibility || !studentCode || !semesterId">
            {{ checkingEligibility ? 'Checking...' : 'Check Eligibility' }}
          </button>
        </div>
      </div>
    </div>

    @if (loadingStudent) {
      <div class="card">
        <div class="loading-state">
          <div class="spinner"></div>
          <p>Loading student data...</p>
        </div>
      </div>
    }

    @if (eligibility) {
      <div class="card eligibility-card">
        <div class="card-header">
          <h3>Eligibility Result</h3>
          @if (eligibility.eligible) {
            <span class="badge badge-success">Eligible</span>
          } @else {
            <span class="badge badge-danger">Not Eligible</span>
          }
        </div>
        <div class="eligibility-info">
          <div class="info-row">
            <span class="label">Student:</span>
            <span class="value">{{ eligibility.studentName }}</span>
          </div>
          <div class="info-row">
            <span class="label">Semester:</span>
            <span class="value">{{ eligibility.semesterName }}</span>
          </div>
          <div class="info-row">
            <span class="label">Active Enrollment:</span>
            <span class="value">
              @if (eligibility.hasActiveEnrollment) {
                <span class="badge badge-warning">Yes</span>
              } @else {
                <span class="badge badge-secondary">No</span>
              }
            </span>
          </div>
          <div class="info-row">
            <span class="label">Academic Hold:</span>
            <span class="value">
              @if (eligibility.hasAcademicHold) {
                <span class="badge badge-danger">Yes</span>
              } @else {
                <span class="badge badge-secondary">No</span>
              }
            </span>
          </div>
          <div class="info-row">
            <span class="label">Financial Hold:</span>
            <span class="value">
              @if (eligibility.hasFinancialHold) {
                <span class="badge badge-danger">Yes</span>
              } @else {
                <span class="badge badge-secondary">No</span>
              }
            </span>
          </div>
          <div class="info-row">
            <span class="label">Registration Completed:</span>
            <span class="value">
              @if (eligibility.registrationCompleted) {
                <span class="badge badge-success">Yes</span>
              } @else {
                <span class="badge badge-warning">No</span>
              }
            </span>
          </div>
          <div class="info-row">
            <span class="label">Fees Paid:</span>
            <span class="value">
              @if (eligibility.feesPaid) {
                <span class="badge badge-success">Yes</span>
              } @else {
                <span class="badge badge-warning">No</span>
              }
            </span>
          </div>
          @if (eligibility.currentOutstandingBalance && eligibility.currentOutstandingBalance > 0) {
            <div class="info-row">
              <span class="label">Outstanding Balance:</span>
              <span class="value text-danger">{{ eligibility.currentOutstandingBalance | number:'1.2-2' }}</span>
            </div>
          }
        </div>

        @if (eligibility.errors.length > 0) {
          <div class="messages-section">
            <h4>Errors</h4>
            @for (error of eligibility.errors; track $index) {
              <div class="message-item error">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/></svg>
                {{ error }}
              </div>
            }
          </div>
        }

        @if (eligibility.warnings.length > 0) {
          <div class="messages-section">
            <h4>Warnings</h4>
            @for (warning of eligibility.warnings; track $index) {
              <div class="message-item warning">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                {{ warning }}
              </div>
            }
          </div>
        }
      </div>

      @if (eligibility.eligible) {
        <div class="card enrollment-form-card">
          <div class="card-header">
            <h3>Enrollment Form</h3>
          </div>
          <form (ngSubmit)="enroll()" class="enrollment-form">
            <div class="form-grid">
              <div class="form-group">
                <label>Student Code</label>
                <input type="text" class="form-control" [value]="studentCode" readonly>
              </div>
              <div class="form-group">
                <label>Student Name</label>
                <input type="text" class="form-control" [value]="eligibility.studentName" readonly>
              </div>
              <div class="form-group">
                <label>Program</label>
                <input type="text" class="form-control" [value]="enrollmentForm.programName || 'N/A'" readonly>
              </div>
              <div class="form-group">
                <label>Faculty</label>
                <input type="text" class="form-control" [value]="enrollmentForm.facultyName || 'N/A'" readonly>
              </div>
              <div class="form-group">
                <label>Department</label>
                <input type="text" class="form-control" [value]="enrollmentForm.departmentName || 'N/A'" readonly>
              </div>
              <div class="form-group">
                <label>Advisor</label>
                <input type="text" class="form-control" [value]="enrollmentForm.advisorName || 'N/A'" readonly>
              </div>
              <div class="form-group">
                <label>Batch</label>
                <input type="text" class="form-control" [value]="enrollmentForm.batchName || 'N/A'" readonly>
              </div>
              <div class="form-group">
                <label>Semester</label>
                <input type="text" class="form-control" [value]="eligibility.semesterName" readonly>
              </div>
              <div class="form-group">
                <label>Credits *</label>
                <input type="number" [(ngModel)]="enrollmentForm.registeredCredits" name="registeredCredits" required class="form-control" min="1" max="30" placeholder="Enter credits">
              </div>
              <div class="form-group">
                <label>Enrollment Type</label>
                <select [(ngModel)]="enrollmentForm.enrollmentType" name="enrollmentType" class="form-control">
                  <option value="Regular">Regular</option>
                  <option value="Late">Late</option>
                  <option value="Special">Special</option>
                </select>
              </div>
              <div class="form-group full-width">
                <label>Remarks</label>
                <textarea [(ngModel)]="enrollmentForm.remarks" name="remarks" class="form-control" rows="2" placeholder="Optional remarks"></textarea>
              </div>
            </div>
            <div class="form-actions">
              <button type="submit" class="btn btn-primary" [disabled]="enrolling">
                {{ enrolling ? 'Enrolling...' : 'Enroll Student' }}
              </button>
            </div>
          </form>
        </div>
      }
    }

    @if (enrolledEnrollment) {
      <div class="card enrollment-result-card">
        <div class="card-header">
          <h3>Enrollment Successful</h3>
          <span class="badge badge-success">{{ enrolledEnrollment.status }}</span>
        </div>
        <div class="enrollment-details">
          <div class="detail-row">
            <span class="label">Enrollment Number:</span>
            <span class="value highlight">{{ enrolledEnrollment.enrollmentNumber }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Student:</span>
            <span class="value">{{ enrolledEnrollment.studentName }} ({{ enrolledEnrollment.studentCode }})</span>
          </div>
          <div class="detail-row">
            <span class="label">Semester:</span>
            <span class="value">{{ enrolledEnrollment.semesterName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Program:</span>
            <span class="value">{{ enrolledEnrollment.programName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Department:</span>
            <span class="value">{{ enrolledEnrollment.departmentName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Faculty:</span>
            <span class="value">{{ enrolledEnrollment.facultyName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Advisor:</span>
            <span class="value">{{ enrolledEnrollment.advisorName || 'Not Assigned' }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Batch:</span>
            <span class="value">{{ enrolledEnrollment.batchName || 'N/A' }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Registered Credits:</span>
            <span class="value">{{ enrolledEnrollment.registeredCredits }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Enrollment Date:</span>
            <span class="value">{{ enrolledEnrollment.enrollmentDate }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Advisor Status:</span>
            <span class="value">
              @if (enrolledEnrollment.advisorStatus === 'Approved') {
                <span class="badge badge-success">{{ enrolledEnrollment.advisorStatus }}</span>
              } @else if (enrolledEnrollment.advisorStatus === 'Rejected') {
                <span class="badge badge-danger">{{ enrolledEnrollment.advisorStatus }}</span>
              } @else if (enrolledEnrollment.advisorStatus === 'Pending') {
                <span class="badge badge-warning">{{ enrolledEnrollment.advisorStatus }}</span>
              } @else {
                <span class="badge badge-secondary">{{ enrolledEnrollment.advisorStatus || 'N/A' }}</span>
              }
            </span>
          </div>
          <div class="detail-row">
            <span class="label">Payment Status:</span>
            <span class="value">
              @if (enrolledEnrollment.paymentStatus === 'Paid') {
                <span class="badge badge-success">{{ enrolledEnrollment.paymentStatus }}</span>
              } @else if (enrolledEnrollment.paymentStatus === 'Pending') {
                <span class="badge badge-warning">{{ enrolledEnrollment.paymentStatus }}</span>
              } @else {
                <span class="badge badge-secondary">{{ enrolledEnrollment.paymentStatus || 'N/A' }}</span>
              }
            </span>
          </div>
          @if (enrolledEnrollment.remarks) {
            <div class="detail-row">
              <span class="label">Remarks:</span>
              <span class="value">{{ enrolledEnrollment.remarks }}</span>
            </div>
          }
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; margin-bottom: 1.25rem; }
    .search-card { padding: 20px; }
    .search-form { display: flex; gap: 16px; align-items: flex-end; flex-wrap: wrap; }
    .search-form .form-group { flex: 1; min-width: 200px; }
    .btn-group-align { display: flex; align-items: flex-end; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    .form-control[readonly] { background: var(--bg-secondary); color: var(--text-muted); cursor: not-allowed; }
    .loading-state { display: flex; flex-direction: column; align-items: center; padding: 40px 20px; gap: 12px; }
    .spinner { width: 32px; height: 32px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .loading-state p { color: var(--text-muted); font-size: 0.875rem; margin: 0; }
    .eligibility-card, .enrollment-form-card, .enrollment-result-card { }
    .card-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid var(--border-color); }
    .card-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .badge { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #d1fae5; color: #065f46; }
    .badge-secondary { background: #e5e7eb; color: #374151; }
    .badge-warning { background: #fef3c7; color: #92400e; }
    .badge-danger { background: #fee2e2; color: #991b1b; }
    .eligibility-info { padding: 16px 20px; display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .info-row { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid var(--border-color); }
    .info-row:last-child { border-bottom: none; }
    .info-row .label { font-size: 0.875rem; color: var(--text-muted); }
    .info-row .value { font-size: 0.875rem; font-weight: 500; color: var(--text-primary); }
    .text-danger { color: #dc2626; }
    .messages-section { padding: 12px 20px; }
    .messages-section h4 { margin: 0 0 8px; font-size: 0.875rem; color: var(--text-secondary); }
    .message-item { display: flex; align-items: center; gap: 8px; padding: 8px 12px; border-radius: 6px; margin-bottom: 4px; font-size: 0.875rem; }
    .message-item.error { background: #fef2f2; color: #dc2626; }
    .message-item.warning { background: #fffbeb; color: #b45309; }
    .enrollment-form { padding: 20px; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .form-group.full-width { grid-column: 1 / -1; }
    .form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border-color); }
    .enrollment-details { padding: 20px; display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .detail-row { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid var(--border-color); }
    .detail-row:last-child { border-bottom: none; }
    .detail-row .label { font-size: 0.875rem; color: var(--text-muted); }
    .detail-row .value { font-size: 0.875rem; font-weight: 500; color: var(--text-primary); }
    .detail-row .value.highlight { color: var(--brand-color); font-weight: 600; font-size: 1rem; }
  `]
})
export class EnrollmentComponent implements OnInit {
  semesters: any[] = [];
  studentCode = '';
  semesterId = '';
  checkingEligibility = false;
  loadingStudent = false;
  enrolling = false;
  eligibility: EnrollmentEligibility | null = null;
  enrolledEnrollment: SemesterEnrollment | null = null;

  enrollmentForm: Partial<SemesterEnrollment> = {
    registeredCredits: 12,
    enrollmentType: 'Regular',
    remarks: ''
  };

  constructor(
    private enrollmentService: SemesterEnrollmentService,
    private configService: EnrollmentConfigService,
    private semesterService: SemesterService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadSemesters();
  }

  loadSemesters() {
    this.semesterService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.semesters = data.content || []; },
      error: () => this.toastService.error('Operation failed. Please try again.')
    });
  }

  searchStudent() {
    if (this.studentCode && this.semesterId) {
      this.checkEligibility();
    }
  }

  checkEligibility() {
    if (!this.studentCode || !this.semesterId) {
      this.toastService.error('Please enter student code and select a semester');
      return;
    }

    this.checkingEligibility = true;
    this.eligibility = null;
    this.enrolledEnrollment = null;

    this.enrollmentService.checkEligibility(parseInt(this.studentCode, 10), parseInt(this.semesterId, 10)).subscribe({
      next: (data) => {
        this.eligibility = data;
        this.checkingEligibility = false;
        if (data.eligible) {
          this.toastService.success('Student is eligible for enrollment');
        } else {
          this.toastService.error('Student is not eligible for enrollment');
        }
      },
      error: (err) => {
        this.checkingEligibility = false;
        this.toastService.error(err.error?.message || 'Failed to check eligibility');
      }
    });
  }

  enroll() {
    if (!this.eligibility || !this.studentCode || !this.semesterId) {
      this.toastService.error('Please check eligibility first');
      return;
    }

    this.enrolling = true;

    const payload: SemesterEnrollment = {
      studentId: this.eligibility.studentId,
      semesterId: parseInt(this.semesterId, 10),
      registeredCredits: this.enrollmentForm.registeredCredits || 12,
      enrollmentType: this.enrollmentForm.enrollmentType || 'Regular',
      remarks: this.enrollmentForm.remarks || '',
      status: 'Pending'
    };

    this.enrollmentService.enroll(payload).subscribe({
      next: (data) => {
        this.enrolledEnrollment = data;
        this.enrolling = false;
        this.eligibility = null;
        this.toastService.success('Student enrolled successfully');
      },
      error: (err) => {
        this.enrolling = false;
        this.toastService.error(err.error?.message || 'Failed to enroll student');
      }
    });
  }
}
