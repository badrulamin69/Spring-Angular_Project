import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdmissionWaitingListService } from '../../../services/admission-waiting-list.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-waiting-list-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent],
  template: `
    <app-toast></app-toast>
    @if (waitingList) {
      <div class="page-header">
        <div>
          <div class="breadcrumb"><a (click)="goBack()">Waiting Lists</a> / {{ waitingList.name }}</div>
          <h2>{{ waitingList.name }}</h2>
          <p class="page-sub">{{ waitingList.description || 'No description' }}</p>
        </div>
        <div class="header-actions">
          @if (waitingList.status === 'DRAFT') {
            <button class="btn btn-success" (click)="publish()">Publish</button>
          }
          @if (waitingList.status === 'PUBLISHED') {
            <button class="btn btn-warning" (click)="unpublish()">Unpublish</button>
          }
        </div>
      </div>

      <div class="info-cards">
        <div class="info-card"><span class="info-label">Status</span><span class="info-value status-badge" [attr.data-status]="waitingList.status">{{ waitingList.status }}</span></div>
        <div class="info-card"><span class="info-label">Test</span><span class="info-value">{{ waitingList.test?.name || 'N/A' }}</span></div>
        <div class="info-card"><span class="info-label">Total Slots</span><span class="info-value">{{ waitingList.totalSlots || 'Unlimited' }}</span></div>
        <div class="info-card"><span class="info-label">Filled Slots</span><span class="info-value">{{ waitingList.filledSlots || 0 }}</span></div>
      </div>

      <div class="filter-bar">
        <input type="text" placeholder="Search by name, roll number..." [(ngModel)]="filters.search" (keyup.enter)="loadEntries()">
        <select [(ngModel)]="filters.status" (change)="loadEntries()">
          <option value="">All Status</option>
          <option value="WAITING">Waiting</option>
          <option value="OFFERED">Offered</option>
          <option value="ACCEPTED">Accepted</option>
          <option value="DECLINED">Declined</option>
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
      <div class="loading-state"><div class="spinner"></div><span>Loading waiting list...</span></div>
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
    .btn-success { background: #28a745; color: #fff; }
    .btn-warning { background: #e6a817; color: #fff; }
    .btn-outline { background: white; color: #475569; border: 1px solid #d1d5db; }
    .btn-sm { padding: 6px 12px; font-size: 0.8125rem; }
    .info-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 0.75rem; margin-bottom: 1rem; }
    .info-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 0.75rem; }
    .info-label { display: block; font-size: 0.75rem; color: #64748b; margin-bottom: 0.25rem; }
    .info-value { font-size: 0.9375rem; font-weight: 600; color: #1e293b; }
    .status-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .status-badge[data-status="DRAFT"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="PUBLISHED"] { background: #d1fae5; color: #065f46; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar input, .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .filter-bar input { flex: 1; min-width: 200px; }
    .loading-state { display: flex; flex-direction: column; align-items: center; padding: 3rem; }
    .spinner { width: 32px; height: 32px; border: 3px solid #e2e8f0; border-top-color: #4F46E5; border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class WaitingListDetailComponent implements OnInit {
  waitingListId: number = 0;
  waitingList: any = null;
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
    { key: 'testMarks', label: 'Test Marks', type: 'number' },
    { key: 'totalWeightedScore', label: 'Score', type: 'number' },
    { key: 'status', label: 'Status', sortable: true }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: AdmissionWaitingListService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.waitingListId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadWaitingList();
    this.loadEntries();
  }

  loadWaitingList() {
    this.service.findById(this.waitingListId).subscribe({
      next: (data) => { this.waitingList = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load waiting list'); }
    });
  }

  loadEntries() {
    this.loadingEntries = true;
    this.service.getEntries(this.waitingListId, this.entryParams, this.filters).subscribe({
      next: (data) => { this.entryData = data; this.loadingEntries = false; },
      error: () => { this.loadingEntries = false; this.toastService.error('Failed to load entries'); }
    });
  }

  onEntryPageChange(params: PageParams) { this.entryParams = params; this.loadEntries(); }
  onEntrySearch(term: string) { this.filters.search = term; this.entryParams = { ...DEFAULT_PAGE_PARAMS, sortBy: 'rank', sortDir: 'asc' }; this.loadEntries(); }

  publish() {
    this.service.publish(this.waitingListId).subscribe({
      next: () => { this.loadWaitingList(); this.toastService.success('Waiting list published'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to publish')
    });
  }

  unpublish() {
    this.service.unpublish(this.waitingListId).subscribe({
      next: () => { this.loadWaitingList(); this.toastService.success('Waiting list unpublished'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to unpublish')
    });
  }

  goBack() {
    this.router.navigate(['/admissions/waiting-lists']);
  }
}
