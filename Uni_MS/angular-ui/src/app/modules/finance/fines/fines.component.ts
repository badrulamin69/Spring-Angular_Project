import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FineService } from '../../../services/fine.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-fines',
  standalone: true,
  imports: [CommonModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Fines</h2>
        <p class="page-sub">Manage student fines</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add New
      </button>
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
        [title]="editingItem ? 'Edit Fine' : 'Add New Fine'"
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
      [confirmText]="confirmBtnText"
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
  `]
})
export class FinesComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';

  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'student', label: 'Student', sortable: true },
    { key: 'amount', label: 'Amount', sortable: true, type: 'number' },
    { key: 'reason', label: 'Reason', sortable: true, type: 'text' },
    { key: 'issuedBy', label: 'Issued By', sortable: true, type: 'text' },
    { key: 'status', label: 'Status', sortable: true, type: 'text' },
    { key: 'issuedDate', label: 'Issued Date', sortable: true, type: 'date' }
  ];

  formColumns: TableColumn[] = [
    { key: 'studentId', label: 'Student ID', type: 'number', required: true, placeholder: 'Student ID' },
    { key: 'invoiceId', label: 'Invoice ID', type: 'number', placeholder: 'Invoice ID' },
    { key: 'feeTypeId', label: 'Fee Type ID', type: 'number', placeholder: 'Fee Type ID' },
    { key: 'amount', label: 'Amount', type: 'number', required: true, placeholder: '500' },
    { key: 'reason', label: 'Reason', type: 'text', required: true, placeholder: 'Late payment fine' },
    { key: 'issuedBy', label: 'Issued By', type: 'text', placeholder: 'Admin name' }
  ];

  showForm = false;
  editingItem: any = null;
  formError = '';

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  confirmBtnText = 'Confirm';
  confirmType: 'danger' | 'warning' | 'info' = 'danger';
  confirmTarget: any = null;
  confirmAction = '';

  constructor(private service: FineService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load fines'); }
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
      this.service.update(this.editingItem.id, data).subscribe({
        next: () => handleSuccess('Fine updated successfully'),
        error: handleError
      });
    } else {
      this.service.save(data).subscribe({
        next: () => handleSuccess('Fine created successfully'),
        error: handleError
      });
    }
  }

  waiveFine(item: any) {
    this.confirmTarget = item;
    this.confirmAction = 'waive';
    this.confirmTitle = 'Waive Fine';
    this.confirmMessage = `Are you sure you want to waive the fine of ${item.amount} for this student?`;
    this.confirmBtnText = 'Waive';
    this.confirmType = 'warning';
    this.showConfirm = true;
  }

  confirmDelete(item: any) {
    this.confirmTarget = item;
    this.confirmAction = 'delete';
    this.confirmTitle = 'Delete Fine';
    this.confirmMessage = `Are you sure you want to delete this fine? This action cannot be undone.`;
    this.confirmBtnText = 'Delete';
    this.confirmType = 'danger';
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.confirmTarget = items;
    this.confirmAction = 'bulkDelete';
    this.confirmTitle = 'Bulk Delete Fines';
    this.confirmMessage = `Are you sure you want to delete ${items.length} selected fines? This action cannot be undone.`;
    this.confirmBtnText = 'Delete';
    this.confirmType = 'danger';
    this.showConfirm = true;
  }

  executeConfirm() {
    this.showConfirm = false;
    const item = this.confirmTarget;

    if (this.confirmAction === 'waive') {
      this.service.waiveFine(item.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Fine waived successfully'); },
        error: () => this.toastService.error('Failed to waive fine')
      });
    } else if (this.confirmAction === 'delete') {
      this.service.delete(item.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Fine deleted successfully'); },
        error: () => this.toastService.error('Failed to delete fine')
      });
    } else if (this.confirmAction === 'bulkDelete') {
      const ids = item.map((i: any) => i.id);
      let completed = 0;
      ids.forEach((id: number) => {
        this.service.delete(id).subscribe({
          next: () => {
            completed++;
            if (completed === ids.length) { this.loadData(); this.toastService.success(`${ids.length} fines deleted`); }
          },
          error: () => this.toastService.error('Failed to delete some records')
        });
      });
    }
    this.confirmTarget = null;
  }
}
