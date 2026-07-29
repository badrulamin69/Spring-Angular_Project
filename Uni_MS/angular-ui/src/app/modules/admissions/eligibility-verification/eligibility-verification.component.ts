import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EligibilityVerificationService } from '../../../services/eligibility-verification.service';
import { AdmissionTestService } from '../../../services/admission-test.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-eligibility-verification',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Eligibility Verification</h2>
        <p class="page-sub">Verify candidate eligibility for admission tests</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="autoVerifyAll()" [disabled]="!selectedTestId || verifying">
          {{ verifying ? 'Verifying...' : 'Auto Verify All' }}
        </button>
      </div>
    </div>

    <div class="filter-bar">
      <label>Filter by Test:</label>
      <select [(ngModel)]="selectedTestId" (change)="onTestChange()">
        <option [ngValue]="null">All Tests</option>
        @for (t of tests; track t.id) { <option [ngValue]="t.id">{{ t.name }}</option> }
      </select>
    </div>

    @if (stats) {
      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-value">{{ stats.total || 0 }}</span>
          <span class="stat-label">Total</span>
        </div>
        <div class="stat-card stat-success">
          <span class="stat-value">{{ stats.eligible || 0 }}</span>
          <span class="stat-label">Eligible</span>
        </div>
        <div class="stat-card stat-danger">
          <span class="stat-value">{{ stats.ineligible || 0 }}</span>
          <span class="stat-label">Ineligible</span>
        </div>
        <div class="stat-card stat-warning">
          <span class="stat-value">{{ stats.pending || 0 }}</span>
          <span class="stat-label">Pending</span>
        </div>
      </div>
    }

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (onDelete)="confirmDelete($event)"
      (bulkDelete)="confirmBulkDelete($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showVerifyModal) {
      <div class="modal-overlay" (click)="showVerifyModal = false">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Verify Eligibility</h3>
            <button class="close-btn" (click)="showVerifyModal = false">&times;</button>
          </div>
          <form (ngSubmit)="submitVerification()">
            <div class="form-group">
              <label>Status *</label>
              <select [(ngModel)]="verifyData.status" name="status" required>
                <option value="">Select Status</option>
                <option value="ELIGIBLE">Eligible</option>
                <option value="INELIGIBLE">Ineligible</option>
              </select>
            </div>
            <div class="form-group">
              <label>Remarks</label>
              <textarea [(ngModel)]="verifyData.remarks" name="remarks" rows="3" placeholder="Optional remarks"></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="showVerifyModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="!verifyData.status">{{ saving ? 'Saving...' : 'Save' }}</button>
            </div>
          </form>
        </div>
      </div>
    }

    @if (showConfirm) {
      <app-confirm-dialog
        [open]="showConfirm"
        [title]="confirmTitle"
        [message]="confirmMessage"
        confirmText="Delete"
        type="danger"
        (confirmed)="executeDelete()"
        (cancelled)="showConfirm = false">
      </app-confirm-dialog>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary, #1e293b); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted, #64748b); }
    .header-actions { display: flex; gap: 0.5rem; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color, #4F46E5); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-secondary { background: #e2e8f0; color: #475569; }
    .btn-outline { background: white; color: #475569; border: 1px solid #d1d5db; }
    .btn-outline:hover { background: #f8fafc; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar label { font-weight: 500; color: #475569; font-size: 0.875rem; }
    .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 1.5rem; }
    .stat-card { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1rem 1.25rem; display: flex; flex-direction: column; gap: 0.25rem; }
    .stat-card .stat-value { font-size: 1.5rem; font-weight: 700; color: #1e293b; }
    .stat-card .stat-label { font-size: 0.8125rem; color: #64748b; }
    .stat-success { border-left: 3px solid #28a745; }
    .stat-danger { border-left: 3px solid #dc3545; }
    .stat-warning { border-left: 3px solid #e6a817; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; padding: 1.5rem; width: 90%; max-width: 500px; max-height: 90vh; overflow-y: auto; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; color: #1e293b; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b; }
    .form-group { margin-bottom: 0.75rem; }
    .form-group label { display: block; margin-bottom: 0.25rem; font-weight: 500; color: #374151; font-size: 0.8125rem; }
    .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.875rem; box-sizing: border-box; }
    .form-group textarea { resize: vertical; }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { outline: none; border-color: #4F46E5; box-shadow: 0 0 0 2px rgba(79,70,229,0.1); }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #e2e8f0; }
  `]
})
export class EligibilityVerificationComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = false;
  saving = false;
  verifying = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  selectedTestId: number | null = null;
  tests: any[] = [];
  stats: any = null;

  showVerifyModal = false;
  verifyData: any = {};
  verifyTarget: any = null;

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'registration?.candidateName', label: 'Candidate Name', sortable: true },
    { key: 'test?.name', label: 'Test Name', sortable: true },
    { key: 'sscGpaVerified', label: 'SSC GPA Verified', type: 'checkbox' },
    { key: 'hscGpaVerified', label: 'HSC GPA Verified', type: 'checkbox' },
    { key: 'documentsVerified', label: 'Documents Verified', type: 'checkbox' },
    { key: 'status', label: 'Status', sortable: true },
    { key: 'verifiedBy', label: 'Verified By', sortable: true }
  ];

  constructor(
    private service: EligibilityVerificationService,
    private admissionTestService: AdmissionTestService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadTests();
    this.loadData();
  }

  loadTests() {
    this.admissionTestService.getForDropdown().subscribe({
      next: (tests) => { this.tests = tests; },
      error: () => { this.tests = []; }
    });
  }

  onTestChange() {
    this.params = { ...DEFAULT_PAGE_PARAMS };
    this.loadStats();
    this.loadData();
  }

  loadStats() {
    if (!this.selectedTestId) {
      this.stats = null;
      return;
    }
    this.service.getStats(this.selectedTestId).subscribe({
      next: (data) => { this.stats = data; },
      error: () => { this.stats = null; }
    });
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, '', this.selectedTestId || undefined).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load verifications'); }
    });
  }

  onPageChange(p: PageParams) { this.params = p; this.loadData(); }
  onSearch(term: string) { this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  openVerify(item: any) {
    this.verifyTarget = item;
    this.verifyData = { status: '', remarks: '' };
    this.showVerifyModal = true;
  }

  submitVerification() {
    if (!this.verifyData.status) return;
    this.saving = true;
    const payload = {
      registrationId: this.verifyTarget.registrationId,
      testId: this.verifyTarget.testId,
      status: this.verifyData.status,
      remarks: this.verifyData.remarks
    };
    this.service.verify(payload).subscribe({
      next: () => {
        this.saving = false;
        this.showVerifyModal = false;
        this.loadData();
        this.loadStats();
        this.toastService.success('Verification saved');
      },
      error: (err) => {
        this.saving = false;
        this.toastService.error(err.error?.message || 'Verification failed');
      }
    });
  }

  autoVerifyAll() {
    if (!this.selectedTestId) return;
    this.verifying = true;
    this.service.autoVerifyAll(this.selectedTestId).subscribe({
      next: () => {
        this.verifying = false;
        this.loadData();
        this.loadStats();
        this.toastService.success('Auto verification completed');
      },
      error: (err) => {
        this.verifying = false;
        this.toastService.error(err.error?.message || 'Auto verification failed');
      }
    });
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Verification';
    this.confirmMessage = 'Delete this verification record? This action cannot be undone.';
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.deleteTarget = items;
    this.confirmTitle = 'Bulk Delete';
    this.confirmMessage = `Delete ${items.length} selected verifications?`;
    this.showConfirm = true;
  }

  executeDelete() {
    this.showConfirm = false;
    if (Array.isArray(this.deleteTarget)) {
      let completed = 0;
      this.deleteTarget.forEach((item: any) => {
        this.service.delete(item.id).subscribe({
          next: () => {
            completed++;
            if (completed === this.deleteTarget.length) {
              this.loadData();
              this.loadStats();
              this.toastService.success(`${completed} verifications deleted`);
            }
          },
          error: () => this.toastService.error('Failed to delete some records')
        });
      });
    } else if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.loadStats(); this.toastService.success('Verification deleted'); },
        error: () => this.toastService.error('Failed to delete verification')
      });
    }
    this.deleteTarget = null;
  }
}
