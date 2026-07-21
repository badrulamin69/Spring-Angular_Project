import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoleService } from '../../../services/role.service';
import { PermissionService } from '../../../services/permission.service';
import { Role } from '../../../models/role';
import { Permission } from '../../../models/permission';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, DynamicFormComponent, ToastComponent],
  templateUrl: './roles.component.html'
})
export class RolesComponent implements OnInit {
  pagedData: PagedResponse<Role> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  allPermissions: Permission[] = [];
  showModal = false;
  isEditing = false;
  selectedRole: any = {};
  selectedPermissionIds: number[] = [];
  columns: TableColumn[] = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Name' },
    { key: 'code', label: 'Code' },
    { key: 'description', label: 'Description' }
  ];

  
  showForm = false;
  editingItem: any = null;

  openForm(item?: any) {
    this.editingItem = item || null;
    this.showForm = true;
  }

  saveItem(data: any) {
    const handleSuccess = () => {
      this.showForm = false;
      this.loadData();
    };
    const handleError = (err: any) => {
      this.toastService.error('Error saving record: ' + (err.error?.message || err.message || 'Validation failed'));
    };

    if (this.editingItem && this.editingItem.id) {
      this.roleService.update(this.editingItem.id, data).subscribe({
        next: handleSuccess,
        error: handleError
      });
    } else {
      this.roleService.save(data).subscribe({
        next: handleSuccess,
        error: handleError
      });
    }
  }

  constructor(private roleService: RoleService, private permissionService: PermissionService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
    this.loadPermissions();
  }

  loadData() {
    this.loading = true;
    this.roleService.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  loadPermissions() {
    this.permissionService.findAll({ page: 0, size: 500, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => { this.allPermissions = res.content || []; }
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  openCreate() {
    this.isEditing = false;
    this.selectedRole = { name: '', code: '', description: '', active: true };
    this.selectedPermissionIds = [];
    this.showModal = true;
  }

  openEdit(role: Role) {
    this.isEditing = true;
    this.selectedRole = { ...role };
    this.selectedPermissionIds = (role.permissions || []).map(p => p.id!);
    this.showModal = true;
  }

  togglePermission(permId: number) {
    const idx = this.selectedPermissionIds.indexOf(permId);
    if (idx >= 0) {
      this.selectedPermissionIds.splice(idx, 1);
    } else {
      this.selectedPermissionIds.push(permId);
    }
  }

  save() {
    const payload: any = {
      name: this.selectedRole.name,
      code: this.selectedRole.code,
      description: this.selectedRole.description,
      active: this.selectedRole.active,
      permissions: this.selectedPermissionIds.map(id => ({ id }))
    };

    if (this.isEditing && this.selectedRole.id) {
      this.roleService.update(this.selectedRole.id, payload).subscribe(() => {
        this.showModal = false;
        this.loadData();
      });
    } else {
      this.roleService.save(payload).subscribe(() => {
        this.showModal = false;
        this.loadData();
      });
    }
  }

  delete(id: number | undefined) {
    if (!id) return;
    if (confirm('Are you sure?')) {
      this.roleService.delete(id).subscribe(() => this.loadData());
    }
  }

  hasPermission(permId: number): boolean {
    return this.selectedPermissionIds.includes(permId);
  }
}
