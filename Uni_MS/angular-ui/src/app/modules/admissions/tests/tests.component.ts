import { DynamicFormComponent } from '../../../shared/dynamic-form/dynamic-form.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdmissionTestService } from '../../../services/admission-test.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-tests',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, DynamicFormComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Admission Tests</h2>
        <p class="page-sub">Manage admission tests, schedules, and configurations</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add New
      </button>
    </div>

    <div class="filter-bar">
      <select [(ngModel)]="filterStatus" (change)="loadData()">
        <option value="">All Status</option>
        <option value="DRAFT">Draft</option>
        <option value="PUBLISHED">Published</option>
        <option value="CLOSED">Closed</option>
      </select>
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
      <div class="modal-overlay" (click)="showForm = false">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingItem ? 'Edit Admission Test' : 'Add Admission Test' }}</h3>
            <button class="close-btn" (click)="showForm = false">&times;</button>
          </div>
          <form (ngSubmit)="saveItem(formData)">
            <div class="form-section">
              <h4>Basic Information</h4>
              <div class="form-row-2">
                <div class="form-group">
                  <label>Test Name *</label>
                  <input type="text" [(ngModel)]="formData.name" name="name" required placeholder="e.g. Fall 2026 Admission Test">
                </div>
                <div class="form-group">
                  <label>Academic Year</label>
                  <input type="text" [(ngModel)]="formData.academicYear" name="academicYear" placeholder="e.g. 2026">
                </div>
              </div>
              <div class="form-row-2">
                <div class="form-group">
                  <label>Test Type</label>
                  <select [(ngModel)]="formData.testType" name="testType">
                    <option value="MCQ">MCQ</option>
                    <option value="WRITTEN">Written</option>
                    <option value="MIXED">Mixed</option>
                    <option value="VIVA">Viva</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>Status</label>
                  <select [(ngModel)]="formData.status" name="status">
                    <option value="DRAFT">Draft</option>
                    <option value="PUBLISHED">Published</option>
                    <option value="CLOSED">Closed</option>
                  </select>
                </div>
              </div>
            </div>

            <div class="form-section">
              <h4>Schedule</h4>
              <div class="form-row-3">
                <div class="form-group">
                  <label>Test Date *</label>
                  <input type="date" [(ngModel)]="formData.testDate" name="testDate" required>
                </div>
                <div class="form-group">
                  <label>Start Time</label>
                  <input type="time" [(ngModel)]="formData.startTime" name="startTime">
                </div>
                <div class="form-group">
                  <label>End Time</label>
                  <input type="time" [(ngModel)]="formData.endTime" name="endTime">
                </div>
              </div>
              <div class="form-group">
                <label>Duration (minutes)</label>
                <input type="number" [(ngModel)]="formData.durationMinutes" name="durationMinutes" min="1" placeholder="120">
              </div>
            </div>

            <div class="form-section">
              <h4>Academic Scope</h4>
              <div class="form-row-3">
                <div class="form-group">
                  <label>Faculty</label>
                  <select [(ngModel)]="formData.facultyId" name="facultyId">
                    <option [ngValue]="null">All Faculties</option>
                    @for (f of faculties; track f.id) { <option [ngValue]="f.id">{{ f.name }}</option> }
                  </select>
                </div>
                <div class="form-group">
                  <label>Department</label>
                  <select [(ngModel)]="formData.departmentId" name="departmentId">
                    <option [ngValue]="null">All Departments</option>
                    @for (d of departments; track d.id) { <option [ngValue]="d.id">{{ d.name }}</option> }
                  </select>
                </div>
                <div class="form-group">
                  <label>Program</label>
                  <select [(ngModel)]="formData.programId" name="programId">
                    <option [ngValue]="null">All Programs</option>
                    @for (p of programs; track p.id) { <option [ngValue]="p.id">{{ p.name }}</option> }
                  </select>
                </div>
              </div>
              <div class="form-group">
                <label>Shift</label>
                <select [(ngModel)]="formData.shift" name="shift">
                  <option value="">Select</option>
                  <option value="MORNING">Morning</option>
                  <option value="AFTERNOON">Afternoon</option>
                  <option value="EVENING">Evening</option>
                </select>
              </div>
            </div>

            <div class="form-section">
              <h4>Marks & Grading</h4>
              <div class="form-row-3">
                <div class="form-group">
                  <label>Total Marks *</label>
                  <input type="number" [(ngModel)]="formData.totalMarks" name="totalMarks" required min="1" placeholder="100">
                </div>
                <div class="form-group">
                  <label>Pass Marks *</label>
                  <input type="number" [(ngModel)]="formData.passingMarks" name="passingMarks" required min="1" placeholder="40">
                </div>
                <div class="form-group">
                  <label>Negative Marking</label>
                  <select [(ngModel)]="formData.negativeMarking" name="negativeMarking">
                    <option [ngValue]="false">No</option>
                    <option [ngValue]="true">Yes</option>
                  </select>
                </div>
              </div>
              @if (formData.negativeMarking) {
                <div class="form-group">
                  <label>Negative Mark Value</label>
                  <input type="number" [(ngModel)]="formData.negativeMarkValue" name="negativeMarkValue" min="0" step="0.25" placeholder="0.25">
                </div>
              }
            </div>

            <div class="form-section">
              <h4>Venue</h4>
              <div class="form-row-3">
                <div class="form-group">
                  <label>Exam Center</label>
                  <input type="text" [(ngModel)]="formData.examCenter" name="examCenter" placeholder="Center name">
                </div>
                <div class="form-group">
                  <label>Building</label>
                  <input type="text" [(ngModel)]="formData.building" name="building" placeholder="Building name">
                </div>
                <div class="form-group">
                  <label>Room</label>
                  <input type="text" [(ngModel)]="formData.room" name="room" placeholder="Room number">
                </div>
              </div>
              <div class="form-group">
                <label>Seat Capacity</label>
                <input type="number" [(ngModel)]="formData.seatCapacity" name="seatCapacity" min="1" placeholder="100">
              </div>
            </div>

            <div class="form-section">
              <h4>Additional</h4>
              <div class="form-group">
                <label>Description</label>
                <textarea [(ngModel)]="formData.description" name="description" rows="2" placeholder="Test description"></textarea>
              </div>
              <div class="form-group">
                <label>Instructions</label>
                <textarea [(ngModel)]="formData.instructions" name="instructions" rows="3" placeholder="Instructions for candidates"></textarea>
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="showForm = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving">{{ saving ? 'Saving...' : 'Save' }}</button>
            </div>
          </form>
        </div>
      </div>
    }

    @if (showConfirm) {
      <app-confirm-dialog
        [open]="showConfirm"
        [title]="confirmTitle"
        [message]="confirmMessage"
        confirmText="Delete"
        type="danger"
        (confirmed)="executeDelete()"
        (cancelled)="showConfirm = false">
      </app-confirm-dialog>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary, #1e293b); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted, #64748b); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color, #4F46E5); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-secondary { background: #e2e8f0; color: #475569; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; padding: 1.5rem; width: 90%; max-width: 700px; max-height: 90vh; overflow-y: auto; }
    .modal-lg { max-width: 750px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; color: #1e293b; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b; }
    .form-section { margin-bottom: 1.25rem; padding-bottom: 1rem; border-bottom: 1px solid #f1f5f9; }
    .form-section:last-of-type { border-bottom: none; }
    .form-section h4 { margin: 0 0 0.75rem; font-size: 0.9375rem; color: #475569; font-weight: 600; }
    .form-group { margin-bottom: 0.75rem; }
    .form-group label { display: block; margin-bottom: 0.25rem; font-weight: 500; color: #374151; font-size: 0.8125rem; }
    .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.875rem; box-sizing: border-box; }
    .form-group textarea { resize: vertical; }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { outline: none; border-color: #4F46E5; box-shadow: 0 0 0 2px rgba(79,70,229,0.1); }
    .form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
    .form-row-3 { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 0.75rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #e2e8f0; }
  `]
})
export class TestsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  pagedData: PagedResponse<any> | null = null;
  loading = true;
  saving = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  filterStatus = '';
  faculties: any[] = [];
  departments: any[] = [];
  programs: any[] = [];

  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Name', sortable: true, type: 'text', required: true, placeholder: 'Test name' },
    { key: 'testDate', label: 'Date', type: 'date', sortable: true, required: true },
    { key: 'testType', label: 'Type', sortable: true },
    { key: 'totalMarks', label: 'Total', sortable: true, type: 'number' },
    { key: 'passingMarks', label: 'Pass', type: 'number' },
    { key: 'durationMinutes', label: 'Duration (min)', type: 'number' },
    { key: 'status', label: 'Status', sortable: true }
  ];

  showForm = false;
  editingItem: any = null;
  formError = '';
  formData: any = {};

  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: any = null;

  constructor(private service: AdmissionTestService, private toastService: ToastService, private http: HttpClient) {}

  ngOnInit() {
    this.initForm();
    this.loadFilters();
    this.loadData();
  }

  initForm() {
    this.formData = {
      name: '', academicYear: '', testType: 'MCQ', status: 'DRAFT',
      testDate: '', startTime: '', endTime: '', durationMinutes: 120,
      facultyId: null, departmentId: null, programId: null, shift: '',
      totalMarks: 100, passingMarks: 40, negativeMarking: false, negativeMarkValue: 0,
      examCenter: '', building: '', room: '', seatCapacity: null,
      description: '', instructions: ''
    };
  }

  loadFilters() {
    this.http.get<any>(`${environment.apiUrl}/faculties?page=0&size=100`).subscribe({
      next: (res) => { this.faculties = res.content || res || []; },
      error: () => { this.faculties = []; }
    });
    this.http.get<any>(`${environment.apiUrl}/departments?page=0&size=100`).subscribe({
      next: (res) => { this.departments = res.content || res || []; },
      error: () => { this.departments = []; }
    });
    this.http.get<any>(`${environment.apiUrl}/programs?page=0&size=100`).subscribe({
      next: (res) => { this.programs = res.content || res || []; },
      error: () => { this.programs = []; }
    });
  }

  loadData() {
    this.loading = true;
    const filters: any = {};
    if (this.filterStatus) filters.status = this.filterStatus;
    this.service.findAll(this.params, this.searchTerm, filters).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load admission tests'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.searchTerm = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  publishTest(item: any) {
    this.service.publish(item.id).subscribe({
      next: () => { this.loadData(); this.toastService.success('Test published'); },
      error: () => this.toastService.error('Failed to publish test')
    });
  }

  closeTest(item: any) {
    this.service.close(item.id).subscribe({
      next: () => { this.loadData(); this.toastService.success('Test closed'); },
      error: () => this.toastService.error('Failed to close test')
    });
  }

  openForm(item?: any) {
    this.editingItem = item ? { ...item } : null;
    this.formError = '';
    if (item) {
      this.formData = { ...item };
    } else {
      this.initForm();
    }
    this.showForm = true;
  }

  closeForm() { this.showForm = false; this.editingItem = null; this.formError = ''; }

  saveItem(data: any) {
    this.saving = true;
    this.formError = '';
    const handleSuccess = (msg: string) => { this.saving = false; this.closeForm(); this.loadData(); this.toastService.success(msg); };
    const handleError = (err: any) => { this.saving = false; this.formError = err.error?.message || err.message || 'Validation failed.'; };
    if (this.editingItem?.id) {
      this.service.update(this.editingItem.id, data).subscribe({ next: () => handleSuccess('Test updated'), error: handleError });
    } else {
      this.service.save(data).subscribe({ next: () => handleSuccess('Test created'), error: handleError });
    }
  }

  confirmDelete(item: any) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Admission Test';
    this.confirmMessage = `Delete "${item.name}"? This action cannot be undone.`;
    this.showConfirm = true;
  }

  confirmBulkDelete(items: any[]) {
    this.deleteTarget = items;
    this.confirmTitle = 'Bulk Delete';
    this.confirmMessage = `Delete ${items.length} selected tests?`;
    this.showConfirm = true;
  }

  executeDelete() {
    this.showConfirm = false;
    if (Array.isArray(this.deleteTarget)) {
      let completed = 0;
      this.deleteTarget.forEach((item: any) => {
        this.service.delete(item.id).subscribe({
          next: () => { completed++; if (completed === this.deleteTarget.length) { this.loadData(); this.toastService.success(`${completed} tests deleted`); } },
          error: () => this.toastService.error('Failed to delete some records')
        });
      });
    } else if (this.deleteTarget?.id) {
      this.service.delete(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Test deleted'); },
        error: () => this.toastService.error('Failed to delete test')
      });
    }
    this.deleteTarget = null;
  }
}
