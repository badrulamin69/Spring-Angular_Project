import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InvoiceService } from '../../../services/invoice.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-invoices',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Invoices</h2>
        <p class="page-sub">Manage student invoices</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="showGenerateForm = true">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          Generate Invoice
        </button>
        <button class="btn btn-primary" (click)="openForm()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          Add New
        </button>
      </div>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (onEdit)="openForm($event)"
      (onDelete)="confirmDelete($event)"
      (bulkDelete)="confirmBulkDelete($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showForm) {
      <app-dynamic-form
        [columns]="formColumns"
        [initialData]="editingItem"
        [title]="editingItem ? 'Edit Invoice' : 'Add New Invoice'"
        [saving]="saving"
        [errorMessage]="formError"
        (save)="saveItem($event)"
        (cancel)="closeForm()">
      </app-dynamic-form>
    }

    @if (showGenerateForm) {
      <div class="modal-overlay" (click)="showGenerateForm = false">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Generate Invoice</h2>
            <button class="close-btn" (click)="showGenerateForm = false">&times;</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Student ID <span class="required">*</span></label>
              <input type="number" class="form-control" [(ngModel)]="generateData.studentId" placeholder="Student ID">
            </div>
            <div class="form-group">
              <label>Semester ID <span class="required">*</span></label>
              <input type="number" class="form-control" [(ngModel)]="generateData.semesterId" placeholder="Semester ID">
            </div>
            <div class="form-group">
              <label>Academic Year <span class="required">*</span></label>
              <input type="text" class="form-control" [(ngModel)]="generateData.academicYear" placeholder="2024-2025">
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" (click)="showGenerateForm = false">Cancel</button>
            <button class="btn btn-primary" (click)="generateInvoice()" [disabled]="!generateData.studentId || !generateData.semesterId || !generateData.academicYear">
              Generate
            </button>
          </div>
        </div>
      </div>
    }

    @if (selectedInvoice) {
      <div class="modal-overlay" (click)="selectedInvoice = null">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Invoice Details - {{ selectedInvoice.invoiceNumber }}</h2>
            <button class="close-btn" (click)="selectedInvoice = null">&times;</button>
          </div>
          <div class="modal-body">
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">Student</span>
                <span class="detail-value">{{ selectedInvoice.student?.firstName }} {{ selectedInvoice.student?.lastName }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Status</span>
                <span class="badge" [attr.data-status]="selectedInvoice.status">{{ selectedInvoice.status }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Total Amount</span>
                <span class="detail-value">{{ selectedInvoice.totalAmount | currency }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Paid Amount</span>
                <span class="detail-value">{{ selectedInvoice.paidAmount | currency }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Due Amount</span>
                <span class="detail-value">{{ selectedInvoice.dueAmount | currency }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Due Date</span>
                <span class="detail-value">{{ selectedInvoice.dueDate }}</span>
              </div>
            </div>
            @if (selectedInvoice.items && selectedInvoice.items.length > 0) {
              <h3 class="section-title">Invoice Items</h3>
              <table class="detail-table">
                <thead>
                  <tr>
                    <th>Fee Type</th>
                    <th>Description</th>
                    <th>Amount</th>
                    <th>Discount</th>
                    <th>Net Amount</th>
                  </tr>
                </thead>
                <tbody>
                  @for (item of selectedInvoice.items; track item.id) {
                    <tr>
                      <td>{{ item.feeType?.name }}</td>
                      <td>{{ item.description }}</td>
                      <td>{{ item.amount | currency }}</td>
                      <td>{{ item.discountAmount | currency }}</td>
                      <td>{{ item.netAmount | currency }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" (click)="selectedInvoice = null">Close</button>
          </div>
        </div>
      </div>
    }

    <app-confirm-dialog
      [open]="showConfirm"
      [title]="confirmTitle"
      [message]="confirmMessage"
      confirmText="Delete"
      type="danger"
      (confirmed)="executeDelete()"
      (cancelled)="showConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .header-actions { display: flex; gap: 8px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); color: var(--text-primary); }
    .btn-secondary { background: var(--bg-tertiary); color: var(--text-primary); border: 1px solid var(--border-color); }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: var(--bg-secondary); border-radius: 12px; width: 100%; max-width: 520px; max-height: 85vh; overflow: hidden; }
    .modal-lg { max-width: 720px; }
    .modal-header { padding: 16px 20px; border-bottom: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center; }
    .modal-header h2 { margin: 0; font-size: 1.125rem; color: var(--text-primary); font-weight: 600; }
    .close-btn { background: none; border: none; font-size: 1.5rem; color: var(--text-muted); cursor: pointer; }
    .close-btn:hover { color: var(--text-primary); }
    .modal-body { padding: 20px; overflow-y: auto; max-height: 60vh; }
    .modal-footer { padding: 14px 20px; border-top: 1px solid var(--border-color); display: flex; justify-content: flex-end; gap: 8px; }
    .form-group { margin-bottom: 14px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: 500; color: var(--text-secondary); font-size: 0.8125rem; }
    .required { color: #ef4444; }
    .form-control { width: 100%; padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; box-sizing: border-box; }
    .form-control:focus { outline: none; border-color: var(--brand-color); }
    .detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 16px; }
    .detail-item { display: flex; flex-direction: column; gap: 4px; }
    .detail-label { font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 600; }
    .detail-value { font-size: 0.9375rem; color: var(--text-primary); }
    .badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; display: inline-block; width: fit-content; }
    .badge[data-status="PENDING"] { background: #fef3c7; color: #92400e; }
    .badge[data-status="PAID"] { background: #dcfce7; color: #166534; }
    .badge[data-status="PARTIAL"] { background: #dbeafe; color: #1d4ed8; }
    .badge[data-status="OVERDUE"] { background: #fee2e2; color: #dc2626; }
    .badge[data-status="CANCELLED"] { background: #f3f4f6; color: #6b7280; }
    .section-title { font-size: 0.9375rem; color: var(--text-primary); font-weight: 600; margin: 0 0 10px; }
    .detail-table { width: 100%; border-collapse: collapse; font-size: 0.8125rem; }
    .detail-table th, .detail-table td { padding: 8px 10px; text-align: left; border-bottom: 1px solid var(--border-color); color: var(--text-primary); }
    .detail-table th { background: var(--bg-tertiary); font-weight: 600; color: var(--text-secondary); }
  `]
})
export class InvoicesComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';

  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'invoiceNumber', label: 'Invoice No.', sortable: true, type: 'text', required: true, placeholder: 'INV001' },
    { key: 'student', label: 'Student', sortable: true },
    { key: 'totalAmount', label: 'Total', sortable: true, type: 'number' },
    { key: 'paidAmount', label: 'Paid', sortable: true, type: 'number' },
    { key: 'dueAmount', label: 'Due', sortable: true, type: 'number' },
    { key: 'status', label: 'Status', sortable: true, type: 'text' },
    { key: 'dueDate', label: 'Due Date', sortable: true, type: 'date' }
  ];

  formColumns: TableColumn[] = [
    { key: 'studentId', label: 'Student ID', type: 'number', required: true, placeholder: 'Student ID' },
    { key: 'semesterId', label: 'Semester ID', type: 'number', placeholder: 'Semester ID' },
    { key: 'academicYear', label: 'Academic Year', type: 'text', placeholder: '2024-2025' },
    { key: 'notes', label: 'Notes', type: 'textarea', placeholder: 'Notes' }
  ];

  showForm = false;
  editingItem: any = null;
  formError = '';

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  showGenerateForm = false;
  generateData: any = { studentId: null, semesterId: null, academicYear: '' };
  selectedInvoice: any = null;

  constructor(private service: InvoiceService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load invoices'); }
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
      this.toastService.success(msg);
    };

    const handleError = (err: any) => {
      this.saving = false;
      this.formError = err.error?.message || err.message || 'Validation failed. Please check your input.';
    };

    if (this.editingItem?.id) {
      this.service.updateStatus(this.editingItem.id, data.status || 'PENDING').subscribe({
        next: () => handleSuccess('Invoice updated successfully'),
        error: handleError
      });
    } else {
      this.service.generateInvoice(data.studentId, data.semesterId, data.academicYear).subscribe({
        next: () => handleSuccess('Invoice generated successfully'),
        error: handleError
      });
    }
  }

  generateInvoice() {
    this.service.generateInvoice(this.generateData.studentId, this.generateData.semesterId, this.generateData.academicYear).subscribe({
      next: () => {
        this.showGenerateForm = false;
        this.generateData = { studentId: null, semesterId: null, academicYear: '' };
        this.loadData();
        this.toastService.success('Invoice generated successfully');
      },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to generate invoice')
    });
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Invoice';
    this.confirmMessage = `Are you sure you want to delete invoice "${item.invoiceNumber}"? This action cannot be undone.`;
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.deleteTarget = items;
    this.confirmTitle = 'Bulk Delete Invoices';
    this.confirmMessage = `Are you sure you want to delete ${items.length} selected invoices? This action cannot be undone.`;
    this.showConfirm = true;
  }

  executeDelete() {
    this.showConfirm = false;
    if (Array.isArray(this.deleteTarget)) {
      const ids = this.deleteTarget.map((i: any) => i.id);
      let completed = 0;
      ids.forEach((id: number) => {
        this.service.delete(id).subscribe({
          next: () => {
            completed++;
            if (completed === ids.length) {
              this.loadData();
              this.toastService.success(`${ids.length} invoices deleted`);
            }
          },
          error: () => this.toastService.error('Failed to delete some records')
        });
      });
    } else if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => {
          this.loadData();
          this.toastService.success('Invoice deleted successfully');
        },
        error: () => this.toastService.error('Failed to delete invoice')
      });
    }
    this.deleteTarget = null;
  }
}
