import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClassRoutineService } from '../../../services/class-routine.service';
import { AcademicCalendarEvent } from '../../../models/class-routine';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-calendar-event',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Calendar Events</h2>
        <p class="page-sub">Manage academic calendar events and holidays</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add Event
      </button>
    </div>

    <div class="filters-bar">
      <select [(ngModel)]="filterSemester" class="form-control filter-select" (change)="loadData()">
        <option value="">All Semesters</option>
        @for (s of semesters; track s.id) {
          <option [ngValue]="s.id">{{ s.name }}</option>
        }
      </select>
      <select [(ngModel)]="filterType" class="form-control filter-select" (change)="filterData()">
        <option value="">All Types</option>
        <option value="ACADEMIC">Academic</option>
        <option value="EXAM">Exam</option>
        <option value="HOLIDAY">Holiday</option>
        <option value="EVENT">Event</option>
        <option value="DEADLINE">Deadline</option>
        <option value="ORIENTATION">Orientation</option>
      </select>
      <div class="search-box">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input type="text" [(ngModel)]="searchTerm" placeholder="Search events..." (input)="filterData()">
      </div>
    </div>

    <div class="card">
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Type</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Semester</th>
              <th>Holiday</th>
              <th>Published</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (event of filteredEvents; track event.id) {
              <tr>
                <td>{{ event.id }}</td>
                <td>{{ event.title }}</td>
                <td><span class="badge" [ngClass]="getTypeBadgeClass(event.eventType)">{{ event.eventType }}</span></td>
                <td>{{ event.startDate }}</td>
                <td>{{ event.endDate || '-' }}</td>
                <td>{{ event.semesterName || ('Semester #' + event.semesterId) }}</td>
                <td>
                  @if (event.isHoliday) {
                    <span class="badge badge-warning">Holiday</span>
                  } @else {
                    <span class="badge badge-secondary">No</span>
                  }
                </td>
                <td>
                  @if (event.isPublished) {
                    <span class="badge badge-success">Published</span>
                  } @else {
                    <span class="badge badge-secondary">Draft</span>
                  }
                </td>
                <td>
                  <div class="actions">
                    <button class="btn-icon" (click)="openForm(event)" title="Edit">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                    </button>
                    <button class="btn-icon btn-danger" (click)="confirmDelete(event)" title="Delete">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="9" class="text-center text-muted">No calendar events found</td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>

    @if (showForm) {
      <div class="modal-overlay" (click)="closeForm()">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingItem ? 'Edit' : 'New' }} Calendar Event</h3>
            <button class="btn-close" (click)="closeForm()">&times;</button>
          </div>
          <form (ngSubmit)="save()">
            <div class="form-grid">
              <div class="form-group full-width">
                <label>Title *</label>
                <input type="text" [(ngModel)]="form.title" name="title" required class="form-control" placeholder="Event title">
              </div>
              <div class="form-group full-width">
                <label>Description</label>
                <textarea [(ngModel)]="form.description" name="description" class="form-control" rows="2"></textarea>
              </div>
              <div class="form-group">
                <label>Event Type *</label>
                <select [(ngModel)]="form.eventType" name="eventType" required class="form-control">
                  <option value="">Select Type</option>
                  <option value="ACADEMIC">Academic</option>
                  <option value="EXAM">Exam</option>
                  <option value="HOLIDAY">Holiday</option>
                  <option value="EVENT">Event</option>
                  <option value="DEADLINE">Deadline</option>
                  <option value="ORIENTATION">Orientation</option>
                </select>
              </div>
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
                <label>Start Date *</label>
                <input type="date" [(ngModel)]="form.startDate" name="startDate" required class="form-control">
              </div>
              <div class="form-group">
                <label>End Date</label>
                <input type="date" [(ngModel)]="form.endDate" name="endDate" class="form-control">
              </div>
              <div class="form-group">
                <label>Start Time</label>
                <input type="time" [(ngModel)]="form.startTime" name="startTime" class="form-control">
              </div>
              <div class="form-group">
                <label>End Time</label>
                <input type="time" [(ngModel)]="form.endTime" name="endTime" class="form-control">
              </div>
              <div class="form-group">
                <label>Color</label>
                <input type="color" [(ngModel)]="form.color" name="color" class="form-control color-input">
              </div>
              <div class="form-group">
                <label>Location</label>
                <input type="text" [(ngModel)]="form.location" name="location" class="form-control">
              </div>
              <div class="form-group">
                <label>Recurrence</label>
                <select [(ngModel)]="form.recurrence" name="recurrence" class="form-control">
                  <option value="">None</option>
                  <option value="WEEKLY">Weekly</option>
                  <option value="MONTHLY">Monthly</option>
                  <option value="YEARLY">Yearly</option>
                </select>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.isHoliday" name="isHoliday"> Holiday</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.isPublished" name="isPublished"> Published</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.isAllDay" name="isAllDay"> All Day</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.notifyStudents" name="notifyStudents"> Notify Students</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.notifyTeachers" name="notifyTeachers"> Notify Teachers</label>
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
    .page-header h2 { margin: 0; font-size: 1.5rem; color: #e2e8f0; font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: #94a3b8; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: #3b82f6; color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-secondary { background: #334155; color: #e2e8f0; }
    .card { background: #1e293b; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; }
    .filters-bar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
    .search-box { display: flex; align-items: center; gap: 8px; background: #1e293b; border: 1px solid #334155; border-radius: 8px; padding: 8px 12px; flex: 1; min-width: 200px; }
    .search-box input { border: none; background: transparent; color: #e2e8f0; font-size: 0.875rem; outline: none; width: 100%; }
    .search-box svg { color: #64748b; flex-shrink: 0; }
    .filter-select { padding: 8px 12px; border: 1px solid #334155; border-radius: 8px; background: #1e293b; color: #e2e8f0; font-size: 0.875rem; min-width: 150px; }
    .form-control { padding: 8px 12px; border: 1px solid #334155; border-radius: 6px; font-size: 0.875rem; background: #0f172a; color: #e2e8f0; }
    .form-control:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    .color-input { height: 36px; padding: 4px; cursor: pointer; }
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
    .badge-warning { background: #422006; color: #fcd34d; }
    .badge-academic { background: #1e3a5f; color: #93c5fd; }
    .badge-exam { background: #4c1d95; color: #c4b5fd; }
    .badge-holiday { background: #422006; color: #fcd34d; }
    .badge-event { background: #064e3b; color: #6ee7b7; }
    .badge-deadline { background: #7f1d1d; color: #fca5a5; }
    .badge-orientation { background: #134e4a; color: #5eead4; }
    .text-center { text-align: center; }
    .text-muted { color: #94a3b8; }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: #1e293b; border-radius: 12px; width: 90%; max-width: 600px; max-height: 90vh; overflow-y: auto; border: 1px solid #334155; }
    .modal-lg { max-width: 700px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #334155; }
    .modal-header h3 { margin: 0; font-size: 1.125rem; color: #e2e8f0; }
    .btn-close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #94a3b8; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; padding: 20px; }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group.full-width { grid-column: 1 / -1; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: #94a3b8; }
    .checkbox-group { justify-content: center; }
    .checkbox-group label { flex-direction: row; align-items: center; gap: 8px; cursor: pointer; }
    .error-message { padding: 8px 12px; background: rgba(239,68,68,0.1); color: #fca5a5; border-radius: 6px; margin: 0 20px; font-size: 0.875rem; border: 1px solid rgba(239,68,68,0.2); }
    .modal-footer { display: flex; justify-content: flex-end; gap: 8px; padding: 16px 20px; border-top: 1px solid #334155; }
  `]
})
export class CalendarEventComponent implements OnInit {
  events: AcademicCalendarEvent[] = [];
  filteredEvents: AcademicCalendarEvent[] = [];
  semesters: any[] = [];
  loading = true;
  saving = false;
  showForm = false;
  editingItem: AcademicCalendarEvent | null = null;
  formError = '';
  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: AcademicCalendarEvent | null = null;
  searchTerm = '';
  filterSemester = '';
  filterType = '';

  form: Partial<AcademicCalendarEvent> = {
    title: '', description: '', eventType: '', startDate: '', endDate: '',
    startTime: '', endTime: '', semesterId: 0, isHoliday: false,
    isPublished: false, isAllDay: true, color: '#3b82f6',
    location: '', recurrence: '', notifyStudents: false, notifyTeachers: false
  };

  constructor(
    private service: ClassRoutineService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.getCalendarEvents().subscribe({
      next: (data) => { this.events = data; this.filterData(); this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load events'); }
    });
  }

  filterData() {
    this.filteredEvents = this.events.filter(e => {
      const matchSearch = !this.searchTerm || e.title.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchType = !this.filterType || e.eventType === this.filterType;
      const matchSemester = !this.filterSemester || e.semesterId === Number(this.filterSemester);
      return matchSearch && matchType && matchSemester;
    });
  }

  getTypeBadgeClass(type: string): string {
    const map: Record<string, string> = {
      ACADEMIC: 'badge-academic', EXAM: 'badge-exam', HOLIDAY: 'badge-holiday',
      EVENT: 'badge-event', DEADLINE: 'badge-deadline', ORIENTATION: 'badge-orientation'
    };
    return map[type] || 'badge-secondary';
  }

  openForm(item?: AcademicCalendarEvent) {
    this.editingItem = item ? { ...item } : null;
    this.form = item ? { ...item } : {
      title: '', description: '', eventType: '', startDate: '', endDate: '',
      startTime: '', endTime: '', semesterId: 0, isHoliday: false,
      isPublished: false, isAllDay: true, color: '#3b82f6',
      location: '', recurrence: '', notifyStudents: false, notifyTeachers: false
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
      this.formError = err.error?.message || 'Save failed';
    };

    if (this.editingItem?.id) {
      this.service.updateCalendarEvent(this.editingItem.id, this.form as AcademicCalendarEvent).subscribe({ next: () => handleSuccess('Event updated'), error: handleError });
    } else {
      this.service.createCalendarEvent(this.form as AcademicCalendarEvent).subscribe({ next: () => handleSuccess('Event created'), error: handleError });
    }
  }

  confirmDelete(item: AcademicCalendarEvent) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Event';
    this.confirmMessage = `Are you sure you want to delete "${item.title}"?`;
    this.showConfirm = true;
  }

  executeDelete() {
    this.showConfirm = false;
    if (this.deleteTarget?.id) {
      this.service.deleteCalendarEvent(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Event deleted'); },
        error: () => this.toastService.error('Failed to delete event')
      });
    }
    this.deleteTarget = null;
  }
}
