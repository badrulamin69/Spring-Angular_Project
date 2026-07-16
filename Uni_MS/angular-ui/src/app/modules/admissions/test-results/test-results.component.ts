import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdmissionTestResultService } from '../../../services/admission-test-result.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-test-results',
  standalone: true,
  imports: [CommonModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Admission Test Results</h2>
        <p class="page-sub">Enter and manage admission test marks</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Enter Marks
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
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showForm) {
      <app-dynamic-form
        [columns]="columns"
        [initialData]="editingItem"
        [title]="editingItem ? 'Edit Test Result' : 'Enter Test Marks'"
        [saving]="saving"
        [errorMessage]="formError"
        (save)="saveItem($event)"
        (cancel)="closeForm()">
      </app-dynamic-form>
    }

    <app-confirm-dialog
      [open]="showConfirm"
      [title]="'Delete Test Result'"
      [message]="'Are you sure you want to delete this test result?'"
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
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; }
    .btn-primary { background: var(--brand-color); color: #fff; }
  `]
})
export class TestResultsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'registrationId', label: 'Registration ID', type: 'number', required: true },
    { key: 'writtenMarks', label: 'Written Marks', type: 'number', required: true },
    { key: 'mcqMarks', label: 'MCQ Marks', type: 'number', required: true },
    { key: 'vivaMarks', label: 'Viva Marks', type: 'number', required: true },
    { key: 'totalWeightedScore', label: 'Weighted Score', type: 'number' },
    { key: 'status', label: 'Status', type: 'text' }
  ];
  showForm = false;
  editingItem: any = null;
  formError = '';
  showConfirm = false;
  deleteTarget: any = null;

  constructor(private service: AdmissionTestResultService, private toastService: ToastService) {}

  ngOnInit() { this.loadData(); }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load test results'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  openForm(item?: any) {
    this.editingItem = item ? { ...item } : null;
    this.formError = '';
    this.showForm = true;
  }

  closeForm() { this.showForm = false; this.editingItem = null; this.formError = ''; }

  saveItem(data: any) {
    this.saving = true;
    this.formError = '';
    const handleSuccess = (msg: string) => { this.saving = false; this.closeForm(); this.loadData(); this.toastService.success(msg); };
    const handleError = (err: any) => { this.saving = false; this.formError = err.error?.message || 'Failed'; };
    if (this.editingItem?.id) {
      this.service.update(this.editingItem.id, data).subscribe({ next: () => handleSuccess('Updated'), error: handleError });
    } else {
      this.service.save(data).subscribe({ next: () => handleSuccess('Created'), error: handleError });
    }
  }

  confirmDelete(item: any) { this.deleteTarget = item; this.showConfirm = true; }

  executeDelete() {
    this.showConfirm = false;
    if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Deleted'); },
        error: () => this.toastService.error('Failed to delete')
      });
    }
  }
}
