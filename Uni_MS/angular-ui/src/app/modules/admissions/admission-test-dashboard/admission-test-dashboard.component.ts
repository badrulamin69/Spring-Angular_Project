import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdmissionTestDashboardService } from '../../../services/admission-test-dashboard.service';

@Component({
  selector: 'app-admission-test-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header">
      <div>
        <h2>Admission Test Dashboard</h2>
        <p class="page-sub">Overview of admission test activities</p>
      </div>
    </div>

    <div class="stats-grid">
      @for (card of statCards; track card.label) {
        <div class="stat-card" [style.border-left-color]="card.color">
          <div class="stat-icon" [style.background]="card.bg">
            <span [innerHTML]="card.icon"></span>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ card.value }}</span>
            <span class="stat-label">{{ card.label }}</span>
          </div>
        </div>
      }
    </div>

    <div class="charts-grid">
      <div class="chart-card">
        <h3>Attendance Overview</h3>
        <div class="chart-bars">
          @if (chartData?.attendanceStats) {
            <div class="bar-item">
              <div class="bar-label">Present</div>
              <div class="bar-track"><div class="bar-fill present" [style.width.%]="getPercentage(chartData.attendanceStats.present, chartData.attendanceStats.present + chartData.attendanceStats.absent + chartData.attendanceStats.late)"></div></div>
              <div class="bar-value">{{ chartData.attendanceStats.present }}</div>
            </div>
            <div class="bar-item">
              <div class="bar-label">Absent</div>
              <div class="bar-track"><div class="bar-fill absent" [style.width.%]="getPercentage(chartData.attendanceStats.absent, chartData.attendanceStats.present + chartData.attendanceStats.absent + chartData.attendanceStats.late)"></div></div>
              <div class="bar-value">{{ chartData.attendanceStats.absent }}</div>
            </div>
            <div class="bar-item">
              <div class="bar-label">Late</div>
              <div class="bar-track"><div class="bar-fill late" [style.width.%]="getPercentage(chartData.attendanceStats.late, chartData.attendanceStats.present + chartData.attendanceStats.absent + chartData.attendanceStats.late)"></div></div>
              <div class="bar-value">{{ chartData.attendanceStats.late }}</div>
            </div>
          }
        </div>
      </div>
      <div class="chart-card">
        <h3>Pass Rate</h3>
        <div class="chart-bars">
          @if (chartData?.passRate) {
            <div class="bar-item">
              <div class="bar-label">Passed</div>
              <div class="bar-track"><div class="bar-fill present" [style.width.%]="getPercentage(chartData.passRate.passed, chartData.passRate.total)"></div></div>
              <div class="bar-value">{{ chartData.passRate.passed }}</div>
            </div>
            <div class="bar-item">
              <div class="bar-label">Failed</div>
              <div class="bar-track"><div class="bar-fill absent" [style.width.%]="getPercentage(chartData.passRate.failed, chartData.passRate.total)"></div></div>
              <div class="bar-value">{{ chartData.passRate.failed }}</div>
            </div>
          }
        </div>
      </div>
    </div>

    <div class="quick-actions">
      <h3>Quick Actions</h3>
      <div class="actions-grid">
        <a routerLink="/admissions/tests" class="action-card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="#4F46E5" stroke-width="2" stroke-linecap="round"/></svg>
          <span>Manage Tests</span>
        </a>
        <a routerLink="/admissions/seat-plan" class="action-card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M4 5a1 1 0 011-1h4a1 1 0 011 1v5a1 1 0 01-1 1H5a1 1 0 01-1-1V5zm10 0a1 1 0 011-1h4a1 1 0 011 1v5a1 1 0 01-1 1h-4a1 1 0 01-1-1V5zM4 14a1 1 0 011-1h4a1 1 0 011 1v5a1 1 0 01-1 1H5a1 1 0 01-1-1v-5zm10 0a1 1 0 011-1h4a1 1 0 011 1v5a1 1 0 01-1 1h-4a1 1 0 01-1-1v-5z" stroke="#059669" stroke-width="2" stroke-linecap="round"/></svg>
          <span>Seat Plan</span>
        </a>
        <a routerLink="/admissions/admit-cards" class="action-card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M15 9h3.75M15 12h3.75M15 15h3.75M4.5 19.5h15a2.25 2.25 0 002.25-2.25V6.75A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25v10.5A2.25 2.25 0 004.5 19.5zm6-10.125a1.875 1.875 0 11-3.75 0 1.875 1.875 0 013.75 0zm1.294 6.336a6.721 6.721 0 01-3.17.789 6.721 6.721 0 01-3.168-.789 3.376 3.376 0 016.338 0z" stroke="#b38600" stroke-width="2" stroke-linecap="round"/></svg>
          <span>Admit Cards</span>
        </a>
        <a routerLink="/admissions/test-attendance" class="action-card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" stroke="#7C3AED" stroke-width="2" stroke-linecap="round"/></svg>
          <span>Attendance</span>
        </a>
        <a routerLink="/admissions/question-bank" class="action-card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z" stroke="#bd2130" stroke-width="2" stroke-linecap="round"/></svg>
          <span>Question Bank</span>
        </a>
        <a routerLink="/admissions/exam-centers" class="action-card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" stroke="#0891B2" stroke-width="2"/><path d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" stroke="#0891B2" stroke-width="2"/></svg>
          <span>Exam Centers</span>
        </a>
      </div>
    </div>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary, #1e293b); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted, #64748b); }
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 1rem; margin-bottom: 1.5rem; }
    .stat-card { background: var(--bg-card, #fff); border-radius: 12px; padding: 1.25rem; display: flex; align-items: center; gap: 1rem; border-left: 4px solid; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
    .stat-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.25rem; flex-shrink: 0; }
    .stat-info { display: flex; flex-direction: column; }
    .stat-value { font-size: 1.5rem; font-weight: 700; color: var(--text-primary, #1e293b); }
    .stat-label { font-size: 0.8125rem; color: var(--text-muted, #64748b); }
    .charts-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem; }
    .chart-card { background: var(--bg-card, #fff); border-radius: 12px; padding: 1.25rem; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
    .chart-card h3 { margin: 0 0 1rem; font-size: 1rem; color: var(--text-primary, #1e293b); }
    .bar-item { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.75rem; }
    .bar-label { width: 60px; font-size: 0.8125rem; color: var(--text-muted, #64748b); }
    .bar-track { flex: 1; height: 24px; background: #f1f5f9; border-radius: 6px; overflow: hidden; }
    .bar-fill { height: 100%; border-radius: 6px; transition: width 0.5s ease; min-width: 2px; }
    .bar-fill.present { background: #059669; }
    .bar-fill.absent { background: #dc3545; }
    .bar-fill.late { background: #b38600; }
    .bar-value { width: 40px; text-align: right; font-weight: 600; font-size: 0.875rem; color: var(--text-primary, #1e293b); }
    .quick-actions { margin-bottom: 1.5rem; }
    .quick-actions h3 { margin: 0 0 1rem; font-size: 1rem; color: var(--text-primary, #1e293b); }
    .actions-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 0.75rem; }
    .action-card { display: flex; flex-direction: column; align-items: center; gap: 0.5rem; padding: 1.25rem; background: var(--bg-card, #fff); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.06); text-decoration: none; color: var(--text-primary, #1e293b); transition: all 0.15s; border: 1px solid transparent; }
    .action-card:hover { border-color: var(--brand-color, #4F46E5); transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
    .action-card span { font-size: 0.875rem; font-weight: 500; }
    @media (max-width: 768px) { .charts-grid { grid-template-columns: 1fr; } .stats-grid { grid-template-columns: 1fr 1fr; } }
    @media (max-width: 480px) { .stats-grid { grid-template-columns: 1fr; } }
  `]
})
export class AdmissionTestDashboardComponent implements OnInit {
  stats: any = {};
  chartData: any = null;
  statCards: any[] = [];

  constructor(private dashboardService: AdmissionTestDashboardService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.statCards = [
          { label: 'Total Tests', value: data.totalTests || 0, color: '#4F46E5', bg: 'rgba(79,70,229,0.1)', icon: '&#128203;' },
          { label: 'Total Applicants', value: data.totalApplicants || 0, color: '#0891B2', bg: 'rgba(8,145,178,0.1)', icon: '&#128101;' },
          { label: 'Admit Cards', value: data.admitCardsGenerated || 0, color: '#b38600', bg: 'rgba(217,119,6,0.1)', icon: '&#127915;' },
          { label: 'Present', value: data.presentCandidates || 0, color: '#059669', bg: 'rgba(5,150,105,0.1)', icon: '&#9989;' },
          { label: 'Absent', value: data.absentCandidates || 0, color: '#dc3545', bg: 'rgba(239,68,68,0.1)', icon: '&#10060;' },
          { label: 'Passed', value: data.passedCandidates || 0, color: '#059669', bg: 'rgba(5,150,105,0.1)', icon: '&#127942;' },
          { label: 'Failed', value: data.failedCandidates || 0, color: '#bd2130', bg: 'rgba(220,38,38,0.1)', icon: '&#10060;' },
          { label: 'Pending Results', value: data.pendingResults || 0, color: '#7C3AED', bg: 'rgba(124,58,237,0.1)', icon: '&#9203;' }
        ];
      }
    });
    this.dashboardService.getChartData().subscribe({
      next: (data) => { this.chartData = data; }
    });
  }

  getPercentage(value: number, total: number): number {
    if (!total || total === 0) return 0;
    return Math.round((value / total) * 100);
  }
}
