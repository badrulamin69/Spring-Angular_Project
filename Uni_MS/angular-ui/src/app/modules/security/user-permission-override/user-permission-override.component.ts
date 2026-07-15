import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { PermissionService } from '../../../services/permission.service';
import { UserPermissionService } from '../../../services/user-permission.service';
import { User } from '../../../models/user';
import { Permission } from '../../../models/permission';
import { UserPermission } from '../../../models/user-permission';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-user-permission-override',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <app-confirm-dialog
      [open]="showConfirm"
      title="Save Overrides"
      message="Save personal permission overrides for this user?"
      confirmText="Save"
      type="info"
      (confirmed)="confirmSave()"
      (cancelled)="showConfirm = false"
    ></app-confirm-dialog>

    <div class="page-header">
      <div>
        <h1>User Permission Override</h1>
        <p class="page-sub">Manage personal permission overrides per user</p>
      </div>
    </div>

    <div class="selector-bar">
      <label>Select User:</label>
      <select [(ngModel)]="selectedUserId" (change)="onUserChange()" class="form-control">
        <option [ngValue]="null">-- Choose a user --</option>
        @for (user of users; track user.id) {
          <option [ngValue]="user.id">{{ user.username }} ({{ user.email }})</option>
        }
      </select>
      @if (selectedUserId) {
        <button class="btn btn-sm btn-outline" (click)="loadUserPermissions()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/><path d="M11 1v2.5h-2.5M3 13v-2.5h2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          Reload
        </button>
      }
    </div>

    @if (loading) {
      <div class="loading-state">
        <div class="spinner"></div>
        <span>Loading permissions...</span>
      </div>
    } @else if (selectedUserId) {
      <div class="permissions-layout">
        <div class="section-card">
          <h3>Role Permissions (inherited)</h3>
          <p class="section-desc">These permissions come from the user's role assignments</p>
          @if (rolePermissions.length === 0) {
            <div class="empty-state">No role permissions assigned</div>
          } @else {
            @for (group of groupedRolePermissions; track group.module) {
              <div class="perm-group">
                <div class="perm-group-title">{{ group.module }}</div>
                @for (perm of group.permissions; track perm.id) {
                  <div class="perm-row">
                    <span class="perm-action">{{ perm.action }}</span>
                    <span class="perm-code">({{ perm.code }})</span>
                    <span class="badge badge-success">Granted</span>
                  </div>
                }
              </div>
            }
          }
        </div>

        <div class="section-card">
          <div class="section-header">
            <h3>Personal Overrides</h3>
            <button class="btn btn-sm btn-primary" (click)="openSave()" [disabled]="saving">
              @if (saving) {
                <span class="btn-spinner"></span> Saving...
              } @else {
                Save Overrides
              }
            </button>
          </div>
          <p class="section-desc">Override role-based permissions for this specific user</p>
          @if (allPermissions.length === 0) {
            <div class="empty-state">No permissions available</div>
          } @else {
            @for (group of groupedAllPermissions; track group.module) {
              <div class="perm-group">
                <div class="perm-group-title">{{ group.module }}</div>
                @for (perm of group.permissions; track perm.id) {
                  <div class="perm-row override-row">
                    <div class="perm-info">
                      <span class="perm-action">{{ perm.action }}</span>
                      <span class="perm-code">({{ perm.code }})</span>
                    </div>
                    <div class="override-toggle">
                      <button
                        class="toggle-btn"
                        [class.active]="getOverride(perm.id!) === 'inherit'"
                        [class.inherit]="getOverride(perm.id!) === 'inherit'"
                        (click)="setOverride(perm.id!, 'inherit')">
                        Inherit
                      </button>
                      <button
                        class="toggle-btn"
                        [class.active]="getOverride(perm.id!) === 'grant'"
                        [class.grant]="getOverride(perm.id!) === 'grant'"
                        (click)="setOverride(perm.id!, 'grant')">
                        Grant
                      </button>
                      <button
                        class="toggle-btn"
                        [class.active]="getOverride(perm.id!) === 'deny'"
                        [class.deny]="getOverride(perm.id!) === 'deny'"
                        (click)="setOverride(perm.id!, 'deny')">
                        Deny
                      </button>
                    </div>
                  </div>
                }
              </div>
            }
          }
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .page-header h1 { margin: 0; font-size: 1.5rem; font-weight: 700; color: var(--text-primary); }
    .page-sub { margin: 4px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .selector-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 1.5rem; background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 12px 16px; }
    .selector-bar label { font-size: 0.875rem; font-weight: 600; color: var(--text-secondary); white-space: nowrap; }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; min-width: 300px; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1); }
    .btn { padding: 6px 12px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8125rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-sm { padding: 5px 10px; font-size: 0.8125rem; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); color: var(--text-primary); }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
    .permissions-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; align-items: start; }
    .section-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.25rem; }
    .section-card h3 { margin: 0; font-size: 1rem; font-weight: 600; color: var(--text-primary); }
    .section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
    .section-desc { margin: 4px 0 1rem; font-size: 0.8125rem; color: var(--text-muted); }
    .perm-group { margin-bottom: 1rem; }
    .perm-group-title { font-size: 0.8125rem; font-weight: 700; color: var(--brand-color); padding: 8px 0 6px; border-bottom: 1px solid var(--border-color); margin-bottom: 4px; }
    .perm-row { display: flex; align-items: center; gap: 8px; padding: 7px 10px; border-radius: 6px; font-size: 0.8125rem; }
    .perm-row:hover { background: var(--bg-hover); }
    .override-row { justify-content: space-between; }
    .perm-info { display: flex; align-items: center; gap: 8px; }
    .perm-action { color: var(--text-primary); font-weight: 500; }
    .perm-code { color: var(--text-muted); font-size: 0.75rem; }
    .badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #dcfce7; color: #166534; }
    .override-toggle { display: flex; gap: 4px; }
    .toggle-btn { padding: 4px 10px; border: 1px solid var(--border-color); border-radius: 6px; background: var(--bg-primary); color: var(--text-muted); font-size: 0.75rem; font-weight: 500; cursor: pointer; transition: all 0.15s; }
    .toggle-btn:hover { background: var(--bg-hover); }
    .toggle-btn.active { font-weight: 600; }
    .toggle-btn.inherit.active { background: #dbeafe; color: #1d4ed8; border-color: #93c5fd; }
    .toggle-btn.grant.active { background: #dcfce7; color: #166534; border-color: #86efac; }
    .toggle-btn.deny.active { background: #fee2e2; color: #dc2626; border-color: #fca5a5; }
    .empty-state { text-align: center; padding: 2rem; color: var(--text-muted); font-size: 0.875rem; }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 4rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    @media (max-width: 1000px) { .permissions-layout { grid-template-columns: 1fr; } }
  `]
})
export class UserPermissionOverrideComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  users: User[] = [];
  allPermissions: Permission[] = [];
  rolePermissions: Permission[] = [];
  existingOverrides: UserPermission[] = [];
  overrideMap: Map<number, 'grant' | 'deny'> = new Map();
  selectedUserId: number | null = null;
  loading = false;
  saving = false;
  showConfirm = false;
  groupedRolePermissions: { module: string; permissions: Permission[] }[] = [];
  groupedAllPermissions: { module: string; permissions: Permission[] }[] = [];

  constructor(
    private userService: UserService,
    private permissionService: PermissionService,
    private userPermissionService: UserPermissionService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.userService.findAll({ page: 0, size: 500, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => { this.users = res.content || []; }
    });
    this.permissionService.findAll({ page: 0, size: 500, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => {
        this.allPermissions = res.content || [];
        this.groupedAllPermissions = this.groupByModule(this.allPermissions);
      }
    });
  }

  groupByModule(perms: Permission[]): { module: string; permissions: Permission[] }[] {
    const map = new Map<string, Permission[]>();
    for (const p of perms) {
      const mod = p.module || 'Other';
      if (!map.has(mod)) map.set(mod, []);
      map.get(mod)!.push(p);
    }
    return Array.from(map.entries()).map(([module, permissions]) => ({
      module,
      permissions: permissions.sort((a, b) => a.action.localeCompare(b.action))
    }));
  }

  onUserChange() {
    if (this.selectedUserId) {
      this.loadUserPermissions();
    } else {
      this.rolePermissions = [];
      this.existingOverrides = [];
      this.overrideMap.clear();
      this.groupedRolePermissions = [];
    }
  }

  loadUserPermissions() {
    if (!this.selectedUserId) return;
    this.loading = true;
    this.overrideMap.clear();

    this.userPermissionService.getEffectivePermissions(this.selectedUserId).subscribe({
      next: (data) => {
        const effective = data || [];
        const rolePermIds = new Set<number>();
        for (const ep of effective) {
          if (ep.source === 'role' || ep.fromRole) {
            rolePermIds.add(ep.permissionId || ep.id);
          }
        }
        this.rolePermissions = this.allPermissions.filter(p => rolePermIds.has(p.id!));
        this.groupedRolePermissions = this.groupByModule(this.rolePermissions);

        this.userPermissionService.findByUserId(this.selectedUserId!).subscribe({
          next: (overrides) => {
            this.existingOverrides = overrides || [];
            for (const o of this.existingOverrides) {
              if (o.granted === true) this.overrideMap.set(o.permissionId, 'grant');
              else if (o.granted === false) this.overrideMap.set(o.permissionId, 'deny');
            }
            this.loading = false;
          },
          error: () => { this.loading = false; }
        });
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load effective permissions');
      }
    });
  }

  getOverride(permissionId: number): 'inherit' | 'grant' | 'deny' {
    return this.overrideMap.get(permissionId) || 'inherit';
  }

  setOverride(permissionId: number, value: 'inherit' | 'grant' | 'deny') {
    if (value === 'inherit') {
      this.overrideMap.delete(permissionId);
    } else {
      this.overrideMap.set(permissionId, value);
    }
  }

  openSave() {
    this.showConfirm = true;
  }

  confirmSave() {
    this.showConfirm = false;
    if (!this.selectedUserId) return;
    this.saving = true;

    const overrides: { permissionId: number; granted: boolean }[] = [];
    this.overrideMap.forEach((value, permissionId) => {
      overrides.push({ permissionId, granted: value === 'grant' });
    });

    this.userPermissionService.bulkSave({
      userId: this.selectedUserId,
      permissionIds: overrides.map(o => o.permissionId),
      granted: true
    }).subscribe({
      next: () => {
        this.saving = false;
        this.toastService.success('Overrides saved successfully');
        this.loadUserPermissions();
      },
      error: () => {
        this.saving = false;
        this.toastService.error('Failed to save overrides');
      }
    });
  }
}
