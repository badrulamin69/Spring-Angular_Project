import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChoiceFillingConfigService } from '../../../services/choice-filling-config.service';
import { DataTableComponent, TableColumn, RowAction } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-choice-filling-config',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Choice Filling Configuration</h2>
        <p class="page-sub">Configure choice submission windows for admission sessions</p>
      </div>
      <button class="btn btn-primary" (click)="openModal()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add Configuration
      </button>
    </div>

    <div class="stats-row">
      <div class="stat-card"><span class="stat-val">{{ stats.total || 0 }}</span><span class="stat-lbl">Total Configs</span></div>
      <div class="stat-card draft"><span class="stat-val">{{ stats.draft || 0 }}</span><span class="stat-lbl">Draft</span></div>
      <div class="stat-card active"><span class="stat-val">{{ stats.active || 0 }}</span><span class="stat-lbl">Active</span></div>
      <div class="stat-card closed"><span class="stat-val">{{ stats.closed || 0 }}</span><span class="stat-lbl">Closed</span></div>
    </div>

    <div class="filter-bar">
      <input type="text" placeholder="Search..." [(ngModel)]="filters.search" (keyup.enter)="loadData()">
      <select [(ngModel)]="filters.status" (change)="loadData()">
        <option value="">All Status</option>
        <option value="DRAFT">Draft</option>
        <option value="ACTIVE">Active</option>
        <option value="CLOSED">Closed</option>
      </select>
      <button class="btn btn-sm btn-outline" (click)="loadData()">Search</button>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      [rowActions]="rowActions"
      [showDefaultActions]="false"
      (pageChange)="onPageChange($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showModal) {
      <div class="modal-overlay" (click)="showModal = false">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingItem ? 'Edit Configuration' : 'Add Configuration' }}</h3>
            <button class="close-btn" (click)="showModal = false">&times;</button>
          </div>
          <form (ngSubmit)="saveConfig()">
            <div class="form-row-2">
              <div class="form-group">
                <label>Academic Session *</label>
                <select [(ngModel)]="formData.sessionId" name="sessionId" required>
                  <option [ngValue]="null" disabled>Select Session</option>
                  @for (s of sessions; track s.id) { <option [ngValue]="s.id">{{ s.name }}</option> }
                </select>
              </div>
              <div class="form-group">
                <label>Status</label>
                <select [(ngModel)]="formData.status" name="status">
                  <option value="DRAFT">Draft</option>
                  <option value="ACTIVE">Active</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Choice Start Date *</label>
                <input type="datetime-local" [(ngModel)]="formData.choiceStartDate" name="choiceStartDate" required>
              </div>
              <div class="form-group">
                <label>Choice End Date *</label>
                <input type="datetime-local" [(ngModel)]="formData.choiceEndDate" name="choiceEndDate" required>
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Minimum Choices *</label>
                <input type="number" [(ngModel)]="formData.minChoices" name="minChoices" min="1" required>
              </div>
              <div class="form-group">
                <label>Maximum Choices *</label>
                <input type="number" [(ngModel)]="formData.maxChoices" name="maxChoices" min="1" required>
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="formData.allowEditingBeforeDeadline" name="allowEditing">
                  Allow Editing Before Deadline
                </label>
              </div>
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="formData.autoLockAfterDeadline" name="autoLock">
                  Auto Lock After Deadline
                </label>
              </div>
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" [(ngModel)]="formData.includeWaitingList" name="includeWaiting">
                Include Waiting List Applicants
              </label>
            </div>
            <div class="form-group">
              <label>Remarks</label>
              <textarea [(ngModel)]="formData.remarks" name="remarks" rows="2" placeholder="Optional remarks"></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="showModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving">{{ saving ? 'Saving...' : 'Save' }}</button>
            </div>
          </form>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary, #1e293b); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted, #64748b); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color, #4F46E5); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-secondary { background: #e2e8f0; color: #475569; }
    .btn-outline { background: white; color: #475569; border: 1px solid #d1d5db; }
    .btn-sm { padding: 6px 12px; font-size: 0.8125rem; }
    .btn-success { background: #28a745; color: #fff; }
    .btn-warning { background: #e6a817; color: #fff; }
    .btn-danger { background: #dc3545; color: #fff; }
    :host ::ng-deep .btn-icon-success { color: #28a745; }
    :host ::ng-deep .btn-icon-success:hover { background: #f0fdf4; color: #1e7e34; }
    :host ::ng-deep .btn-icon-warning { color: #e6a817; }
    :host ::ng-deep .btn-icon-warning:hover { background: #fffbeb; color: #b38600; }
    :host ::ng-deep .btn-icon-danger { color: #dc3545; }
    :host ::ng-deep .btn-icon-danger:hover { background: #fef2f2; color: #bd2130; }
    :host ::ng-deep .btn-icon-info { color: #0056b3; }
    :host ::ng-deep .btn-icon-info:hover { background: #eff6ff; color: #004080; }
    .stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 1.25rem; }
    .stat-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 1rem; text-align: center; }
    .stat-card .stat-val { display: block; font-size: 1.75rem; font-weight: 700; color: #1e293b; }
    .stat-card .stat-lbl { font-size: 0.8125rem; color: #64748b; }
    .stat-card.draft .stat-val { color: #e6a817; }
    .stat-card.active .stat-val { color: #28a745; }
    .stat-card.closed .stat-val { color: #6b7280; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar input, .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .filter-bar input { flex: 1; min-width: 200px; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; padding: 1.5rem; width: 90%; max-width: 700px; max-height: 90vh; overflow-y: auto; }
    .modal-lg { max-width: 700px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; color: #1e293b; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b; }
    .form-group { margin-bottom: 0.75rem; }
    .form-group label { display: block; margin-bottom: 0.25rem; font-weight: 500; color: #374151; font-size: 0.8125rem; }
    .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.875rem; box-sizing: border-box; }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { outline: none; border-color: #4F46E5; box-shadow: 0 0 0 2px rgba(79,70,229,0.1); }
    .form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #e2e8f0; }
    .checkbox-label { display: flex !important; align-items: center; gap: 0.5rem; cursor: pointer; }
    .checkbox-label input[type="checkbox"] { width: auto; }
  `]
})
export class ChoiceFillingConfigComponent implements OnInit {
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  stats: any = {};
  sessions: any[] = [];
  filters: any = { search: '', status: '' };

  showModal = false;
  saving = false;
  editingItem: any = null;
  formData: any = this.getEmptyForm();

  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'session.name', label: 'Session', sortable: true },
    { key: 'choiceStartDate', label: 'Start Date', type: 'date' },
    { key: 'choiceEndDate', label: 'End Date', type: 'date' },
    { key: 'minChoices', label: 'Min', type: 'number' },
    { key: 'maxChoices', label: 'Max', type: 'number' },
    { key: 'status', label: 'Status', sortable: true }
  ];

  rowActions: RowAction[] = [
    { label: 'Edit', icon: '✏️', title: 'Edit', onClick: (item) => this.openModal(item) },
    { label: 'Activate', icon: '✅', title: 'Activate', class: 'btn-icon-success', condition: (item) => item.status !== 'ACTIVE', onClick: (item) => this.activateConfig(item) },
    { label: 'Close', icon: '🔒', title: 'Close', class: 'btn-icon-warning', condition: (item) => item.status === 'ACTIVE', onClick: (item) => this.closeConfig(item) },
    { label: 'Delete', icon: '🗑', title: 'Delete', class: 'btn-icon-danger', condition: (item) => item.status !== 'ACTIVE', onClick: (item) => this.deleteConfig(item) },
  ];

  constructor(
    private configService: ChoiceFillingConfigService,
    private toastService: ToastService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.loadData();
    this.loadStats();
    this.loadSessions();
  }

  loadSessions() {
    this.http.get<any>(`${environment.apiUrl}/academic-sessions?page=0&size=100`).subscribe({
      next: (res) => { this.sessions = res.content || res || []; },
      error: () => { this.sessions = []; }
    });
  }

  loadStats() {
    this.configService.getStats().subscribe({
      next: (data) => { this.stats = data; },
      error: () => this.toastService.error('Operation failed. Please try again.')
    });
  }

  loadData() {
    this.loading = true;
    this.configService.findAll(this.params, this.filters).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load configurations'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.filters.search = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  getEmptyForm() {
    return {
      sessionId: null, choiceStartDate: '', choiceEndDate: '',
      minChoices: 1, maxChoices: 10, allowEditingBeforeDeadline: true,
      autoLockAfterDeadline: true, includeWaitingList: false,
      status: 'DRAFT', remarks: ''
    };
  }

  openModal(item?: any) {
    this.editingItem = item ? { ...item } : null;
    if (item) {
      this.formData = {
        sessionId: item.sessionId || item.session?.id || null,
        choiceStartDate: item.choiceStartDate ? item.choiceStartDate.substring(0, 16) : '',
        choiceEndDate: item.choiceEndDate ? item.choiceEndDate.substring(0, 16) : '',
        minChoices: item.minChoices || 1,
        maxChoices: item.maxChoices || 10,
        allowEditingBeforeDeadline: item.allowEditingBeforeDeadline ?? true,
        autoLockAfterDeadline: item.autoLockAfterDeadline ?? true,
        includeWaitingList: item.includeWaitingList ?? false,
        status: item.status || 'DRAFT',
        remarks: item.remarks || ''
      };
    } else {
      this.formData = this.getEmptyForm();
    }
    this.showModal = true;
  }

  saveConfig() {
    this.saving = true;
    const payload: any = {
      session: { id: this.formData.sessionId },
      choiceStartDate: this.formData.choiceStartDate,
      choiceEndDate: this.formData.choiceEndDate,
      minChoices: this.formData.minChoices,
      maxChoices: this.formData.maxChoices,
      allowEditingBeforeDeadline: this.formData.allowEditingBeforeDeadline,
      autoLockAfterDeadline: this.formData.autoLockAfterDeadline,
      includeWaitingList: this.formData.includeWaitingList,
      status: this.formData.status,
      remarks: this.formData.remarks,
      isActive: this.formData.status === 'ACTIVE'
    };

    const req = this.editingItem?.id
      ? this.configService.update(this.editingItem.id, payload)
      : this.configService.save(payload);

    req.subscribe({
      next: () => {
        this.saving = false;
        this.showModal = false;
        this.toastService.success(this.editingItem ? 'Configuration updated' : 'Configuration created');
        this.loadData();
        this.loadStats();
      },
      error: (err) => { this.saving = false; this.toastService.error(err.error?.message || 'Failed to save'); }
    });
  }

  activateConfig(item: any) {
    this.configService.activate(item.id).subscribe({
      next: () => { this.loadData(); this.loadStats(); this.toastService.success('Configuration activated'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to activate')
    });
  }

  closeConfig(item: any) {
    this.configService.close(item.id).subscribe({
      next: () => { this.loadData(); this.loadStats(); this.toastService.success('Configuration closed'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to close')
    });
  }

  deleteConfig(item: any) {
    if (confirm(`Delete this configuration?`)) {
      this.configService.delete(item.id).subscribe({
        next: () => { this.loadData(); this.loadStats(); this.toastService.success('Configuration deleted'); },
        error: (err) => this.toastService.error(err.error?.message || 'Failed to delete')
      });
    }
  }
}
