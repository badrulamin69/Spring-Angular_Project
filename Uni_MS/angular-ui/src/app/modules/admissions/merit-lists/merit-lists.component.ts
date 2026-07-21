import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdmissionMeritListService } from '../../../services/admission-merit-list.service';
import { DataTableComponent, TableColumn, RowAction } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-merit-lists',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Merit Lists</h2>
        <p class="page-sub">Generate, manage, and publish admission merit lists</p>
      </div>
      <button class="btn btn-primary" (click)="showGenerateModal = true">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Generate Merit List
      </button>
    </div>

    <div class="stats-row">
      <div class="stat-card"><span class="stat-val">{{ stats.total || 0 }}</span><span class="stat-lbl">Total Lists</span></div>
      <div class="stat-card draft"><span class="stat-val">{{ stats.draft || 0 }}</span><span class="stat-lbl">Draft</span></div>
      <div class="stat-card published"><span class="stat-val">{{ stats.published || 0 }}</span><span class="stat-lbl">Published</span></div>
      <div class="stat-card archived"><span class="stat-val">{{ stats.archived || 0 }}</span><span class="stat-lbl">Archived</span></div>
    </div>

    <div class="filter-bar">
      <input type="text" placeholder="Search lists..." [(ngModel)]="filters.search" (keyup.enter)="loadData()">
      <select [(ngModel)]="filters.status" (change)="loadData()">
        <option value="">All Status</option>
        <option value="DRAFT">Draft</option>
        <option value="PUBLISHED">Published</option>
        <option value="ARCHIVED">Archived</option>
      </select>
      <select [(ngModel)]="filters.testId" (change)="loadData()">
        <option [ngValue]="null">All Tests</option>
        @for (t of tests; track t.id) { <option [ngValue]="t.id">{{ t.name }}</option> }
      </select>
      <button class="btn btn-sm btn-outline" (click)="loadData()">Search</button>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      [rowActions]="rowActions"
      [showDefaultActions]="false"
      (pageChange)="onPageChange($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showGenerateModal) {
      <div class="modal-overlay" (click)="showGenerateModal = false">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Generate Merit List</h3>
            <button class="close-btn" (click)="showGenerateModal = false">&times;</button>
          </div>
          <form (ngSubmit)="generateList()">
            <div class="form-row-2">
              <div class="form-group">
                <label>Select Test *</label>
                <select [(ngModel)]="generateData.testId" name="testId" required>
                  <option [ngValue]="null" disabled>Select Test</option>
                  @for (t of tests; track t.id) { <option [ngValue]="t.id">{{ t.name }}</option> }
                </select>
              </div>
              <div class="form-group">
                <label>List Name</label>
                <input type="text" [(ngModel)]="generateData.listName" name="listName" placeholder="Auto-generated if empty">
              </div>
            </div>
            <div class="form-row-3">
              <div class="form-group">
                <label>Total Seats</label>
                <input type="number" [(ngModel)]="generateData.totalSeats" name="totalSeats" min="1" placeholder="No limit">
              </div>
              <div class="form-group">
                <label>Academic Year</label>
                <input type="text" [(ngModel)]="generateData.academicYear" name="academicYear" placeholder="e.g. 2026">
              </div>
              <div class="form-group">
                <label>Shift</label>
                <select [(ngModel)]="generateData.shift" name="shift">
                  <option value="">All Shifts</option>
                  <option value="MORNING">Morning</option>
                  <option value="AFTERNOON">Afternoon</option>
                  <option value="EVENING">Evening</option>
                </select>
              </div>
            </div>
            <div class="form-row-3">
              <div class="form-group">
                <label>Faculty</label>
                <select [(ngModel)]="generateData.facultyId" name="facultyId">
                  <option [ngValue]="null">All Faculties</option>
                  @for (f of faculties; track f.id) { <option [ngValue]="f.id">{{ f.name }}</option> }
                </select>
              </div>
              <div class="form-group">
                <label>Department</label>
                <select [(ngModel)]="generateData.departmentId" name="departmentId">
                  <option [ngValue]="null">All Departments</option>
                  @for (d of departments; track d.id) { <option [ngValue]="d.id">{{ d.name }}</option> }
                </select>
              </div>
              <div class="form-group">
                <label>Program</label>
                <select [(ngModel)]="generateData.programId" name="programId">
                  <option [ngValue]="null">All Programs</option>
                  @for (p of programs; track p.id) { <option [ngValue]="p.id">{{ p.name }}</option> }
                </select>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="showGenerateModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="generating">{{ generating ? 'Generating...' : 'Generate' }}</button>
            </div>
          </form>
        </div>
      </div>
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
    .btn-outline { background: white; color: #475569; border: 1px solid #d1d5db; }
    .btn-sm { padding: 6px 12px; font-size: 0.8125rem; }
    .btn-success { background: #28a745; color: #fff; }
    .btn-warning { background: #e6a817; color: #fff; }
    .btn-danger { background: #dc3545; color: #fff; }
    :host ::ng-deep .btn-icon-success { color: #28a745; }
    :host ::ng-deep .btn-icon-success:hover { background: #f0fdf4; color: #1e7e34; }
    :host ::ng-deep .btn-icon-warning { color: #e6a817; }
    :host ::ng-deep .btn-icon-warning:hover { background: #fffbeb; color: #b38600; }
    :host ::ng-deep .btn-icon-muted { color: #6b7280; }
    :host ::ng-deep .btn-icon-muted:hover { background: #f3f4f6; color: #4b5563; }
    .stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 1.25rem; }
    .stat-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 1rem; text-align: center; }
    .stat-card .stat-val { display: block; font-size: 1.75rem; font-weight: 700; color: #1e293b; }
    .stat-card .stat-lbl { font-size: 0.8125rem; color: #64748b; }
    .stat-card.draft .stat-val { color: #e6a817; }
    .stat-card.published .stat-val { color: #28a745; }
    .stat-card.archived .stat-val { color: #6b7280; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar input, .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .filter-bar input { flex: 1; min-width: 200px; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; padding: 1.5rem; width: 90%; max-width: 700px; max-height: 90vh; overflow-y: auto; }
    .modal-lg { max-width: 750px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; color: #1e293b; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b; }
    .form-group { margin-bottom: 0.75rem; }
    .form-group label { display: block; margin-bottom: 0.25rem; font-weight: 500; color: #374151; font-size: 0.8125rem; }
    .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.875rem; box-sizing: border-box; }
    .form-group input:focus, .form-group select:focus { outline: none; border-color: #4F46E5; box-shadow: 0 0 0 2px rgba(79,70,229,0.1); }
    .form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
    .form-row-3 { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 0.75rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #e2e8f0; }
  `]
})
export class MeritListsComponent implements OnInit {
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  stats: any = {};
  tests: any[] = [];
  faculties: any[] = [];
  departments: any[] = [];
  programs: any[] = [];
  filters: any = { search: '', status: '', testId: null };

  showGenerateModal = false;
  generating = false;
  generateData: any = { testId: null, listName: '', totalSeats: null, academicYear: '', shift: '', facultyId: null, departmentId: null, programId: null };

  columns: TableColumn[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Name', sortable: true },
    { key: 'test.name', label: 'Test' },
    { key: 'totalSeats', label: 'Seats', type: 'number' },
    { key: 'totalApplicants', label: 'Applicants', type: 'number' },
    { key: 'selectedCount', label: 'Selected', type: 'number' },
    { key: 'cutoffScore', label: 'Cutoff', type: 'number' },
    { key: 'status', label: 'Status', sortable: true }
  ];

  rowActions: RowAction[] = [
    { label: 'View', icon: '👁', title: 'View Entries', onClick: (item) => this.viewEntries(item) },
    { label: 'Publish', icon: '✅', title: 'Publish', class: 'btn-icon-success', condition: (item) => item.status === 'DRAFT', onClick: (item) => this.publishList(item) },
    { label: 'Unpublish', icon: '↩', title: 'Unpublish', class: 'btn-icon-warning', condition: (item) => item.status === 'PUBLISHED', onClick: (item) => this.unpublishList(item) },
    { label: 'Archive', icon: '📦', title: 'Archive', class: 'btn-icon-muted', condition: (item) => item.status === 'PUBLISHED', onClick: (item) => this.archiveList(item) },
  ];

  constructor(
    private service: AdmissionMeritListService,
    private toastService: ToastService,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.loadData();
    this.loadStats();
    this.loadDropdowns();
  }

  loadDropdowns() {
    this.http.get<any>(`${environment.apiUrl}/admission-tests?page=0&size=100`).subscribe({
      next: (res) => { this.tests = res.content || res || []; },
      error: () => { this.tests = []; }
    });
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

  loadStats() {
    this.service.getStats().subscribe({
      next: (data) => { this.stats = data; },
      error: () => {}
    });
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.filters).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load merit lists'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.filters.search = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  generateList() {
    if (!this.generateData.testId) return;
    this.generating = true;
    this.service.generate(
      this.generateData.testId, this.generateData.listName || undefined,
      this.generateData.totalSeats || undefined, this.generateData.academicYear || undefined,
      this.generateData.facultyId || undefined, this.generateData.departmentId || undefined,
      this.generateData.programId || undefined, this.generateData.shift || undefined
    ).subscribe({
      next: () => {
        this.generating = false;
        this.showGenerateModal = false;
        this.generateData = { testId: null, listName: '', totalSeats: null, academicYear: '', shift: '', facultyId: null, departmentId: null, programId: null };
        this.toastService.success('Merit list generated successfully');
        this.loadData();
        this.loadStats();
      },
      error: (err) => { this.generating = false; this.toastService.error(err.error?.message || 'Failed to generate merit list'); }
    });
  }

  publishList(item: any) {
    this.service.publish(item.id).subscribe({
      next: () => { this.loadData(); this.loadStats(); this.toastService.success('Merit list published'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to publish')
    });
  }

  unpublishList(item: any) {
    this.service.unpublish(item.id).subscribe({
      next: () => { this.loadData(); this.loadStats(); this.toastService.success('Merit list unpublished'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to unpublish')
    });
  }

  archiveList(item: any) {
    this.service.archive(item.id).subscribe({
      next: () => { this.loadData(); this.loadStats(); this.toastService.success('Merit list archived'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to archive')
    });
  }

  viewEntries(item: any) {
    this.router.navigate(['/admissions/merit-lists', item.id]);
  }


}
