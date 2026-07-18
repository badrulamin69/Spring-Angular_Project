import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DepartmentAllocationService } from '../../../services/department-allocation.service';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-allocations',
  standalone: true,
  imports: [CommonModule, FormsModule, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Department Allocations</h2>
        <p class="page-sub">Manage department allocation for admitted applicants</p>
      </div>
    </div>

    <div class="table-wrapper">
      <div class="table-toolbar">
        <div class="search-box">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><circle cx="6.5" cy="6.5" r="5.5" stroke="currentColor" stroke-width="1.5"/><path d="M10.5 10.5L14.5 14.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          <input type="text" placeholder="Search..." [(ngModel)]="searchTerm" (input)="onSearch()">
        </div>
        <button class="btn btn-sm btn-outline" (click)="loadData()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/></svg>
          Refresh
        </button>
      </div>

      @if (loading) {
        <div class="loading-state"><div class="spinner"></div><span>Loading...</span></div>
      } @else {
        <div class="table-scroll">
          <table>
            <thead>
              <tr>
                <th>Allocation No</th>
                <th>Registration ID</th>
                <th>Total Score</th>
                <th>Rank</th>
                <th>Status</th>
                <th class="col-actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (item of pagedData?.content || []; track item.id) {
                <tr>
                  <td>{{ item.allocationNumber }}</td>
                  <td>{{ item.registrationId }}</td>
                  <td>{{ item.totalScore }}</td>
                  <td>{{ item.meritRank }}</td>
                  <td><span class="status-badge" [attr.data-status]="item.status">{{ item.status }}</span></td>
                  <td class="col-actions">
                    <button class="btn-icon" (click)="openForm(item)" title="Edit">
                      <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M8.5 2.5l3 3M1 13l.7-2.6L10 1.7l3 3L4.7 13.3 1 13z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </button>
                    @if (item.status === 'ALLOCATED') {
                      <button class="btn-icon btn-icon-success" (click)="confirmAction(item, 'confirm')" title="Confirm">
                        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M3 7.5l3 3 5-6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
                      </button>
                      <button class="btn-icon btn-icon-danger" (click)="confirmAction(item, 'cancel')" title="Cancel">
                        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M3.5 3.5l7 7M10.5 3.5l-7 7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                      </button>
                    }
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="6" class="empty-state">No allocations found</td></tr>
              }
            </tbody>
          </table>
        </div>

        @if (pagedData && pagedData.totalElements > 0) {
          <div class="pagination">
            <div class="pagination-info">
              Showing {{ pagedData.page * pagedData.size + 1 }}&ndash;{{ min((pagedData.page + 1) * pagedData.size, pagedData.totalElements) }} of {{ pagedData.totalElements }}
            </div>
            <div class="pagination-controls">
              <button [disabled]="pagedData.first" (click)="goToPage(pagedData.page - 1)">&laquo; Prev</button>
              <span>Page {{ pagedData.page + 1 }} of {{ pagedData.totalPages }}</span>
              <button [disabled]="pagedData.last" (click)="goToPage(pagedData.page + 1)">Next &raquo;</button>
            </div>
          </div>
        }
      }
    </div>

    @if (showForm) {
      <app-dynamic-form
        [columns]="formColumns"
        [initialData]="editingItem"
        [title]="'Edit Allocation'"
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
      (confirmed)="executeAction()"
      (cancelled)="showConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .table-wrapper { background: var(--bg-secondary); border-radius: 12px; border: 1px solid var(--border-color); overflow: hidden; }
    .table-toolbar { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid var(--border-color); gap: 12px; }
    .search-box { display: flex; align-items: center; gap: 8px; background: var(--bg-primary); border: 1px solid var(--border-color); border-radius: 8px; padding: 6px 12px; }
    .search-box input { border: none; background: transparent; color: var(--text-primary); font-size: 0.875rem; outline: none; width: 200px; }
    .search-box input::placeholder { color: var(--text-muted); }
    .search-box svg { color: var(--text-muted); }
    .btn { padding: 6px 12px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8125rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; }
    .btn-sm { padding: 5px 10px; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); }
    .table-scroll { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 10px 14px; text-align: left; border-bottom: 1px solid var(--border-color); color: var(--text-primary); white-space: nowrap; }
    th { background: var(--bg-tertiary); font-weight: 600; color: var(--text-secondary); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .col-actions { width: 120px; text-align: center; white-space: nowrap; }
    tr:hover { background: var(--bg-hover); }
    .btn-icon { background: none; border: none; cursor: pointer; padding: 4px 6px; border-radius: 4px; color: var(--text-muted); display: inline-flex; }
    .btn-icon:hover { background: var(--bg-hover); color: var(--text-primary); }
    .btn-icon-success:hover { background: #dcfce7; color: #16a34a; }
    .btn-icon-danger:hover { background: #fef2f2; color: #ef4444; }
    .status-badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; }
    .status-badge[data-status="ALLOCATED"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="CONFIRMED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="CANCELLED"] { background: #fee2e2; color: #991b1b; }
    .status-badge[data-status="ENROLLED"] { background: #a7f3d0; color: #065f46; }
    .empty-state { text-align: center; padding: 3rem 1rem !important; color: var(--text-muted); }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 3rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    .pagination { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px; border-top: 1px solid var(--border-color); }
    .pagination-info { font-size: 0.8125rem; color: var(--text-muted); }
    .pagination-controls { display: flex; gap: 8px; align-items: center; }
    .pagination-controls button { padding: 4px 10px; border: 1px solid var(--border-color); background: var(--bg-secondary); color: var(--text-primary); border-radius: 4px; cursor: pointer; font-size: 0.8125rem; }
    .pagination-controls button:disabled { opacity: 0.4; cursor: not-allowed; }
    .pagination-controls span { font-size: 0.8125rem; color: var(--text-muted); }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class AllocationsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  min = Math.min;
  formColumns = [
    { key: 'registrationId', label: 'Registration ID', type: 'number' as const, required: true },
    { key: 'totalScore', label: 'Total Score', type: 'number' as const, required: true },
    { key: 'meritRank', label: 'Merit Rank', type: 'number' as const, required: true },
    { key: 'status', label: 'Status', type: 'text' as const }
  ];
  showForm = false;
  editingItem: any = null;
  formError = '';
  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  confirmBtnText = '';
  confirmType: 'danger' | 'warning' | 'info' = 'danger';
  actionTarget: any = null;
  actionType = '';

  constructor(private service: DepartmentAllocationService, private toastService: ToastService) {}

  ngOnInit() { this.loadData(); }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch() { this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  openForm(item?: any) { this.editingItem = item ? { ...item } : null; this.formError = ''; this.showForm = true; }
  closeForm() { this.showForm = false; this.editingItem = null; this.formError = ''; }

  saveItem(data: any) {
    this.saving = true;
    const handleSuccess = () => { this.saving = false; this.closeForm(); this.loadData(); this.toastService.success('Updated'); };
    if (this.editingItem?.id) {
      this.service.update(this.editingItem.id, data).subscribe({ next: handleSuccess, error: () => { this.saving = false; this.formError = 'Failed'; } });
    }
  }

  goToPage(page: number) { this.params = { ...this.params, page }; this.loadData(); }

  confirmAction(item: any, type: string) {
    this.actionTarget = item;
    this.actionType = type;
    this.confirmTitle = type === 'confirm' ? 'Confirm Allocation' : 'Cancel Allocation';
    this.confirmMessage = `Are you sure you want to ${type} this allocation?`;
    this.confirmBtnText = type === 'confirm' ? 'Confirm' : 'Cancel';
    this.confirmType = type === 'confirm' ? 'info' : 'danger';
    this.showConfirm = true;
  }

  executeAction() {
    this.showConfirm = false;
    if (!this.actionTarget) return;
    const obs = this.actionType === 'confirm'
      ? this.service.confirm(this.actionTarget.id)
      : this.service.cancel(this.actionTarget.id);
    obs.subscribe({
      next: () => { this.loadData(); this.toastService.success(`Allocation ${this.actionType}ed`); },
      error: () => this.toastService.error('Failed')
    });
  }
}
