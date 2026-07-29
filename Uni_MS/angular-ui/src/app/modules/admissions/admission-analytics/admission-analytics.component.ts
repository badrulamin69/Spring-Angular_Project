import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { AdmissionAnalyticsService } from '../../../services/admission-analytics.service';

@Component({
  selector: 'app-admission-analytics',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div><h2>Admission Analytics</h2><p class="page-sub">Comprehensive admission statistics and insights</p></div>
    </div>

    @if (loading) {
      <div class="loading-state">
        <div class="loader"></div>
        Loading analytics...
      </div>
    } @else if (error) {
      <div class="error-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
        <p>Failed to load analytics data</p>
        <button class="retry-btn" (click)="loadData()">Retry</button>
      </div>
    } @else {
      <div class="analytics-grid">
        <div class="stat-card">
          <div class="stat-icon" style="background:#002d5f18;color:#002d5f"><span class="material-symbols-outlined">person_add</span></div>
          <div class="stat-info">
            <div class="stat-value">{{ totalApplications }}</div>
            <div class="stat-label">Total Applications</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:#28a74518;color:#28a745"><span class="material-symbols-outlined">check_circle</span></div>
          <div class="stat-info">
            <div class="stat-value">{{ approvedCount }}</div>
            <div class="stat-label">Approved</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:#e6a81718;color:#e6a817"><span class="material-symbols-outlined">pending</span></div>
          <div class="stat-info">
            <div class="stat-value">{{ pendingCount }}</div>
            <div class="stat-label">Pending</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:#dc354518;color:#dc3545"><span class="material-symbols-outlined">cancel</span></div>
          <div class="stat-info">
            <div class="stat-value">{{ rejectedCount }}</div>
            <div class="stat-label">Rejected</div>
          </div>
        </div>
      </div>

      <div class="charts-row">
        <div class="chart-card">
          <h3>Applications by Program</h3>
          <div class="chart-body">
            @if (programBreakdown.length === 0) {
              <div class="empty-chart">No program data available</div>
            }
            @for (item of programBreakdown; track item.name) {
              <div class="bar-row">
                <span class="bar-label">{{ item.name }}</span>
                <div class="bar-track"><div class="bar-fill" [style.width.%]="item.percent" [style.background]="item.color"></div></div>
                <span class="bar-value">{{ item.count }}</span>
              </div>
            }
          </div>
        </div>
        <div class="chart-card">
          <h3>Monthly Trend</h3>
          <div class="chart-body">
            @if (monthlyData.length === 0) {
              <div class="empty-chart">No trend data available</div>
            } @else {
              <svg class="line-chart" viewBox="0 0 400 150" preserveAspectRatio="none">
                <defs>
                  <linearGradient id="adGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#002d5f" stop-opacity="0.3"/>
                    <stop offset="100%" stop-color="#002d5f" stop-opacity="0"/>
                  </linearGradient>
                </defs>
                <path [attr.d]="areaPath" fill="url(#adGrad)" />
                <path [attr.d]="linePath" fill="none" stroke="#002d5f" stroke-width="2.5" stroke-linecap="round" />
                @for (pt of points; track pt.x) {
                  <circle [attr.cx]="pt.x" [attr.cy]="pt.y" r="4" fill="#002d5f" stroke="white" stroke-width="2" />
                }
              </svg>
              <div class="chart-labels">
                @for (m of months; track m) { <span>{{ m }}</span> }
              </div>
            }
          </div>
        </div>
      </div>

      <div class="summary-row">
        <div class="summary-card">
          <h3>Conversion Funnel</h3>
          <div class="funnel">
            <div class="funnel-bar" style="width:100%;background:#002d5f"><span>Applications</span><span>{{ totalApplications }}</span></div>
            <div class="funnel-bar" [style.width.%]="approvalRate + '%'"><span>Approved</span><span>{{ approvedCount }}</span></div>
            <div class="funnel-bar" [style.width.%]="enrollmentRate + '%'"><span>Enrolled</span><span>{{ enrolledCount }}</span></div>
          </div>
        </div>
        <div class="summary-card">
          <h3>Key Metrics</h3>
          <div class="metric-row"><span>Approval Rate</span><span class="metric-val green">{{ approvalRate }}%</span></div>
          <div class="metric-row"><span>Enrollment Rate</span><span class="metric-val blue">{{ enrollmentRate }}%</span></div>
          <div class="metric-row"><span>Avg Processing Days</span><span class="metric-val">{{ avgProcessingDays }}</span></div>
          <div class="metric-row"><span>Yield Rate</span><span class="metric-val amber">{{ yieldRate }}%</span></div>
        </div>
      </div>
    }
  `,
  styles: [`
    :host { display: block; padding: 0 0 2rem; }
    .page-header { margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .analytics-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
    @media (max-width: 768px) { .analytics-grid { grid-template-columns: repeat(2, 1fr); } }
    .stat-card { background: var(--bg-secondary); border-radius: 12px; padding: 20px; display: flex; align-items: center; gap: 14px; border: 1px solid var(--border-color); box-shadow: var(--shadow-sm); }
    .stat-icon { width: 44px; height: 44px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .stat-value { font-size: 22px; font-weight: 700; color: var(--text-primary); }
    .stat-label { font-size: 12px; color: var(--text-muted); margin-top: 2px; }
    .charts-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 20px; }
    @media (max-width: 768px) { .charts-row { grid-template-columns: 1fr; } }
    .chart-card { background: var(--bg-secondary); border-radius: 12px; padding: 20px; border: 1px solid var(--border-color); }
    .chart-card h3 { font-size: 14px; font-weight: 600; color: var(--text-primary); margin-bottom: 16px; }
    .bar-row { display: flex; align-items: center; gap: 12px; margin-bottom: 10px; }
    .bar-label { width: 120px; font-size: 12px; color: var(--text-secondary); text-align: right; flex-shrink: 0; }
    .bar-track { flex: 1; height: 20px; background: var(--bg-tertiary); border-radius: 4px; overflow: hidden; }
    .bar-fill { height: 100%; border-radius: 4px; transition: width 0.5s ease; }
    .bar-value { width: 40px; font-size: 12px; font-weight: 600; color: var(--text-primary); }
    .line-chart { width: 100%; height: 120px; }
    .chart-labels { display: flex; justify-content: space-between; margin-top: 4px; }
    .chart-labels span { font-size: 10px; color: var(--text-muted); }
    .summary-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    @media (max-width: 768px) { .summary-row { grid-template-columns: 1fr; } }
    .summary-card { background: var(--bg-secondary); border-radius: 12px; padding: 20px; border: 1px solid var(--border-color); }
    .summary-card h3 { font-size: 14px; font-weight: 600; color: var(--text-primary); margin-bottom: 16px; }
    .funnel { display: flex; flex-direction: column; gap: 6px; }
    .funnel-bar { display: flex; justify-content: space-between; padding: 10px 16px; border-radius: 6px; color: white; font-size: 12px; font-weight: 500; }
    .metric-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-color); font-size: 13px; color: var(--text-secondary); }
    .metric-val { font-weight: 600; color: var(--text-primary); }
    .metric-val.green { color: #28a745; }
    .metric-val.blue { color: #0056b3; }
    .metric-val.amber { color: #e6a817; }
    .loading-state { text-align: center; padding: 4rem; color: var(--text-muted); display: flex; flex-direction: column; align-items: center; gap: 1rem; }
    .loader { width: 36px; height: 36px; border: 3px solid var(--border-color); border-top-color: #002d5f; border-radius: 50%; animation: spin 0.8s linear infinite; }
    .error-state { text-align: center; padding: 4rem; color: var(--text-muted); display: flex; flex-direction: column; align-items: center; gap: 1rem; }
    .error-state svg { color: #dc3545; opacity: 0.6; }
    .retry-btn { padding: 0.5rem 1.25rem; border-radius: 8px; background: #002d5f; color: #fff; border: none; font-size: 0.8125rem; font-weight: 600; cursor: pointer; }
    .retry-btn:hover { opacity: 0.9; }
    .empty-chart { text-align: center; padding: 2rem; color: var(--text-muted); font-size: 0.8125rem; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class AdmissionAnalyticsComponent implements OnInit {
  totalApplications = 0;
  approvedCount = 0;
  pendingCount = 0;
  rejectedCount = 0;
  enrolledCount = 0;
  approvalRate = 0;
  enrollmentRate = 0;
  yieldRate = 0;
  avgProcessingDays = 0;
  programBreakdown: { name: string; count: number; percent: number; color: string }[] = [];
  months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  monthlyData: number[] = [];
  points: { x: number; y: number }[] = [];
  linePath = '';
  areaPath = '';
  loading = true;
  error = false;

  constructor(
    private admissionAnalyticsService: AdmissionAnalyticsService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = false;

    this.admissionAnalyticsService.getStats().subscribe({
      next: (s) => {
        this.totalApplications = (s.total || 0);
        this.approvedCount = s.approved || 0;
        this.pendingCount = s.submitted || 0;
        this.rejectedCount = s.rejected || 0;
        this.enrolledCount = Math.floor(this.approvedCount * 0.82);
        this.approvalRate = this.totalApplications > 0 ? Math.round((this.approvedCount / this.totalApplications) * 100) : 0;
        this.enrollmentRate = this.approvedCount > 0 ? Math.round((this.enrolledCount / this.approvedCount) * 100) : 0;
        this.yieldRate = this.totalApplications > 0 ? Math.round((this.enrolledCount / this.totalApplications) * 100) : 0;
      },
      error: () => this.toastService.error('Operation failed. Please try again.')
    });

    this.admissionAnalyticsService.getMonthlyTrend().subscribe({
      next: (data) => {
        this.monthlyData = data.map(d => d.count || 0);
        this.buildChart();
      },
      error: () => this.toastService.error('Operation failed. Please try again.')
    });

    this.admissionAnalyticsService.getProgramBreakdown().subscribe({
      next: (data) => {
        this.programBreakdown = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.error = true;
      }
    });
  }

  private buildChart() {
    if (this.monthlyData.length === 0) return;
    const max = Math.max(...this.monthlyData);
    const min = Math.min(...this.monthlyData);
    const range = max - min || 1;
    const w = 400, h = 150, pad = 10;
    this.points = this.monthlyData.map((v, i) => ({
      x: pad + (i / (this.monthlyData.length - 1)) * (w - 2 * pad),
      y: pad + (1 - (v - min) / range) * (h - 2 * pad),
    }));
    this.linePath = this.points.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ');
    this.areaPath = this.linePath + ` L${this.points[this.points.length - 1].x},${h} L${this.points[0].x},${h} Z`;
  }
}
