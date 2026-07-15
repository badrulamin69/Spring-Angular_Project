import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdmissionEnrollmentService } from '../../../services/admission-enrollment.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-enrollments',
  standalone: true,
  imports: [CommonModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div><h2>Admission Enrollments</h2><p class="page-sub">Manage admission enrollment records</p></div>
      <button class="btn btn-primary" (click)="openForm()"><svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg> Add New</button>
    </div>
    <app-data-table [columns]="columns" [data]="pagedData?.content || []" [pagedData]="pagedData" [loading]="loading" [params]="params" (pageChange)="onPageChange($event)" (onEdit)="openForm($event)" (onDelete)="confirmDelete($event)" (bulkDelete)="confirmBulkDelete($event)" (refresh)="loadData()" (search)="onSearch($event)"></app-data-table>
    @if (showForm) { <app-dynamic-form [columns]="columns" [initialData]="editingItem" [title]="editingItem ? 'Edit Enrollment' : 'Add New Enrollment'" [saving]="saving" [errorMessage]="formError" (save)="saveItem($event)" (cancel)="closeForm()"></app-dynamic-form> }
    <app-confirm-dialog [open]="showConfirm" [title]="confirmTitle" [message]="confirmMessage" confirmText="Delete" type="danger" (confirmed)="executeDelete()" (cancelled)="showConfirm = false"></app-confirm-dialog>
  `,
  styles: [`.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; } .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; } .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); } .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; } .btn-primary { background: var(--brand-color); color: #fff; }`]
})
export class EnrollmentsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true; saving = false; params: PageParams = { ...DEFAULT_PAGE_PARAMS }; searchTerm = '';
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'enrollmentNumber', label: 'Enrollment Number', sortable: true, type: 'text', placeholder: 'EN001' },
    { key: 'applicationId', label: 'Application ID', type: 'number', required: true, placeholder: 'Application ID' },
    { key: 'programId', label: 'Program ID', type: 'number', required: true, placeholder: 'Program ID' },
    { key: 'semesterId', label: 'Semester ID', type: 'number', placeholder: 'Semester ID' },
    { key: 'batchId', label: 'Batch ID', type: 'number', placeholder: 'Batch ID' },
    { key: 'sectionId', label: 'Section ID', type: 'number', placeholder: 'Section ID' },
    { key: 'status', label: 'Status', type: 'select', options: [{ label: 'Enrolled', value: 'ENROLLED' }, { label: 'Pending', value: 'PENDING' }, { label: 'Cancelled', value: 'CANCELLED' }] },
    { key: 'isDocumentVerified', label: 'Document Verified', type: 'select', options: [{ label: 'Yes', value: true }, { label: 'No', value: false }] },
    { key: 'isFeePaid', label: 'Fee Paid', type: 'select', options: [{ label: 'Yes', value: true }, { label: 'No', value: false }] },
    { key: 'totalFeePaid', label: 'Total Fee Paid', type: 'number', placeholder: '0' }
  ];
  showForm = false; editingItem: any = null; formError = '';
  showConfirm = false; confirmTitle = ''; confirmMessage = ''; deleteTarget: any = null;

  constructor(private service: AdmissionEnrollmentService, private toastService: ToastService) {}
  ngOnInit() { this.loadData(); }
  loadData() { this.loading = true; this.service.findAll(this.params).subscribe({ next: (data) => { this.pagedData = data; this.loading = false; }, error: () => { this.loading = false; this.toastService.error('Failed to load enrollments'); } }); }
  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.searchTerm = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }
  openForm(item?: any) { this.editingItem = item ? { ...item } : null; this.formError = ''; this.showForm = true; }
  closeForm() { this.showForm = false; this.editingItem = null; this.formError = ''; }
  saveItem(data: any) {
    this.saving = true; this.formError = '';
    const handleSuccess = (msg: string) => { this.saving = false; this.closeForm(); this.loadData(); this.toastService.success(msg); };
    const handleError = (err: any) => { this.saving = false; this.formError = err.error?.message || err.message || 'Validation failed.'; };
    if (this.editingItem?.id) { this.service.update(this.editingItem.id, data).subscribe({ next: () => handleSuccess('Enrollment updated'), error: handleError }); }
    else { this.service.create(data).subscribe({ next: () => handleSuccess('Enrollment created'), error: handleError }); }
  }
  confirmDelete(item: any) { this.deleteTarget = item; this.confirmTitle = 'Delete Enrollment'; this.confirmMessage = `Delete "${item.enrollmentNumber}"?`; this.showConfirm = true; }
  confirmBulkDelete(items: any[]) { this.deleteTarget = items; this.confirmTitle = 'Bulk Delete'; this.confirmMessage = `Delete ${items.length} enrollments?`; this.showConfirm = true; }
  executeDelete() {
    this.showConfirm = false;
    if (Array.isArray(this.deleteTarget)) { let c = 0; this.deleteTarget.forEach((i: any) => { this.service.delete(i.id).subscribe({ next: () => { c++; if (c === this.deleteTarget.length) { this.loadData(); this.toastService.success('Deleted'); } } }); }); }
    else if (this.deleteTarget?.id) { this.service.delete(this.deleteTarget.id).subscribe({ next: () => { this.loadData(); this.toastService.success('Deleted'); } }); }
    this.deleteTarget = null;
  }
}

