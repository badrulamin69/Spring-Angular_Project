import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SecurityDashboardService } from '../../../services/security-dashboard.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-security-dashboard',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h1>Security Dashboard</h1>
        <p class="page-sub">Monitor security metrics, user activity, and system health</p>
      </div>
      <button class="btn btn-sm btn-outline" (click)="loadAll()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/><path d="M11 1v2.5h-2.5M3 13v-2.5h2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        Refresh
      </button>
    </div>

    @if (loading) {
      <div class="loading-state">
        <div class="spinner"></div>
        <span>Loading dashboard...</span>
      </div>
    } @else {
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalUsers ?? 0 }}</div>
          <div class="stat-label">Total Users</div>
        </div>
        <div class="stat-card highlight">
          <div class="stat-value">{{ stats.activeUsers ?? 0 }}</div>
          <div class="stat-label">Active Users</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.inactiveUsers ?? 0 }}</div>
          <div class="stat-label">Inactive Users</div>
        </div>
        <div class="stat-card highlight">
          <div class="stat-value">{{ stats.onlineUsers ?? 0 }}</div>
          <div class="stat-label">Online Users</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalRoles ?? 0 }}</div>
          <div class="stat-label">Roles</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalPermissions ?? 0 }}</div>
          <div class="stat-label">Permissions</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.failedLogins ?? 0 }}</div>
          <div class="stat-label">Failed Logins</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.lockedAccounts ?? 0 }}</div>
          <div class="stat-label">Locked Accounts</div>
        </div>
        <div class="stat-card highlight">
          <div class="stat-value">{{ stats.activeSessions ?? 0 }}</div>
          <div class="stat-label">Active Sessions</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.userOverrides ?? 0 }}</div>
          <div class="stat-label">User Overrides</div>
        </div>
      </div>

      <div class="section-grid">
        <div class="section-card">
          <h3>Login Statistics</h3>
          <div class="login-stats">
            <div class="login-stat-row">
              <span class="login-stat-label">Successful Logins</span>
              <span class="login-stat-value success">{{ loginStats.successfulLogins ?? 0 }}</span>
            </div>
            <div class="login-stat-row">
              <span class="login-stat-label">Failed Logins</span>
              <span class="login-stat-value danger">{{ loginStats.failedLogins ?? 0 }}</span>
            </div>
            <div class="login-stat-row">
              <span class="login-stat-label">Logins Today</span>
              <span class="login-stat-value">{{ loginStats.loginsToday ?? 0 }}</span>
            </div>
            <div class="login-stat-row">
              <span class="login-stat-label">Logins This Week</span>
              <span class="login-stat-value">{{ loginStats.loginsThisWeek ?? 0 }}</span>
            </div>
          </div>
        </div>

        <div class="section-card activity-card">
          <h3>Recent Activity</h3>
          @if (recentActivities.length === 0) {
            <div class="empty-activity">No recent activity</div>
          } @else {
            <div class="table-scroll">
              <table>
                <thead>
                  <tr>
                    <th>User</th>
                    <th>Action</th>
                    <th>Module</th>
                    <th>Description</th>
                    <th>Time</th>
                  </tr>
                </thead>
                <tbody>
                  @for (activity of recentActivities; track activity.id ?? $index) {
                    <tr>
                      <td>{{ activity.username || activity.user || 'System' }}</td>
                      <td><span class="badge badge-info">{{ activity.action }}</span></td>
                      <td>{{ activity.module || '-' }}</td>
                      <td>{{ activity.description || '-' }}</td>
                      <td>{{ activity.createdAt || activity.timestamp || '-' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .page-header h1 { margin: 0; font-size: 1.5rem; font-weight: 700; color: var(--text-primary); }
    .page-sub { margin: 4px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .btn { padding: 6px 12px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8125rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-sm { padding: 5px 10px; font-size: 0.8125rem; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); color: var(--text-primary); }
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem; }
    .stat-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.25rem; }
    .stat-card .stat-value { font-size: 2rem; font-weight: 700; color: var(--text-primary); }
    .stat-card .stat-label { font-size: 0.875rem; color: var(--text-muted); margin-top: 4px; }
    .stat-card.highlight { border-color: var(--brand-color); background: rgba(99, 102, 241, 0.05); }
    .section-grid { display: grid; grid-template-columns: 350px 1fr; gap: 1.5rem; align-items: start; }
    .section-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.25rem; }
    .section-card h3 { margin: 0 0 1rem; font-size: 1rem; font-weight: 600; color: var(--text-primary); }
    .login-stats { display: flex; flex-direction: column; gap: 0.75rem; }
    .login-stat-row { display: flex; justify-content: space-between; align-items: center; padding: 10px 14px; background: var(--bg-primary); border-radius: 8px; }
    .login-stat-label { font-size: 0.875rem; color: var(--text-secondary); }
    .login-stat-value { font-size: 1.125rem; font-weight: 700; color: var(--text-primary); }
    .login-stat-value.success { color: #1e7e34; }
    .login-stat-value.danger { color: #bd2130; }
    .activity-card { max-height: 500px; overflow: hidden; display: flex; flex-direction: column; }
    .activity-card .table-scroll { overflow-x: auto; flex: 1; }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 10px 14px; text-align: left; border-bottom: 1px solid var(--border-color); color: var(--text-primary); white-space: nowrap; font-size: 0.8125rem; }
    th { background: var(--bg-tertiary); font-weight: 600; color: var(--text-secondary); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; position: sticky; top: 0; }
    tr:hover { background: var(--bg-hover); }
    .badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; }
    .badge-info { background: #dbeafe; color: #002d5f; }
    .empty-activity { text-align: center; padding: 2rem; color: var(--text-muted); font-size: 0.875rem; }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 4rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    @media (max-width: 900px) { .section-grid { grid-template-columns: 1fr; } }
  `]
})
export class SecurityDashboardComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  stats: any = {};
  recentActivities: any[] = [];
  loginStats: any = {};
  loading = true;

  constructor(
    private dashboardService: SecurityDashboardService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    this.loading = true;
    this.dashboardService.getStats().subscribe({
      next: (data) => { this.stats = data?.data || data || {}; },
      error: () => { this.toastService.error('Failed to load stats'); }
    });
    this.dashboardService.getRecentActivities().subscribe({
      next: (data) => { this.recentActivities = data || []; },
      error: () => { this.toastService.error('Failed to load recent activities'); }
    });
    this.dashboardService.getLoginStats().subscribe({
      next: (data) => {
        this.loginStats = data?.data || data || {};
        this.loading = false;
      },
      error: () => {
        this.toastService.error('Failed to load login stats');
        this.loading = false;
      }
    });
  }
}
