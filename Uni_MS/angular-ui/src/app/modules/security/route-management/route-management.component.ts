import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MenuService, MenuItem } from '../../../services/menu.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';

interface RouteEntry {
  id: number;
  route: string;
  title: string;
  module: string;
  permissionCode: string;
  icon: string;
  orderNo: number;
  parentTitle: string;
  visible: boolean;
  active: boolean;
  original: MenuItem;
}

@Component({
  selector: 'app-route-management',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <style>
      .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
      .page-header h2 { margin: 0; font-size: 1.5rem; color: #1a1a2e; }
      .page-sub { font-size: 0.875rem; color: #6c757d; margin-top: 0.25rem; }
      .page-header-right { display: flex; gap: 0.5rem; align-items: center; }
      .btn { padding: 0.5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: 0.875rem; display: inline-flex; align-items: center; gap: 0.375rem; font-weight: 500; transition: all 0.15s; }
      .btn-primary { background: #4361ee; color: #fff; }
      .btn-primary:hover { background: #3a56d4; }
      .btn-outline { background: transparent; border: 1px solid #dee2e6; color: #495057; }
      .btn-outline:hover { background: #f8f9fa; }
      .btn-sm { padding: 0.375rem 0.75rem; font-size: 0.8125rem; }
      .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; }
      .filter-bar select, .filter-bar input { padding: 0.4rem 0.75rem; border: 1px solid #dee2e6; border-radius: 6px; font-size: 0.8125rem; background: #fff; color: #1a1a2e; }
      .filter-bar label { font-size: 0.8125rem; color: #495057; font-weight: 500; }
      .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; backdrop-filter: blur(4px); }
      .modal { background: #fff; border-radius: 12px; padding: 1.5rem; width: 520px; max-height: 80vh; overflow-y: auto; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }
      .modal h3 { margin-top: 0; margin-bottom: 1.25rem; font-size: 1.125rem; color: #1a1a2e; }
      .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
      .form-group { margin-bottom: 0; }
      .form-group.full { grid-column: 1 / -1; }
      .form-group label { display: block; margin-bottom: 0.25rem; font-weight: 500; font-size: 0.8125rem; color: #495057; }
      .form-group input, .form-group select { width: 100%; padding: 0.5rem 0.75rem; border: 1px solid #dee2e6; border-radius: 6px; font-size: 0.875rem; box-sizing: border-box; }
      .form-group input:focus, .form-group select:focus { outline: none; border-color: #4361ee; box-shadow: 0 0 0 3px rgba(67,97,238,0.1); }
      .form-check { display: flex; align-items: center; gap: 0.5rem; margin-top: 1.5rem; }
      .form-check input[type="checkbox"] { width: auto; accent-color: #4361ee; }
      .form-check label { margin-bottom: 0; }
      .form-actions { display: flex; gap: 0.5rem; justify-content: flex-end; margin-top: 1.25rem; padding-top: 1rem; border-top: 1px solid #e9ecef; }
      .stats-row { display: flex; gap: 1rem; margin-bottom: 1.25rem; }
      .stat-card { background: #fff; border: 1px solid #e9ecef; border-radius: 8px; padding: 0.875rem 1.25rem; flex: 1; }
      .stat-card .stat-label { font-size: 0.75rem; color: #6c757d; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 0.25rem; }
      .stat-card .stat-value { font-size: 1.25rem; font-weight: 700; color: #1a1a2e; }
    </style>

    <div class="page-header">
      <div>
        <h2>Route Management</h2>
        <div class="page-sub">All registered frontend routes with permissions and role access</div>
      </div>
      <div class="page-header-right">
        <button class="btn btn-outline btn-sm" (click)="loadData()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/><path d="M11 1v2.5h-2.5M3 13v-2.5h2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          Refresh
        </button>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-label">Total Routes</div>
        <div class="stat-value">{{ filteredRoutes.length }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">Active</div>
        <div class="stat-value">{{ filteredRoutes.filter(r => r.active).length }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">Visible</div>
        <div class="stat-value">{{ filteredRoutes.filter(r => r.visible).length }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">Modules</div>
        <div class="stat-value">{{ uniqueModules.length }}</div>
      </div>
    </div>

    <div class="filter-bar">
      <label>Module:</label>
      <select [(ngModel)]="selectedModule" (change)="applyFilters()">
        <option value="">All Modules</option>
        @for (mod of uniqueModules; track mod) {
          <option [value]="mod">{{ mod }}</option>
        }
      </select>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedRoutes"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (onEdit)="openEdit($event)"
      (onDelete)="confirmDelete($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showModal) {
      <div class="modal-overlay" (click)="showModal = false">
        <div class="modal" (click)="$event.stopPropagation()">
          <h3>Edit Route</h3>
          <div class="form-grid">
            <div class="form-group">
              <label>Route Path</label>
              <input type="text" [(ngModel)]="editingRoute.route" placeholder="/security/users">
            </div>
            <div class="form-group">
              <label>Menu Title</label>
              <input type="text" [(ngModel)]="editingRoute.title" placeholder="Users">
            </div>
            <div class="form-group">
              <label>Module</label>
              <input type="text" [(ngModel)]="editingRoute.module" placeholder="Security" disabled>
            </div>
            <div class="form-group">
              <label>Required Permission</label>
              <input type="text" [(ngModel)]="editingRoute.permissionCode" placeholder="USER_VIEW">
            </div>
            <div class="form-group">
              <label>Icon</label>
              <input type="text" [(ngModel)]="editingRoute.icon" placeholder="icon">
            </div>
            <div class="form-group">
              <label>Order No</label>
              <input type="number" [(ngModel)]="editingRoute.orderNo">
            </div>
            <div class="form-group">
              <label>Parent Menu</label>
              <input type="text" [value]="editingRoute.parentTitle || 'ΓÇö'" disabled>
            </div>
            <div class="form-check">
              <input type="checkbox" id="editVisible" [(ngModel)]="editingRoute.visible">
              <label for="editVisible">Visible</label>
            </div>
            <div class="form-check">
              <input type="checkbox" id="editActive" [(ngModel)]="editingRoute.active">
              <label for="editActive">Active</label>
            </div>
          </div>
          <div class="form-actions">
            <button class="btn btn-outline" (click)="showModal = false">Cancel</button>
            <button class="btn btn-primary" (click)="saveRoute()">Update</button>
          </div>
        </div>
      </div>
    }

    <app-confirm-dialog
      [open]="showConfirm"
      title="Delete Route"
      message="Are you sure you want to delete this menu entry? All children will also be removed."
      confirmText="Delete"
      type="danger"
      (confirmed)="onDeleteConfirmed()"
      (cancelled)="showConfirm = false"
    ></app-confirm-dialog>

    <app-toast></app-toast>
  `,
  styles: []
})
export class RouteManagementComponent implements OnInit {
  allRoutes: RouteEntry[] = [];
  filteredRoutes: RouteEntry[] = [];
  pagedRoutes: RouteEntry[] = [];
  pagedData: PagedResponse<RouteEntry> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS, sortBy: 'orderNo', sortDir: 'asc' };
  uniqueModules: string[] = [];
  selectedModule = '';
  searchTerm = '';

  showModal = false;
  showConfirm = false;
  editingRoute: any = {};
  routeToDelete: RouteEntry | null = null;

  columns: TableColumn[] = [
    { key: 'route', label: 'Route Path', sortable: true, type: 'text' },
    { key: 'title', label: 'Menu Title', sortable: true, type: 'text' },
    { key: 'module', label: 'Module', sortable: true, type: 'text' },
    { key: 'permissionCode', label: 'Required Permission', type: 'text' },
    { key: 'icon', label: 'Icon', type: 'text', hidden: true },
    { key: 'orderNo', label: 'Order', sortable: true, type: 'number' },
    { key: 'parentTitle', label: 'Parent Menu', type: 'text' },
    { key: 'visible', label: 'Visible', type: 'checkbox' },
    { key: 'active', label: 'Active', type: 'checkbox' }
  ];

  constructor(private menuService: MenuService, private toast: ToastService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.menuService.getAllMenus().subscribe({
      next: (res) => {
        const menus: MenuItem[] = res?.data || res || [];
        this.allRoutes = this.flattenMenuTree(menus, null);
        this.uniqueModules = [...new Set(this.allRoutes.map(r => r.module).filter(Boolean))].sort();
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toast.error('Failed to load routes');
      }
    });
  }

  private flattenMenuTree(menus: MenuItem[], parentTitle: string | null): RouteEntry[] {
    let result: RouteEntry[] = [];
    for (const menu of menus) {
      result.push({
        id: menu.id,
        route: menu.route || '',
        title: menu.title || '',
        module: (menu as any).module || '',
        permissionCode: menu.permissionCode || '',
        icon: menu.icon || '',
        orderNo: menu.orderNo || 0,
        parentTitle: parentTitle || 'ΓÇö',
        visible: (menu as any).visible ?? true,
        active: (menu as any).active ?? true,
        original: menu
      });
      if (menu.children && menu.children.length > 0) {
        result = result.concat(this.flattenMenuTree(menu.children, menu.title));
      }
    }
    return result;
  }

  applyFilters() {
    let result = [...this.allRoutes];
    if (this.selectedModule) {
      result = result.filter(r => r.module === this.selectedModule);
    }
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(r =>
        r.route.toLowerCase().includes(term) ||
        r.title.toLowerCase().includes(term) ||
        r.permissionCode.toLowerCase().includes(term)
      );
    }
    this.filteredRoutes = this.sortRoutes(result);
    this.updatePagedData();
  }

  private sortRoutes(routes: RouteEntry[]): RouteEntry[] {
    const key = this.params.sortBy as keyof RouteEntry;
    const dir = this.params.sortDir === 'asc' ? 1 : -1;
    return routes.sort((a, b) => {
      const aVal = a[key];
      const bVal = b[key];
      if (typeof aVal === 'string') return aVal.localeCompare(bVal as string) * dir;
      return ((aVal as number) - (bVal as number)) * dir;
    });
  }

  updatePagedData() {
    const start = this.params.page * this.params.size;
    const end = start + this.params.size;
    this.pagedRoutes = this.filteredRoutes.slice(start, end);
    this.pagedData = {
      content: this.pagedRoutes,
      page: this.params.page,
      size: this.params.size,
      totalElements: this.filteredRoutes.length,
      totalPages: Math.ceil(this.filteredRoutes.length / this.params.size),
      first: this.params.page === 0,
      last: end >= this.filteredRoutes.length,
      empty: this.filteredRoutes.length === 0
    };
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.applyFilters();
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.params.page = 0;
    this.applyFilters();
  }

  openEdit(route: RouteEntry) {
    this.editingRoute = { ...route };
    this.showModal = true;
  }

  saveRoute() {
    if (!this.editingRoute.id) return;
    const payload: any = {
      title: this.editingRoute.title,
      icon: this.editingRoute.icon,
      route: this.editingRoute.route,
      orderNo: this.editingRoute.orderNo,
      permissionCode: this.editingRoute.permissionCode,
      module: this.editingRoute.module,
      visible: this.editingRoute.visible,
      active: this.editingRoute.active,
      parent: this.editingRoute.original?.parent || null
    };
    this.menuService.updateMenu(this.editingRoute.id, payload).subscribe({
      next: () => {
        this.showModal = false;
        this.toast.success('Route updated successfully');
        this.loadData();
      },
      error: (err) => {
        this.toast.error('Failed to update route: ' + (err.error?.message || 'Unknown error'));
      }
    });
  }

  confirmDelete(route: RouteEntry) {
    this.routeToDelete = route;
    this.showConfirm = true;
  }

  onDeleteConfirmed() {
    if (!this.routeToDelete) return;
    this.menuService.deleteMenu(this.routeToDelete.id).subscribe({
      next: () => {
        this.showConfirm = false;
        this.routeToDelete = null;
        this.toast.success('Route deleted successfully');
        this.loadData();
      },
      error: (err) => {
        this.toast.error('Failed to delete route: ' + (err.error?.message || 'Unknown error'));
      }
    });
  }
}
