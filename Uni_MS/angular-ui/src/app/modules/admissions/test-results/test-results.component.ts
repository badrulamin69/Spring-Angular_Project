import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdmissionTestResultService } from '../../../services/admission-test-result.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-test-results',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Admission Test Results</h2>
        <p class="page-sub">Enter and manage admission test marks</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="openBulkForm()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1 3h12M1 7h12M1 11h12" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/></svg>
          Bulk Entry
        </button>
        <button class="btn btn-primary" (click)="openForm()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          Enter Marks
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

    @if (showBulkForm) {
      <div class="modal-overlay" (click)="showBulkForm = false">
        <div class="modal-content bulk-modal" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Bulk Entry - Test Results</h3>
            <button class="close-btn" (click)="showBulkForm = false">&times;</button>
          </div>

          <div class="bulk-tabs">
            <button [class.active]="bulkMode === 'manual'" (click)="bulkMode = 'manual'">Manual Entry</button>
            <button [class.active]="bulkMode === 'csv'" (click)="bulkMode = 'csv'">CSV Upload</button>
          </div>

          @if (bulkMode === 'manual') {
            <div class="bulk-manual">
              <div class="bulk-table-header">
                <span>Registration ID</span>
                <span>Written</span>
                <span>MCQ</span>
                <span>Viva</span>
                <span></span>
              </div>
              @for (row of bulkRows; track $index) {
                <div class="bulk-table-row">
                  <input type="number" [(ngModel)]="row.registrationId" placeholder="Reg ID" class="bulk-input">
                  <input type="number" [(ngModel)]="row.writtenMarks" placeholder="0" class="bulk-input" min="0">
                  <input type="number" [(ngModel)]="row.mcqMarks" placeholder="0" class="bulk-input" min="0">
                  <input type="number" [(ngModel)]="row.vivaMarks" placeholder="0" class="bulk-input" min="0">
                  <button class="btn-icon btn-icon-danger" (click)="removeBulkRow($index)" title="Remove">
                    <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M3.5 3.5l7 7M10.5 3.5l-7 7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                  </button>
                </div>
              }
              <button class="btn btn-sm btn-outline" (click)="addBulkRow()">+ Add Row</button>
            </div>
          }

          @if (bulkMode === 'csv') {
            <div class="bulk-csv">
              <div class="upload-zone" (dragover)="$event.preventDefault()" (drop)="onCsvDrop($event)">
                <svg width="40" height="40" viewBox="0 0 40 40" fill="none"><rect width="40" height="40" rx="8" fill="#f1f5f9"/><path d="M20 12v10M15 17l5-5 5 5M12 26h16" stroke="#64748b" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
                <p>Drag CSV file here or <label class="upload-link" for="csvInput">browse</label></p>
                <input type="file" id="csvInput" accept=".csv" (change)="onCsvSelect($event)" hidden>
                <span class="upload-hint">Format: registrationId, writtenMarks, mcqMarks, vivaMarks</span>
              </div>
              @if (csvData.length > 0) {
                <div class="csv-preview">
                  <p>{{ csvData.length }} rows loaded</p>
                  <div class="csv-table">
                    <div class="csv-header"><span>Reg ID</span><span>Written</span><span>MCQ</span><span>Viva</span></div>
                    @for (row of csvData.slice(0, 5); track $index) {
                      <div class="csv-row"><span>{{ row.registrationId }}</span><span>{{ row.writtenMarks }}</span><span>{{ row.mcqMarks }}</span><span>{{ row.vivaMarks }}</span></div>
                    }
                    @if (csvData.length > 5) {
                      <div class="csv-row csv-more">... and {{ csvData.length - 5 }} more rows</div>
                    }
                  </div>
                </div>
              }
            </div>
          }

          @if (bulkResult) {
            <div class="bulk-result" [class.has-errors]="bulkResult.errorCount > 0">
              <p><strong>{{ bulkResult.message }}</strong></p>
              @if (bulkResult.errors?.length > 0) {
                <ul>
                  @for (err of bulkResult.errors; track $index) {
                    <li>{{ err }}</li>
                  }
                </ul>
              }
            </div>
          }

          <div class="bulk-actions">
            <button class="btn btn-outline" (click)="showBulkForm = false">Cancel</button>
            <button class="btn btn-primary" (click)="submitBulk()" [disabled]="bulkSaving || getBulkData().length === 0">
              {{ bulkSaving ? 'Saving...' : 'Submit ' + getBulkData().length + ' Results' }}
            </button>
          </div>
        </div>
      </div>
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
    .header-actions { display: flex; gap: 8px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; }
    .btn-sm { padding: 5px 10px; font-size: 0.8125rem; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: #fff; border-radius: 12px; padding: 24px; max-width: 700px; width: 90%; max-height: 85vh; overflow-y: auto; }
    .bulk-modal { max-width: 800px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; }
    .bulk-tabs { display: flex; gap: 4px; margin-bottom: 16px; background: var(--bg-secondary); border-radius: 8px; padding: 4px; }
    .bulk-tabs button { flex: 1; padding: 8px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8125rem; font-weight: 500; background: transparent; color: var(--text-muted); }
    .bulk-tabs button.active { background: var(--bg-primary); color: var(--text-primary); box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .bulk-table-header, .bulk-table-row { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr 40px; gap: 8px; align-items: center; margin-bottom: 6px; }
    .bulk-table-header span { font-size: 0.75rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase; }
    .bulk-input { padding: 8px 10px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; width: 100%; box-sizing: border-box; }
    .bulk-input:focus { outline: none; border-color: var(--brand-color); }
    .btn-icon { background: none; border: none; cursor: pointer; padding: 4px 6px; border-radius: 4px; color: var(--text-muted); display: inline-flex; }
    .btn-icon-danger:hover { background: #fef2f2; color: #ef4444; }
    .upload-zone { border: 2px dashed var(--border-color); border-radius: 10px; padding: 32px; text-align: center; cursor: pointer; transition: border-color 0.15s; }
    .upload-zone:hover { border-color: var(--brand-color); }
    .upload-zone p { margin: 8px 0 4px; color: var(--text-secondary); font-size: 0.875rem; }
    .upload-link { color: var(--brand-color); cursor: pointer; font-weight: 500; }
    .upload-hint { font-size: 0.75rem; color: var(--text-muted); }
    .csv-preview { margin-top: 12px; }
    .csv-preview p { margin: 0 0 8px; font-size: 0.875rem; color: var(--text-secondary); }
    .csv-table { border: 1px solid var(--border-color); border-radius: 6px; overflow: hidden; }
    .csv-header, .csv-row { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; padding: 6px 12px; }
    .csv-header { background: var(--bg-secondary); font-size: 0.75rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase; }
    .csv-row { border-top: 1px solid var(--border-color); font-size: 0.8125rem; color: var(--text-primary); }
    .csv-more { color: var(--text-muted); font-style: italic; }
    .bulk-result { margin-top: 12px; padding: 12px; border-radius: 8px; background: #d1fae5; color: #065f46; }
    .bulk-result.has-errors { background: #fee2e2; color: #991b1b; }
    .bulk-result p { margin: 0 0 4px; }
    .bulk-result ul { margin: 4px 0 0; padding-left: 16px; font-size: 0.8125rem; }
    .bulk-actions { margin-top: 16px; display: flex; justify-content: flex-end; gap: 8px; }
  `]
})
export class TestResultsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
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

  showBulkForm = false;
  bulkMode: 'manual' | 'csv' = 'manual';
  bulkRows: any[] = [{ registrationId: null, writtenMarks: null, mcqMarks: null, vivaMarks: null }];
  csvData: any[] = [];
  bulkSaving = false;
  bulkResult: any = null;

  constructor(private service: AdmissionTestResultService, private toastService: ToastService) {}

  ngOnInit() { this.loadData(); }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load test results'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.searchTerm = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

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

  openBulkForm() {
    this.bulkRows = [{ registrationId: null, writtenMarks: null, mcqMarks: null, vivaMarks: null }];
    this.csvData = [];
    this.bulkResult = null;
    this.bulkMode = 'manual';
    this.showBulkForm = true;
  }

  addBulkRow() {
    this.bulkRows.push({ registrationId: null, writtenMarks: null, mcqMarks: null, vivaMarks: null });
  }

  removeBulkRow(index: number) {
    this.bulkRows.splice(index, 1);
  }

  onCsvSelect(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) this.parseCsv(file);
  }

  onCsvDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files?.[0];
    if (file) this.parseCsv(file);
  }

  parseCsv(file: File) {
    const reader = new FileReader();
    reader.onload = (e) => {
      const text = e.target?.result as string;
      const lines = text.split('\n').filter(l => l.trim());
      const data: any[] = [];
      for (let i = 0; i < lines.length; i++) {
        const parts = lines[i].split(',').map(p => p.trim());
        if (parts.length >= 4 && (!isNaN(Number(parts[0])) || i > 0)) {
          const startIdx = i === 0 && isNaN(Number(parts[0])) ? 1 : 0;
          if (i >= startIdx) {
            data.push({
              registrationId: Number(parts[startIdx]),
              writtenMarks: Number(parts[startIdx + 1]),
              mcqMarks: Number(parts[startIdx + 2]),
              vivaMarks: Number(parts[startIdx + 3])
            });
          }
        }
      }
      this.csvData = data;
    };
    reader.readAsText(file);
  }

  getBulkData(): any[] {
    if (this.bulkMode === 'csv') return this.csvData;
    return this.bulkRows.filter(r => r.registrationId);
  }

  submitBulk() {
    const data = this.getBulkData();
    if (data.length === 0) return;
    this.bulkSaving = true;
    this.bulkResult = null;
    this.service.saveBulk(data).subscribe({
      next: (res) => {
        this.bulkSaving = false;
        this.bulkResult = res;
        if (res.errorCount === 0) {
          this.loadData();
        }
      },
      error: (err) => {
        this.bulkSaving = false;
        this.bulkResult = { message: err.error?.message || 'Bulk save failed', errorCount: data.length, errors: [] };
      }
    });
  }
}
