import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdmissionAttendanceService } from '../../../services/admission-attendance.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastService } from '../../../shared/toast/toast.component';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-test-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent],
  template: `
    <div class="page-header">
      <div>
        <h2>Test Attendance</h2>
        <p class="page-sub">Mark and manage attendance for admission tests</p>
      </div>
    </div>
    <div class="filter-bar">
      <label>Filter by Test:</label>
      <select [(ngModel)]="selectedTestId" (change)="loadData()">
        <option [ngValue]="null">All Tests</option>
        @for (t of tests; track t.id) { <option [ngValue]="t.id">{{ t.name }}</option> }
      </select>
    </div>
    @if (selectedTestId && stats) {
      <div class="stats-row">
        <div class="stat-badge present">Present: {{ stats.present }}</div>
        <div class="stat-badge absent">Absent: {{ stats.absent }}</div>
        <div class="stat-badge late">Late: {{ stats.late }}</div>
        <div class="stat-badge total">Total: {{ stats.total }}</div>
      </div>
    }
    <div class="mark-section">
      <h3>Mark Attendance</h3>
      <div class="mark-form">
        <select [(ngModel)]="markData.testId" required>
          <option [ngValue]="null" disabled>Select Test</option>
          @for (t of tests; track t.id) { <option [ngValue]="t.id">{{ t.name }}</option> }
        </select>
        <input type="text" [(ngModel)]="markData.registrationId" placeholder="Registration ID" required>
        <select [(ngModel)]="markData.status" required>
          <option value="PRESENT">Present</option>
          <option value="ABSENT">Absent</option>
          <option value="LATE">Late</option>
        </select>
        <button class="btn btn-primary" (click)="markAttendance()" [disabled]="!markData.testId || !markData.registrationId">Mark</button>
      </div>
    </div>
    <app-data-table [columns]="columns" [data]="pagedData?.content || []" [pagedData]="pagedData" [loading]="loading" [params]="params" (pageChange)="onPageChange($event)" (refresh)="loadData()" (search)="onSearch($event)"></app-data-table>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: #1e293b; }
    .page-sub { margin: 0.25rem 0 0; color: #64748b; font-size: 0.875rem; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar label { font-weight: 500; color: #475569; font-size: 0.875rem; }
    .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .stats-row { display: flex; gap: 0.75rem; margin-bottom: 1.5rem; flex-wrap: wrap; }
    .stat-badge { padding: 0.5rem 1rem; border-radius: 8px; font-weight: 600; font-size: 0.875rem; }
    .stat-badge.present { background: #d1fae5; color: #065f46; }
    .stat-badge.absent { background: #fee2e2; color: #991b1b; }
    .stat-badge.late { background: #fef3c7; color: #92400e; }
    .stat-badge.total { background: #e0e7ff; color: #3730a3; }
    .mark-section { margin-bottom: 1.5rem; padding: 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .mark-section h3 { margin: 0 0 0.75rem; font-size: 1rem; color: #1e293b; }
    .mark-form { display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap; }
    .mark-form select, .mark-form input { padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.875rem; }
    .btn { padding: 0.5rem 1rem; border-radius: 6px; border: none; cursor: pointer; font-size: 0.875rem; font-weight: 500; }
    .btn-primary { background: #4F46E5; color: white; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class TestAttendanceComponent implements OnInit {
  pagedData: PagedResponse<any> | null = null;
  loading = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  selectedTestId: number | null = null;
  tests: any[] = [];
  stats: any = null;
  markData: any = { testId: null, registrationId: '', status: 'PRESENT' };
  columns: TableColumn[] = [
    { key: 'id', label: 'ID' },
    { key: 'registration', label: 'Registration', type: 'text' },
    { key: 'status', label: 'Status' },
    { key: 'checkInTime', label: 'Check In', type: 'text' },
    { key: 'checkOutTime', label: 'Check Out', type: 'text' },
    { key: 'remarks', label: 'Remarks' }
  ];
  constructor(private attService: AdmissionAttendanceService, private toast: ToastService, private http: HttpClient) {}
  ngOnInit() { this.loadTests(); this.loadData(); }
  loadTests() {
    this.http.get<any>(`${environment.apiUrl}/admission-tests?page=0&size=100`).subscribe({
      next: (res) => { this.tests = res.content || res || []; },
      error: () => { this.tests = []; }
    });
  }
  loadData() {
    this.loading = true;
    this.attService.findAll(this.params, '', this.selectedTestId || undefined).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toast.error('Failed to load attendance'); }
    });
    if (this.selectedTestId) {
      this.attService.getStats(this.selectedTestId).subscribe({
        next: (data) => { this.stats = data; },
        error: () => { this.stats = null; }
      });
    } else {
      this.stats = null;
    }
  }
  onPageChange(p: PageParams) { this.params = p; this.loadData(); }
  onSearch(term: string) { this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }
  markAttendance() {
    if (!this.markData.testId || !this.markData.registrationId) return;
    this.attService.markAttendance(this.markData).subscribe({
      next: () => { this.toast.success('Attendance marked'); this.loadData(); },
      error: (err) => { this.toast.error(err.error?.message || 'Failed to mark attendance'); }
    });
  }
}
