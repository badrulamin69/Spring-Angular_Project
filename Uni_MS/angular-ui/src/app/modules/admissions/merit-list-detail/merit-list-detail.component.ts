import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdmissionMeritListService } from '../../../services/admission-merit-list.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-merit-list-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent],
  template: `
    <app-toast></app-toast>
    @if (meritList) {
      <div class="page-header">
        <div>
          <div class="breadcrumb"><a (click)="goBack()">Merit Lists</a> / {{ meritList.name }}</div>
          <h2>{{ meritList.name }}</h2>
          <p class="page-sub">{{ meritList.description || 'No description' }}</p>
        </div>
        <div class="header-actions">
          @if (meritList.status === 'DRAFT') {
            <button class="btn btn-success" (click)="publish()">Publish</button>
          }
          @if (meritList.status === 'PUBLISHED') {
            <button class="btn btn-warning" (click)="unpublish()">Unpublish</button>
          }
          <button class="btn btn-outline" (click)="exportPdf()">Export PDF</button>
        </div>
      </div>

      <div class="info-cards">
        <div class="info-card"><span class="info-label">Status</span><span class="info-value status-badge" [attr.data-status]="meritList.status">{{ meritList.status }}</span></div>
        <div class="info-card"><span class="info-label">Test</span><span class="info-value">{{ meritList.test?.name || 'N/A' }}</span></div>
        <div class="info-card"><span class="info-label">Total Seats</span><span class="info-value">{{ meritList.totalSeats || 'Unlimited' }}</span></div>
        <div class="info-card"><span class="info-label">Cutoff Score</span><span class="info-value">{{ meritList.cutoffScore ? (meritList.cutoffScore | number:'1.1-1') : 'N/A' }}</span></div>
        <div class="info-card"><span class="info-label">Published</span><span class="info-value">{{ meritList.publishedAt ? (meritList.publishedAt | date:'medium') : 'Not published' }}</span></div>
      </div>

      <div class="stats-row">
        <div class="stat-card"><span class="stat-val">{{ meritList.totalApplicants || 0 }}</span><span class="stat-lbl">Total Applicants</span></div>
        <div class="stat-card selected"><span class="stat-val">{{ meritList.selectedCount || 0 }}</span><span class="stat-lbl">Selected</span></div>
        <div class="stat-card waiting"><span class="stat-val">{{ meritList.waitingCount || 0 }}</span><span class="stat-lbl">Waiting</span></div>
      </div>

      <div class="filter-bar">
        <input type="text" placeholder="Search by name, roll number..." [(ngModel)]="filters.search" (keyup.enter)="loadEntries()">
        <select [(ngModel)]="filters.status" (change)="loadEntries()">
          <option value="">All Status</option>
          <option value="SELECTED">Selected</option>
          <option value="WAITING">Waiting</option>
          <option value="NOT_SELECTED">Not Selected</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
        <button class="btn btn-sm btn-outline" (click)="loadEntries()">Search</button>
      </div>

      <app-data-table
        [columns]="columns"
        [data]="entryData?.content || []"
        [pagedData]="entryData"
        [loading]="loadingEntries"
        [params]="entryParams"
        (pageChange)="onEntryPageChange($event)"
        (refresh)="loadEntries()"
        (search)="onEntrySearch($event)"
      ></app-data-table>
    } @else if (loading) {
      <div class="loading-state"><div class="spinner"></div><span>Loading merit list...</span></div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.25rem; }
    .breadcrumb { font-size: 0.8125rem; color: #64748b; margin-bottom: 0.25rem; }
    .breadcrumb a { color: #4F46E5; cursor: pointer; text-decoration: none; }
    .breadcrumb a:hover { text-decoration: underline; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: #1e293b; font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: #64748b; }
    .header-actions { display: flex; gap: 0.5rem; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; }
    .btn-primary { background: #4F46E5; color: #fff; }
    .btn-secondary { background: #e2e8f0; color: #475569; }
    .btn-outline { background: white; color: #475569; border: 1px solid #d1d5db; }
    .btn-sm { padding: 6px 12px; font-size: 0.8125rem; }
    .btn-success { background: #28a745; color: #fff; }
    .btn-warning { background: #e6a817; color: #fff; }
    .info-cards { display: grid; grid-template-columns: repeat(5, 1fr); gap: 0.75rem; margin-bottom: 1rem; }
    .info-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 0.75rem; }
    .info-label { display: block; font-size: 0.75rem; color: #64748b; margin-bottom: 0.25rem; }
    .info-value { font-size: 0.9375rem; font-weight: 600; color: #1e293b; }
    .status-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .status-badge[data-status="DRAFT"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="PUBLISHED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="ARCHIVED"] { background: #e5e7eb; color: #374151; }
    .stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 1rem; }
    .stat-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 1rem; text-align: center; }
    .stat-card .stat-val { display: block; font-size: 1.75rem; font-weight: 700; color: #1e293b; }
    .stat-card .stat-lbl { font-size: 0.8125rem; color: #64748b; }
    .stat-card.selected .stat-val { color: #28a745; }
    .stat-card.waiting .stat-val { color: #e6a817; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar input, .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .filter-bar input { flex: 1; min-width: 200px; }
    .loading-state { display: flex; flex-direction: column; align-items: center; padding: 3rem; }
    .spinner { width: 32px; height: 32px; border: 3px solid #e2e8f0; border-top-color: #4F46E5; border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class MeritListDetailComponent implements OnInit {
  meritListId: number = 0;
  meritList: any = null;
  loading = true;
  entryData: PagedResponse<any> | null = null;
  loadingEntries = true;
  entryParams: PageParams = { ...DEFAULT_PAGE_PARAMS, sortBy: 'rank', sortDir: 'asc' };
  filters: any = { search: '', status: '' };

  columns: TableColumn[] = [
    { key: 'rank', label: 'Rank', sortable: true, type: 'number' },
    { key: 'rollNumber', label: 'Roll No', sortable: true },
    { key: 'applicantName', label: 'Applicant Name', sortable: true },
    { key: 'applicationNumber', label: 'Application No' },
    { key: 'facultyName', label: 'Faculty' },
    { key: 'departmentName', label: 'Department' },
    { key: 'programName', label: 'Program' },
    { key: 'testMarks', label: 'Test Marks', type: 'number' },
    { key: 'totalWeightedScore', label: 'Score', type: 'number' },
    { key: 'status', label: 'Status', sortable: true }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: AdmissionMeritListService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.meritListId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadMeritList();
    this.loadEntries();
  }

  loadMeritList() {
    this.service.findById(this.meritListId).subscribe({
      next: (data) => { this.meritList = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load merit list'); }
    });
  }

  loadEntries() {
    this.loadingEntries = true;
    this.service.getEntries(this.meritListId, this.entryParams, this.filters).subscribe({
      next: (data) => { this.entryData = data; this.loadingEntries = false; },
      error: () => { this.loadingEntries = false; this.toastService.error('Failed to load entries'); }
    });
  }

  onEntryPageChange(params: PageParams) { this.entryParams = params; this.loadEntries(); }
  onEntrySearch(term: string) { this.filters.search = term; this.entryParams = { ...DEFAULT_PAGE_PARAMS, sortBy: 'rank', sortDir: 'asc' }; this.loadEntries(); }

  publish() {
    this.service.publish(this.meritListId).subscribe({
      next: () => { this.loadMeritList(); this.toastService.success('Merit list published'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to publish')
    });
  }

  unpublish() {
    this.service.unpublish(this.meritListId).subscribe({
      next: () => { this.loadMeritList(); this.toastService.success('Merit list unpublished'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to unpublish')
    });
  }

  exportPdf() {
    window.open(`${environment.apiUrl}/admission-merit-lists/${this.meritListId}/pdf`, '_blank');
  }

  goBack() {
    this.router.navigate(['/admissions/merit-lists']);
  }
}
