import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-student-id-generation',
  standalone: true,
  imports: [CommonModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div><h2>Student ID Generation</h2><p class="page-sub">Generate and manage student identification numbers</p></div>
      <button class="btn btn-primary" (click)="openForm()"><svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg> Generate ID</button>
    </div>
    <app-data-table [columns]="columns" [data]="pagedData?.content || []" [pagedData]="pagedData" [loading]="loading" [params]="params" (pageChange)="onPageChange($event)" (onEdit)="openForm($event)" (onDelete)="confirmDelete($event)" (bulkDelete)="confirmBulkDelete($event)" (refresh)="loadData()" (search)="onSearch($event)"></app-data-table>
    @if (showForm) { <app-dynamic-form [columns]="columns" [initialData]="editingItem" [title]="editingItem ? 'Edit Student ID' : 'Generate New Student ID'" [saving]="saving" [errorMessage]="formError" (save)="saveItem($event)" (cancel)="closeForm()"></app-dynamic-form> }
    <app-confirm-dialog [open]="showConfirm" [title]="confirmTitle" [message]="confirmMessage" confirmText="Delete" type="danger" (confirmed)="executeDelete()" (cancelled)="showConfirm = false"></app-confirm-dialog>
  `,
  styles: [`.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; } .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; } .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); } .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; } .btn-primary { background: var(--brand-color); color: #fff; }`]
})
export class StudentIdGenerationComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true; saving = false; params: PageParams = { ...DEFAULT_PAGE_PARAMS }; searchTerm = '';
  private api = `${environment.apiUrl}/student-id-generation`;
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'studentId', label: 'Student ID', sortable: true, type: 'text', required: true, placeholder: 'STU2026001' },
    { key: 'studentName', label: 'Student Name', sortable: true, type: 'text', required: true, placeholder: 'Full name' },
    { key: 'program', label: 'Program', sortable: true, type: 'text', placeholder: 'Program name' },
    { key: 'batch', label: 'Batch', type: 'text', placeholder: '2026' },
    { key: 'generatedDate', label: 'Generated Date', type: 'date' },
    { key: 'idCardStatus', label: 'Card Status', type: 'select', options: [{ label: 'Pending', value: 'PENDING' }, { label: 'Printed', value: 'PRINTED' }, { label: 'Distributed', value: 'DISTRIBUTED' }, { label: 'Damaged', value: 'DAMAGED' }] },
    { key: 'status', label: 'Status', type: 'select', options: [{ label: 'Active', value: 'ACTIVE' }, { label: 'Inactive', value: 'INACTIVE' }, { label: 'Revoked', value: 'REVOKED' }] }
  ];
  showForm = false; editingItem: any = null; formError = '';
  showConfirm = false; confirmTitle = ''; confirmMessage = ''; deleteTarget: any = null;

  constructor(private http: HttpClient, private toastService: ToastService) {}
  ngOnInit() { this.loadData(); }
  loadData() { this.loading = true; const p = this.params; const params: any = { page: p.page, size: p.size, sort: p.sortBy, direction: p.sortDir }; if (this.searchTerm) { params.search = this.searchTerm; } this.http.get<PagedResponse<any>>(this.api, { params }).subscribe({ next: (data) => { this.pagedData = data; this.loading = false; }, error: () => { this.pagedData = { content: [], page: 0, size: 20, totalElements: 0, totalPages: 0, first: true, last: true, empty: true }; this.loading = false; } }); }
  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.searchTerm = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }
  openForm(item?: any) { this.editingItem = item ? { ...item } : null; this.formError = ''; this.showForm = true; }
  closeForm() { this.showForm = false; this.editingItem = null; this.formError = ''; }
  saveItem(data: any) {
    this.saving = true; this.formError = '';
    const ok = (m: string) => { this.saving = false; this.closeForm(); this.loadData(); this.toastService.success(m); };
    const err = (e: any) => { this.saving = false; this.formError = e.error?.message || 'Failed.'; };
    if (this.editingItem?.id) { this.http.put(`${this.api}/${this.editingItem.id}`, data).subscribe({ next: () => ok('Updated'), error: err }); }
    else { this.http.post(this.api, data).subscribe({ next: () => ok('ID generated'), error: err }); }
  }
  confirmDelete(item: any) { this.deleteTarget = item; this.confirmTitle = 'Revoke Student ID'; this.confirmMessage = `Revoke ID "${item.studentId}"?`; this.showConfirm = true; }
  confirmBulkDelete(items: any[]) { this.deleteTarget = items; this.confirmTitle = 'Bulk Revoke'; this.confirmMessage = `Revoke ${items.length} student IDs?`; this.showConfirm = true; }
  executeDelete() { this.showConfirm = false; if (Array.isArray(this.deleteTarget)) { let c = 0; this.deleteTarget.forEach((i: any) => { this.http.delete(`${this.api}/${i.id}`).subscribe({ next: () => { c++; if (c === this.deleteTarget.length) { this.loadData(); this.toastService.success('Revoked'); } } }); }); } else if (this.deleteTarget?.id) { this.http.delete(`${this.api}/${this.deleteTarget.id}`).subscribe({ next: () => { this.loadData(); this.toastService.success('Revoked'); } }); } this.deleteTarget = null; }
}
