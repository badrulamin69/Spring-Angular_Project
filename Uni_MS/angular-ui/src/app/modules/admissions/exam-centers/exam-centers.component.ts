import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExamCenterService } from '../../../services/exam-center.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';
import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';

@Component({
  selector: 'app-exam-centers',
  standalone: true,
  imports: [CommonModule, DataTableComponent, DynamicFormComponent, ConfirmDialogComponent],
  template: `
    <div class="page-header">
      <div>
        <h2>Exam Centers</h2>
        <p class="page-sub">Manage examination centers and venues</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add New
      </button>
    </div>
    <app-data-table [columns]="columns" [data]="pagedData?.content || []" [pagedData]="pagedData" [loading]="loading" [params]="params" (pageChange)="onPageChange($event)" (onEdit)="openForm($event)" (onDelete)="confirmDelete($event)" (refresh)="loadData()" (search)="onSearch($event)"></app-data-table>
    @if (showForm) {
      <app-dynamic-form [columns]="columns" [initialData]="editingItem" [title]="editingItem ? 'Edit Exam Center' : 'Add Exam Center'" [saving]="saving" [errorMessage]="formError" (save)="saveItem($event)" (cancel)="closeForm()"></app-dynamic-form>
    }
    @if (showConfirm) {
      <app-confirm-dialog [open]="showConfirm" [title]="confirmTitle" [message]="confirmMessage" confirmText="Delete" type="danger" (confirmed)="executeDelete()" (cancelled)="showConfirm = false"></app-confirm-dialog>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary, #1e293b); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted, #64748b); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color, #4F46E5); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
  `]
})
export class ExamCentersComponent implements OnInit {
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Name', sortable: true, type: 'text', required: true, placeholder: 'Center name' },
    { key: 'code', label: 'Code', sortable: true, type: 'text', required: true, placeholder: 'CENTER-001' },
    { key: 'address', label: 'Address', type: 'textarea', placeholder: 'Full address' },
    { key: 'city', label: 'City', type: 'text', placeholder: 'City name' },
    { key: 'totalCapacity', label: 'Capacity', type: 'number', placeholder: '500' },
    { key: 'contactPerson', label: 'Contact Person', type: 'text', placeholder: 'Name' },
    { key: 'contactPhone', label: 'Contact Phone', type: 'text', placeholder: '+880...' }
  ];
  showForm = false;
  editingItem: any = null;
  formError = '';
  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  constructor(private service: ExamCenterService, private toastService: ToastService) {}
  ngOnInit() { this.loadData(); }
  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load exam centers'); }
    });
  }
  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.searchTerm = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }
  openForm(item?: any) { this.editingItem = item ? { ...item } : null; this.formError = ''; this.showForm = true; }
  closeForm() { this.showForm = false; this.editingItem = null; this.formError = ''; }
  saveItem(data: any) {
    this.saving = true;
    this.formError = '';
    const handleSuccess = (msg: string) => { this.saving = false; this.closeForm(); this.loadData(); this.toastService.success(msg); };
    const handleError = (err: any) => { this.saving = false; this.formError = err.error?.message || err.message || 'Failed'; };
    if (this.editingItem?.id) {
      this.service.update(this.editingItem.id, data).subscribe({ next: () => handleSuccess('Exam center updated'), error: handleError });
    } else {
      this.service.save(data).subscribe({ next: () => handleSuccess('Exam center created'), error: handleError });
    }
  }
  confirmDelete(item: any) { this.deleteTarget = item; this.confirmTitle = 'Delete Exam Center'; this.confirmMessage = `Delete "${item.name}"? This cannot be undone.`; this.showConfirm = true; }
  executeDelete() {
    this.showConfirm = false;
    if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Exam center deleted'); },
        error: () => this.toastService.error('Failed to delete')
      });
    }
    this.deleteTarget = null;
  }
}
