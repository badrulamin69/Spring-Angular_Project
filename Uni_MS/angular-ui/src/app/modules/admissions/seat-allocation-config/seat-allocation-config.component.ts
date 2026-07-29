import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataTableComponent, TableColumn, RowAction } from '../../../shared/data-table/data-table.component';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { SeatAllocationConfig } from '../../../models/seat-allocation';
import { SeatAllocationConfigService } from '../../../services/seat-allocation-config.service';
import { AcademicSessionService } from '../../../services/academic-session.service';
import { map } from 'rxjs';

@Component({
  selector: 'app-seat-allocation-config',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Seat Allocation Configuration</h2>
        <p class="subtitle">Configure allocation rounds, deadlines, and rules</p>
      </div>
      <button class="btn btn-primary" (click)="openModal()">+ New Configuration</button>
    </div>

    <div class="stats-row">
      <div class="stat-card"><div class="stat-value">{{ stats.total || 0 }}</div><div class="stat-label">Total Configs</div></div>
      <div class="stat-card"><div class="stat-value">{{ stats.active || 0 }}</div><div class="stat-label">Active</div></div>
      <div class="stat-card"><div class="stat-value">{{ stats.draft || 0 }}</div><div class="stat-label">Draft</div></div>
      <div class="stat-card"><div class="stat-value">{{ stats.closed || 0 }}</div><div class="stat-label">Closed</div></div>
    </div>

    <div class="filter-bar">
      <input type="text" placeholder="Search sessions..." [(ngModel)]="filters.search" (keyup.enter)="loadData()" class="form-control" />
      <select [(ngModel)]="filters.status" (change)="loadData()" class="form-control">
        <option value="">All Status</option>
        <option value="DRAFT">Draft</option>
        <option value="ACTIVE">Active</option>
        <option value="CLOSED">Closed</option>
      </select>
      <button class="btn btn-secondary" (click)="loadData()">Search</button>
    </div>

    <app-data-table [columns]="columns" [data]="pagedData?.content || []" [pagedData]="pagedData"
      [loading]="loading" [params]="params" [rowActions]="rowActions" [showDefaultActions]="false"
      (pageChange)="onPageChange($event)" (refresh)="loadData()" (search)="onSearch($event)">
    </app-data-table>

    @if (showModal) {
      <div class="modal-overlay" (click)="showModal = false">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingItem ? 'Edit' : 'New' }} Allocation Configuration</h3>
            <button class="btn-close" (click)="showModal = false">&times;</button>
          </div>
          <form (ngSubmit)="saveConfig()">
            <div class="form-row-2">
              <div class="form-group">
                <label>Session *</label>
                <select [(ngModel)]="formData.sessionId" name="sessionId" class="form-control" required>
                  <option value="">Select Session</option>
                  @for (s of sessions; track s.id) {
                    <option [value]="s.id">{{ s.name }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Academic Year *</label>
                <input type="text" [(ngModel)]="formData.academicYear" name="academicYear" class="form-control" required placeholder="e.g. 2025-2026" />
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Allocation Round</label>
                <input type="number" [(ngModel)]="formData.allocationRound" name="allocationRound" class="form-control" min="1" />
              </div>
              <div class="form-group">
                <label>Accept Deadline (Hours)</label>
                <input type="number" [(ngModel)]="formData.acceptDeadlineHours" name="acceptDeadlineHours" class="form-control" min="1" />
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Allocation Start Date *</label>
                <input type="datetime-local" [(ngModel)]="formData.allocationStartDate" name="allocationStartDate" class="form-control" required />
              </div>
              <div class="form-group">
                <label>Allocation End Date *</label>
                <input type="datetime-local" [(ngModel)]="formData.allocationEndDate" name="allocationEndDate" class="form-control" required />
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label><input type="checkbox" [(ngModel)]="formData.autoAllocation" name="autoAllocation" /> Auto Allocation</label>
              </div>
              <div class="form-group">
                <label><input type="checkbox" [(ngModel)]="formData.manualAllocation" name="manualAllocation" /> Manual Allocation</label>
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label><input type="checkbox" [(ngModel)]="formData.enableQuota" name="enableQuota" /> Enable Quota</label>
              </div>
              <div class="form-group">
                <label><input type="checkbox" [(ngModel)]="formData.enableReservedSeats" name="enableReservedSeats" /> Enable Reserved Seats</label>
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label><input type="checkbox" [(ngModel)]="formData.lockAfterPublish" name="lockAfterPublish" /> Lock After Publish</label>
              </div>
              <div class="form-group">
                <label>Remarks</label>
                <input type="text" [(ngModel)]="formData.remarks" name="remarks" class="form-control" />
              </div>
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
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .page-header h2 { margin: 0; font-size: 24px; }
    .subtitle { color: #6b7280; margin: 4px 0 0; }
    .stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
    .stat-card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); text-align: center; }
    .stat-value { font-size: 28px; font-weight: 700; color: #1e40af; }
    .stat-label { font-size: 13px; color: #6b7280; margin-top: 4px; }
    .filter-bar { display: flex; gap: 12px; margin-bottom: 20px; }
    .filter-bar .form-control { flex: 1; }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; width: 90%; max-width: 700px; max-height: 90vh; overflow-y: auto; padding: 24px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .modal-header h3 { margin: 0; }
    .btn-close { background: none; border: none; font-size: 24px; cursor: pointer; }
    .form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
    .form-group { display: flex; flex-direction: column; }
    .form-group label { font-weight: 500; margin-bottom: 6px; font-size: 14px; }
    .form-control { padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; }
    .form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; padding-top: 16px; border-top: 1px solid #e5e7eb; }
    .btn { padding: 8px 16px; border-radius: 6px; border: none; cursor: pointer; font-size: 14px; font-weight: 500; }
    .btn-primary { background: #004080; color: white; }
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
  `]
})
export class SeatAllocationConfigComponent implements OnInit {
  pagedData: PagedResponse<SeatAllocationConfig> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  stats: any = {};
  filters: any = { search: '', status: '' };
  showModal = false;
  saving = false;
  editingItem: any = null;
  sessions: any[] = [];
  formData: any = this.getEmptyForm();

  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'session.name', label: 'Session', sortable: true },
    { key: 'academicYear', label: 'Academic Year', sortable: true },
    { key: 'allocationRound', label: 'Round', type: 'number' },
    { key: 'allocationStartDate', label: 'Start Date', type: 'date' },
    { key: 'allocationEndDate', label: 'End Date', type: 'date' },
    { key: 'status', label: 'Status', sortable: true }
  ];

  rowActions: RowAction[] = [
    { label: 'Edit', icon: '✏️', title: 'Edit', onClick: (item) => this.openModal(item) },
    { label: 'Activate', icon: '✅', title: 'Activate', class: 'btn-icon-success',
      condition: (item) => item.status !== 'ACTIVE', onClick: (item) => this.activateConfig(item) },
    { label: 'Close', icon: '🔒', title: 'Close', class: 'btn-icon-warning',
      condition: (item) => item.status === 'ACTIVE', onClick: (item) => this.closeConfig(item) },
    { label: 'Delete', icon: '🗑️', title: 'Delete', class: 'btn-icon-danger',
      condition: (item) => item.status !== 'ACTIVE', onClick: (item) => this.deleteConfig(item) },
  ];

  constructor(
    private configService: SeatAllocationConfigService,
    private academicSessionService: AcademicSessionService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
    this.loadStats();
    this.loadSessions();
  }

  loadData() {
    this.loading = true;
    this.configService.findAll(this.params, this.filters).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load configurations'); }
    });
  }

  loadStats() {
    this.configService.findAll({ ...DEFAULT_PAGE_PARAMS, size: 1 }, {}).subscribe({
      next: (data) => {
        this.stats = { total: data.totalElements || 0, active: 0, draft: 0, closed: 0 };
        this.configService.findAll({ ...DEFAULT_PAGE_PARAMS, size: 200 }, {}).subscribe({
          next: (all) => {
            const configs = all.content || [];
            this.stats.active = configs.filter((c: any) => c.status === 'ACTIVE').length;
            this.stats.draft = configs.filter((c: any) => c.status === 'DRAFT').length;
            this.stats.closed = configs.filter((c: any) => c.status === 'CLOSED').length;
          }
        });
      }
    });
  }

  loadSessions() {
    this.academicSessionService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'asc' }).pipe(
      map(res => res.content || [])
    ).subscribe({
      next: (sessions) => { this.sessions = sessions; }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.filters.search = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  getEmptyForm() {
    return {
      sessionId: '', academicYear: '', allocationRound: 1, acceptDeadlineHours: 72,
      allocationStartDate: '', allocationEndDate: '', autoAllocation: true, manualAllocation: true,
      enableQuota: false, enableReservedSeats: false, lockAfterPublish: true, remarks: ''
    };
  }

  openModal(item?: any) {
    this.editingItem = item ? { ...item } : null;
    this.formData = item ? {
      sessionId: item.sessionId || '',
      academicYear: item.academicYear || '',
      allocationRound: item.allocationRound || 1,
      acceptDeadlineHours: item.acceptDeadlineHours || 72,
      allocationStartDate: item.allocationStartDate ? item.allocationStartDate.substring(0, 16) : '',
      allocationEndDate: item.allocationEndDate ? item.allocationEndDate.substring(0, 16) : '',
      autoAllocation: item.autoAllocation !== false,
      manualAllocation: item.manualAllocation !== false,
      enableQuota: item.enableQuota || false,
      enableReservedSeats: item.enableReservedSeats || false,
      lockAfterPublish: item.lockAfterPublish !== false,
      remarks: item.remarks || ''
    } : this.getEmptyForm();
    this.showModal = true;
  }

  saveConfig() {
    this.saving = true;
    const payload: any = {
      session: { id: Number(this.formData.sessionId) },
      academicYear: this.formData.academicYear,
      allocationRound: this.formData.allocationRound,
      acceptDeadlineHours: this.formData.acceptDeadlineHours,
      allocationStartDate: this.formData.allocationStartDate,
      allocationEndDate: this.formData.allocationEndDate,
      autoAllocation: this.formData.autoAllocation,
      manualAllocation: this.formData.manualAllocation,
      enableQuota: this.formData.enableQuota,
      enableReservedSeats: this.formData.enableReservedSeats,
      lockAfterPublish: this.formData.lockAfterPublish,
      remarks: this.formData.remarks
    };
    const req = this.editingItem?.id
      ? this.configService.update(this.editingItem.id, payload)
      : this.configService.save(payload);
    req.subscribe({
      next: () => {
        this.saving = false; this.showModal = false;
        this.toastService.success(this.editingItem ? 'Updated' : 'Created');
        this.loadData(); this.loadStats();
      },
      error: (err) => { this.saving = false; this.toastService.error(err.error?.message || 'Failed'); }
    });
  }

  activateConfig(item: any) {
    this.configService.activate(item.id).subscribe({
      next: () => { this.loadData(); this.loadStats(); this.toastService.success('Activated'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed')
    });
  }

  closeConfig(item: any) {
    if (confirm('Close this configuration?')) {
      this.configService.close(item.id).subscribe({
        next: () => { this.loadData(); this.loadStats(); this.toastService.success('Closed'); },
        error: (err) => this.toastService.error(err.error?.message || 'Failed')
      });
    }
  }

  deleteConfig(item: any) {
    if (confirm('Delete this configuration?')) {
      this.configService.delete(item.id).subscribe({
        next: () => { this.loadData(); this.loadStats(); this.toastService.success('Deleted'); },
        error: (err) => this.toastService.error(err.error?.message || 'Failed')
      });
    }
  }
}
