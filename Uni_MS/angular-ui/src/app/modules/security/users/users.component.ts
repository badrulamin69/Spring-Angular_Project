import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { RoleService } from '../../../services/role.service';
import { User } from '../../../models/user';
import { Role } from '../../../models/role';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, DynamicFormComponent, ToastComponent],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  pagedData: PagedResponse<User> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  allRoles: Role[] = [];
  showModal = false;
  isEditing = false;
  selectedUser: any = {};
  columns: TableColumn[] = [
    { key: 'id', label: 'ID' },
    { key: 'uniqueCode', label: 'User Code', sortable: true },
    { key: 'username', label: 'Username' },
    { key: 'email', label: 'Email' },
    { key: 'firstName', label: 'First Name' },
    { key: 'lastName', label: 'Last Name' }
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
      this.userService.update(this.editingItem.id, data).subscribe({
        next: handleSuccess,
        error: handleError
      });
    } else {
      this.userService.save(data).subscribe({
        next: handleSuccess,
        error: handleError
      });
    }
  }

  constructor(private userService: UserService, private roleService: RoleService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
    this.loadRoles();
  }

  loadData() {
    this.loading = true;
    this.userService.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  loadRoles() {
    this.roleService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => { this.allRoles = res.content || []; }
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  openCreate() {
    this.isEditing = false;
    this.selectedUser = { username: '', email: '', password: '', firstName: '', lastName: '', phone: '', roleId: null, active: true };
    this.showModal = true;
  }

  openEdit(user: User) {
    this.isEditing = true;
    this.selectedUser = { ...user, roleId: user.role?.id || null, password: '' };
    this.showModal = true;
  }

  save() {
    const payload: any = {
      username: this.selectedUser.username,
      email: this.selectedUser.email,
      firstName: this.selectedUser.firstName,
      lastName: this.selectedUser.lastName,
      phone: this.selectedUser.phone,
      active: this.selectedUser.active,
      role: this.selectedUser.roleId ? { id: this.selectedUser.roleId } : null
    };
    if (this.selectedUser.password) {
      payload.password = this.selectedUser.password;
    }

    if (this.isEditing && this.selectedUser.id) {
      this.userService.update(this.selectedUser.id, payload).subscribe(() => {
        this.showModal = false;
        this.loadData();
      });
    } else {
      this.userService.save(payload).subscribe(() => {
        this.showModal = false;
        this.loadData();
      });
    }
  }

  delete(id: number | undefined) {
    if (!id) return;
    if (confirm('Are you sure?')) {
      this.userService.delete(id).subscribe(() => this.loadData());
    }
  }
}
