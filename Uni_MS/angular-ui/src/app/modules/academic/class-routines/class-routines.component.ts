import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClassRoutineService } from '../../../services/class-routine.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-class-routines',
  standalone: true,
  imports: [CommonModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Class Routines</h2>
        <p class="page-sub">Manage class schedules and routines</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add New
      </button>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (onEdit)="openForm($event)"
      (onDelete)="confirmDelete($event)"
      (bulkDelete)="confirmBulkDelete($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showForm) {
      <app-dynamic-form
        [columns]="columns"
        [initialData]="editingItem"
        [title]="editingItem ? 'Edit Class Routine' : 'Add New Class Routine'"
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
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
  `]
})
export class ClassRoutinesComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'subjectId', label: 'Subject ID', type: 'number', required: true, placeholder: 'Subject ID' },
    { key: 'administrationId', label: 'Administration ID', type: 'number', required: true, placeholder: 'Administration ID' },
    { key: 'sectionId', label: 'Section ID', type: 'number', required: true, placeholder: 'Section ID' },
    { key: 'semesterId', label: 'Semester ID', type: 'number', required: true, placeholder: 'Semester ID' },
    { key: 'dayOfWeek', label: 'Day', sortable: true, type: 'select', options: [{ label: 'Monday', value: 'Monday' }, { label: 'Tuesday', value: 'Tuesday' }, { label: 'Wednesday', value: 'Wednesday' }, { label: 'Thursday', value: 'Thursday' }, { label: 'Friday', value: 'Friday' }, { label: 'Saturday', value: 'Saturday' }, { label: 'Sunday', value: 'Sunday' }], required: true },
    { key: 'startTime', label: 'Start Time', type: 'text', required: true, placeholder: '09:00' },
    { key: 'endTime', label: 'End Time', type: 'text', required: true, placeholder: '10:30' },
    { key: 'room', label: 'Room', type: 'text', placeholder: 'Room 101' },
    { key: 'building', label: 'Building', type: 'text' },
    { key: 'classType', label: 'Type', type: 'select', options: [{ label: 'Lecture', value: 'Lecture' }, { label: 'Lab', value: 'Lab' }, { label: 'Tutorial', value: 'Tutorial' }], required: true },
    { key: 'isActive', label: 'Active', type: 'checkbox' }
  ];

  showForm = false;
  editingItem: any = null;
  formError = '';

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  constructor(private service: ClassRoutineService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load class routines'); }
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.params = { ...DEFAULT_PAGE_PARAMS };
    this.loadData();
  }

  openForm(item?: any) {
    this.editingItem = item ? { ...item } : null;
    this.formError = '';
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editingItem = null;
    this.formError = '';
  }

  saveItem(data: any) {
    this.saving = true;
    this.formError = '';

    const handleSuccess = (msg: string) => {
      this.saving = false;
      this.closeForm();
      this.loadData();
      this.toastService.success(msg);
    };

    const handleError = (err: any) => {
      this.saving = false;
      this.formError = err.error?.message || err.message || 'Validation failed. Please check your input.';
    };

    if (this.editingItem?.id) {
      this.service.update(this.editingItem.id, data).subscribe({
        next: () => handleSuccess('Class routine updated successfully'),
        error: handleError
      });
    } else {
      this.service.save(data).subscribe({
        next: () => handleSuccess('Class routine created successfully'),
        error: handleError
      });
    }
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Class Routine';
    this.confirmMessage = `Are you sure you want to delete this class routine? This action cannot be undone.`;
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.deleteTarget = items;
    this.confirmTitle = 'Bulk Delete Class Routines';
    this.confirmMessage = `Are you sure you want to delete ${items.length} selected class routines? This action cannot be undone.`;
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
              this.toastService.success(`${ids.length} class routines deleted`);
            }
          },
          error: () => this.toastService.error('Failed to delete some records')
        });
      });
    } else if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => {
          this.loadData();
          this.toastService.success('Class routine deleted successfully');
        },
        error: () => this.toastService.error('Failed to delete class routine')
      });
    }
    this.deleteTarget = null;
  }
}
