import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MenuService, MenuItem } from '../../../services/menu.service';
import { PermissionService } from '../../../services/permission.service';
import { Permission } from '../../../models/permission';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-menu-permission-mapping',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <app-confirm-dialog
      [open]="showConfirm"
      title="Save Mapping"
      message="Save all menu permission mappings?"
      confirmText="Save"
      type="info"
      (confirmed)="confirmSave()"
      (cancelled)="showConfirm = false"
    ></app-confirm-dialog>

    <div class="page-header">
      <div>
        <h1>Menu Permission Mapping</h1>
        <p class="page-sub">Map permission codes to menu items</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-sm btn-outline" (click)="loadMenus()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/><path d="M11 1v2.5h-2.5M3 13v-2.5h2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          Refresh
        </button>
        <button class="btn btn-sm btn-primary" (click)="openSave()" [disabled]="saving || !hasChanges">
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
        <span>Loading menus...</span>
      </div>
    } @else {
      <div class="table-wrapper">
        <div class="table-scroll">
          <table>
            <thead>
              <tr>
                <th>Menu Title</th>
                <th>Parent</th>
                <th>Route</th>
                <th>Permission Code</th>
                <th>Module</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              @for (item of flatMenus; track item.id) {
                <tr [class.child-row]="item._depth! > 0" [class.modified]="isModified(item)">
                  <td>
                    <span class="menu-title" [style.paddingLeft]="((item._depth || 0) * 20 + 10) + 'px'">
                      @if ((item._depth || 0) === 0) {
                        <span class="tree-icon">{{ item._expanded ? 'Γû╝' : 'Γû╢' }}</span>
                        <button class="tree-toggle" (click)="toggleExpand(item)">{{ item.title }}</button>
                      } @else {
                        {{ item.title }}
                      }
                    </span>
                  </td>
                  <td>{{ item._parentTitle || '-' }}</td>
                  <td class="route-cell">{{ item.route || '-' }}</td>
                  <td>
                    <select class="permission-select" [(ngModel)]="item.permissionCode" (change)="onPermissionChange(item)">
                      <option value="">None</option>
                      @for (perm of permissions; track perm.id) {
                        <option [value]="perm.code">{{ perm.code }} ({{ perm.action }})</option>
                      }
                    </select>
                  </td>
                  <td>{{ item.module || '-' }}</td>
                  <td>
                    <span class="badge" [class.badge-success]="item.visible" [class.badge-muted]="!item.visible">
                      {{ item.visible ? 'Visible' : 'Hidden' }}
                    </span>
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="6" class="empty-state">
                    <div class="empty-icon">≡ƒôä</div>
                    <div>No menus found</div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
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
    .table-wrapper { background: var(--bg-secondary); border-radius: 12px; border: 1px solid var(--border-color); overflow: hidden; }
    .table-scroll { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 10px 14px; text-align: left; border-bottom: 1px solid var(--border-color); color: var(--text-primary); white-space: nowrap; font-size: 0.8125rem; }
    th { background: var(--bg-tertiary); font-weight: 600; color: var(--text-secondary); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; position: sticky; top: 0; z-index: 1; }
    tr:hover { background: var(--bg-hover); }
    tr.modified { background: rgba(251, 191, 36, 0.08); }
    tr.child-row { background: var(--bg-primary); }
    .menu-title { display: flex; align-items: center; gap: 6px; }
    .tree-icon { font-size: 0.65rem; color: var(--text-muted); width: 12px; }
    .tree-toggle { background: none; border: none; cursor: pointer; font-weight: 600; color: var(--text-primary); font-size: 0.875rem; padding: 0; }
    .tree-toggle:hover { color: var(--brand-color); }
    .route-cell { font-family: monospace; font-size: 0.75rem; color: var(--text-muted); }
    .permission-select { padding: 5px 8px; border: 1px solid var(--border-color); border-radius: 6px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.8125rem; min-width: 200px; }
    .permission-select:focus { outline: none; border-color: var(--brand-color); }
    .badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #dcfce7; color: #166534; }
    .badge-muted { background: var(--bg-tertiary); color: var(--text-muted); }
    .empty-state { text-align: center; padding: 3rem 1rem !important; color: var(--text-muted); }
    .empty-icon { font-size: 2rem; margin-bottom: 8px; }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 4rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class MenuPermissionMappingComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  menus: MenuItem[] = [];
  flatMenus: (MenuItem & { _depth?: number; _parentTitle?: string; parent?: any })[] = [];
  permissions: Permission[] = [];
  originalMap: Map<number, string> = new Map();
  loading = true;
  saving = false;
  hasChanges = false;
  showConfirm = false;

  constructor(
    private menuService: MenuService,
    private permissionService: PermissionService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadMenus();
    this.loadPermissions();
  }

  loadMenus() {
    this.loading = true;
    this.menuService.getAllMenus().subscribe({
      next: (res) => {
        this.menus = res?.data || res || [];
        this.originalMap.clear();
        for (const menu of this.menus) {
          this.originalMap.set(menu.id, menu.permissionCode || '');
          this.flattenWithChildren(menu, 0);
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load menus');
      }
    });
  }

  flattenWithChildren(menu: MenuItem, depth: number, parentTitle?: string) {
    const entry = { ...menu, _depth: depth, _parentTitle: parentTitle } as any;
    this.flatMenus.push(entry);
    if (menu.children) {
      for (const child of menu.children) {
        this.flattenWithChildren(child, depth + 1, menu.title);
      }
    }
  }

  loadPermissions() {
    this.permissionService.findAll({ page: 0, size: 500, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => { this.permissions = res.content || []; }
    });
  }

  toggleExpand(menu: MenuItem) {
    menu._expanded = !menu._expanded;
    this.rebuildFlatList();
  }

  rebuildFlatList() {
    this.flatMenus = [];
    for (const menu of this.menus) {
      this.flattenWithChildren(menu, 0);
    }
  }

  onPermissionChange(item: any) {
    const original = this.originalMap.get(item.id) || '';
    this.hasChanges = this.flatMenus.some(m => (m.permissionCode || '') !== (this.originalMap.get(m.id) || ''));
  }

  isModified(item: any): boolean {
    return (item.permissionCode || '') !== (this.originalMap.get(item.id) || '');
  }

  openSave() {
    this.showConfirm = true;
  }

  confirmSave() {
    this.showConfirm = false;
    this.saving = true;

    const changed = this.flatMenus.filter(m => this.isModified(m));
    if (changed.length === 0) {
      this.saving = false;
      this.toastService.info('No changes to save');
      return;
    }

    let completed = 0;
    let errors = 0;
    const total = changed.length;

    const done = () => {
      completed++;
      if (completed === total) {
        this.saving = false;
        if (errors > 0) {
          this.toastService.error(`${errors} updates failed`);
        } else {
          this.toastService.success('Menu permissions updated');
          this.loadMenus();
        }
      }
    };

    for (const menu of changed) {
      const payload: any = {
        title: menu.title,
        icon: menu.icon,
        route: menu.route,
        orderNo: menu.orderNo,
        permissionCode: menu.permissionCode,
        module: menu.module,
        visible: menu.visible
      };

      this.menuService.updateMenu(menu.id, payload).subscribe({
        next: done,
        error: () => { errors++; done(); }
      });
    }
  }
}
