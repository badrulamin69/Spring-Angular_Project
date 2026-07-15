import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableColumn } from '../data-table/data-table.component';

@Component({
  selector: 'app-dynamic-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-overlay" (click)="onCancel()">
      <div class="modal-content" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h2>{{ title }}</h2>
          <button class="close-btn" (click)="onCancel()">&times;</button>
        </div>
        <div class="modal-body">
          @if (errorMessage) {
            <div class="alert alert-error">{{ errorMessage }}</div>
          }
          <form (ngSubmit)="onSubmit()" #form="ngForm">
            @for (col of formColumns; track col.key) {
              <div class="form-group">
                <label [for]="col.key">
                  {{ col.label }}
                  @if (col.required) {
                    <span class="required">*</span>
                  }
                </label>
                @if (col.type === 'select') {
                  <select [id]="col.key" [name]="col.key" [(ngModel)]="formData[col.key]" class="form-control" [required]="!!col.required">
                    <option value="">Select {{ col.label }}</option>
                    @for (opt of col.options; track opt.value) {
                      <option [ngValue]="opt.value">{{ opt.label }}</option>
                    }
                  </select>
                } @else if (col.type === 'textarea') {
                  <textarea [id]="col.key" [name]="col.key" [(ngModel)]="formData[col.key]" class="form-control" rows="3" [placeholder]="col.placeholder || ''" [required]="!!col.required"></textarea>
                } @else if (col.type === 'checkbox') {
                  <label class="checkbox-label">
                    <input type="checkbox" [id]="col.key" [name]="col.key" [(ngModel)]="formData[col.key]">
                    {{ col.label }}
                  </label>
                } @else if (col.type === 'date') {
                  <input type="date" [id]="col.key" [name]="col.key" [(ngModel)]="formData[col.key]" class="form-control" [required]="!!col.required">
                } @else {
                  <input [type]="col.type || 'text'" [id]="col.key" [name]="col.key" [(ngModel)]="formData[col.key]" class="form-control" [placeholder]="col.placeholder || ''" [required]="!!col.required">
                }
                @if (getFieldError(col.key)) {
                  <span class="field-error">{{ getFieldError(col.key) }}</span>
                }
              </div>
            }
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="onCancel()" [disabled]="saving">Cancel</button>
              <button type="button" class="btn btn-outline" (click)="resetForm()">Reset</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving || form.invalid">
                @if (saving) {
                  <span class="btn-spinner"></span> Saving...
                } @else {
                  {{ initialData ? 'Update' : 'Save' }}
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; backdrop-filter: blur(4px); }
    .modal-content { background: var(--bg-secondary); border-radius: 12px; width: 100%; max-width: 520px; max-height: 85vh; box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden; animation: slideDown 0.25s ease-out; display: flex; flex-direction: column; }
    .modal-header { padding: 16px 20px; border-bottom: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
    .modal-header h2 { margin: 0; font-size: 1.125rem; color: var(--text-primary); font-weight: 600; }
    .close-btn { background: none; border: none; font-size: 1.5rem; color: var(--text-muted); cursor: pointer; padding: 0; line-height: 1; }
    .close-btn:hover { color: var(--text-primary); }
    .modal-body { padding: 20px; overflow-y: auto; flex: 1; }
    .form-group { margin-bottom: 14px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: 500; color: var(--text-secondary); font-size: 0.8125rem; }
    .required { color: #ef4444; margin-left: 2px; }
    .form-control { width: 100%; padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; transition: border-color 0.2s, box-shadow 0.2s; box-sizing: border-box; font-family: inherit; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
    textarea.form-control { resize: vertical; min-height: 60px; }
    select.form-control { cursor: pointer; appearance: auto; }
    .checkbox-label { display: flex; align-items: center; gap: 8px; font-size: 0.875rem; color: var(--text-primary); cursor: pointer; }
    .checkbox-label input { width: 16px; height: 16px; accent-color: var(--brand-color); }
    .alert { padding: 10px 14px; border-radius: 8px; margin-bottom: 14px; font-size: 0.8125rem; }
    .alert-error { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
    .field-error { color: #dc2626; font-size: 0.75rem; margin-top: 4px; display: block; }
    .modal-footer { margin-top: 16px; padding-top: 14px; border-top: 1px solid var(--border-color); display: flex; justify-content: flex-end; gap: 8px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; font-size: 0.875rem; font-weight: 500; cursor: pointer; transition: all 0.15s; display: inline-flex; align-items: center; gap: 6px; }
    .btn-secondary { background: var(--bg-tertiary); color: var(--text-primary); border: 1px solid var(--border-color); }
    .btn-secondary:hover { background: var(--bg-hover-strong); }
    .btn-outline { background: transparent; color: var(--text-secondary); border: 1px solid var(--border-color); }
    .btn-outline:hover { background: var(--bg-hover); }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
    @keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
    @keyframes spin { to { transform: rotate(360deg); } }

    @media (max-width: 640px) {
      .modal-content {
        max-width: 95%;
        max-height: 90vh;
      }
      .modal-body {
        padding: 16px;
      }
      .modal-footer {
        flex-direction: column;
      }
      .modal-footer .btn {
        width: 100%;
        justify-content: center;
      }
    }
  `]
})
export class DynamicFormComponent implements OnInit {
  @Input() columns: TableColumn[] = [];
  @Input() initialData: any = null;
  @Input() title: string = 'Add Record';
  @Input() saving = false;
  @Input() errorMessage = '';

  @Output() save = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  formData: any = {};
  formColumns: TableColumn[] = [];
  fieldErrors: { [key: string]: string } = {};

  ngOnInit() {
    const excludedKeys = ['id', 'actions', 'uniquecode'];
    this.formColumns = this.columns.filter(col => !excludedKeys.includes(col.key.toLowerCase()));

    if (this.initialData) {
      this.formData = { ...this.initialData };
    } else {
      this.formData = {};
      this.formColumns.forEach(col => {
        if (col.type === 'checkbox') {
          this.formData[col.key] = false;
        } else {
          this.formData[col.key] = '';
        }
      });
    }
  }

  resetForm() {
    if (this.initialData) {
      this.formData = { ...this.initialData };
    } else {
      this.formColumns.forEach(col => {
        this.formData[col.key] = col.type === 'checkbox' ? false : '';
      });
    }
  }

  onSubmit() {
    this.fieldErrors = {};
    this.save.emit(this.formData);
  }

  parseValidationErrors(error: any) {
    this.fieldErrors = {};
    if (error?.error?.errors) {
      this.fieldErrors = error.error.errors;
      const messages = Object.values(error.error.errors);
      this.errorMessage = messages.join('. ');
    } else if (error?.error?.message) {
      this.errorMessage = error.error.message;
    } else {
      this.errorMessage = error?.message || 'An error occurred. Please try again.';
    }
  }

  getFieldError(key: string): string | null {
    return this.fieldErrors[key] || null;
  }

  onCancel() {
    this.cancel.emit();
  }
}
