import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnrollmentService } from '../../../services/enrollment.service';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-enrollments',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Student Enrollment</h2>
        <p class="page-sub">Enroll confirmed applicants as students</p>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalStudents || 0 }}</div>
        <div class="stat-label">Total Students</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ pagedData?.totalElements || 0 }}</div>
        <div class="stat-label">Pending Enrollment</div>
      </div>
    </div>

    <div class="table-wrapper">
      <div class="table-toolbar">
        <div class="search-box">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><circle cx="6.5" cy="6.5" r="5.5" stroke="currentColor" stroke-width="1.5"/><path d="M10.5 10.5L14.5 14.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          <input type="text" placeholder="Search..." [(ngModel)]="searchTerm" (input)="onSearch()">
        </div>
        <button class="btn btn-sm btn-outline" (click)="loadData()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/></svg>
          Refresh
        </button>
      </div>

      @if (loading) {
        <div class="loading-state"><div class="spinner"></div><span>Loading...</span></div>
      } @else {
        <div class="table-scroll">
          <table>
            <thead>
              <tr>
                <th>Allocation No</th>
                <th>Registration No</th>
                <th>Applicant Name</th>
                <th>Program</th>
                <th>Department</th>
                <th>Score</th>
                <th>Rank</th>
                <th>Status</th>
                <th class="col-actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (item of pagedData?.content || []; track item.id) {
                <tr>
                  <td>{{ item.allocationNumber }}</td>
                  <td>{{ item.registration?.registrationNumber || '-' }}</td>
                  <td>{{ item.registration?.firstName }} {{ item.registration?.lastName }}</td>
                  <td>{{ item.allocatedProgram?.name || '-' }}</td>
                  <td>{{ item.allocatedDepartment?.name || '-' }}</td>
                  <td>{{ item.totalScore }}</td>
                  <td>#{{ item.meritRank }}</td>
                  <td><span class="status-badge" [attr.data-status]="item.status">{{ item.status }}</span></td>
                  <td class="col-actions">
                    @if (item.status === 'CONFIRMED') {
                      <button class="btn btn-sm btn-enroll" (click)="confirmEnroll(item)" [disabled]="enrollingId === item.id">
                        @if (enrollingId === item.id) {
                          <span class="spinner-sm"></span> Enrolling...
                        } @else {
                          Enroll
                        }
                      </button>
                    } @else {
                      <span class="text-muted">-</span>
                    }
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="9" class="empty-state">No confirmed allocations pending enrollment</td></tr>
              }
            </tbody>
          </table>
        </div>

        @if (pagedData && pagedData.totalElements > 0) {
          <div class="pagination">
            <div class="pagination-info">
              Showing {{ pagedData.page * pagedData.size + 1 }}&ndash;{{ min((pagedData.page + 1) * pagedData.size, pagedData.totalElements) }} of {{ pagedData.totalElements }}
            </div>
            <div class="pagination-controls">
              <button [disabled]="pagedData.first" (click)="goToPage(pagedData.page - 1)">&laquo; Prev</button>
              <span>Page {{ pagedData.page + 1 }} of {{ pagedData.totalPages }}</span>
              <button [disabled]="pagedData.last" (click)="goToPage(pagedData.page + 1)">Next &raquo;</button>
            </div>
          </div>
        }
      }
    </div>

    <app-confirm-dialog
      [open]="showConfirm"
      [title]="confirmTitle"
      [message]="confirmMessage"
      confirmText="Enroll"
      type="info"
      (confirmed)="executeEnroll()"
      (cancelled)="showConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .stats-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.25rem; }
    .stat-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.25rem; text-align: center; }
    .stat-value { font-size: 2rem; font-weight: 700; color: var(--brand-color); }
    .stat-label { font-size: 0.8125rem; color: var(--text-muted); margin-top: 4px; }
    .table-wrapper { background: var(--bg-secondary); border-radius: 12px; border: 1px solid var(--border-color); overflow: hidden; }
    .table-toolbar { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid var(--border-color); gap: 12px; }
    .search-box { display: flex; align-items: center; gap: 8px; background: var(--bg-primary); border: 1px solid var(--border-color); border-radius: 8px; padding: 6px 12px; }
    .search-box input { border: none; background: transparent; color: var(--text-primary); font-size: 0.875rem; outline: none; width: 200px; }
    .search-box input::placeholder { color: var(--text-muted); }
    .search-box svg { color: var(--text-muted); }
    .btn { padding: 6px 12px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8125rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; }
    .btn-sm { padding: 5px 10px; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); }
    .btn-enroll { background: #22c55e; color: #fff; }
    .btn-enroll:hover { background: #16a34a; }
    .btn-enroll:disabled { opacity: 0.6; cursor: not-allowed; }
    .table-scroll { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 10px 14px; text-align: left; border-bottom: 1px solid var(--border-color); color: var(--text-primary); white-space: nowrap; }
    th { background: var(--bg-tertiary); font-weight: 600; color: var(--text-secondary); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .col-actions { width: 120px; text-align: center; white-space: nowrap; }
    tr:hover { background: var(--bg-hover); }
    .status-badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; }
    .status-badge[data-status="CONFIRMED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="ENROLLED"] { background: #a7f3d0; color: #065f46; }
    .status-badge[data-status="ALLOCATED"] { background: #fef3c7; color: #92400e; }
    .empty-state { text-align: center; padding: 3rem 1rem !important; color: var(--text-muted); }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 3rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    .spinner-sm { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.8s linear infinite; display: inline-block; }
    .pagination { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px; border-top: 1px solid var(--border-color); }
    .pagination-info { font-size: 0.8125rem; color: var(--text-muted); }
    .pagination-controls { display: flex; gap: 8px; align-items: center; }
    .pagination-controls button { padding: 4px 10px; border: 1px solid var(--border-color); background: var(--bg-secondary); color: var(--text-primary); border-radius: 4px; cursor: pointer; font-size: 0.8125rem; }
    .pagination-controls button:disabled { opacity: 0.4; cursor: not-allowed; }
    .pagination-controls span { font-size: 0.8125rem; color: var(--text-muted); }
    .text-muted { color: var(--text-muted); }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class EnrollmentsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  min = Math.min;
  stats: any = {};
  enrollingId: number | null = null;
  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  enrollTarget: any = null;

  constructor(private enrollmentService: EnrollmentService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
    this.loadStats();
  }

  loadData() {
    this.loading = true;
    this.enrollmentService.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load enrollments'); }
    });
  }

  loadStats() {
    this.enrollmentService.getStats().subscribe({
      next: (data) => { this.stats = data; }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch() { this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }
  goToPage(page: number) { this.params = { ...this.params, page }; this.loadData(); }

  confirmEnroll(item: any) {
    this.enrollTarget = item;
    this.confirmTitle = 'Enroll Student';
    this.confirmMessage = `Enroll ${item.registration?.firstName} ${item.registration?.lastName} as a student? This will create a student record, generate a student code, and assign ROLE_STUDENT.`;
    this.showConfirm = true;
  }

  executeEnroll() {
    this.showConfirm = false;
    if (!this.enrollTarget) return;
    this.enrollingId = this.enrollTarget.id;
    this.enrollmentService.enroll(this.enrollTarget.id).subscribe({
      next: (result) => {
        this.enrollingId = null;
        this.toastService.success(`Enrolled! Student Code: ${result.studentCode}`);
        this.loadData();
        this.loadStats();
      },
      error: (err) => {
        this.enrollingId = null;
        this.toastService.error(err.error?.error || 'Enrollment failed');
      }
    });
  }
}

