import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AcademicPolicyService } from '../../../services/academic-policy.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-academic-policies',
  standalone: true,
  imports: [CommonModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Academic Policies</h2>
        <p class="page-sub">Manage academic policies and regulations</p>
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
        [columns]="columns"
        [initialData]="editingItem"
        [title]="editingItem ? 'Edit Academic Policy' : 'Add New Academic Policy'"
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
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
  `]
})
export class AcademicPoliciesComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Name', sortable: true, type: 'text', required: true, placeholder: 'Policy name' },
    { key: 'description', label: 'Description', type: 'textarea', required: true },
    { key: 'policyType', label: 'Type', sortable: true, type: 'select', options: [{ label: 'Grading', value: 'Grading' }, { label: 'Attendance', value: 'Attendance' }, { label: 'Examination', value: 'Examination' }, { label: 'Promotion', value: 'Promotion' }, { label: 'Discipline', value: 'Discipline' }, { label: 'Credit Transfer', value: 'Credit Transfer' }, { label: 'Other', value: 'Other' }], required: true },
    { key: 'policyValue', label: 'Value', type: 'textarea', placeholder: 'Policy content' },
    { key: 'programId', label: 'Program ID', type: 'number', placeholder: 'Leave empty for global' },
    { key: 'isActive', label: 'Active', type: 'checkbox' },
    { key: 'effectiveFrom', label: 'Effective From', type: 'date' },
    { key: 'effectiveTo', label: 'Effective To', type: 'date' }
  ];

  showForm = false;
  editingItem: any = null;
  formError = '';

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  constructor(private service: AcademicPolicyService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load academic policies'); }
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
        next: () => handleSuccess('Academic policy updated successfully'),
        error: handleError
      });
    } else {
      this.service.save(data).subscribe({
        next: () => handleSuccess('Academic policy created successfully'),
        error: handleError
      });
    }
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Academic Policy';
    this.confirmMessage = `Are you sure you want to delete "${item.name}"? This action cannot be undone.`;
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.deleteTarget = items;
    this.confirmTitle = 'Bulk Delete Academic Policies';
    this.confirmMessage = `Are you sure you want to delete ${items.length} selected academic policies? This action cannot be undone.`;
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
              this.toastService.success(`${ids.length} academic policies deleted`);
            }
          },
          error: () => this.toastService.error('Failed to delete some records')
        });
      });
    } else if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => {
          this.loadData();
          this.toastService.success('Academic policy deleted successfully');
        },
        error: () => this.toastService.error('Failed to delete academic policy')
      });
    }
    this.deleteTarget = null;
  }
}
