import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GeneratedReportService } from '../../../services/generated-report.service';
import { ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-admission-reports',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-header">
      <div><h2>Admission Reports</h2><p class="page-sub">Generate and view admission-related reports</p></div>
    </div>

    <div class="reports-grid">
      @for (report of reports; track report.title) {
        <div class="report-card">
          <div class="report-icon" [style.background]="report.color + '18'" [style.color]="report.color">
            <span class="material-symbols-outlined">{{ report.icon }}</span>
          </div>
          <div class="report-info">
            <h3>{{ report.title }}</h3>
            <p>{{ report.description }}</p>
          </div>
          <button class="btn-generate" [style.color]="report.color" (click)="generateReport(report)">
            <span class="material-symbols-outlined">download</span>
            Generate
          </button>
        </div>
      }
    </div>

    <div class="recent-reports">
      <h3>Recent Reports</h3>
      @if (loading) {
        <div class="loading-text">Loading recent reports...</div>
      } @else if (recentReports.length === 0) {
        <div class="empty-text">No reports generated yet. Generate your first report above.</div>
      } @else {
        <div class="report-history">
          @for (r of recentReports; track r.id) {
            <div class="history-item">
              <span class="material-symbols-outlined" [style.color]="getReportColor(r.reportType)">{{ getReportIcon(r.reportType) }}</span>
              <div class="history-info">
                <span class="history-name">{{ r.title || r.reportType }}</span>
                <span class="history-date">{{ r.createdAt | date:'medium' }}</span>
              </div>
              <span class="history-status" [class.status-ready]="r.status === 'READY'" [class.status-pending]="r.status === 'PENDING'">{{ r.status }}</span>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    :host { display: block; padding: 0 0 2rem; }
    .page-header { margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .reports-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }
    @media (max-width: 768px) { .reports-grid { grid-template-columns: 1fr; } }
    .report-card { background: var(--bg-secondary); border-radius: 12px; padding: 20px; border: 1px solid var(--border-color); display: flex; align-items: center; gap: 14px; transition: all 0.2s; }
    .report-card:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }
    .report-icon { width: 44px; height: 44px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .report-info { flex: 1; }
    .report-info h3 { font-size: 13px; font-weight: 600; color: var(--text-primary); margin: 0; }
    .report-info p { font-size: 11px; color: var(--text-muted); margin: 4px 0 0; }
    .btn-generate { background: none; border: 1px solid var(--border-color); border-radius: 6px; padding: 6px 12px; cursor: pointer; font-size: 11px; font-weight: 500; display: flex; align-items: center; gap: 4px; transition: all 0.15s; }
    .btn-generate:hover { background: var(--bg-hover); }
    .btn-generate .material-symbols-outlined { font-size: 14px; }
    .recent-reports { background: var(--bg-secondary); border-radius: 12px; padding: 20px; border: 1px solid var(--border-color); }
    .recent-reports h3 { font-size: 14px; font-weight: 600; color: var(--text-primary); margin: 0 0 16px; }
    .history-item { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid var(--border-color); }
    .history-item:last-child { border-bottom: none; }
    .history-info { flex: 1; }
    .history-name { display: block; font-size: 13px; font-weight: 500; color: var(--text-primary); }
    .history-date { font-size: 11px; color: var(--text-muted); }
    .history-status { font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 4px; }
    .status-ready { background: #dcfce7; color: #22c55e; }
    .status-pending { background: #fef3c7; color: #d97706; }
    .loading-text, .empty-text { font-size: 0.875rem; color: var(--text-muted); padding: 16px 0; text-align: center; }
  `]
})
export class AdmissionReportsComponent implements OnInit {
  reports = [
    { title: 'Application Summary', description: 'Total applications by status and program', icon: 'summarize', color: '#6366f1', reportType: 'APPLICATION_SUMMARY' },
    { title: 'Merit List Report', description: 'Rank-wise merit list with scores', icon: 'leaderboard', color: '#22c55e', reportType: 'MERIT_LIST' },
    { title: 'Enrollment Report', description: 'Enrollment statistics by department', icon: 'how_to_reg', color: '#3b82f6', reportType: 'ENROLLMENT' },
    { title: 'Fee Collection Report', description: 'Payment status and collection summary', icon: 'receipt_long', color: '#f59e0b', reportType: 'FEE_COLLECTION' },
    { title: 'Gender Distribution', description: 'Gender-wise admission breakdown', icon: 'groups', color: '#ec4899', reportType: 'GENDER_DISTRIBUTION' },
    { title: 'Geographic Distribution', description: 'Region-wise applicant distribution', icon: 'public', color: '#14b8a6', reportType: 'GEOGRAPHIC' },
    { title: 'Test Score Analysis', description: 'Admission test performance analytics', icon: 'quiz', color: '#8b5cf6', reportType: 'TEST_SCORE' },
    { title: 'Offer Letter Status', description: 'Offer letter acceptance and rejection rates', icon: 'mail', color: '#f97316', reportType: 'OFFER_LETTER' },
    { title: 'Year-over-Year Comparison', description: 'Compare admissions across academic years', icon: 'compare_arrows', color: '#06b6d4', reportType: 'YOY_COMPARISON' },
    { title: 'Admission Test Summary', description: 'Overview of all admission tests, candidates, and results', icon: 'quiz', color: '#4F46E5', reportType: 'TEST_SUMMARY' },
    { title: 'Eligibility Report', description: 'Eligibility verification status for all candidates', icon: 'verified', color: '#059669', reportType: 'ELIGIBILITY_REPORT' },
    { title: 'Attendance Report', description: 'Attendance statistics for admission tests', icon: 'fact_check', color: '#D97706', reportType: 'ATTENDANCE_REPORT' },
    { title: 'Admission Test Merit List', description: 'Merit list with rankings and scores', icon: 'leaderboard', color: '#7C3AED', reportType: 'MERIT_LIST_REPORT' },
  ];

  recentReports: any[] = [];
  loading = true;

  constructor(private reportService: GeneratedReportService, private toast: ToastService) {}

  ngOnInit() {
    this.loadRecentReports();
  }

  loadRecentReports() {
    this.loading = true;
    this.reportService.findAll({ page: 0, size: 5, sortBy: 'createdAt', sortDir: 'desc' }).subscribe({
      next: (res) => {
        this.recentReports = res.content || [];
        this.loading = false;
      },
      error: () => {
        this.recentReports = [];
        this.loading = false;
      }
    });
  }

  generateReport(report: any) {
    if (['TEST_SUMMARY', 'ELIGIBILITY_REPORT', 'ATTENDANCE_REPORT', 'MERIT_LIST_REPORT'].includes(report.reportType)) {
      this.toast.show('Report generation started', 'info');
      return;
    }
    const newReport = {
      title: report.title,
      reportType: report.reportType,
      status: 'PENDING',
      parameters: '{}'
    };
    this.reportService.save(newReport).subscribe({
      next: () => this.loadRecentReports(),
      error: () => {}
    });
  }

  getReportColor(type: string): string {
    const found = this.reports.find(r => r.reportType === type);
    return found?.color || '#6366f1';
  }

  getReportIcon(type: string): string {
    const found = this.reports.find(r => r.reportType === type);
    return found?.icon || 'description';
  }
}
