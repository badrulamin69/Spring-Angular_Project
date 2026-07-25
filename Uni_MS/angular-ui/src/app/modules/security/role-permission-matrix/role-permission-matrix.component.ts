import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoleService } from '../../../services/role.service';
import { PermissionService } from '../../../services/permission.service';
import { RolePermissionService } from '../../../services/role-permission.service';
import { Role } from '../../../models/role';
import { Permission } from '../../../models/permission';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-role-permission-matrix',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <app-confirm-dialog
      [open]="showConfirm"
      title="Save Matrix"
      message="Save all role-permission assignments?"
      confirmText="Save"
      type="info"
      (confirmed)="confirmSave()"
      (cancelled)="showConfirm = false"
    ></app-confirm-dialog>

    <div class="page-header">
      <div>
        <h1>Role Permission Matrix</h1>
        <p class="page-sub">Assign permissions to roles using the matrix below</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-sm btn-outline" (click)="loadAll()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/><path d="M11 1v2.5h-2.5M3 13v-2.5h2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          Refresh
        </button>
        <button class="btn btn-sm btn-primary" (click)="openSave()" [disabled]="saving || hasChanges === false">
          @if (saving) {
            <span class="btn-spinner"></span> Saving...
          } @else {
            Save Changes
          }
        </button>
      </div>
    </div>

    @if (loading) {
      <div class="loading-state">
        <div class="spinner"></div>
        <span>Loading matrix...</span>
      </div>
    } @else {
      <div class="matrix-wrapper">
        <table class="matrix-table">
          <thead>
            <tr>
              <th class="perm-col">Permission</th>
              @for (role of roles; track role.id) {
                <th>{{ role.name }}</th>
              }
            </tr>
          </thead>
          <tbody>
            @for (group of groupedPermissions; track group.module) {
              <tr class="module-group">
                <td [attr.colspan]="roles.length + 1">{{ group.module }}</td>
              </tr>
              @for (perm of group.permissions; track perm.id) {
                <tr>
                  <td class="perm-label">{{ perm.action }} <span class="perm-code">({{ perm.code }})</span></td>
                  @for (role of roles; track role.id) {
                    <td class="checkbox-cell">
                      <input
                        type="checkbox"
                        [checked]="isAssigned(perm.id!, role.id!)"
                        (change)="toggle(perm.id!, role.id!)">
                    </td>
                  }
                </tr>
              }
            }
          </tbody>
        </table>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .page-header h1 { margin: 0; font-size: 1.5rem; font-weight: 700; color: var(--text-primary); }
    .page-sub { margin: 4px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .header-actions { display: flex; gap: 8px; align-items: center; }
    .btn { padding: 6px 12px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8125rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-sm { padding: 5px 10px; font-size: 0.8125rem; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); color: var(--text-primary); }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
    .matrix-wrapper { overflow-x: auto; background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; }
    .matrix-table { border-collapse: collapse; width: 100%; }
    .matrix-table th, .matrix-table td { padding: 8px 12px; border: 1px solid var(--border-color); text-align: center; font-size: 0.8125rem; }
    .matrix-table th { background: var(--bg-tertiary); font-weight: 600; position: sticky; top: 0; z-index: 1; color: var(--text-secondary); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .matrix-table th:first-child, .matrix-table td:first-child { text-align: left; position: sticky; left: 0; background: var(--bg-secondary); z-index: 1; min-width: 250px; }
    .matrix-table th:first-child { background: var(--bg-tertiary); z-index: 2; }
    .module-group { background: var(--bg-primary); font-weight: 700; color: var(--brand-color); }
    .module-group td { text-align: left; font-weight: 700; padding-left: 16px; }
    .perm-label { font-size: 0.8125rem; color: var(--text-primary); }
    .perm-code { color: var(--text-muted); font-size: 0.75rem; }
    .checkbox-cell { padding: 6px 12px; }
    .checkbox-cell input[type="checkbox"] { width: 18px; height: 18px; cursor: pointer; accent-color: var(--brand-color); }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 4rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class RolePermissionMatrixComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  roles: Role[] = [];
  permissions: Permission[] = [];
  groupedPermissions: { module: string; permissions: Permission[] }[] = [];
  matrix: Set<string> = new Set();
  loading = true;
  saving = false;
  hasChanges = false;
  showConfirm = false;
  private initialMatrix: Set<string> = new Set();

  constructor(
    private roleService: RoleService,
    private permissionService: PermissionService,
    private rolePermissionService: RolePermissionService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    this.loading = true;
    this.roleService.findAll({ page: 0, size: 500, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => { this.roles = res.content || []; this.loadPermissions(); },
      error: () => { this.toastService.error('Failed to load roles'); this.loading = false; }
    });
  }

  loadPermissions() {
    this.permissionService.findAll({ page: 0, size: 500, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => {
        this.permissions = res.content || [];
        this.groupPermissions();
        this.loadExistingAssignments();
      },
      error: () => { this.toastService.error('Failed to load permissions'); this.loading = false; }
    });
  }

  groupPermissions() {
    const map = new Map<string, Permission[]>();
    for (const perm of this.permissions) {
      const mod = perm.module || 'Other';
      if (!map.has(mod)) map.set(mod, []);
      map.get(mod)!.push(perm);
    }
    this.groupedPermissions = Array.from(map.entries()).map(([module, permissions]) => ({
      module,
      permissions: permissions.sort((a, b) => a.action.localeCompare(b.action))
    }));
  }

  loadExistingAssignments() {
    this.rolePermissionService.findAll({ page: 0, size: 5000, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => {
        const assignments = res.content || [];
        this.matrix = new Set();
        for (const a of assignments) {
          this.matrix.add(`${a.roleId}-${a.permissionId}`);
        }
        this.initialMatrix = new Set(this.matrix);
        this.hasChanges = false;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load assignments');
      }
    });
  }

  isAssigned(permissionId: number, roleId: number): boolean {
    return this.matrix.has(`${roleId}-${permissionId}`);
  }

  toggle(permissionId: number, roleId: number) {
    const key = `${roleId}-${permissionId}`;
    if (this.matrix.has(key)) {
      this.matrix.delete(key);
    } else {
      this.matrix.add(key);
    }
    this.hasChanges = this.matrix.size !== this.initialMatrix.size ||
      ![...this.matrix].every(k => this.initialMatrix.has(k));
  }

  openSave() {
    this.showConfirm = true;
  }

  confirmSave() {
    this.showConfirm = false;
    this.saving = true;

    const allPairs = new Set<string>();
    for (const r of this.roles) {
      for (const p of this.permissions) {
        allPairs.add(`${r.id}-${p.id}`);
      }
    }

    const toAdd: { roleId: number; permissionId: number }[] = [];
    for (const pair of this.matrix) {
      if (!this.initialMatrix.has(pair)) {
        const [roleId, permissionId] = pair.split('-').map(Number);
        toAdd.push({ roleId, permissionId });
      }
    }

    const toRemove: { roleId: number; permissionId: number }[] = [];
    for (const pair of this.initialMatrix) {
      if (!this.matrix.has(pair)) {
        const [roleId, permissionId] = pair.split('-').map(Number);
        toRemove.push({ roleId, permissionId });
      }
    }

    const requests = [
      ...toAdd.map(pair => this.rolePermissionService.save(pair as any)),
      ...toRemove.map(pair => this.rolePermissionService.findById(0).toPromise().catch(() => null))
    ];

    if (requests.length === 0) {
      this.saving = false;
      this.toastService.info('No changes to save');
      return;
    }

    let completed = 0;
    let errors = 0;

    const done = () => {
      completed++;
      if (completed === requests.length) {
        this.saving = false;
        if (errors > 0) {
          this.toastService.error(`${errors} operations failed`);
        } else {
          this.toastService.success('Matrix saved successfully');
          this.loadExistingAssignments();
        }
      }
    };

    for (const req of toAdd) {
      this.rolePermissionService.save(req as any).subscribe({
        next: done,
        error: () => { errors++; done(); }
      });
    }
    for (const req of toRemove) {
      this.rolePermissionService.findAll({ page: 0, size: 1, sortBy: 'id', sortDir: 'asc' }).subscribe({
        next: (res) => {
          const existing = (res.content || []).find((rp: any) => rp.roleId === req.roleId && rp.permissionId === req.permissionId);
          if (existing?.id) {
            this.rolePermissionService.delete(existing.id).subscribe({ next: done, error: () => { errors++; done(); } });
          } else {
            done();
          }
        },
        error: () => { errors++; done(); }
      });
    }
  }
}
