import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApplicantChoiceService } from '../../../services/applicant-choice.service';
import { ChoiceFillingConfigService } from '../../../services/choice-filling-config.service';
import { DataTableComponent, TableColumn, RowAction } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-choice-submissions',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Choice Submissions</h2>
        <p class="page-sub">View and manage all applicant choice submissions</p>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card"><span class="stat-val">{{ stats.total || 0 }}</span><span class="stat-lbl">Total Submissions</span></div>
      <div class="stat-card draft"><span class="stat-val">{{ stats.draft || 0 }}</span><span class="stat-lbl">Draft</span></div>
      <div class="stat-card submitted"><span class="stat-val">{{ stats.submitted || 0 }}</span><span class="stat-lbl">Submitted</span></div>
      <div class="stat-card locked"><span class="stat-val">{{ stats.locked || 0 }}</span><span class="stat-lbl">Locked</span></div>
    </div>

    <div class="filter-bar">
      <input type="text" placeholder="Search by name or submission ID..." [(ngModel)]="filters.search" (keyup.enter)="loadData()">
      <select [(ngModel)]="filters.status" (change)="loadData()">
        <option value="">All Status</option>
        <option value="DRAFT">Draft</option>
        <option value="SUBMITTED">Submitted</option>
        <option value="LOCKED">Locked</option>
      </select>
      <select [(ngModel)]="filters.configId" (change)="loadData()">
        <option [ngValue]="null">All Configs</option>
        @for (c of configs; track c.id) { <option [ngValue]="c.id">{{ c.session?.name }} - {{ c.status }}</option> }
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

    @if (showDetailModal && selectedSubmission) {
      <div class="modal-overlay" (click)="showDetailModal = false">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Choices for {{ selectedSubmission.applicantName }}</h3>
            <button class="close-btn" (click)="showDetailModal = false">&times;</button>
          </div>
          <div class="detail-info">
            <div class="info-row"><span>Submission ID:</span><strong>{{ selectedSubmission.submissionId }}</strong></div>
            <div class="info-row"><span>Status:</span><strong class="status-badge" [attr.data-status]="selectedSubmission.status">{{ selectedSubmission.status }}</strong></div>
            <div class="info-row"><span>Merit Rank:</span><strong>#{{ selectedSubmission.meritRank || 'N/A' }}</strong></div>
            <div class="info-row"><span>Total Choices:</span><strong>{{ selectedSubmission.totalChoices }}</strong></div>
          </div>
          @if (selectedChoices.length > 0) {
            <div class="choices-list">
              <h4>Choice Priority List</h4>
              @for (choice of selectedChoices; track choice.id) {
                <div class="choice-item">
                  <span class="choice-rank">#{{ choice.priority }}</span>
                  <div class="choice-details">
                    <strong>{{ choice.programName }}</strong>
                    <span>{{ choice.departmentName }} - {{ choice.facultyName }}</span>
                  </div>
                </div>
              }
            </div>
          } @else {
            <p class="no-choices">No choices submitted yet</p>
          }
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary, #1e293b); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted, #64748b); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; }
    .btn-outline { background: white; color: #475569; border: 1px solid #d1d5db; }
    .btn-sm { padding: 6px 12px; font-size: 0.8125rem; }
    :host ::ng-deep .btn-icon-success { color: #28a745; }
    :host ::ng-deep .btn-icon-success:hover { background: #f0fdf4; color: #1e7e34; }
    :host ::ng-deep .btn-icon-warning { color: #e6a817; }
    :host ::ng-deep .btn-icon-warning:hover { background: #fffbeb; color: #b38600; }
    :host ::ng-deep .btn-icon-info { color: #0056b3; }
    :host ::ng-deep .btn-icon-info:hover { background: #eff6ff; color: #004080; }
    .stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 1.25rem; }
    .stat-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 1rem; text-align: center; }
    .stat-card .stat-val { display: block; font-size: 1.75rem; font-weight: 700; color: #1e293b; }
    .stat-card .stat-lbl { font-size: 0.8125rem; color: #64748b; }
    .stat-card.draft .stat-val { color: #e6a817; }
    .stat-card.submitted .stat-val { color: #0056b3; }
    .stat-card.locked .stat-val { color: #28a745; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar input, .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .filter-bar input { flex: 1; min-width: 200px; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; padding: 1.5rem; width: 90%; max-width: 700px; max-height: 90vh; overflow-y: auto; }
    .modal-lg { max-width: 700px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; color: #1e293b; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b; }
    .detail-info { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1rem; margin-bottom: 1rem; }
    .info-row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 0.875rem; }
    .info-row span { color: #64748b; }
    .info-row strong { color: #1e293b; }
    .status-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .status-badge[data-status="DRAFT"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="SUBMITTED"] { background: #dbeafe; color: #002d5f; }
    .status-badge[data-status="LOCKED"] { background: #d1fae5; color: #065f46; }
    .choices-list h4 { margin: 0 0 0.75rem; color: #1e293b; }
    .choice-item { display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 8px; margin-bottom: 0.5rem; }
    .choice-rank { background: var(--brand-color, #4F46E5); color: #fff; width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.875rem; flex-shrink: 0; }
    .choice-details { flex: 1; }
    .choice-details strong { display: block; color: #1e293b; margin-bottom: 2px; }
    .choice-details span { font-size: 0.8125rem; color: #64748b; }
    .no-choices { color: #94a3b8; font-style: italic; text-align: center; padding: 2rem; }
  `]
})
export class ChoiceSubmissionsComponent implements OnInit {
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  stats: any = {};
  configs: any[] = [];
  filters: any = { search: '', status: '', configId: null };

  showDetailModal = false;
  selectedSubmission: any = null;
  selectedChoices: any[] = [];

  columns: TableColumn[] = [
    { key: 'submissionId', label: 'Submission ID', sortable: true },
    { key: 'applicantName', label: 'Applicant', sortable: true },
    { key: 'meritRank', label: 'Rank', type: 'number', sortable: true },
    { key: 'meritScore', label: 'Score', type: 'number' },
    { key: 'totalChoices', label: 'Choices', type: 'number' },
    { key: 'status', label: 'Status', sortable: true }
  ];

  rowActions: RowAction[] = [
    { label: 'View', icon: '👁', title: 'View Choices', class: 'btn-icon-info', onClick: (item) => this.viewChoices(item) },
    { label: 'Lock', icon: '🔒', title: 'Lock Submission', class: 'btn-icon-success', condition: (item) => item.status === 'SUBMITTED', onClick: (item) => this.lockSubmission(item) },
    { label: 'Reopen', icon: '🔓', title: 'Reopen Submission', class: 'btn-icon-warning', condition: (item) => item.status === 'LOCKED' || item.status === 'SUBMITTED', onClick: (item) => this.reopenSubmission(item) },
  ];

  constructor(
    private choiceService: ApplicantChoiceService,
    private configService: ChoiceFillingConfigService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
    this.loadConfigs();
  }

  loadConfigs() {
    this.configService.findAll({ ...DEFAULT_PAGE_PARAMS, size: 100 }).subscribe({
      next: (data) => { this.configs = data.content || []; if (this.configs.length > 0) this.loadStats(this.configs[0].id); }
    });
  }

  loadStats(configId: number) {
    this.choiceService.getStats(configId).subscribe({
      next: (data) => { this.stats = data; },
      error: () => this.toastService.error('Operation failed. Please try again.')
    });
  }

  loadData() {
    this.loading = true;
    this.choiceService.getAllSubmissions(this.params, this.filters).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load submissions'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.filters.search = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  viewChoices(item: any) {
    this.selectedSubmission = item;
    this.choiceService.getSubmissionChoices(item.id).subscribe({
      next: (choices) => { this.selectedChoices = choices; this.showDetailModal = true; },
      error: () => { this.toastService.error('Failed to load choices'); }
    });
  }

  lockSubmission(item: any) {
    this.choiceService.lockSubmission(item.id).subscribe({
      next: () => { this.loadData(); this.toastService.success('Submission locked'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to lock')
    });
  }

  reopenSubmission(item: any) {
    this.choiceService.reopenSubmission(item.id).subscribe({
      next: () => { this.loadData(); this.toastService.success('Submission reopened'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to reopen')
    });
  }
}
