import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentReportService } from '../../../services/payment-report.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-payment-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Payment Reports</h2>
        <p class="page-sub">Financial analytics and reports</p>
      </div>
      <div class="header-filters">
        <select class="form-control-sm" [(ngModel)]="reportType" (change)="loadReport()">
          <option value="daily">Daily</option>
          <option value="monthly">Monthly</option>
          <option value="yearly">Yearly</option>
        </select>
        @if (reportType === 'daily') {
          <input type="date" class="form-control-sm" [(ngModel)]="selectedDate" (change)="loadReport()">
        }
        @if (reportType === 'monthly') {
          <select class="form-control-sm" [(ngModel)]="selectedMonth" (change)="loadReport()">
            @for (m of months; track m.value) {
              <option [value]="m.value">{{ m.label }}</option>
            }
          </select>
          <select class="form-control-sm" [(ngModel)]="selectedYear" (change)="loadReport()">
            @for (y of years; track y) {
              <option [value]="y">{{ y }}</option>
            }
          </select>
        }
        @if (reportType === 'yearly') {
          <select class="form-control-sm" [(ngModel)]="selectedYear" (change)="loadReport()">
            @for (y of years; track y) {
              <option [value]="y">{{ y }}</option>
            }
          </select>
        }
      </div>
    </div>

    @if (loading) {
      <div class="loading-state">
        <div class="spinner"></div>
        <span>Loading report...</span>
      </div>
    } @else {
      <div class="stats-grid">
        <div class="stat-card stat-revenue">
          <div class="stat-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ reportData?.totalRevenue | number:'1.0-0' }}</span>
            <span class="stat-label">Total Revenue</span>
          </div>
        </div>
        <div class="stat-card stat-today">
          <div class="stat-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 8v4l3 3M3 12a9 9 0 1018 0 9 9 0 00-18 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ reportData?.todayCollection | number:'1.0-0' }}</span>
            <span class="stat-label">Today's Collection</span>
          </div>
        </div>
        <div class="stat-card stat-pending">
          <div class="stat-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ reportData?.pendingPayments | number:'1.0-0' }}</span>
            <span class="stat-label">Pending Payments</span>
          </div>
        </div>
        <div class="stat-card stat-failed">
          <div class="stat-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ reportData?.failedPayments | number:'1.0-0' }}</span>
            <span class="stat-label">Failed Payments</span>
          </div>
        </div>
      </div>

      <div class="report-grid">
        <div class="report-card">
          <h3>Payment Method Breakdown</h3>
          <div class="table-container">
            <table class="report-table">
              <thead>
                <tr>
                  <th>Method</th>
                  <th>Count</th>
                  <th>Amount</th>
                  <th>Percentage</th>
                </tr>
              </thead>
              <tbody>
                @if (reportData?.methodBreakdown && reportData.methodBreakdown.length > 0) {
                  @for (method of reportData.methodBreakdown; track method.method) {
                    <tr>
                      <td>
                        <span class="method-badge" [attr.data-method]="method.method">{{ method.method }}</span>
                      </td>
                      <td>{{ method.count }}</td>
                      <td>{{ method.amount | number:'1.0-0' }}</td>
                      <td>
                        <div class="progress-bar">
                          <div class="progress-fill" [style.width.%]="method.percentage"></div>
                          <span>{{ method.percentage | number:'1.1-1' }}%</span>
                        </div>
                      </td>
                    </tr>
                  }
                } @else {
                  <tr>
                    <td colspan="4" class="empty-cell">No data available</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>

        <div class="report-card">
          <h3>Status Overview</h3>
          <div class="status-list">
            @if (reportData?.statusBreakdown && reportData.statusBreakdown.length > 0) {
              @for (status of reportData.statusBreakdown; track status.status) {
                <div class="status-item">
                  <div class="status-info">
                    <span class="status-dot" [attr.data-status]="status.status"></span>
                    <span class="status-name">{{ status.status }}</span>
                  </div>
                  <div class="status-stats">
                    <span class="status-count">{{ status.count }}</span>
                    <span class="status-amount">{{ status.amount | number:'1.0-0' }}</span>
                  </div>
                </div>
              }
            } @else {
              <div class="empty-cell">No data available</div>
            }
          </div>
        </div>
      </div>

      @if (reportData?.monthlyTrend && reportData.monthlyTrend.length > 0) {
        <div class="report-card full-width">
          <h3>Monthly Trend</h3>
          <div class="table-container">
            <table class="report-table">
              <thead>
                <tr>
                  <th>Month</th>
                  <th>Payments</th>
                  <th>Collection</th>
                  <th>Pending</th>
                  <th>Failed</th>
                </tr>
              </thead>
              <tbody>
                @for (month of reportData.monthlyTrend; track month.month) {
                  <tr>
                    <td>{{ month.month }}</td>
                    <td>{{ month.totalPayments }}</td>
                    <td class="text-green">{{ month.collection | number:'1.0-0' }}</td>
                    <td class="text-yellow">{{ month.pending | number:'1.0-0' }}</td>
                    <td class="text-red">{{ month.failed | number:'1.0-0' }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; flex-wrap: wrap; gap: 12px; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .header-filters { display: flex; gap: 8px; align-items: center; }
    .form-control-sm { padding: 6px 10px; border: 1px solid var(--border-color); border-radius: 6px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.8125rem; }

    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 1.25rem; }
    .stat-card { display: flex; align-items: center; gap: 16px; padding: 20px; background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; }
    .stat-icon { width: 52px; height: 52px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .stat-revenue .stat-icon { background: #ede9fe; color: #7c3aed; }
    .stat-today .stat-icon { background: #dcfce7; color: #16a34a; }
    .stat-pending .stat-icon { background: #fef3c7; color: #d97706; }
    .stat-failed .stat-icon { background: #fee2e2; color: #dc2626; }
    .stat-content { display: flex; flex-direction: column; gap: 2px; }
    .stat-value { font-size: 1.5rem; font-weight: 700; color: var(--text-primary); }
    .stat-label { font-size: 0.8125rem; color: var(--text-muted); }

    .report-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
    .report-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 20px; }
    .report-card.full-width { grid-column: 1 / -1; }
    .report-card h3 { margin: 0 0 16px; font-size: 1rem; color: var(--text-primary); font-weight: 600; }

    .table-container { overflow-x: auto; }
    .report-table { width: 100%; border-collapse: collapse; font-size: 0.8125rem; }
    .report-table th, .report-table td { padding: 10px 12px; text-align: left; border-bottom: 1px solid var(--border-color); color: var(--text-primary); }
    .report-table th { background: var(--bg-tertiary); font-weight: 600; color: var(--text-secondary); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; }

    .method-badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; }
    .method-badge[data-method="CASH"] { background: #dcfce7; color: #166534; }
    .method-badge[data-method="CARD"] { background: #dbeafe; color: #1d4ed8; }
    .method-badge[data-method="BANK_TRANSFER"] { background: #ede9fe; color: #7c3aed; }
    .method-badge[data-method="ONLINE"] { background: #fef3c7; color: #92400e; }
    .method-badge[data-method="CHECK"] { background: #f3f4f6; color: #6b7280; }

    .progress-bar { display: flex; align-items: center; gap: 8px; }
    .progress-fill { height: 6px; background: var(--brand-color); border-radius: 3px; min-width: 20px; max-width: 80px; }

    .status-list { display: flex; flex-direction: column; gap: 10px; }
    .status-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 12px; background: var(--bg-primary); border-radius: 8px; }
    .status-info { display: flex; align-items: center; gap: 10px; }
    .status-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
    .status-dot[data-status="COMPLETED"] { background: #16a34a; }
    .status-dot[data-status="APPROVED"] { background: #16a34a; }
    .status-dot[data-status="PENDING"] { background: #d97706; }
    .status-dot[data-status="FAILED"] { background: #dc2626; }
    .status-dot[data-status="REJECTED"] { background: #dc2626; }
    .status-dot[data-status="REFUNDED"] { background: #2563eb; }
    .status-name { font-size: 0.875rem; color: var(--text-primary); font-weight: 500; }
    .status-stats { display: flex; flex-direction: column; align-items: flex-end; }
    .status-count { font-size: 0.875rem; font-weight: 600; color: var(--text-primary); }
    .status-amount { font-size: 0.75rem; color: var(--text-muted); }

    .text-green { color: #16a34a; }
    .text-yellow { color: #d97706; }
    .text-red { color: #dc2626; }

    .empty-cell { text-align: center; padding: 2rem 1rem !important; color: var(--text-muted); }

    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 3rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }

    @media (max-width: 768px) {
      .stats-grid { grid-template-columns: repeat(2, 1fr); }
      .report-grid { grid-template-columns: 1fr; }
    }
    @media (max-width: 480px) {
      .stats-grid { grid-template-columns: 1fr; }
      .header-filters { flex-wrap: wrap; }
    }
  `]
})
export class PaymentReportsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  loading = true;
  reportType = 'daily';
  selectedDate = '';
  selectedMonth = new Date().getMonth() + 1;
  selectedYear = new Date().getFullYear();

  reportData: any = null;

  months = [
    { value: 1, label: 'January' }, { value: 2, label: 'February' },
    { value: 3, label: 'March' }, { value: 4, label: 'April' },
    { value: 5, label: 'May' }, { value: 6, label: 'June' },
    { value: 7, label: 'July' }, { value: 8, label: 'August' },
    { value: 9, label: 'September' }, { value: 10, label: 'October' },
    { value: 11, label: 'November' }, { value: 12, label: 'December' }
  ];

  years = [2023, 2024, 2025, 2026];

  constructor(
    private reportService: PaymentReportService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    const today = new Date();
    this.selectedDate = today.toISOString().split('T')[0];
    this.loadReport();
  }

  loadReport() {
    this.loading = true;

    let request;

    switch (this.reportType) {
      case 'daily':
        request = this.reportService.getDailyReport(this.selectedDate);
        break;
      case 'monthly':
        request = this.reportService.getMonthlyReport(this.selectedMonth, this.selectedYear);
        break;
      case 'yearly':
        request = this.reportService.getYearlyReport(this.selectedYear);
        break;
      default:
        request = this.reportService.getDailyReport(this.selectedDate);
    }

    request.subscribe({
      next: (data) => {
        this.reportData = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load report data');
      }
    });
  }
}
