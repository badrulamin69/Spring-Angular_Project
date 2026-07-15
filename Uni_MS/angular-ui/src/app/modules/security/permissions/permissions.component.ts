import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PermissionService } from '../../../services/permission.service';
import { SystemSettingService } from '../../../services/system-setting.service';
import { Permission } from '../../../models/permission';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';

@Component({
  selector: 'app-permissions',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, DynamicFormComponent],
  templateUrl: './permissions.component.html'
})
export class PermissionsComponent implements OnInit {
  pagedData: PagedResponse<Permission> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  showModal = false;
  isEditing = false;
  selectedPerm: any = {};
  modules: string[] = [];
  actions: string[] = [];
  columns: TableColumn[] = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Name' },
    { key: 'code', label: 'Code' },
    { key: 'module', label: 'Module' },
    { key: 'action', label: 'Action' }
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
      alert('Error saving record: ' + (err.error?.message || err.message || 'Validation failed'));
    };

    if (this.editingItem && this.editingItem.id) {
      this.service.update(this.editingItem.id, data).subscribe({
        next: handleSuccess,
        error: handleError
      });
    } else {
      this.service.save(data).subscribe({
        next: handleSuccess,
        error: handleError
      });
    }
  }

  constructor(private service: PermissionService, private systemSettingService: SystemSettingService) {}

  ngOnInit() {
    this.loadData();
    this.loadDropdowns();
  }

  loadDropdowns() {
    this.systemSettingService.getDropdowns().subscribe({
      next: (res) => {
        const data = res?.data || res;
        this.modules = data?.modules || [];
        this.actions = data?.actions || [];
      }
    });
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  openCreate() {
    this.isEditing = false;
    this.selectedPerm = { name: '', code: '', module: this.modules[0] || 'Security', action: this.actions[0] || 'VIEW', description: '' };
    this.showModal = true;
  }

  openEdit(perm: Permission) {
    this.isEditing = true;
    this.selectedPerm = { ...perm };
    this.showModal = true;
  }

  save() {
    const payload: Permission = {
      name: this.selectedPerm.name,
      code: this.selectedPerm.code,
      module: this.selectedPerm.module,
      action: this.selectedPerm.action,
      description: this.selectedPerm.description
    };

    if (this.isEditing && this.selectedPerm.id) {
      this.service.update(this.selectedPerm.id, payload).subscribe(() => {
        this.showModal = false;
        this.loadData();
      });
    } else {
      this.service.save(payload).subscribe(() => {
        this.showModal = false;
        this.loadData();
      });
    }
  }

  delete(id: number | undefined) {
    if (!id) return;
    if (confirm('Are you sure?')) {
      this.service.delete(id).subscribe(() => this.loadData());
    }
  }
}
