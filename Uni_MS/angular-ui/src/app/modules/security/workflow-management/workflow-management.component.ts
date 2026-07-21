import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WorkflowService } from '../../../services/workflow.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';
import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';

@Component({
  selector: 'app-workflow-management',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Workflow Management</h2>
        <p class="page-sub">Configure approval workflows for leave requests, admissions, and more</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add Workflow
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
        [title]="editingItem ? 'Edit Workflow' : 'Add New Workflow'"
        [saving]="saving"
        [errorMessage]="formError"
        (save)="saveItem($event)"
        (cancel)="closeForm()">
      </app-dynamic-form>
    }

    @if (showSteps) {
      <div class="modal-overlay" (click)="showSteps = false">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Workflow Steps: {{ selectedWorkflow?.name }}</h3>
            <button class="modal-close" (click)="showSteps = false">&times;</button>
          </div>
          <div class="modal-body">
            <div class="steps-list">
              @for (step of steps; track step.id; let i = $index) {
                <div class="step-item">
                  <div class="step-number">{{ i + 1 }}</div>
                  <div class="step-info">
                    <div class="step-name">{{ step.name }}</div>
                    <div class="step-meta">
                      <span class="badge badge-blue">{{ step.requiredRole || 'Any Role' }}</span>
                      <span class="badge badge-green">{{ step.requiredPermission || 'Any Permission' }}</span>
                    </div>
                  </div>
                  <div class="step-actions">
                    <button class="btn-icon" title="Edit" (click)="editStep(step)">
                      <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M10.5 1.5l2 2-8 8H2.5v-2l8-8z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </button>
                    <button class="btn-icon danger" title="Delete" (click)="deleteStep(step)">
                      <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M2 4h10M5 4V2.5h4V4M3.5 4v7.5a1 1 0 001 1h5a1 1 0 001-1V4" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </button>
                  </div>
                </div>
              } @empty {
                <div class="empty-state">No steps configured yet.</div>
              }
            </div>
            <button class="btn btn-secondary mt-3" (click)="addStep()">+ Add Step</button>
          </div>
        </div>
      </div>
    }

    @if (showStepForm) {
      <div class="modal-overlay" (click)="showStepForm = false">
        <div class="modal-content modal-sm" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingStep ? 'Edit Step' : 'Add Step' }}</h3>
            <button class="modal-close" (click)="showStepForm = false">&times;</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Step Name</label>
              <input type="text" class="form-control" [(ngModel)]="stepForm.name" placeholder="e.g. Department Head Approval">
            </div>
            <div class="form-group">
              <label>Step Order</label>
              <input type="number" class="form-control" [(ngModel)]="stepForm.stepOrder" min="1">
            </div>
            <div class="form-group">
              <label>Required Role</label>
              <input type="text" class="form-control" [(ngModel)]="stepForm.requiredRole" placeholder="e.g. ROLE_DEPT_HEAD">
            </div>
            <div class="form-group">
              <label>Required Permission</label>
              <input type="text" class="form-control" [(ngModel)]="stepForm.requiredPermission" placeholder="e.g. LEAVE_APPROVE">
            </div>
            <div class="modal-actions">
              <button class="btn btn-secondary" (click)="showStepForm = false">Cancel</button>
              <button class="btn btn-primary" (click)="saveStep()">Save</button>
            </div>
          </div>
        </div>
      </div>
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
    .btn-secondary { background: var(--bg-primary); color: var(--text-primary); border: 1px solid var(--border-color); }
    .mt-3 { margin-top: 1rem; }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: var(--bg-secondary); border-radius: 12px; width: 90%; max-width: 600px; max-height: 80vh; overflow-y: auto; }
    .modal-content.modal-sm { max-width: 400px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid var(--border-color); }
    .modal-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .modal-close { background: none; border: none; font-size: 1.5rem; color: var(--text-muted); cursor: pointer; }
    .modal-body { padding: 20px; }
    .modal-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 16px; }
    .steps-list { display: flex; flex-direction: column; gap: 8px; }
    .step-item { display: flex; align-items: center; gap: 12px; padding: 12px; background: var(--bg-primary); border: 1px solid var(--border-color); border-radius: 8px; }
    .step-number { width: 32px; height: 32px; border-radius: 50%; background: var(--brand-color); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 600; font-size: 0.875rem; flex-shrink: 0; }
    .step-info { flex: 1; }
    .step-name { font-weight: 500; color: var(--text-primary); }
    .step-meta { display: flex; gap: 6px; margin-top: 4px; }
    .badge { padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 500; }
    .badge-blue { background: #dbeafe; color: #002d5f; }
    .badge-green { background: #dcfce7; color: #15803d; }
    .step-actions { display: flex; gap: 4px; }
    .btn-icon { background: none; border: none; padding: 4px; cursor: pointer; color: var(--text-muted); border-radius: 4px; }
    .btn-icon:hover { background: var(--bg-tertiary); }
    .btn-icon.danger:hover { color: #dc3545; }
    .form-group { margin-bottom: 12px; }
    .form-group label { display: block; font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; margin-bottom: 4px; }
    .form-control { width: 100%; padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
    .empty-state { text-align: center; color: var(--text-muted); padding: 2rem; }
  `]
})
export class WorkflowManagementComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Name', sortable: true, type: 'text', required: true, placeholder: 'Workflow name' },
    { key: 'description', label: 'Description', type: 'textarea', placeholder: 'Description' },
    { key: 'moduleName', label: 'Module', sortable: true, type: 'text', required: true, placeholder: 'e.g. HRM' },
    { key: 'entityType', label: 'Entity Type', type: 'text', placeholder: 'e.g. LEAVE_REQUEST' },
    { key: 'active', label: 'Active', type: 'checkbox' }
  ];

  showForm = false;
  editingItem: any = null;
  formError = '';

  showSteps = false;
  selectedWorkflow: any = null;
  steps: any[] = [];

  showStepForm = false;
  editingStep: any = null;
  stepForm: any = { name: '', stepOrder: 1, requiredRole: '', requiredPermission: '' };

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  constructor(private service: WorkflowService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data: any) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load workflows'); }
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
      this.formError = err.error?.message || 'Failed to save';
    };

    if (this.editingItem?.id) {
      this.service.update(this.editingItem.id, data).subscribe({ next: () => handleSuccess('Workflow updated'), error: handleError });
    } else {
      this.service.save(data).subscribe({ next: () => handleSuccess('Workflow created'), error: handleError });
    }
  }

  viewSteps(workflow: any) {
    this.selectedWorkflow = workflow;
    this.service.getSteps(workflow.id).subscribe({
      next: (steps: any) => { this.steps = steps; this.showSteps = true; },
      error: () => this.toastService.error('Failed to load steps')
    });
  }

  addStep() {
    this.editingStep = null;
    this.stepForm = { name: '', stepOrder: this.steps.length + 1, requiredRole: '', requiredPermission: '' };
    this.showStepForm = true;
  }

  editStep(step: any) {
    this.editingStep = step;
    this.stepForm = { ...step };
    this.showStepForm = true;
  }

  saveStep() {
    if (this.editingStep) {
      this.service.updateStep(this.editingStep.id, this.stepForm).subscribe({
        next: () => { this.showStepForm = false; this.viewSteps(this.selectedWorkflow); this.toastService.success('Step updated'); },
        error: () => this.toastService.error('Failed to update step')
      });
    } else {
      this.service.addStep(this.selectedWorkflow.id, this.stepForm).subscribe({
        next: () => { this.showStepForm = false; this.viewSteps(this.selectedWorkflow); this.toastService.success('Step added'); },
        error: () => this.toastService.error('Failed to add step')
      });
    }
  }

  deleteStep(step: any) {
    this.service.deleteStep(step.id).subscribe({
      next: () => { this.viewSteps(this.selectedWorkflow); this.toastService.success('Step deleted'); },
      error: () => this.toastService.error('Failed to delete step')
    });
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Workflow';
    this.confirmMessage = `Are you sure you want to delete "${item.name}"? This action cannot be undone.`;
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.deleteTarget = items;
    this.confirmTitle = 'Bulk Delete Workflows';
    this.confirmMessage = `Are you sure you want to delete ${items.length} selected workflows?`;
    this.showConfirm = true;
  }

  executeDelete() {
    this.showConfirm = false;
    if (Array.isArray(this.deleteTarget)) {
      let completed = 0;
      this.deleteTarget.forEach((item: any) => {
        this.service.delete(item.id).subscribe({
          next: () => { completed++; if (completed === this.deleteTarget.length) { this.loadData(); this.toastService.success(`${completed} workflows deleted`); } },
          error: () => this.toastService.error('Failed to delete some workflows')
        });
      });
    } else if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Workflow deleted'); },
        error: () => this.toastService.error('Failed to delete workflow')
      });
    }
    this.deleteTarget = null;
  }
}
