import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivityLogService } from '../../../services/activity-log.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-activity-logs',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Activity Logs</h2>
        <p class="page-sub">Track all user activities across the system</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="loadData()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/><path d="M11 1v2.5h-2.5M3 13v-2.5h2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          Refresh
        </button>
      </div>
    </div>

    <div class="filters-bar">
      <div class="filter-group">
        <label>Module</label>
        <select [(ngModel)]="filterModule" (change)="onFilterChange()">
          <option value="">All Modules</option>
          <option value="AUTH">Auth</option>
          <option value="USER">User</option>
          <option value="ROLE">Role</option>
          <option value="PERMISSION">Permission</option>
          <option value="MENU">Menu</option>
          <option value="DEPARTMENT">Department</option>
          <option value="ADMINISTRATION">Administration</option>
          <option value="SEMESTER">Semester</option>
          <option value="COURSE">Course</option>
          <option value="ENROLLMENT">Enrollment</option>
          <option value="ADMISSION">Admission</option>
          <option value="EXAM">Exam</option>
          <option value="RESULT">Result</option>
          <option value="ATTENDANCE">Attendance</option>
          <option value="FEES">Fees</option>
          <option value="SCHEDULE">Schedule</option>
          <option value="SYSTEM">System</option>
        </select>
      </div>
      <div class="filter-group">
        <label>Action</label>
        <select [(ngModel)]="filterAction" (change)="onFilterChange()">
          <option value="">All Actions</option>
          <option value="CREATE">Create</option>
          <option value="UPDATE">Update</option>
          <option value="DELETE">Delete</option>
          <option value="LOGIN">Login</option>
          <option value="LOGOUT">Logout</option>
          <option value="VIEW">View</option>
          <option value="EXPORT">Export</option>
          <option value="IMPORT">Import</option>
        </select>
      </div>
      @if (filterModule || filterAction) {
        <button class="btn btn-sm btn-outline" (click)="clearFilters()">Clear Filters</button>
      }
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (onDelete)="confirmDelete($event)"
      (bulkDelete)="confirmBulkDelete($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

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
    .header-actions { display: flex; gap: 8px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-sm { padding: 5px 10px; font-size: 0.8125rem; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); color: var(--text-primary); }
    .filters-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 1rem; flex-wrap: wrap; }
    .filter-group { display: flex; align-items: center; gap: 6px; }
    .filter-group label { font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; white-space: nowrap; }
    .filter-group select { padding: 6px 10px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.8125rem; cursor: pointer; }
  `]
})
export class ActivityLogsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS, sortBy: 'createdAt', sortDir: 'desc' };
  searchTerm = '';
  filterModule = '';
  filterAction = '';
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true, hidden: true },
    { key: 'username', label: 'User', sortable: true, type: 'text' },
    { key: 'action', label: 'Action', sortable: true, type: 'text' },
    { key: 'module', label: 'Module', sortable: true, type: 'text' },
    { key: 'description', label: 'Description', type: 'text' },
    { key: 'entityType', label: 'Entity Type', type: 'text' },
    { key: 'entityId', label: 'Entity ID', type: 'text' },
    { key: 'ipAddress', label: 'IP', type: 'text' },
    { key: 'createdAt', label: 'Time', sortable: true, type: 'text' }
  ];

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  constructor(private service: ActivityLogService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load activity logs'); }
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.params = { ...DEFAULT_PAGE_PARAMS, sortBy: 'createdAt', sortDir: 'desc' };
    this.loadData();
  }

  onFilterChange() {
    this.params = { ...DEFAULT_PAGE_PARAMS, sortBy: 'createdAt', sortDir: 'desc' };
    this.loadData();
  }

  clearFilters() {
    this.filterModule = '';
    this.filterAction = '';
    this.params = { ...DEFAULT_PAGE_PARAMS, sortBy: 'createdAt', sortDir: 'desc' };
    this.loadData();
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Activity Log';
    this.confirmMessage = 'Are you sure you want to delete this activity log? This action cannot be undone.';
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.deleteTarget = items;
    this.confirmTitle = 'Bulk Delete Activity Logs';
    this.confirmMessage = `Are you sure you want to delete ${items.length} selected activity logs? This action cannot be undone.`;
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
              this.toastService.success(`${ids.length} activity logs deleted`);
            }
          },
          error: () => this.toastService.error('Failed to delete some records')
        });
      });
    } else if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => {
          this.loadData();
          this.toastService.success('Activity log deleted');
        },
        error: () => this.toastService.error('Failed to delete activity log')
      });
    }
    this.deleteTarget = null;
  }
}
