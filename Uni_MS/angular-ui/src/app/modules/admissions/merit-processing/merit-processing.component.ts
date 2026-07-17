import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PreAdmissionService } from '../../../services/pre-admission.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-merit-processing',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Merit Processing</h2>
        <p class="page-sub">Process merit scores and generate rankings</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="loadPreview()" [disabled]="loadingPreview">
          {{ loadingPreview ? 'Loading...' : 'Preview Stats' }}
        </button>
        <button class="btn btn-primary" (click)="confirmProcessMerit()" [disabled]="processing || !preview">
          {{ processing ? 'Processing...' : 'Process Merit' }}
        </button>
      </div>
    </div>

    @if (preview) {
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-value">{{ preview.totalEligible }}</div>
          <div class="stat-label">Total Eligible</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ preview.withTestResults }}</div>
          <div class="stat-label">With Test Results</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ preview.withoutTestResults }}</div>
          <div class="stat-label">Without Test Results</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ preview.avgSscGpa }}</div>
          <div class="stat-label">Avg SSC GPA</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ preview.avgHscGpa }}</div>
          <div class="stat-label">Avg HSC GPA</div>
        </div>
      </div>

      @if (preview.statusBreakdown) {
        <div class="breakdown-card">
          <h3>Status Breakdown</h3>
          <div class="breakdown-grid">
            @for (entry of getStatusEntries(); track entry[0]) {
              <div class="breakdown-item">
                <span class="breakdown-badge" [attr.data-status]="entry[0]">{{ formatStatus(entry[0]) }}</span>
                <span class="breakdown-count">{{ entry[1] }}</span>
              </div>
            }
          </div>
        </div>
      }
    }

    @if (processingResult) {
      <div class="result-card">
        <h3>Processing Complete</h3>
        <div class="result-grid">
          <div class="result-item">
            <span class="result-label">Total Processed:</span>
            <span class="result-value">{{ processingResult.totalProcessed }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">Allocations Created:</span>
            <span class="result-value">{{ processingResult.allocationsCreated }}</span>
          </div>
        </div>
        @if (processingResult.departmentWise) {
          <h4>Department-wise Allocation</h4>
          <div class="dept-grid">
            @for (entry of getDeptEntries(); track entry[0]) {
              <div class="dept-item">
                <span class="dept-name">{{ entry[0] }}</span>
                <span class="dept-count">{{ entry[1] }} allocated</span>
              </div>
            }
          </div>
        }
      </div>
    }

    <div class="info-card">
      <p>Click "Preview Stats" to see eligible applicants, then "Process Merit" to calculate weighted scores, rank applicants, and allocate departments based on preferences and merit.</p>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    <app-confirm-dialog
      [open]="showConfirm"
      title="Process Merit"
      [message]="'This will calculate weighted scores, rank all eligible applicants, and create department allocations. This action updates existing allocations. Continue?'"
      confirmText="Process Merit"
      type="warning"
      (confirmed)="executeProcessMerit()"
      (cancelled)="showConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .header-actions { display: flex; gap: 8px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .stats-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; margin-bottom: 16px; }
    .stat-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 10px; padding: 16px; text-align: center; }
    .stat-value { font-size: 1.5rem; font-weight: 700; color: var(--brand-color); }
    .stat-label { font-size: 0.75rem; color: var(--text-muted); margin-top: 4px; text-transform: uppercase; letter-spacing: 0.5px; }
    .breakdown-card, .result-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 10px; padding: 16px; margin-bottom: 16px; }
    .breakdown-card h3, .result-card h3, .result-card h4 { margin: 0 0 12px; font-size: 0.9375rem; color: var(--text-primary); }
    .result-card h4 { margin-top: 16px; font-size: 0.875rem; }
    .breakdown-grid { display: flex; gap: 12px; flex-wrap: wrap; }
    .breakdown-item { display: flex; align-items: center; gap: 8px; }
    .breakdown-badge { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .breakdown-badge[data-status="SUBMITTED"] { background: #dbeafe; color: #1d4ed8; }
    .breakdown-badge[data-status="ADMIT_CARD_GENERATED"] { background: #d1fae5; color: #065f46; }
    .breakdown-badge[data-status="TEST_COMPLETED"] { background: #e0e7ff; color: #3730a3; }
    .breakdown-badge[data-status="MERIT_PROCESSED"] { background: #fef3c7; color: #92400e; }
    .breakdown-badge[data-status="ALLOCATED"] { background: #d1fae5; color: #065f46; }
    .breakdown-count { font-weight: 600; color: var(--text-primary); }
    .result-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .result-item { display: flex; justify-content: space-between; padding: 8px 12px; background: var(--bg-primary); border-radius: 6px; }
    .result-label { color: var(--text-muted); font-size: 0.875rem; }
    .result-value { font-weight: 600; color: var(--text-primary); }
    .dept-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 8px; }
    .dept-item { display: flex; justify-content: space-between; padding: 8px 12px; background: var(--bg-primary); border-radius: 6px; }
    .dept-name { font-weight: 500; color: var(--text-primary); font-size: 0.875rem; }
    .dept-count { color: var(--text-muted); font-size: 0.8125rem; }
    .info-card { background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 8px; padding: 12px 16px; margin-bottom: 16px; }
    .info-card p { margin: 0; color: #1e40af; font-size: 0.875rem; }
    @media (max-width: 768px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
  `]
})
export class MeritProcessingComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  loadingPreview = false;
  processing = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  preview: any = null;
  processingResult: any = null;
  showConfirm = false;
  columns: TableColumn[] = [
    { key: 'registrationNumber', label: 'Reg. No', sortable: true },
    { key: 'firstName', label: 'First Name', sortable: true },
    { key: 'lastName', label: 'Last Name', sortable: true },
    { key: 'programPreference1', label: 'Preference 1' },
    { key: 'sscGpa', label: 'SSC GPA', sortable: true },
    { key: 'hscGpa', label: 'HSC GPA', sortable: true },
    { key: 'status', label: 'Status' }
  ];

  constructor(private service: PreAdmissionService, private toastService: ToastService) {}

  ngOnInit() { this.loadData(); }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load'); }
    });
  }

  loadPreview() {
    this.loadingPreview = true;
    this.service.getMeritPreview().subscribe({
      next: (data) => { this.preview = data; this.loadingPreview = false; },
      error: () => { this.loadingPreview = false; this.toastService.error('Failed to load preview'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  confirmProcessMerit() {
    this.showConfirm = true;
  }

  executeProcessMerit() {
    this.showConfirm = false;
    this.processing = true;
    this.service.processMerit().subscribe({
      next: (res) => {
        this.processing = false;
        this.processingResult = res;
        this.loadData();
        this.loadPreview();
        this.toastService.success(res.message || 'Merit processing completed');
      },
      error: (err) => {
        this.processing = false;
        this.toastService.error(err.error?.message || 'Merit processing failed');
      }
    });
  }

  getStatusEntries(): [string, number][] {
    if (!this.preview?.statusBreakdown) return [];
    return Object.entries(this.preview.statusBreakdown) as [string, number][];
  }

  getDeptEntries(): [string, number][] {
    if (!this.processingResult?.departmentWise) return [];
    return Object.entries(this.processingResult.departmentWise) as [string, number][];
  }

  formatStatus(status: string): string {
    return status?.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase()) || '';
  }
}
