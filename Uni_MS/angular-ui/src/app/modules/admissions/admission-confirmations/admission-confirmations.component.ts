import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdmissionConfirmationService } from '../../../services/admission-confirmation.service';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admission-confirmations',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Admission Confirmations</h2>
        <p class="page-sub">Manage admission confirmations, document verification, and fee collection</p>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ stats.total || 0 }}</div>
        <div class="stat-label">Total</div>
      </div>
      <div class="stat-card warning">
        <div class="stat-value">{{ stats.pending || 0 }}</div>
        <div class="stat-label">Pending</div>
      </div>
      <div class="stat-card info">
        <div class="stat-value">{{ stats.documentsSubmitted || 0 }}</div>
        <div class="stat-label">Docs Submitted</div>
      </div>
      <div class="stat-card success">
        <div class="stat-value">{{ stats.documentsVerified || 0 }}</div>
        <div class="stat-label">Docs Verified</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.feePaid || 0 }}</div>
        <div class="stat-label">Fee Paid</div>
      </div>
      <div class="stat-card success">
        <div class="stat-value">{{ stats.enrolled || 0 }}</div>
        <div class="stat-label">Enrolled</div>
      </div>
    </div>

    <div class="table-wrapper">
      <div class="table-toolbar">
        <div class="search-box">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><circle cx="6.5" cy="6.5" r="5.5" stroke="currentColor" stroke-width="1.5"/><path d="M10.5 10.5L14.5 14.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          <input type="text" placeholder="Search..." [(ngModel)]="searchTerm" (input)="onSearch()">
        </div>
        <div class="filter-group">
          <select [(ngModel)]="statusFilter" (change)="loadData()">
            <option value="">All Status</option>
            <option value="PENDING">Pending</option>
            <option value="DOCUMENTS_SUBMITTED">Docs Submitted</option>
            <option value="DOCUMENTS_VERIFIED">Docs Verified</option>
            <option value="DOCUMENTS_REJECTED">Docs Rejected</option>
            <option value="FEE_PAID">Fee Paid</option>
            <option value="CONFIRMED">Confirmed</option>
            <option value="ENROLLED">Enrolled</option>
          </select>
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
                <th>Confirmation No</th>
                <th>Registration No</th>
                <th>Applicant Name</th>
                <th>Program</th>
                <th>Department</th>
                <th>Documents</th>
                <th>Fee</th>
                <th>Status</th>
                <th class="col-actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (item of pagedData?.content || []; track item.id) {
                <tr>
                  <td>{{ item.confirmationNumber }}</td>
                  <td>{{ item.registration?.registrationNumber || '-' }}</td>
                  <td>{{ item.registration?.firstName }} {{ item.registration?.lastName }}</td>
                  <td>{{ item.allocation?.allocatedProgram?.name || '-' }}</td>
                  <td>{{ item.allocation?.allocatedDepartment?.name || '-' }}</td>
                  <td>
                    @if (item.documentsVerified) {
                      <span class="status-badge" data-status="CONFIRMED">Verified</span>
                    } @else if (item.documentsSubmitted) {
                      <span class="status-badge" data-status="ALLOCATED">Submitted</span>
                    } @else {
                      <span class="status-badge" data-status="PENDING">Pending</span>
                    }
                  </td>
                  <td>
                    @if (item.feePaid) {
                      <span class="status-badge" data-status="CONFIRMED">Paid</span>
                    } @else {
                      <span class="status-badge" data-status="PENDING">Unpaid</span>
                    }
                  </td>
                  <td><span class="status-badge" [attr.data-status]="item.status">{{ item.status }}</span></td>
                  <td class="col-actions">
                    <div class="action-btns">
                      <button class="btn btn-sm btn-outline" (click)="viewDetails(item)">View</button>
                      @if (item.status === 'DOCUMENTS_SUBMITTED') {
                        <button class="btn btn-sm btn-success" (click)="openVerifyDialog(item, true)">Verify</button>
                        <button class="btn btn-sm btn-danger" (click)="openVerifyDialog(item, false)">Reject</button>
                      }
                      @if (item.status === 'FEE_PAID') {
                        <button class="btn btn-sm btn-primary" (click)="confirmAdmission(item)">Enroll</button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="9" class="empty-state">No confirmations found</td></tr>
              }
            </tbody>
          </table>
        </div>

        @if (pagedData && pagedData.totalPages > 1) {
          <div class="pagination">
            <button class="btn btn-sm btn-outline" [disabled]="pagedData.first" (click)="goToPage(currentPage - 1)">Previous</button>
            <span class="page-info">Page {{ currentPage + 1 }} of {{ pagedData.totalPages }}</span>
            <button class="btn btn-sm btn-outline" [disabled]="pagedData.last" (click)="goToPage(currentPage + 1)">Next</button>
          </div>
        }
      }
    </div>

    @if (showDetailModal) {
      <div class="modal-overlay" (click)="closeDetailModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Confirmation Details</h3>
            <button class="btn-close" (click)="closeDetailModal()">&times;</button>
          </div>
          <div class="modal-body">
            @if (selectedItem) {
              <div class="detail-grid">
                <div class="detail-item">
                  <label>Confirmation Number</label>
                  <span>{{ selectedItem.confirmationNumber }}</span>
                </div>
                <div class="detail-item">
                  <label>Applicant</label>
                  <span>{{ selectedItem.registration?.firstName }} {{ selectedItem.registration?.lastName }}</span>
                </div>
                <div class="detail-item">
                  <label>Email</label>
                  <span>{{ selectedItem.registration?.email }}</span>
                </div>
                <div class="detail-item">
                  <label>Program</label>
                  <span>{{ selectedItem.allocation?.allocatedProgram?.name || '-' }}</span>
                </div>
                <div class="detail-item">
                  <label>Department</label>
                  <span>{{ selectedItem.allocation?.allocatedDepartment?.name || '-' }}</span>
                </div>
                <div class="detail-item">
                  <label>Status</label>
                  <span class="status-badge" [attr.data-status]="selectedItem.status">{{ selectedItem.status }}</span>
                </div>
                <div class="detail-item">
                  <label>Documents Verified</label>
                  <span>{{ selectedItem.documentsVerified ? 'Yes' : 'No' }}</span>
                </div>
                <div class="detail-item">
                  <label>Fee Paid</label>
                  <span>{{ selectedItem.feePaid ? 'Yes (' + (selectedItem.feeAmount || 0) + ')' : 'No' }}</span>
                </div>
                @if (selectedItem.documentRemarks) {
                  <div class="detail-item full-width">
                    <label>Document Remarks</label>
                    <span>{{ selectedItem.documentRemarks }}</span>
                  </div>
                }
              </div>

              @if (documents.length > 0) {
                <h4 style="margin-top: 16px;">Submitted Documents</h4>
                <div class="doc-list">
                  @for (doc of documents; track doc.id) {
                    <div class="doc-item">
                      <span class="doc-type">{{ doc.documentType }}</span>
                      <span class="doc-name">{{ doc.documentName }}</span>
                      <span class="status-badge" [attr.data-status]="doc.status">{{ doc.status }}</span>
                    </div>
                  }
                </div>
              }

              @if (showFeeModal && selectedItem) {
                <div class="fee-form" style="margin-top: 16px;">
                  <h4>Record Fee Payment</h4>
                  <div class="form-row">
                    <div class="form-group">
                      <label>Amount</label>
                      <input type="number" [(ngModel)]="feeAmount" placeholder="Enter amount" class="form-control">
                    </div>
                    <div class="form-group">
                      <label>Payment Method</label>
                      <select [(ngModel)]="feePaymentMethod" class="form-control">
                        <option value="CASH">Cash</option>
                        <option value="BANK_TRANSFER">Bank Transfer</option>
                        <option value="CARD">Card</option>
                        <option value="MOBILE_BANKING">Mobile Banking</option>
                      </select>
                    </div>
                  </div>
                  <button class="btn btn-sm btn-primary" (click)="recordFeePayment()">Record Payment</button>
                </div>
              }
            }
          </div>
        </div>
      </div>
    }

    <app-confirm-dialog
      [open]="showVerifyDialog"
      [title]="verifyAction === true ? 'Verify Documents' : 'Reject Documents'"
      [message]="verifyAction === true ? 'Are you sure you want to verify these documents?' : 'Are you sure you want to reject these documents?'"
      [confirmText]="verifyAction === true ? 'Verify' : 'Reject'"
      [type]="verifyAction === true ? 'info' : 'danger'"
      (confirmed)="onVerifyConfirmed()"
      (cancelled)="showVerifyDialog = false"
    ></app-confirm-dialog>
  `,
  styles: [`
    .stats-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 16px; margin-bottom: 24px; }
    .stat-card { background: var(--bg-secondary); border-radius: 12px; padding: 20px; border: 1px solid var(--border-color); }
    .stat-card.warning .stat-value { color: #e6a817; }
    .stat-card.info .stat-value { color: #0056b3; }
    .stat-card.success .stat-value { color: #28a745; }
    .stat-value { font-size: 1.75rem; font-weight: 700; color: var(--text-primary); }
    .stat-label { font-size: 0.8rem; color: var(--text-muted); margin-top: 4px; }
    .table-wrapper { background: var(--bg-secondary); border-radius: 12px; border: 1px solid var(--border-color); overflow: hidden; }
    .table-toolbar { display: flex; align-items: center; gap: 12px; padding: 16px; border-bottom: 1px solid var(--border-color); flex-wrap: wrap; }
    .search-box { display: flex; align-items: center; gap: 8px; padding: 8px 12px; background: var(--bg-tertiary); border-radius: 8px; flex: 1; min-width: 200px; }
    .search-box input { border: none; background: transparent; outline: none; color: var(--text-primary); width: 100%; font-size: 0.875rem; }
    .filter-group select { padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border-color); background: var(--bg-tertiary); color: var(--text-primary); font-size: 0.875rem; }
    .btn { padding: 8px 16px; border-radius: 8px; font-size: 0.875rem; font-weight: 500; cursor: pointer; border: none; transition: all 0.15s; }
    .btn-sm { padding: 6px 12px; font-size: 0.8rem; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-primary); }
    .btn-outline:hover { background: var(--bg-hover); }
    .btn-primary { background: #0056b3; color: #fff; }
    .btn-primary:hover { background: #004080; }
    .btn-success { background: #28a745; color: #fff; }
    .btn-success:hover { background: #059669; }
    .btn-danger { background: #dc3545; color: #fff; }
    .btn-danger:hover { background: #bd2130; }
    .table-scroll { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 12px 16px; text-align: left; font-size: 0.875rem; border-bottom: 1px solid var(--border-color); }
    th { background: var(--bg-tertiary); font-weight: 600; color: var(--text-muted); white-space: nowrap; }
    td { color: var(--text-primary); }
    .col-actions { white-space: nowrap; }
    .action-btns { display: flex; gap: 6px; }
    .status-badge { padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 500; white-space: nowrap; }
    .status-badge[data-status="PENDING"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="ALLOCATED"], .status-badge[data-status="DOCUMENTS_SUBMITTED"] { background: #dbeafe; color: #002d5f; }
    .status-badge[data-status="DOCUMENTS_VERIFIED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="DOCUMENTS_REJECTED"] { background: #fee2e2; color: #991b1b; }
    .status-badge[data-status="FEE_PAID"] { background: #e0e7ff; color: #3730a3; }
    .status-badge[data-status="CONFIRMED"], .status-badge[data-status="ENROLLED"] { background: #dcfce7; color: #166534; }
    .empty-state { text-align: center; padding: 40px 16px; color: var(--text-muted); }
    .loading-state { display: flex; align-items: center; justify-content: center; gap: 12px; padding: 40px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: #0056b3; border-radius: 50%; animation: spin 0.8s linear infinite; }
    .spinner-sm { width: 14px; height: 14px; border: 2px solid var(--border-color); border-top-color: #0056b3; border-radius: 50%; animation: spin 0.8s linear infinite; display: inline-block; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .pagination { display: flex; align-items: center; justify-content: center; gap: 16px; padding: 16px; }
    .page-info { font-size: 0.875rem; color: var(--text-muted); }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; backdrop-filter: blur(4px); }
    .modal-content { background: var(--bg-secondary); border-radius: 16px; width: 90%; max-width: 600px; max-height: 80vh; overflow-y: auto; }
    .modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px; border-bottom: 1px solid var(--border-color); }
    .modal-header h3 { margin: 0; font-size: 1.125rem; }
    .btn-close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--text-muted); }
    .modal-body { padding: 20px; }
    .detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .detail-item label { display: block; font-size: 0.75rem; color: var(--text-muted); margin-bottom: 4px; }
    .detail-item span { font-size: 0.875rem; color: var(--text-primary); }
    .detail-item.full-width { grid-column: 1 / -1; }
    .doc-list { display: flex; flex-direction: column; gap: 8px; }
    .doc-item { display: flex; align-items: center; gap: 12px; padding: 10px; background: var(--bg-tertiary); border-radius: 8px; }
    .doc-type { font-weight: 600; font-size: 0.875rem; min-width: 120px; }
    .doc-name { flex: 1; font-size: 0.875rem; color: var(--text-muted); }
    .fee-form { padding: 16px; background: var(--bg-tertiary); border-radius: 8px; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 12px; }
    .form-group label { display: block; font-size: 0.75rem; color: var(--text-muted); margin-bottom: 4px; }
    .form-control { width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border-color); background: var(--bg-secondary); color: var(--text-primary); font-size: 0.875rem; }
    .text-muted { color: var(--text-muted); }
    @media (max-width: 768px) {
      .stats-row { grid-template-columns: repeat(2, 1fr); }
      .detail-grid { grid-template-columns: 1fr; }
      .form-row { grid-template-columns: 1fr; }
    }
  `]
})
export class AdmissionConfirmationsComponent implements OnInit {
  pagedData: PagedResponse<any> | null = null;
  currentPage = 0;
  searchTerm = '';
  statusFilter = '';
  loading = false;
  stats: any = {};
  showDetailModal = false;
  selectedItem: any = null;
  documents: any[] = [];
  showFeeModal = false;
  feeAmount = 0;
  feePaymentMethod = 'CASH';
  showVerifyDialog = false;
  verifyAction = true;
  verifyItem: any = null;

  constructor(
    private service: AdmissionConfirmationService,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
    this.loadStats();
  }

  loadData() {
    this.loading = true;
    const params: PageParams = { ...DEFAULT_PAGE_PARAMS, page: this.currentPage };
    this.service.findAll(params, this.searchTerm, this.statusFilter).subscribe({
      next: (res) => { this.pagedData = res; this.loading = false; },
      error: () => { this.toast.error('Failed to load confirmations'); this.loading = false; }
    });
  }

  loadStats() {
    this.service.getStats().subscribe({
      next: (res) => this.stats = res,
      error: () => {}
    });
  }

  onSearch() {
    this.currentPage = 0;
    this.loadData();
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.loadData();
  }

  viewDetails(item: any) {
    this.selectedItem = item;
    this.showDetailModal = true;
    this.showFeeModal = item.status === 'FEE_PAID' || item.status === 'DOCUMENTS_VERIFIED';
    this.service.getDocuments(item.id).subscribe({
      next: (docs) => this.documents = docs,
      error: () => this.documents = []
    });
  }

  closeDetailModal() {
    this.showDetailModal = false;
    this.selectedItem = null;
    this.documents = [];
  }

  openVerifyDialog(item: any, verified: boolean) {
    this.verifyItem = item;
    this.verifyAction = verified;
    this.showVerifyDialog = true;
  }

  onVerifyConfirmed() {
    if (!this.verifyItem) return;
    this.service.verifyDocuments(this.verifyItem.id, this.verifyAction, '').subscribe({
      next: () => {
        this.toast.success(this.verifyAction ? 'Documents verified' : 'Documents rejected');
        this.showVerifyDialog = false;
        this.loadData();
        this.loadStats();
      },
      error: (err) => { this.toast.error(err.error?.message || 'Operation failed'); }
    });
  }

  recordFeePayment() {
    if (!this.selectedItem || this.feeAmount <= 0) return;
    this.service.payFee(this.selectedItem.id, this.feeAmount, this.feePaymentMethod, 'TXN-' + Date.now()).subscribe({
      next: () => {
        this.toast.success('Fee payment recorded');
        this.showFeeModal = false;
        this.loadData();
        this.loadStats();
      },
      error: (err) => { this.toast.error(err.error?.message || 'Payment failed'); }
    });
  }

  confirmAdmission(item: any) {
    this.service.confirmAdmission(item.id).subscribe({
      next: () => {
        this.toast.success('Admission confirmed and enrollment completed');
        this.loadData();
        this.loadStats();
      },
      error: (err) => { this.toast.error(err.error?.message || 'Confirmation failed'); }
    });
  }
}
