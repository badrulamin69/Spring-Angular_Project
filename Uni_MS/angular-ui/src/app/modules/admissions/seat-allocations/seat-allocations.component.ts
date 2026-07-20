import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProgramSeatAllocationService } from '../../../services/program-seat-allocation.service';
import { SeatAllocationConfigService } from '../../../services/seat-allocation-config.service';
import { ProgramSeatAllocation, SeatAllocationConfig, AllocationStats } from '../../../models/seat-allocation';
import { DataTableComponent, TableColumn, RowAction } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-seat-allocations',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Seat Allocations</h2>
        <p class="page-sub">Manage program seat allocation records for applicants</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="showManualModal = true" [disabled]="!selectedConfigId">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          Manual Allocate
        </button>
        <button class="btn btn-success" (click)="runAutoAllocation()" [disabled]="!selectedConfigId || processing">
          {{ processing ? 'Processing...' : 'Run Auto Allocation' }}
        </button>
        <button class="btn btn-warning" (click)="runReallocate()" [disabled]="!selectedConfigId || processing">
          Reallocate
        </button>
        <button class="btn btn-outline btn-danger-text" (click)="expireOverdue()" [disabled]="!selectedConfigId || processing">
          Expire Overdue
        </button>
      </div>
    </div>

    <div class="config-selector">
      <label>Select Allocation Config:</label>
      <select [(ngModel)]="selectedConfigId" (change)="onConfigChange()">
        <option [ngValue]="null">-- Select Config --</option>
        @for (c of configs; track c.id) {
          <option [ngValue]="c.id">
            {{ c.session?.name || 'Session' }} - {{ c.academicYear || '' }}
            (Round {{ c.allocationRound || '-' }}) [{{ c.status }}]
          </option>
        }
      </select>
    </div>

    @if (selectedConfigId) {
      <div class="stats-row">
        <div class="stat-card"><span class="stat-val">{{ stats.totalSeats || 0 }}</span><span class="stat-lbl">Total Seats</span></div>
        <div class="stat-card allocated"><span class="stat-val">{{ stats.allocated || 0 }}</span><span class="stat-lbl">Allocated</span></div>
        <div class="stat-card confirmed"><span class="stat-val">{{ stats.confirmed || 0 }}</span><span class="stat-lbl">Confirmed</span></div>
        <div class="stat-card waiting"><span class="stat-val">{{ stats.waiting || 0 }}</span><span class="stat-lbl">Waiting</span></div>
        <div class="stat-card declined"><span class="stat-val">{{ stats.declined || 0 }}</span><span class="stat-lbl">Declined</span></div>
        <div class="stat-card expired"><span class="stat-val">{{ stats.expired || 0 }}</span><span class="stat-lbl">Expired</span></div>
        <div class="stat-card remaining"><span class="stat-val">{{ stats.remainingSeats || 0 }}</span><span class="stat-lbl">Remaining</span></div>
        <div class="stat-card utilization"><span class="stat-val">{{ stats.utilizationPercent || 0 }}%</span><span class="stat-lbl">Utilization</span></div>
      </div>

      <div class="filter-bar">
        <input type="text" placeholder="Search by name, allocation #, or registration #..." [(ngModel)]="filters.search" (keyup.enter)="loadData()">
        <select [(ngModel)]="filters.status" (change)="loadData()">
          <option value="">All Status</option>
          <option value="ALLOCATED">Allocated</option>
          <option value="CONFIRMED">Confirmed</option>
          <option value="DECLINED">Declined</option>
          <option value="CANCELLED">Cancelled</option>
          <option value="EXPIRED">Expired</option>
          <option value="NOT_ALLOCATED">Not Allocated</option>
        </select>
        <label class="checkbox-label">
          <input type="checkbox" [(ngModel)]="waitingOnly" (change)="onWaitingToggle()">
          Waiting Only
        </label>
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
    } @else {
      <div class="empty-state">
        <p>Please select an allocation config to view seat allocations.</p>
      </div>
    }

    @if (showManualModal) {
      <div class="modal-overlay" (click)="closeManualModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Manual Seat Allocation</h3>
            <button class="close-btn" (click)="closeManualModal()">&times;</button>
          </div>
          <form (ngSubmit)="submitManualAllocate()">
            <div class="form-group">
              <label>Registration ID *</label>
              <input type="number" [(ngModel)]="manualData.registrationId" name="registrationId" required placeholder="Enter registration ID">
            </div>
            <div class="form-group">
              <label>Program *</label>
              <select [(ngModel)]="manualData.programId" name="programId" required>
                <option [ngValue]="null" disabled>Select Program</option>
                @for (p of programs; track p.id) {
                  <option [ngValue]="p.id">{{ p.name }}</option>
                }
              </select>
            </div>
            <div class="form-group">
              <label>Shift *</label>
              <select [(ngModel)]="manualData.shift" name="shift" required>
                <option value="">Select Shift</option>
                <option value="MORNING">Morning</option>
                <option value="EVENING">Evening</option>
              </select>
            </div>
            <div class="form-group">
              <label>Remarks</label>
              <textarea [(ngModel)]="manualData.remarks" name="remarks" rows="3" placeholder="Optional remarks"></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="closeManualModal()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving">{{ saving ? 'Allocating...' : 'Allocate' }}</button>
            </div>
          </form>
        </div>
      </div>
    }

    @if (showChangeModal && changeTarget) {
      <div class="modal-overlay" (click)="closeChangeModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Change Allocation — {{ changeTarget.allocationNumber }}</h3>
            <button class="close-btn" (click)="closeChangeModal()">&times;</button>
          </div>
          <div class="current-info">
            <div class="info-row"><span>Current Program:</span><strong>{{ changeTarget.allocatedProgram?.name || 'N/A' }}</strong></div>
            <div class="info-row"><span>Current Shift:</span><strong>{{ changeTarget.shift || 'N/A' }}</strong></div>
            <div class="info-row"><span>Merit Rank:</span><strong>#{{ changeTarget.meritRank || 'N/A' }}</strong></div>
          </div>
          <form (ngSubmit)="submitChangeAllocation()">
            <div class="form-group">
              <label>New Program *</label>
              <select [(ngModel)]="changeData.newProgramId" name="newProgramId" required>
                <option [ngValue]="null" disabled>Select New Program</option>
                @for (p of programs; track p.id) {
                  <option [ngValue]="p.id">{{ p.name }}</option>
                }
              </select>
            </div>
            <div class="form-group">
              <label>New Shift *</label>
              <select [(ngModel)]="changeData.shift" name="shift" required>
                <option value="">Select Shift</option>
                <option value="MORNING">Morning</option>
                <option value="EVENING">Evening</option>
              </select>
            </div>
            <div class="form-group">
              <label>Remarks</label>
              <textarea [(ngModel)]="changeData.remarks" name="remarks" rows="3" placeholder="Optional remarks"></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="closeChangeModal()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving">{{ saving ? 'Changing...' : 'Change Allocation' }}</button>
            </div>
          </form>
        </div>
      </div>
    }

    @if (showCancelModal && cancelTarget) {
      <div class="modal-overlay" (click)="closeCancelModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Cancel Allocation — {{ cancelTarget.allocationNumber }}</h3>
            <button class="close-btn" (click)="closeCancelModal()">&times;</button>
          </div>
          <div class="current-info">
            <div class="info-row"><span>Registration #:</span><strong>{{ cancelTarget.registration?.registrationNumber || 'N/A' }}</strong></div>
            <div class="info-row"><span>Program:</span><strong>{{ cancelTarget.allocatedProgram?.name || 'N/A' }}</strong></div>
            <div class="info-row"><span>Status:</span><strong class="status-badge" [attr.data-status]="cancelTarget.status">{{ cancelTarget.status }}</strong></div>
          </div>
          <form (ngSubmit)="submitCancelAllocation()">
            <div class="form-group">
              <label>Reason for Cancellation</label>
              <textarea [(ngModel)]="cancelRemarks" name="cancelRemarks" rows="3" placeholder="Optional reason for cancellation"></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="closeCancelModal()">Close</button>
              <button type="submit" class="btn btn-danger" [disabled]="saving">{{ saving ? 'Cancelling...' : 'Confirm Cancel' }}</button>
            </div>
          </form>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; flex-wrap: wrap; gap: 0.75rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary, #1e293b); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted, #64748b); }
    .header-actions { display: flex; gap: 0.5rem; flex-wrap: wrap; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-primary { background: #2563eb; color: #fff; }
    .btn-primary:hover:not(:disabled) { background: #1d4ed8; }
    .btn-secondary { background: #e2e8f0; color: #475569; }
    .btn-outline { background: white; color: #475569; border: 1px solid #d1d5db; }
    .btn-outline:hover:not(:disabled) { background: #f8fafc; }
    .btn-sm { padding: 6px 12px; font-size: 0.8125rem; }
    .btn-success { background: #22c55e; color: #fff; }
    .btn-success:hover:not(:disabled) { background: #16a34a; }
    .btn-warning { background: #f59e0b; color: #fff; }
    .btn-warning:hover:not(:disabled) { background: #d97706; }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-danger:hover:not(:disabled) { background: #dc2626; }
    .btn-danger-text { color: #ef4444; border-color: #fca5a5; }
    .btn-danger-text:hover:not(:disabled) { background: #fef2f2; color: #dc2626; }
    :host ::ng-deep .btn-icon-success { color: #22c55e; }
    :host ::ng-deep .btn-icon-success:hover { background: #f0fdf4; color: #16a34a; }
    :host ::ng-deep .btn-icon-warning { color: #f59e0b; }
    :host ::ng-deep .btn-icon-warning:hover { background: #fffbeb; color: #d97706; }
    :host ::ng-deep .btn-icon-info { color: #3b82f6; }
    :host ::ng-deep .btn-icon-info:hover { background: #eff6ff; color: #2563eb; }
    :host ::ng-deep .btn-icon-danger { color: #ef4444; }
    :host ::ng-deep .btn-icon-danger:hover { background: #fef2f2; color: #dc2626; }
    .config-selector { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1.25rem; padding: 0.75rem 1rem; background: #fff; border: 1px solid #e2e8f0; border-radius: 8px; box-shadow: 0 1px 2px rgba(0,0,0,0.04); }
    .config-selector label { font-weight: 600; font-size: 0.875rem; color: #374151; white-space: nowrap; }
    .config-selector select { flex: 1; padding: 0.5rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; max-width: 500px; }
    .stats-row { display: grid; grid-template-columns: repeat(8, 1fr); gap: 0.75rem; margin-bottom: 1.25rem; }
    .stat-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 0.875rem 0.5rem; text-align: center; box-shadow: 0 1px 2px rgba(0,0,0,0.04); }
    .stat-card .stat-val { display: block; font-size: 1.5rem; font-weight: 700; color: #1e293b; }
    .stat-card .stat-lbl { font-size: 0.75rem; color: #64748b; }
    .stat-card.allocated .stat-val { color: #3b82f6; }
    .stat-card.confirmed .stat-val { color: #22c55e; }
    .stat-card.waiting .stat-val { color: #f59e0b; }
    .stat-card.declined .stat-val { color: #ef4444; }
    .stat-card.expired .stat-val { color: #94a3b8; }
    .stat-card.remaining .stat-val { color: #8b5cf6; }
    .stat-card.utilization .stat-val { color: #2563eb; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; flex-wrap: wrap; }
    .filter-bar input, .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .filter-bar input[type="text"] { flex: 1; min-width: 250px; }
    .checkbox-label { display: inline-flex; align-items: center; gap: 0.375rem; font-size: 0.875rem; color: #374151; cursor: pointer; white-space: nowrap; }
    .checkbox-label input[type="checkbox"] { width: 16px; height: 16px; cursor: pointer; }
    .empty-state { text-align: center; padding: 4rem 2rem; background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; }
    .empty-state p { color: #94a3b8; font-size: 1rem; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; padding: 1.5rem; width: 90%; max-width: 520px; max-height: 90vh; overflow-y: auto; box-shadow: 0 20px 60px rgba(0,0,0,0.2); }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; color: #1e293b; font-weight: 700; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b; line-height: 1; }
    .close-btn:hover { color: #1e293b; }
    .current-info { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 0.875rem 1rem; margin-bottom: 1.25rem; }
    .info-row { display: flex; justify-content: space-between; padding: 4px 0; font-size: 0.875rem; }
    .info-row span { color: #64748b; }
    .info-row strong { color: #1e293b; }
    .form-group { margin-bottom: 1rem; }
    .form-group label { display: block; margin-bottom: 0.375rem; font-weight: 600; color: #374151; font-size: 0.8125rem; }
    .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.875rem; box-sizing: border-box; font-family: inherit; }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { outline: none; border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,0.1); }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #e2e8f0; }
    .status-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .status-badge[data-status="ALLOCATED"] { background: #dbeafe; color: #1d4ed8; }
    .status-badge[data-status="CONFIRMED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="DECLINED"] { background: #fee2e2; color: #991b1b; }
    .status-badge[data-status="CANCELLED"] { background: #f3f4f6; color: #374151; }
    .status-badge[data-status="EXPIRED"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="NOT_ALLOCATED"] { background: #f1f5f9; color: #64748b; }
    @media (max-width: 1200px) {
      .stats-row { grid-template-columns: repeat(4, 1fr); }
    }
    @media (max-width: 768px) {
      .stats-row { grid-template-columns: repeat(2, 1fr); }
      .page-header { flex-direction: column; align-items: flex-start; }
    }
  `]
})
export class SeatAllocationsComponent implements OnInit {
  configs: SeatAllocationConfig[] = [];
  selectedConfigId: number | null = null;
  pagedData: PagedResponse<ProgramSeatAllocation> | null = null;
  loading = false;
  saving = false;
  processing = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  stats: AllocationStats = {
    total: 0, allocated: 0, confirmed: 0, declined: 0, cancelled: 0,
    expired: 0, waiting: 0, notAllocated: 0, totalSeats: 0,
    allocatedSeats: 0, remainingSeats: 0, utilizationPercent: 0
  };
  programs: any[] = [];

  filters: any = { search: '', status: '', isWaiting: null };
  waitingOnly = false;

  showManualModal = false;
  manualData = { registrationId: null as number | null, programId: null as number | null, shift: '', remarks: '' };

  showChangeModal = false;
  changeTarget: ProgramSeatAllocation | null = null;
  changeData = { newProgramId: null as number | null, shift: '', remarks: '' };

  showCancelModal = false;
  cancelTarget: ProgramSeatAllocation | null = null;
  cancelRemarks = '';

  columns: TableColumn[] = [
    { key: 'allocationNumber', label: 'Allocation #', sortable: true },
    { key: 'registration.registrationNumber', label: 'Reg. #' },
    { key: 'registration.firstName', label: 'First Name', sortable: true },
    { key: 'meritRank', label: 'Merit Rank', type: 'number', sortable: true },
    { key: 'allocatedProgram.name', label: 'Program' },
    { key: 'allocatedDepartment.name', label: 'Department' },
    { key: 'shift', label: 'Shift' },
    { key: 'choiceNumber', label: 'Choice #', type: 'number' },
    { key: 'allocationRound', label: 'Round', type: 'number' },
    { key: 'status', label: 'Status', sortable: true },
    { key: 'deadline', label: 'Deadline', type: 'date' }
  ];

  rowActions: RowAction[] = [
    {
      label: 'Change',
      icon: '🔄',
      title: 'Change Allocation',
      class: 'btn-icon-info',
      condition: (item) => item.status === 'ALLOCATED' && !item.isWaiting,
      onClick: (item) => this.openChangeModal(item)
    },
    {
      label: 'Cancel',
      icon: '❌',
      title: 'Cancel Allocation',
      class: 'btn-icon-danger',
      condition: (item) => item.status === 'ALLOCATED' || item.status === 'NOT_ALLOCATED',
      onClick: (item) => this.openCancelModal(item)
    }
  ];

  constructor(
    private allocationService: ProgramSeatAllocationService,
    private configService: SeatAllocationConfigService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadConfigs();
  }

  loadConfigs() {
    this.configService.findAll({ ...DEFAULT_PAGE_PARAMS, size: 100 }).subscribe({
      next: (data) => {
        this.configs = data.content || [];
      },
      error: () => {
        this.toastService.error('Failed to load allocation configs');
      }
    });
  }

  loadPrograms() {
    this.allocationService.findAll(this.selectedConfigId!, { ...DEFAULT_PAGE_PARAMS, size: 1 }, {}).subscribe({
      next: () => {},
      error: () => {}
    });
  }

  onConfigChange() {
    if (this.selectedConfigId) {
      this.params = { ...DEFAULT_PAGE_PARAMS };
      this.filters = { search: '', status: '', isWaiting: null };
      this.waitingOnly = false;
      this.loadData();
      this.loadStats();
    }
  }

  onWaitingToggle() {
    this.filters.isWaiting = this.waitingOnly ? true : null;
    this.params = { ...DEFAULT_PAGE_PARAMS };
    this.loadData();
  }

  loadStats() {
    if (!this.selectedConfigId) return;
    this.allocationService.getStats(this.selectedConfigId).subscribe({
      next: (data) => { this.stats = data; },
      error: () => {}
    });
  }

  loadData() {
    if (!this.selectedConfigId) return;
    this.loading = true;
    this.allocationService.findAll(this.selectedConfigId, this.params, this.filters).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load allocations'); }
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  onSearch(term: string) {
    this.filters.search = term;
    this.params = { ...DEFAULT_PAGE_PARAMS };
    this.loadData();
  }

  runAutoAllocation() {
    if (!this.selectedConfigId) return;
    if (!confirm('Run automatic seat allocation? This will process all pending registrations.')) return;
    this.processing = true;
    this.allocationService.runAutoAllocation(this.selectedConfigId).subscribe({
      next: (result) => {
        this.processing = false;
        this.toastService.success(
          `Auto allocation complete: ${result.allocated} allocated, ${result.waiting} waiting, ${result.notAllocated} not allocated (Round ${result.round})`
        );
        this.loadData();
        this.loadStats();
      },
      error: (err) => {
        this.processing = false;
        this.toastService.error(err.error?.message || 'Failed to run auto allocation');
      }
    });
  }

  runReallocate() {
    if (!this.selectedConfigId) return;
    if (!confirm('Run reallocation? This will redistribute declined/expired seats.')) return;
    this.processing = true;
    this.allocationService.runReallocation(this.selectedConfigId).subscribe({
      next: () => {
        this.processing = false;
        this.toastService.success('Reallocation completed successfully');
        this.loadData();
        this.loadStats();
      },
      error: (err) => {
        this.processing = false;
        this.toastService.error(err.error?.message || 'Failed to run reallocation');
      }
    });
  }

  expireOverdue() {
    if (!this.selectedConfigId) return;
    if (!confirm('Expire all overdue allocations? This cannot be undone.')) return;
    this.processing = true;
    this.allocationService.expireOverdue(this.selectedConfigId).subscribe({
      next: () => {
        this.processing = false;
        this.toastService.success('Overdue allocations expired');
        this.loadData();
        this.loadStats();
      },
      error: (err) => {
        this.processing = false;
        this.toastService.error(err.error?.message || 'Failed to expire overdue allocations');
      }
    });
  }

  openChangeModal(item: ProgramSeatAllocation) {
    this.changeTarget = item;
    this.changeData = { newProgramId: null, shift: item.shift || '', remarks: '' };
    this.showChangeModal = true;
  }

  closeChangeModal() {
    this.showChangeModal = false;
    this.changeTarget = null;
    this.changeData = { newProgramId: null, shift: '', remarks: '' };
  }

  submitChangeAllocation() {
    if (!this.changeTarget?.id || !this.changeData.newProgramId || !this.changeData.shift) return;
    this.saving = true;
    this.allocationService.changeAllocation(
      this.changeTarget.id,
      this.changeData.newProgramId,
      this.changeData.shift,
      this.changeData.remarks || undefined
    ).subscribe({
      next: () => {
        this.saving = false;
        this.closeChangeModal();
        this.toastService.success('Allocation changed successfully');
        this.loadData();
        this.loadStats();
      },
      error: (err) => {
        this.saving = false;
        this.toastService.error(err.error?.message || 'Failed to change allocation');
      }
    });
  }

  openCancelModal(item: ProgramSeatAllocation) {
    this.cancelTarget = item;
    this.cancelRemarks = '';
    this.showCancelModal = true;
  }

  closeCancelModal() {
    this.showCancelModal = false;
    this.cancelTarget = null;
    this.cancelRemarks = '';
  }

  submitCancelAllocation() {
    if (!this.cancelTarget?.id) return;
    if (!confirm('Are you sure you want to cancel this allocation?')) return;
    this.saving = true;
    this.allocationService.cancelAllocation(this.cancelTarget.id, this.cancelRemarks || undefined).subscribe({
      next: () => {
        this.saving = false;
        this.closeCancelModal();
        this.toastService.success('Allocation cancelled');
        this.loadData();
        this.loadStats();
      },
      error: (err) => {
        this.saving = false;
        this.toastService.error(err.error?.message || 'Failed to cancel allocation');
      }
    });
  }

  closeManualModal() {
    this.showManualModal = false;
    this.manualData = { registrationId: null, programId: null, shift: '', remarks: '' };
  }

  submitManualAllocate() {
    if (!this.selectedConfigId || !this.manualData.registrationId || !this.manualData.programId || !this.manualData.shift) return;
    this.saving = true;
    this.allocationService.manualAllocate(
      this.manualData.registrationId,
      this.manualData.programId,
      this.selectedConfigId,
      this.manualData.shift,
      this.manualData.remarks || undefined
    ).subscribe({
      next: () => {
        this.saving = false;
        this.closeManualModal();
        this.toastService.success('Seat allocated manually');
        this.loadData();
        this.loadStats();
      },
      error: (err) => {
        this.saving = false;
        this.toastService.error(err.error?.message || 'Failed to manually allocate seat');
      }
    });
  }
}
