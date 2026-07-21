import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClassRoutineService } from '../../../services/class-routine.service';
import { ClassRoutine } from '../../../models/class-routine';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-semester-routine',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Semester Routine</h2>
        <p class="page-sub">View and manage routines grouped by semester</p>
      </div>
    </div>

    <div class="filters-bar">
      <select [(ngModel)]="filterSemester" class="form-control filter-select" (change)="loadData()">
        <option value="">Select Semester</option>
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
      <div class="search-box">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input type="text" [(ngModel)]="searchTerm" placeholder="Search..." (input)="filterData()">
      </div>
    </div>

    <div class="day-grid">
      @for (day of days; track day) {
        <div class="day-column">
          <div class="day-header">{{ day }}</div>
          <div class="day-routines">
            @for (routine of getRoutinesByDay(day); track routine.id) {
              <div class="routine-card">
                <div class="routine-time">{{ routine.startTime }} - {{ routine.endTime }}</div>
                <div class="routine-subject">{{ routine.subjectName || ('Subject #' + routine.subjectId) }}</div>
                <div class="routine-teacher">{{ routine.teacherName || '' }}</div>
                <div class="routine-room">{{ routine.room || routine.classroomNumber || '-' }}</div>
                <div class="routine-type">
                  <span class="badge badge-info">{{ routine.classType }}</span>
                </div>
              </div>
            } @empty {
              <div class="empty-day">No classes</div>
            }
          </div>
        </div>
      }
    </div>

    <div class="card" style="margin-top: 24px;">
      <div class="table-header">
        <h3 class="section-title">All Routines</h3>
      </div>
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Subject</th>
              <th>Teacher</th>
              <th>Section</th>
              <th>Day</th>
              <th>Time</th>
              <th>Room</th>
              <th>Type</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            @for (routine of filteredRoutines; track routine.id) {
              <tr>
                <td>{{ routine.id }}</td>
                <td>{{ routine.subjectName || ('Subject #' + routine.subjectId) }}</td>
                <td>{{ routine.teacherName || '-' }}</td>
                <td>{{ routine.sectionName || ('Section #' + routine.sectionId) }}</td>
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
              </tr>
            } @empty {
              <tr><td colspan="9" class="text-center text-muted">No routines found for the selected filters</td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: #e2e8f0; font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: #94a3b8; }
    .filters-bar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
    .search-box { display: flex; align-items: center; gap: 8px; background: #1e293b; border: 1px solid #334155; border-radius: 8px; padding: 8px 12px; flex: 1; min-width: 200px; }
    .search-box input { border: none; background: transparent; color: #e2e8f0; font-size: 0.875rem; outline: none; width: 100%; }
    .search-box svg { color: #64748b; flex-shrink: 0; }
    .filter-select { padding: 8px 12px; border: 1px solid #334155; border-radius: 8px; background: #1e293b; color: #e2e8f0; font-size: 0.875rem; min-width: 150px; }
    .form-control { padding: 8px 12px; border: 1px solid #334155; border-radius: 6px; font-size: 0.875rem; background: #0f172a; color: #e2e8f0; }
    .day-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 12px; overflow-x: auto; }
    .day-column { background: #1e293b; border-radius: 10px; overflow: hidden; min-width: 150px; }
    .day-header { padding: 10px 12px; background: #0f172a; color: #94a3b8; font-weight: 600; font-size: 0.8rem; text-transform: uppercase; text-align: center; letter-spacing: 0.05em; }
    .day-routines { padding: 8px; display: flex; flex-direction: column; gap: 6px; min-height: 100px; }
    .routine-card { background: #0f172a; border-radius: 6px; padding: 8px 10px; border-left: 3px solid #3b82f6; }
    .routine-time { font-size: 0.7rem; color: #3b82f6; font-weight: 600; }
    .routine-subject { font-size: 0.8rem; color: #e2e8f0; margin-top: 2px; font-weight: 500; }
    .routine-teacher { font-size: 0.7rem; color: #94a3b8; }
    .routine-room { font-size: 0.7rem; color: #64748b; }
    .routine-type { margin-top: 4px; }
    .empty-day { color: #475569; font-size: 0.8rem; text-align: center; padding: 20px 10px; }
    .card { background: #1e293b; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; }
    .table-header { padding: 16px 20px; border-bottom: 1px solid #334155; }
    .section-title { margin: 0; font-size: 1rem; color: #e2e8f0; }
    .table-responsive { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #334155; font-size: 0.875rem; }
    .data-table th { background: #0f172a; font-weight: 600; color: #94a3b8; }
    .data-table tr:hover { background: rgba(59,130,246,0.05); }
    .badge { padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #064e3b; color: #6ee7b7; }
    .badge-secondary { background: #334155; color: #94a3b8; }
    .badge-info { background: #1e3a5f; color: #93c5fd; }
    .text-center { text-align: center; }
    .text-muted { color: #94a3b8; }
    @media (max-width: 1200px) { .day-grid { grid-template-columns: repeat(4, 1fr); } }
    @media (max-width: 768px) { .day-grid { grid-template-columns: repeat(2, 1fr); } }
  `]
})
export class SemesterRoutineComponent implements OnInit {
  routines: ClassRoutine[] = [];
  filteredRoutines: ClassRoutine[] = [];
  semesters: any[] = [];
  sections: any[] = [];
  loading = true;
  searchTerm = '';
  filterSemester = '';
  filterSection = '';
  days = ['SATURDAY', 'SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

  constructor(
    private service: ClassRoutineService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    const params: any = {};
    if (this.filterSemester) params.semesterId = Number(this.filterSemester);
    if (this.filterSection) params.sectionId = Number(this.filterSection);
    this.service.getClassRoutines(params).subscribe({
      next: (data) => { this.routines = data; this.filterData(); this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load routines'); }
    });
  }

  filterData() {
    this.filteredRoutines = this.routines.filter(r => {
      if (!this.searchTerm) return true;
      const term = this.searchTerm.toLowerCase();
      return (r.subjectName || '').toLowerCase().includes(term) || (r.teacherName || '').toLowerCase().includes(term);
    });
  }

  getRoutinesByDay(day: string): ClassRoutine[] {
    return this.filteredRoutines.filter(r => r.dayOfWeek === day).sort((a, b) => a.startTime.localeCompare(b.startTime));
  }
}
