import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClassRoutineService } from '../../../services/class-routine.service';
import { ClassRoutine, ClassRoutineRequest, ConflictCheckResponse, TimeSlot, Classroom } from '../../../models/class-routine';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-class-routine-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Class Routine Management</h2>
        <p class="page-sub">Manage class schedules and timetables</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="publishAll()">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 2L11 13"/><path d="M22 2L15 22L11 13L2 9L22 2Z"/></svg>
          Publish
        </button>
        <button class="btn btn-primary" (click)="openForm()">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          Add Routine
        </button>
      </div>
    </div>

    <div class="filters-bar">
      <select [(ngModel)]="filterSemester" class="form-control filter-select" (change)="loadData()">
        <option value="">All Semesters</option>
        @for (s of semesters; track s.id) {
          <option [ngValue]="s.id">{{ s.name }}</option>
        }
      </select>
      <select [(ngModel)]="filterSection" class="form-control filter-select" (change)="loadData()">
        <option value="">All Sections</option>
        @for (s of sections; track s.id) {
          <option [ngValue]="s.id">{{ s.name }}</option>
        }
      </select>
      <select [(ngModel)]="filterDay" class="form-control filter-select" (change)="loadData()">
        <option value="">All Days</option>
        <option value="SATURDAY">Saturday</option>
        <option value="SUNDAY">Sunday</option>
        <option value="MONDAY">Monday</option>
        <option value="TUESDAY">Tuesday</option>
        <option value="WEDNESDAY">Wednesday</option>
        <option value="THURSDAY">Thursday</option>
        <option value="FRIDAY">Friday</option>
      </select>
      <div class="search-box">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input type="text" [(ngModel)]="searchTerm" placeholder="Search routines..." (input)="filterData()">
      </div>
    </div>

    <div class="card">
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Subject</th>
              <th>Teacher</th>
              <th>Section</th>
              <th>Semester</th>
              <th>Day</th>
              <th>Time</th>
              <th>Room</th>
              <th>Type</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (routine of filteredRoutines; track routine.id) {
              <tr>
                <td>{{ routine.id }}</td>
                <td>{{ routine.subjectName || ('Subject #' + routine.subjectId) }}</td>
                <td>{{ routine.teacherName || ('Teacher #' + routine.administrationId) }}</td>
                <td>{{ routine.sectionName || ('Section #' + routine.sectionId) }}</td>
                <td>{{ routine.semesterName || ('Semester #' + routine.semesterId) }}</td>
                <td><span class="badge badge-info">{{ routine.dayOfWeek }}</span></td>
                <td>{{ routine.startTime }} - {{ routine.endTime }}</td>
                <td>{{ routine.room || routine.classroomNumber || '-' }}</td>
                <td><span class="badge badge-info">{{ routine.classType }}</span></td>
                <td>
                  @if (routine.isActive) {
                    <span class="badge badge-success">Active</span>
                  } @else {
                    <span class="badge badge-secondary">Inactive</span>
                  }
                </td>
                <td>
                  <div class="actions">
                    <button class="btn-icon" (click)="openForm(routine)" title="Edit">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                    </button>
                    <button class="btn-icon btn-danger" (click)="confirmDelete(routine)" title="Delete">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="11" class="text-center text-muted">No routines found</td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>

    @if (showForm) {
      <div class="modal-overlay" (click)="closeForm()">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingItem ? 'Edit' : 'New' }} Class Routine</h3>
            <button class="btn-close" (click)="closeForm()">&times;</button>
          </div>
          <form (ngSubmit)="save()">
            <div class="form-grid">
              <div class="form-group">
                <label>Semester *</label>
                <select [(ngModel)]="form.semesterId" name="semesterId" required class="form-control">
                  <option value="">Select Semester</option>
                  @for (s of semesters; track s.id) {
                    <option [ngValue]="s.id">{{ s.name }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Section *</label>
                <select [(ngModel)]="form.sectionId" name="sectionId" required class="form-control">
                  <option value="">Select Section</option>
                  @for (s of sections; track s.id) {
                    <option [ngValue]="s.id">{{ s.name }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Subject *</label>
                <select [(ngModel)]="form.subjectId" name="subjectId" required class="form-control">
                  <option value="">Select Subject</option>
                  @for (s of subjects; track s.id) {
                    <option [ngValue]="s.id">{{ s.name }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Teacher *</label>
                <select [(ngModel)]="form.administrationId" name="administrationId" required class="form-control">
                  <option value="">Select Teacher</option>
                  @for (t of teachers; track t.id) {
                    <option [ngValue]="t.id">{{ t.name }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Day of Week *</label>
                <select [(ngModel)]="form.dayOfWeek" name="dayOfWeek" required class="form-control">
                  <option value="">Select Day</option>
                  <option value="SATURDAY">Saturday</option>
                  <option value="SUNDAY">Sunday</option>
                  <option value="MONDAY">Monday</option>
                  <option value="TUESDAY">Tuesday</option>
                  <option value="WEDNESDAY">Wednesday</option>
                  <option value="THURSDAY">Thursday</option>
                  <option value="FRIDAY">Friday</option>
                </select>
              </div>
              <div class="form-group">
                <label>Time Slot</label>
                <select [(ngModel)]="form.timeSlotId" name="timeSlotId" class="form-control" (change)="onTimeSlotChange()">
                  <option value="">Select Time Slot</option>
                  @for (ts of timeSlots; track ts.id) {
                    <option [ngValue]="ts.id">{{ ts.name }} ({{ ts.startTime }} - {{ ts.endTime }})</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Start Time *</label>
                <input type="time" [(ngModel)]="form.startTime" name="startTime" required class="form-control">
              </div>
              <div class="form-group">
                <label>End Time *</label>
                <input type="time" [(ngModel)]="form.endTime" name="endTime" required class="form-control">
              </div>
              <div class="form-group">
                <label>Classroom</label>
                <select [(ngModel)]="form.classroomId" name="classroomId" class="form-control">
                  <option value="">Select Classroom</option>
                  @for (c of classrooms; track c.id) {
                    <option [ngValue]="c.id">{{ c.buildingName }} - {{ c.roomNumber }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Room</label>
                <input type="text" [(ngModel)]="form.room" name="room" class="form-control" placeholder="e.g. Room 101">
              </div>
              <div class="form-group">
                <label>Building</label>
                <input type="text" [(ngModel)]="form.building" name="building" class="form-control">
              </div>
              <div class="form-group">
                <label>Class Type *</label>
                <select [(ngModel)]="form.classType" name="classType" required class="form-control">
                  <option value="">Select Type</option>
                  <option value="LECTURE">Lecture</option>
                  <option value="LAB">Lab</option>
                  <option value="TUTORIAL">Tutorial</option>
                  <option value="SEMINAR">Seminar</option>
                </select>
              </div>
              <div class="form-group">
                <label>Shift</label>
                <select [(ngModel)]="form.shift" name="shift" class="form-control">
                  <option value="">Select Shift</option>
                  <option value="MORNING">Morning</option>
                  <option value="AFTERNOON">Afternoon</option>
                  <option value="EVENING">Evening</option>
                </select>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.isActive" name="isActive"> Active</label>
              </div>
            </div>
            @if (conflictResult && conflictResult.hasConflict) {
              <div class="conflict-warning">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                <span>{{ conflictResult.conflictMessage }}</span>
              </div>
            }
            @if (formError) {
              <div class="error-message">{{ formError }}</div>
            }
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeForm()">Cancel</button>
              <button type="button" class="btn btn-outline" (click)="checkConflicts()" [disabled]="saving">Check Conflicts</button>
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
    .page-header h2 { margin: 0; font-size: 1.5rem; color: #e2e8f0; font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: #94a3b8; }
    .header-actions { display: flex; gap: 8px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: #3b82f6; color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-outline { background: transparent; color: #3b82f6; border: 1px solid #3b82f6; }
    .btn-outline:hover { background: rgba(59,130,246,0.1); }
    .btn-secondary { background: #334155; color: #e2e8f0; }
    .card { background: #1e293b; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; }
    .filters-bar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
    .search-box { display: flex; align-items: center; gap: 8px; background: #1e293b; border: 1px solid #334155; border-radius: 8px; padding: 8px 12px; flex: 1; min-width: 200px; }
    .search-box input { border: none; background: transparent; color: #e2e8f0; font-size: 0.875rem; outline: none; width: 100%; }
    .search-box svg { color: #64748b; flex-shrink: 0; }
    .filter-select { padding: 8px 12px; border: 1px solid #334155; border-radius: 8px; background: #1e293b; color: #e2e8f0; font-size: 0.875rem; min-width: 150px; }
    .table-responsive { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #334155; font-size: 0.875rem; }
    .data-table th { background: #0f172a; font-weight: 600; color: #94a3b8; }
    .data-table tr:hover { background: rgba(59,130,246,0.05); }
    .actions { display: flex; gap: 4px; }
    .btn-icon { width: 32px; height: 32px; border: none; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; background: transparent; color: #94a3b8; transition: all 0.15s; }
    .btn-icon:hover { background: #334155; color: #3b82f6; }
    .btn-danger:hover { color: #ef4444; }
    .badge { padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #064e3b; color: #6ee7b7; }
    .badge-secondary { background: #334155; color: #94a3b8; }
    .badge-info { background: #1e3a5f; color: #93c5fd; }
    .text-center { text-align: center; }
    .text-muted { color: #94a3b8; }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: #1e293b; border-radius: 12px; width: 90%; max-width: 600px; max-height: 90vh; overflow-y: auto; border: 1px solid #334155; }
    .modal-lg { max-width: 750px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #334155; }
    .modal-header h3 { margin: 0; font-size: 1.125rem; color: #e2e8f0; }
    .btn-close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #94a3b8; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; padding: 20px; }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: #94a3b8; }
    .form-control { padding: 8px 12px; border: 1px solid #334155; border-radius: 6px; font-size: 0.875rem; background: #0f172a; color: #e2e8f0; }
    .form-control:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    .checkbox-group { justify-content: center; }
    .checkbox-group label { flex-direction: row; align-items: center; gap: 8px; cursor: pointer; }
    .conflict-warning { display: flex; align-items: center; gap: 8px; padding: 10px 16px; background: rgba(245,158,11,0.1); color: #fcd34d; border-radius: 6px; margin: 0 20px; font-size: 0.875rem; border: 1px solid rgba(245,158,11,0.2); }
    .error-message { padding: 8px 12px; background: rgba(239,68,68,0.1); color: #fca5a5; border-radius: 6px; margin: 0 20px; font-size: 0.875rem; border: 1px solid rgba(239,68,68,0.2); }
    .modal-footer { display: flex; justify-content: flex-end; gap: 8px; padding: 16px 20px; border-top: 1px solid #334155; }
  `]
})
export class ClassRoutineComponent implements OnInit {
  routines: ClassRoutine[] = [];
  filteredRoutines: ClassRoutine[] = [];
  timeSlots: TimeSlot[] = [];
  classrooms: Classroom[] = [];
  semesters: any[] = [];
  sections: any[] = [];
  subjects: any[] = [];
  teachers: any[] = [];
  loading = true;
  saving = false;
  showForm = false;
  editingItem: ClassRoutine | null = null;
  formError = '';
  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: ClassRoutine | null = null;
  searchTerm = '';
  filterSemester = '';
  filterSection = '';
  filterDay = '';
  conflictResult: ConflictCheckResponse | null = null;

  days = ['SATURDAY', 'SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

  form: Partial<ClassRoutineRequest> = {
    subjectId: 0, administrationId: 0, sectionId: 0, semesterId: 0,
    timeSlotId: 0, classroomId: 0, dayOfWeek: '', startTime: '', endTime: '',
    room: '', building: '', classType: '', shift: '', isActive: true
  };

  constructor(
    private service: ClassRoutineService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
    this.loadDropdowns();
  }

  loadDropdowns() {
    this.service.getTimeslots().subscribe({ next: (d) => this.timeSlots = d, error: () => this.toastService.error('Operation failed. Please try again.') });
    this.service.getClassrooms().subscribe({ next: (d) => this.classrooms = d, error: () => this.toastService.error('Operation failed. Please try again.') });
  }

  loadData() {
    this.loading = true;
    const params: any = {};
    if (this.filterSemester) params.semesterId = Number(this.filterSemester);
    if (this.filterSection) params.sectionId = Number(this.filterSection);
    if (this.filterDay) params.dayOfWeek = this.filterDay;
    this.service.getClassRoutines(params).subscribe({
      next: (data) => { this.routines = data; this.filterData(); this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load routines'); }
    });
  }

  filterData() {
    this.filteredRoutines = this.routines.filter(r => {
      if (!this.searchTerm) return true;
      const term = this.searchTerm.toLowerCase();
      return (r.subjectName || '').toLowerCase().includes(term) ||
             (r.teacherName || '').toLowerCase().includes(term) ||
             (r.room || '').toLowerCase().includes(term);
    });
  }

  openForm(item?: ClassRoutine) {
    this.editingItem = item ? { ...item } : null;
    this.conflictResult = null;
    this.form = item ? {
      subjectId: item.subjectId, administrationId: item.administrationId,
      sectionId: item.sectionId, semesterId: item.semesterId,
      timeSlotId: item.timeSlotId || 0, classroomId: item.classroomId || 0,
      dayOfWeek: item.dayOfWeek, startTime: item.startTime, endTime: item.endTime,
      room: item.room || '', building: item.building || '',
      classType: item.classType, shift: item.shift || '', isActive: item.isActive
    } : {
      subjectId: 0, administrationId: 0, sectionId: 0, semesterId: 0,
      timeSlotId: 0, classroomId: 0, dayOfWeek: '', startTime: '', endTime: '',
      room: '', building: '', classType: '', shift: '', isActive: true
    };
    this.formError = '';
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editingItem = null;
    this.formError = '';
    this.conflictResult = null;
  }

  onTimeSlotChange() {
    if (this.form.timeSlotId) {
      const ts = this.timeSlots.find(t => t.id === this.form.timeSlotId);
      if (ts) {
        this.form.startTime = ts.startTime;
        this.form.endTime = ts.endTime;
      }
    }
  }

  checkConflicts() {
    this.conflictResult = null;
    this.service.getConflicts(this.form as ClassRoutineRequest).subscribe({
      next: (result) => {
        this.conflictResult = result;
        if (!result.hasConflict) {
          this.toastService.success('No conflicts detected');
        }
      },
      error: () => this.toastService.error('Failed to check conflicts')
    });
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
      this.formError = err.error?.message || 'Save failed';
    };

    if (this.editingItem?.id) {
      this.service.updateClassRoutine(this.editingItem.id, this.form as ClassRoutineRequest).subscribe({ next: () => handleSuccess('Routine updated'), error: handleError });
    } else {
      this.service.createClassRoutine(this.form as ClassRoutineRequest).subscribe({ next: () => handleSuccess('Routine created'), error: handleError });
    }
  }

  publishAll() {
    if (this.filterSemester) {
      this.service.publishRoutine(Number(this.filterSemester)).subscribe({
        next: () => { this.loadData(); this.toastService.success('Routine published'); },
        error: () => this.toastService.error('Failed to publish routine')
      });
    } else {
      this.toastService.warning('Please select a semester first');
    }
  }

  confirmDelete(item: ClassRoutine) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Routine';
    this.confirmMessage = `Are you sure you want to delete this routine for ${item.subjectName || 'Subject #' + item.subjectId}?`;
    this.showConfirm = true;
  }

  executeDelete() {
    this.showConfirm = false;
    if (this.deleteTarget?.id) {
      this.service.deleteClassRoutine(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Routine deleted'); },
        error: () => this.toastService.error('Failed to delete routine')
      });
    }
    this.deleteTarget = null;
  }
}
