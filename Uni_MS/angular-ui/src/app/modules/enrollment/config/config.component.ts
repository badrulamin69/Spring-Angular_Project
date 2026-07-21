import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnrollmentConfigService } from '../../../services/enrollment-config.service';
import { SemesterService } from '../../../services/semester.service';
import { EnrollmentConfig } from '../../../models/semester-enrollment';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-enrollment-config',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Enrollment Configuration</h2>
        <p class="page-sub">Configure semester enrollment periods and rules</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add New
      </button>
    </div>

    <div class="card">
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Semester</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Credits</th>
              <th>Advisor Approval</th>
              <th>Payment Required</th>
              <th>Late Enrollment</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (config of configs; track config.id) {
              <tr>
                <td>{{ config.id }}</td>
                <td>{{ config.semesterName }}</td>
                <td>{{ config.startDate }}</td>
                <td>{{ config.endDate }}</td>
                <td>{{ config.minCredits }} - {{ config.maxCredits }}</td>
                <td>
                  @if (config.requiresAdvisorApproval) {
                    <span class="badge badge-success">Yes</span>
                  } @else {
                    <span class="badge badge-secondary">No</span>
                  }
                </td>
                <td>
                  @if (config.requiresPayment) {
                    <span class="badge badge-success">Yes</span>
                  } @else {
                    <span class="badge badge-secondary">No</span>
                  }
                </td>
                <td>
                  @if (config.allowLateEnrollment) {
                    <span class="badge badge-success">Yes</span>
                  } @else {
                    <span class="badge badge-secondary">No</span>
                  }
                </td>
                <td>
                  @if (config.isClosed) {
                    <span class="badge badge-secondary">Closed</span>
                  } @else if (config.isActive) {
                    <span class="badge badge-success">Active</span>
                  } @else {
                    <span class="badge badge-warning">Inactive</span>
                  }
                </td>
                <td>
                  <div class="actions">
                    <button class="btn-icon" (click)="openForm(config)" title="Edit">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                    </button>
                    @if (!config.isClosed && config.isActive) {
                      <button class="btn-icon btn-warning" (click)="closeConfig(config)" title="Close Enrollment">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/></svg>
                      </button>
                    }
                    @if (config.isClosed) {
                      <button class="btn-icon btn-success" (click)="reopenConfig(config)" title="Reopen Enrollment">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>
                      </button>
                    }
                    <button class="btn-icon btn-danger" (click)="confirmDelete(config)" title="Delete">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="10" class="text-center text-muted">No enrollment configurations found</td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>

    @if (showForm) {
      <div class="modal-overlay" (click)="closeForm()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingItem ? 'Edit' : 'New' }} Enrollment Config</h3>
            <button class="btn-close" (click)="closeForm()">&times;</button>
          </div>
          <form (ngSubmit)="save()">
            <div class="form-grid">
              <div class="form-group">
                <label>Semester *</label>
                <select [(ngModel)]="form.semesterId" name="semesterId" required class="form-control">
                  <option value="">Select Semester</option>
                  @for (sem of semesters; track sem.id) {
                    <option [ngValue]="sem.id">{{ sem.name }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Start Date *</label>
                <input type="date" [(ngModel)]="form.startDate" name="startDate" required class="form-control">
              </div>
              <div class="form-group">
                <label>End Date *</label>
                <input type="date" [(ngModel)]="form.endDate" name="endDate" required class="form-control">
              </div>
              <div class="form-group">
                <label>Late Enrollment Date</label>
                <input type="date" [(ngModel)]="form.lateEnrollmentDate" name="lateEnrollmentDate" class="form-control">
              </div>
              <div class="form-group">
                <label>Min Credits *</label>
                <input type="number" [(ngModel)]="form.minCredits" name="minCredits" required class="form-control">
              </div>
              <div class="form-group">
                <label>Max Credits *</label>
                <input type="number" [(ngModel)]="form.maxCredits" name="maxCredits" required class="form-control">
              </div>
              <div class="form-group checkbox-group">
                <label>
                  <input type="checkbox" [(ngModel)]="form.allowLateEnrollment" name="allowLateEnrollment"> Allow Late Enrollment
                </label>
              </div>
              <div class="form-group checkbox-group">
                <label>
                  <input type="checkbox" [(ngModel)]="form.requiresAdvisorApproval" name="requiresAdvisorApproval"> Requires Advisor Approval
                </label>
              </div>
              <div class="form-group checkbox-group">
                <label>
                  <input type="checkbox" [(ngModel)]="form.requiresPayment" name="requiresPayment"> Requires Payment
                </label>
              </div>
              <div class="form-group">
                <label>Remarks</label>
                <textarea [(ngModel)]="form.remarks" name="remarks" class="form-control" rows="2"></textarea>
              </div>
            </div>
            @if (formError) {
              <div class="error-message">{{ formError }}</div>
            }
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeForm()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving">
                {{ saving ? 'Saving...' : 'Save' }}
              </button>
            </div>
          </form>
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
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; }
    .table-responsive { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid var(--border-color); font-size: 0.875rem; }
    .data-table th { background: var(--bg-secondary); font-weight: 600; color: var(--text-secondary); }
    .data-table tr:hover { background: var(--bg-secondary); }
    .actions { display: flex; gap: 4px; }
    .btn-icon { width: 32px; height: 32px; border: none; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; background: transparent; color: var(--text-muted); transition: all 0.15s; }
    .btn-icon:hover { background: var(--bg-secondary); color: var(--brand-color); }
    .btn-warning:hover { color: #f59e0b; }
    .btn-success:hover { color: #10b981; }
    .btn-danger:hover { color: #ef4444; }
    .badge { padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #d1fae5; color: #065f46; }
    .badge-secondary { background: #e5e7eb; color: #374151; }
    .badge-warning { background: #fef3c7; color: #92400e; }
    .text-center { text-align: center; }
    .text-muted { color: var(--text-muted); }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: var(--card-bg); border-radius: 12px; width: 90%; max-width: 600px; max-height: 90vh; overflow-y: auto; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid var(--border-color); }
    .modal-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .btn-close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--text-muted); }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; padding: 20px; }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    .checkbox-group { justify-content: center; }
    .checkbox-group label { flex-direction: row; align-items: center; gap: 8px; cursor: pointer; }
    .error-message { padding: 8px 12px; background: #fef2f2; color: #dc2626; border-radius: 6px; margin: 0 20px; font-size: 0.875rem; }
    .modal-footer { display: flex; justify-content: flex-end; gap: 8px; padding: 16px 20px; border-top: 1px solid var(--border-color); }
  `]
})
export class EnrollmentConfigComponent implements OnInit {
  configs: EnrollmentConfig[] = [];
  semesters: any[] = [];
  loading = true;
  saving = false;
  showForm = false;
  editingItem: any = null;
  formError = '';
  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  form: Partial<EnrollmentConfig> = {
    semesterId: 0,
    startDate: '',
    endDate: '',
    lateEnrollmentDate: '',
    minCredits: 12,
    maxCredits: 24,
    allowLateEnrollment: true,
    requiresAdvisorApproval: true,
    requiresPayment: true,
    isActive: true
  };

  constructor(
    private configService: EnrollmentConfigService,
    private semesterService: SemesterService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
    this.loadSemesters();
  }

  loadData() {
    this.loading = true;
    this.configService.findAll().subscribe({
      next: (data) => { this.configs = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load configurations'); }
    });
  }

  loadSemesters() {
    this.semesterService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.semesters = data.content || []; },
      error: () => this.toastService.error('Operation failed. Please try again.')
    });
  }

  openForm(item?: any) {
    this.editingItem = item ? { ...item } : null;
    this.form = item ? { ...item } : {
      semesterId: 0,
      startDate: '',
      endDate: '',
      lateEnrollmentDate: '',
      minCredits: 12,
      maxCredits: 24,
      allowLateEnrollment: true,
      requiresAdvisorApproval: true,
      requiresPayment: true,
      isActive: true
    };
    this.formError = '';
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editingItem = null;
    this.formError = '';
  }

  save() {
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
      this.formError = err.error?.message || err.error?.data || 'Save failed';
    };

    if (this.editingItem?.id) {
      this.configService.update(this.editingItem.id, this.form as EnrollmentConfig).subscribe({ next: () => handleSuccess('Config updated'), error: handleError });
    } else {
      this.configService.create(this.form as EnrollmentConfig).subscribe({ next: () => handleSuccess('Config created'), error: handleError });
    }
  }

  closeConfig(config: EnrollmentConfig) {
    this.configService.closeEnrollment(config.id!).subscribe({
      next: () => { this.loadData(); this.toastService.success('Enrollment closed'); },
      error: () => this.toastService.error('Failed to close enrollment')
    });
  }

  reopenConfig(config: EnrollmentConfig) {
    this.configService.reopenEnrollment(config.id!).subscribe({
      next: () => { this.loadData(); this.toastService.success('Enrollment reopened'); },
      error: () => this.toastService.error('Failed to reopen enrollment')
    });
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Enrollment Config';
    this.confirmMessage = 'Are you sure you want to delete this enrollment configuration?';
    this.showConfirm = true;
  }

  executeDelete() {
    this.showConfirm = false;
    if (this.deleteTarget?.id) {
      this.configService.delete(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Configuration deleted'); },
        error: () => this.toastService.error('Failed to delete')
      });
    }
    this.deleteTarget = null;
  }
}
