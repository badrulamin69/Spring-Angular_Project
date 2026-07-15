import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginSessionService } from '../../../services/login-session.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-login-sessions',
  standalone: true,
  imports: [CommonModule, DataTableComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Login Sessions</h2>
        <p class="page-sub">Monitor and manage active user sessions</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="loadData()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/><path d="M11 1v2.5h-2.5M3 13v-2.5h2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          Refresh
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
      (onEdit)="terminateSession($event)"
      (onDelete)="confirmDelete($event)"
      (bulkDelete)="confirmBulkDelete($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

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
    .header-actions { display: flex; gap: 8px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); color: var(--text-primary); }
  `]
})
export class LoginSessionsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS, sortBy: 'loginTime', sortDir: 'desc' };
  searchTerm = '';
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true, hidden: true },
    { key: 'userId', label: 'User ID', sortable: true, type: 'number' },
    { key: 'ipAddress', label: 'IP Address', type: 'text' },
    { key: 'browser', label: 'Browser', sortable: true, type: 'text' },
    { key: 'operatingSystem', label: 'OS', sortable: true, type: 'text' },
    { key: 'deviceType', label: 'Device', type: 'text' },
    { key: 'loginTime', label: 'Login Time', sortable: true, type: 'text' },
    { key: 'logoutTime', label: 'Logout Time', type: 'text' },
    { key: 'isActive', label: 'Active', type: 'checkbox' }
  ];

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  confirmBtnText = 'Confirm';
  confirmType: 'danger' | 'warning' = 'danger';
  deleteTarget: any = null;
  pendingAction: 'delete' | 'bulkDelete' | 'terminate' | 'terminateAll' = 'delete';

  constructor(private service: LoginSessionService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load login sessions'); }
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.params = { ...DEFAULT_PAGE_PARAMS, sortBy: 'loginTime', sortDir: 'desc' };
    this.loadData();
  }

  terminateSession(item: any) {
    if (!item.isActive) {
      this.toastService.warning('Session is already terminated');
      return;
    }
    this.deleteTarget = item;
    this.pendingAction = 'terminate';
    this.confirmTitle = 'Terminate Session';
    this.confirmMessage = `Are you sure you want to terminate this session for user ID ${item.userId}?`;
    this.confirmBtnText = 'Terminate';
    this.confirmType = 'warning';
    this.showConfirm = true;
  }

  terminateAllForUser(item: any) {
    this.deleteTarget = item;
    this.pendingAction = 'terminateAll';
    this.confirmTitle = 'Terminate All Sessions';
    this.confirmMessage = `Are you sure you want to terminate ALL active sessions for user ID ${item.userId}?`;
    this.confirmBtnText = 'Terminate All';
    this.confirmType = 'danger';
    this.showConfirm = true;
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.pendingAction = 'delete';
    this.confirmTitle = 'Delete Session Record';
    this.confirmMessage = 'Are you sure you want to delete this session record? This action cannot be undone.';
    this.confirmBtnText = 'Delete';
    this.confirmType = 'danger';
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.deleteTarget = items;
    this.pendingAction = 'bulkDelete';
    this.confirmTitle = 'Bulk Delete Sessions';
    this.confirmMessage = `Are you sure you want to delete ${items.length} selected session records? This action cannot be undone.`;
    this.confirmBtnText = 'Delete All';
    this.confirmType = 'danger';
    this.showConfirm = true;
  }

  executeAction() {
    this.showConfirm = false;

    switch (this.pendingAction) {
      case 'terminate':
        this.service.terminateSession(this.deleteTarget.id).subscribe({
          next: () => {
            this.loadData();
            this.toastService.success('Session terminated successfully');
          },
          error: () => this.toastService.error('Failed to terminate session')
        });
        break;

      case 'terminateAll':
        this.service.terminateAllUserSessions(this.deleteTarget.userId).subscribe({
          next: () => {
            this.loadData();
            this.toastService.success('All sessions terminated successfully');
          },
          error: () => this.toastService.error('Failed to terminate sessions')
        });
        break;

      case 'delete':
        this.service.delete(this.deleteTarget.id).subscribe({
          next: () => {
            this.loadData();
            this.toastService.success('Session record deleted');
          },
          error: () => this.toastService.error('Failed to delete session record')
        });
        break;

      case 'bulkDelete':
        const ids = this.deleteTarget.map((i: any) => i.id);
        let completed = 0;
        ids.forEach((id: number) => {
          this.service.delete(id).subscribe({
            next: () => {
              completed++;
              if (completed === ids.length) {
                this.loadData();
                this.toastService.success(`${ids.length} session records deleted`);
              }
            },
            error: () => this.toastService.error('Failed to delete some records')
          });
        });
        break;
    }

    this.deleteTarget = null;
  }
}
