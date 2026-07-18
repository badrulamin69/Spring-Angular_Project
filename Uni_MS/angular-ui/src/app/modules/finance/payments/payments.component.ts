import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../../services/payment.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Payments</h2>
        <p class="page-sub">Manage payment records</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        New Payment
      </button>
    </div>

    @if (stats) {
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon stat-total">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M10 2v16M6 6h8M4 10h12M6 14h8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalPayments || 0 }}</span>
            <span class="stat-label">Total Payments</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-approved">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M4 10l4 4 8-8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.approvedCount || 0 }}</span>
            <span class="stat-label">Approved</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-pending">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M10 6v4l3 3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.pendingCount || 0 }}</span>
            <span class="stat-label">Pending</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-total-amount">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M10 2a8 8 0 110 16 8 8 0 010-16z" stroke="currentColor" stroke-width="1.5"/><path d="M10 6v8M7 8.5a2 2 0 104 0M7 12.5a2 2 0 104 0" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalAmount | number:'1.0-0' }}</span>
            <span class="stat-label">Total Amount</span>
          </div>
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
      (onEdit)="openForm($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showForm) {
      <app-dynamic-form
        [columns]="formColumns"
        [initialData]="editingItem"
        [title]="editingItem ? 'Edit Payment' : 'New Payment'"
        [saving]="saving"
        [errorMessage]="formError"
        (save)="saveItem($event)"
        (cancel)="closeForm()">
      </app-dynamic-form>
    }

    <app-confirm-dialog
      [open]="showConfirm"
      [title]="confirmTitle"
      [message]="confirmMessage"
      confirmText="Confirm"
      [type]="confirmType"
      (confirmed)="executeConfirm()"
      (cancelled)="showConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-sm { padding: 4px 10px; font-size: 0.75rem; }
    .btn-success { background: #16a34a; color: #fff; }
    .btn-success:hover { background: #15803d; }
    .btn-warning { background: #f59e0b; color: #fff; }
    .btn-warning:hover { background: #d97706; }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-danger:hover { background: #dc2626; }
    .btn-info { background: #3b82f6; color: #fff; }
    .btn-info:hover { background: #2563eb; }
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 1.25rem; }
    .stat-card { display: flex; align-items: center; gap: 14px; padding: 16px 20px; background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; }
    .stat-icon { width: 44px; height: 44px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .stat-total { background: #ede9fe; color: #7c3aed; }
    .stat-approved { background: #dcfce7; color: #16a34a; }
    .stat-pending { background: #fef3c7; color: #d97706; }
    .stat-total-amount { background: #dbeafe; color: #2563eb; }
    .stat-info { display: flex; flex-direction: column; }
    .stat-value { font-size: 1.25rem; font-weight: 700; color: var(--text-primary); }
    .stat-label { font-size: 0.75rem; color: var(--text-muted); }
    .badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; display: inline-block; width: fit-content; }
    .badge[data-status="PENDING"] { background: #fef3c7; color: #92400e; }
    .badge[data-status="APPROVED"] { background: #dcfce7; color: #166534; }
    .badge[data-status="COMPLETED"] { background: #dcfce7; color: #166534; }
    .badge[data-status="REJECTED"] { background: #fee2e2; color: #dc2626; }
    .badge[data-status="FAILED"] { background: #fee2e2; color: #dc2626; }
    .badge[data-status="REFUNDED"] { background: #dbeafe; color: #1d4ed8; }
    .action-buttons { display: flex; gap: 4px; flex-wrap: nowrap; }
    @media (max-width: 768px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
  `]
})
export class PaymentsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  stats: any = null;

  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'paymentNumber', label: 'Payment No.', sortable: true, type: 'text' },
    { key: 'student', label: 'Student', sortable: true },
    { key: 'amount', label: 'Amount', sortable: true, type: 'number' },
    { key: 'paymentMethod', label: 'Method', sortable: true, type: 'text' },
    { key: 'paymentStatus', label: 'Status', sortable: true, type: 'text' },
    { key: 'paymentDate', label: 'Date', sortable: true, type: 'date' }
  ];

  formColumns: TableColumn[] = [
    { key: 'invoiceId', label: 'Invoice ID', type: 'number', required: true, placeholder: 'Invoice ID' },
    { key: 'studentId', label: 'Student ID', type: 'number', required: true, placeholder: 'Student ID' },
    { key: 'amount', label: 'Amount', type: 'number', required: true, placeholder: '5000.00' },
    { key: 'paymentMethod', label: 'Payment Method', type: 'select', required: true, options: [
      { label: 'Cash', value: 'CASH' },
      { label: 'Card', value: 'CARD' },
      { label: 'Bank Transfer', value: 'BANK_TRANSFER' },
      { label: 'Online', value: 'ONLINE' },
      { label: 'Check', value: 'CHECK' }
    ]},
    { key: 'notes', label: 'Notes', type: 'textarea', placeholder: 'Notes' }
  ];

  showForm = false;
  editingItem: any = null;
  formError = '';

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  confirmType: 'danger' | 'warning' | 'info' = 'danger';
  confirmTarget: any = null;
  confirmAction = '';

  constructor(private service: PaymentService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
    this.loadStats();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load payments'); }
    });
  }

  loadStats() {
    this.service.getStats().subscribe({
      next: (data) => this.stats = data,
      error: () => {}
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.params = { ...DEFAULT_PAGE_PARAMS };
    this.loadData();
  }

  openForm(item?: any) {
    this.editingItem = item ? { ...item } : null;
    this.formError = '';
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editingItem = null;
    this.formError = '';
  }

  saveItem(data: any) {
    this.saving = true;
    this.formError = '';

    const handleSuccess = (msg: string) => {
      this.saving = false;
      this.closeForm();
      this.loadData();
      this.loadStats();
      this.toastService.success(msg);
    };

    const handleError = (err: any) => {
      this.saving = false;
      this.formError = err.error?.message || err.message || 'Validation failed. Please check your input.';
    };

    if (this.editingItem?.id) {
      this.toastService.error('Payments cannot be edited. Use approve/reject instead.');
      this.saving = false;
      this.closeForm();
    } else {
      this.service.initiatePayment(data).subscribe({
        next: () => handleSuccess('Payment initiated successfully'),
        error: handleError
      });
    }
  }

  approvePayment(item: any) {
    this.confirmTarget = item;
    this.confirmAction = 'approve';
    this.confirmTitle = 'Approve Payment';
    this.confirmMessage = `Are you sure you want to approve this payment of ${item.amount}?`;
    this.confirmType = 'warning';
    this.showConfirm = true;
  }

  rejectPayment(item: any) {
    this.confirmTarget = item;
    this.confirmAction = 'reject';
    this.confirmTitle = 'Reject Payment';
    this.confirmMessage = `Are you sure you want to reject this payment?`;
    this.confirmType = 'danger';
    this.showConfirm = true;
  }

  refundPayment(item: any) {
    this.confirmTarget = item;
    this.confirmAction = 'refund';
    this.confirmTitle = 'Refund Payment';
    this.confirmMessage = `Are you sure you want to refund ${item.amount} for this payment?`;
    this.confirmType = 'danger';
    this.showConfirm = true;
  }

  executeConfirm() {
    this.showConfirm = false;
    const item = this.confirmTarget;

    switch (this.confirmAction) {
      case 'approve':
        this.service.approvePayment(item.id, 'admin').subscribe({
          next: () => { this.loadData(); this.loadStats(); this.toastService.success('Payment approved'); },
          error: () => this.toastService.error('Failed to approve payment')
        });
        break;
      case 'reject':
        this.service.rejectPayment(item.id).subscribe({
          next: () => { this.loadData(); this.loadStats(); this.toastService.success('Payment rejected'); },
          error: () => this.toastService.error('Failed to reject payment')
        });
        break;
      case 'refund':
        this.service.refundPayment(item.id, { amount: item.amount, reason: 'Refund requested' }).subscribe({
          next: () => { this.loadData(); this.loadStats(); this.toastService.success('Payment refunded'); },
          error: () => this.toastService.error('Failed to refund payment')
        });
        break;
    }
    this.confirmTarget = null;
  }
}
