import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subject, takeUntil, interval, forkJoin } from 'rxjs';
import { DashboardService } from '../../services/dashboard.service';
import { CurrentUserService } from '../../services/current-user.service';
import { ToastService } from '../../shared/toast/toast.component';
import { ActivityLogService } from '../../services/activity-log.service';
import { NotificationService } from '../../services/notification.service';

interface StatCard {
  label: string;
  value: number | string;
  icon: string;
  colorClass: string;
  route?: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-container">
      <div class="page-header">
        <div>
          <h2>Welcome back, {{ userName() }}</h2>
          <p class="page-sub">Here's what's happening across your university today</p>
        </div>
        <div class="header-actions">
          <div class="header-clock">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            {{ currentTime }}
          </div>
          <button class="refresh-btn" (click)="refreshDashboard()" [class.spinning]="refreshing">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>
          </button>
        </div>
      </div>

      @if (loading) {
        <div class="loading-state">
          <div class="loader"></div>
          Loading dashboard...
        </div>
      } @else if (error) {
        <div class="error-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
          <p>Failed to load dashboard data</p>
          <button class="retry-btn" (click)="loadDashboard()">Retry</button>
        </div>
      } @else {
        <div class="section-group">
          <div class="section-header">
            <span class="section-icon icon-overview">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            </span>
            <h3>{{ roleSectionTitle() }}</h3>
          </div>
          <div class="stats-grid">
            @for (stat of primaryStats(); track stat.label) {
              <a [routerLink]="stat.route || '/'" class="stat-card card-3d" [ngClass]="stat.colorClass">
                <div class="card-inner">
                  <div class="stat-icon-wrap" [ngClass]="'icon-' + getColorName(stat.colorClass)">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" [innerHTML]="stat.icon"></svg>
                  </div>
                  <div class="stat-content">
                    <div class="stat-top">
                      <span class="stat-value">{{ stat.value }}</span>
                      <span class="live-badge">Live</span>
                    </div>
                    <span class="stat-label">{{ stat.label }}</span>
                  </div>
                </div>
                <div class="card-shine"></div>
              </a>
            }
          </div>
        </div>

        @if (secondaryStats().length) {
          <div class="section-group">
            <div class="section-header">
              <span class="section-icon icon-secondary">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              </span>
              <h3>ADDITIONAL METRICS</h3>
            </div>
            <div class="stats-grid">
              @for (stat of secondaryStats(); track stat.label) {
                <div class="stat-card card-3d" [ngClass]="stat.colorClass">
                  <div class="card-inner">
                    <div class="stat-icon-wrap" [ngClass]="'icon-' + getColorName(stat.colorClass)">
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" [innerHTML]="stat.icon"></svg>
                    </div>
                    <div class="stat-content">
                      <div class="stat-top">
                        <span class="stat-value">{{ stat.value }}</span>
                        <span class="live-badge">Live</span>
                      </div>
                      <span class="stat-label">{{ stat.label }}</span>
                    </div>
                  </div>
                  <div class="card-shine"></div>
                </div>
              }
            </div>
          </div>
        }

        <div class="bottom-section">
          <div class="panel activities-panel">
            <div class="panel-header">
              <h3>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                Recent Activities
              </h3>
            </div>
            <div class="panel-body">
              @for (act of recentActivities; track act.title) {
                <div class="activity-item">
                  <div class="activity-icon-wrap" [style.background]="act.color + '18'" [style.color]="act.color">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">{{ act.title }}</div>
                    <div class="activity-desc">{{ act.description }}</div>
                    <div class="activity-meta">
                      <span class="activity-time">{{ act.time }}</span>
                    </div>
                  </div>
                </div>
              }
              @if (recentActivities.length === 0) {
                <div class="empty-state">No recent activities</div>
              }
            </div>
          </div>

          <div class="panel quick-panel">
            <div class="panel-header">
              <h3>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
                Quick Actions
              </h3>
            </div>
            <div class="quick-links">
              @for (action of quickActions(); track action.label) {
                <a [routerLink]="action.route" class="quick-link">
                  <span class="quick-link-icon" [style.color]="action.color">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" [innerHTML]="action.icon"></svg>
                  </span>
                  {{ action.label }}
                </a>
              }
            </div>
          </div>

          <div class="panel notifications-panel">
            <div class="panel-header">
              <h3>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
                Notifications
              </h3>
              @if (notifications.length > 0) {
                <span class="badge warning">{{ notifications.length }} new</span>
              }
            </div>
            <div class="panel-body">
              @for (n of notifications; track n.title) {
                <div class="notif-item" [ngClass]="'notif-' + n.type">
                  <div class="notif-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                  </div>
                  <div class="notif-content">
                    <div class="notif-title">{{ n.title }}</div>
                    <div class="notif-msg">{{ n.message }}</div>
                  </div>
                </div>
              }
              @if (notifications.length === 0) {
                <div class="empty-state">No notifications</div>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 0.25rem;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.75rem;
      flex-wrap: wrap;
      gap: 1rem;
    }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; letter-spacing: -0.02em; }
    .page-sub { margin: 4px 0 0; font-size: 0.875rem; color: var(--text-muted); }

    .header-actions { display: flex; align-items: center; gap: 0.75rem; }
    .header-clock {
      display: flex; align-items: center; gap: 0.375rem;
      font-size: 0.8125rem; color: var(--text-secondary);
      background: var(--bg-secondary); padding: 0.375rem 0.75rem; border-radius: 8px;
    }
    .refresh-btn {
      display: flex; align-items: center; justify-content: center;
      width: 34px; height: 34px; border-radius: 8px;
      background: var(--bg-secondary); border: 1px solid var(--border-color);
      color: var(--text-secondary); cursor: pointer; transition: all 0.2s;
    }
    .refresh-btn:hover { background: var(--bg-hover-strong, #f3f4f6); color: var(--text-primary); }
    .refresh-btn.spinning svg { animation: spin 0.8s linear infinite; }

    .loading-state {
      text-align: center; padding: 4rem;
      color: var(--text-muted); font-size: 0.875rem;
      display: flex; flex-direction: column; align-items: center; gap: 1rem;
    }
    .loader {
      width: 36px; height: 36px;
      border: 3px solid var(--border-color); border-top-color: var(--brand-color, #0d9488);
      border-radius: 50%; animation: spin 0.8s linear infinite;
    }

    .error-state {
      text-align: center; padding: 4rem;
      color: var(--text-muted); font-size: 0.875rem;
      display: flex; flex-direction: column; align-items: center; gap: 1rem;
    }
    .error-state svg { color: #ef4444; opacity: 0.6; }
    .retry-btn {
      padding: 0.5rem 1.25rem; border-radius: 8px;
      background: var(--brand-color, #0d9488); color: #fff;
      border: none; font-size: 0.8125rem; font-weight: 600; cursor: pointer;
      transition: opacity 0.2s;
    }
    .retry-btn:hover { opacity: 0.9; }

    .section-group { margin-bottom: 2rem; }
    .section-header {
      display: flex; align-items: center; gap: 0.625rem; margin-bottom: 1rem;
    }
    .section-icon {
      display: flex; align-items: center; justify-content: center;
      width: 32px; height: 32px; border-radius: 8px;
    }
    .section-icon.icon-overview { background: rgba(13, 148, 136, 0.12); color: #0d9488; }
    .section-icon.icon-secondary { background: rgba(99, 102, 241, 0.12); color: #6366f1; }
    .section-header h3 {
      font-size: 0.75rem; font-weight: 700; color: var(--text-muted);
      letter-spacing: 0.08em; text-transform: uppercase;
    }

    .stats-grid {
      display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem;
    }

    .stat-card.card-3d {
      position: relative; border-radius: 16px; overflow: hidden;
      cursor: default; transition: transform 0.25s ease, box-shadow 0.25s ease;
      background: var(--bg-secondary); border: 1px solid var(--border-color);
      text-decoration: none; color: inherit;
      box-shadow:
        0 1px 2px rgba(0, 0, 0, 0.04),
        0 4px 12px rgba(0, 0, 0, 0.06),
        inset 0 1px 0 rgba(255, 255, 255, 0.06);
    }
    .stat-card.card-3d:hover {
      transform: translateY(-3px) scale(1.01);
      box-shadow:
        0 4px 8px rgba(0, 0, 0, 0.08),
        0 12px 28px rgba(0, 0, 0, 0.12),
        inset 0 1px 0 rgba(255, 255, 255, 0.08);
    }

    .card-shine {
      position: absolute; top: 0; left: 0; right: 0; height: 50%;
      background: linear-gradient(180deg, rgba(255,255,255,0.03) 0%, transparent 100%);
      pointer-events: none; border-radius: 16px 16px 0 0;
    }

    .card-inner {
      position: relative; z-index: 1; padding: 1.25rem;
      display: flex; align-items: flex-start; gap: 1rem;
    }

    .stat-icon-wrap {
      flex-shrink: 0; width: 48px; height: 48px; border-radius: 14px;
      display: flex; align-items: center; justify-content: center;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.2);
    }

    .icon-purple { background: linear-gradient(135deg, #6366f1, #818cf8); color: #fff; }
    .icon-teal { background: linear-gradient(135deg, #14b8a6, #2dd4bf); color: #fff; }
    .icon-green { background: linear-gradient(135deg, #22c55e, #4ade80); color: #fff; }
    .icon-amber { background: linear-gradient(135deg, #f59e0b, #fbbf24); color: #fff; }
    .icon-blue { background: linear-gradient(135deg, #3b82f6, #60a5fa); color: #fff; }
    .icon-pink { background: linear-gradient(135deg, #ec4899, #f472b6); color: #fff; }
    .icon-indigo { background: linear-gradient(135deg, #8b5cf6, #a78bfa); color: #fff; }
    .icon-emerald { background: linear-gradient(135deg, #10b981, #34d399); color: #fff; }
    .icon-red { background: linear-gradient(135deg, #ef4444, #f87171); color: #fff; }

    .stat-content { flex: 1; min-width: 0; }
    .stat-top {
      display: flex; align-items: baseline; justify-content: space-between; gap: 0.5rem;
    }
    .stat-value {
      font-size: 1.75rem; font-weight: 800; color: var(--text-primary);
      line-height: 1.1; letter-spacing: -0.03em;
    }
    .live-badge {
      font-size: 0.625rem; font-weight: 600; color: #22c55e;
      background: rgba(34, 197, 94, 0.1); padding: 2px 8px;
      border-radius: 20px; letter-spacing: 0.04em;
      text-transform: uppercase; flex-shrink: 0;
    }
    .stat-label {
      display: block; font-size: 0.8125rem; color: var(--text-muted);
      margin-top: 4px; font-weight: 500;
    }

    .bottom-section {
      display: grid; grid-template-columns: 1fr 320px 320px; gap: 1.25rem;
      margin-bottom: 2rem;
    }

    .panel {
      background: var(--bg-secondary); border: 1px solid var(--border-color);
      border-radius: 16px; overflow: hidden;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04), 0 4px 12px rgba(0, 0, 0, 0.06);
    }
    .panel-header {
      display: flex; justify-content: space-between; align-items: center;
      padding: 1rem 1.25rem; border-bottom: 1px solid var(--border-color);
    }
    .panel-header h3 {
      display: flex; align-items: center; gap: 0.5rem;
      font-size: 0.9375rem; font-weight: 600; color: var(--text-primary);
    }
    .panel-header h3 svg { color: var(--text-muted); }
    .badge {
      background: var(--bg-tertiary, #f3f4f6); color: var(--text-secondary);
      font-size: 0.6875rem; font-weight: 600; padding: 2px 8px; border-radius: 10px;
    }
    .badge.warning { background: #fef3c7; color: #d97706; }
    .panel-body { padding: 0.75rem 1.25rem; max-height: 380px; overflow-y: auto; }
    .empty-state { text-align: center; padding: 2rem; color: var(--text-muted); font-size: 0.8125rem; }

    .activity-item {
      display: flex; gap: 0.75rem; padding: 0.625rem 0;
      border-bottom: 1px solid var(--border-color);
    }
    .activity-item:last-child { border-bottom: none; }
    .activity-icon-wrap {
      width: 32px; height: 32px; border-radius: 8px;
      display: flex; align-items: center; justify-content: center; flex-shrink: 0;
    }
    .activity-content { flex: 1; min-width: 0; }
    .activity-title { font-size: 0.8125rem; font-weight: 600; color: var(--text-primary); }
    .activity-desc {
      font-size: 0.75rem; color: var(--text-secondary); margin-top: 2px;
      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
    }
    .activity-meta { display: flex; justify-content: space-between; margin-top: 4px; }
    .activity-time { font-size: 0.6875rem; color: var(--text-muted); }

    .quick-links { display: flex; flex-direction: column; gap: 0.5rem; padding: 0.75rem 0; }
    .quick-link {
      display: flex; align-items: center; gap: 0.625rem;
      padding: 0.625rem 0.875rem; background: var(--bg-tertiary, #f9fafb);
      border: 1px solid var(--border-color); border-radius: 10px;
      color: var(--text-primary); text-decoration: none;
      font-size: 0.8125rem; font-weight: 500; transition: all 0.2s ease; cursor: pointer;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
    }
    .quick-link:hover {
      border-color: var(--brand-color, #0d9488); color: var(--brand-color, #0d9488);
      background: var(--bg-secondary); transform: translateX(4px);
      box-shadow: 0 2px 8px rgba(13, 148, 136, 0.1);
    }
    .quick-link-icon { display: flex; flex-shrink: 0; }

    .notif-item {
      display: flex; gap: 0.625rem; padding: 0.75rem 0;
      border-bottom: 1px solid var(--border-color);
    }
    .notif-item:last-child { border-bottom: none; }
    .notif-icon {
      flex-shrink: 0; width: 28px; height: 28px; border-radius: 6px;
      display: flex; align-items: center; justify-content: center;
    }
    .notif-warning { border-left: 3px solid #f59e0b; padding-left: 0.625rem; margin-left: -0.625rem; }
    .notif-warning .notif-icon { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
    .notif-danger { border-left: 3px solid #ef4444; padding-left: 0.625rem; margin-left: -0.625rem; }
    .notif-danger .notif-icon { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
    .notif-info { border-left: 3px solid #3b82f6; padding-left: 0.625rem; margin-left: -0.625rem; }
    .notif-info .notif-icon { background: rgba(59, 130, 246, 0.1); color: #3b82f6; }
    .notif-success { border-left: 3px solid #22c55e; padding-left: 0.625rem; margin-left: -0.625rem; }
    .notif-success .notif-icon { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
    .notif-content { flex: 1; min-width: 0; }
    .notif-title { font-size: 0.8125rem; font-weight: 600; color: var(--text-primary); }
    .notif-msg { font-size: 0.75rem; color: var(--text-secondary); margin-top: 2px; }

    @keyframes spin { to { transform: rotate(360deg); } }

    @media (max-width: 1200px) {
      .stats-grid { grid-template-columns: repeat(2, 1fr); }
      .bottom-section { grid-template-columns: 1fr 1fr; }
      .notifications-panel { grid-column: span 2; }
    }
    @media (max-width: 768px) {
      .stats-grid { grid-template-columns: 1fr; }
      .bottom-section { grid-template-columns: 1fr; }
      .notifications-panel { grid-column: span 1; }
      .page-header { flex-direction: column; align-items: flex-start; }
      .header-actions { width: 100%; justify-content: flex-end; }
    }
    @media (max-width: 640px) {
      .page-header h2 { font-size: 1.25rem; }
      .stat-card .card-inner { padding: 1rem; }
      .stat-icon-wrap { width: 40px; height: 40px; }
      .stat-value { font-size: 1.5rem; }
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  private dashboardService = inject(DashboardService);
  private currentUserService = inject(CurrentUserService);
  private toastService = inject(ToastService);
  private activityLogService = inject(ActivityLogService);
  private notificationService = inject(NotificationService);
  private destroy$ = new Subject<void>();

  dashboardData: any = null;
  loading = true;
  error = false;
  refreshing = false;
  currentTime = '';

  recentActivities: { title: string; description: string; color: string; time: string }[] = [];
  notifications: { title: string; message: string; type: string }[] = [];

  userName = computed(() => {
    const user = this.currentUserService.user();
    return user?.firstName || user?.username || 'User';
  });

  roleCode = computed(() => this.currentUserService.roleCode() || 'ROLE_SUPER_ADMIN');

  roleSectionTitle = computed(() => {
    const role = this.roleCode();
    if (role.includes('SUPER_ADMIN')) return 'SUPER ADMIN OVERVIEW';
    if (role.includes('UNIVERSITY_ADMIN') || role.includes('HALL_PROVOST') || role.includes('TRANSPORT_MANAGER') || role.includes('GENERAL_STAFF')) return 'ADMINISTRATION OVERVIEW';
    if (role.includes('FACULTY') || role.includes('ADVISOR')) return 'FACULTY OVERVIEW';
    if (role.includes('DEPT_HEAD')) return 'DEPARTMENT HEAD OVERVIEW';
    if (role.includes('STUDENT')) return 'STUDENT OVERVIEW';
    if (role.includes('ADMISSION_OFFICER')) return 'ADMISSIONS OVERVIEW';
    if (role.includes('ACCOUNTS_OFFICER')) return 'FINANCE OVERVIEW';
    if (role.includes('LIBRARIAN')) return 'LIBRARY OVERVIEW';
    if (role.includes('REGISTRAR')) return 'REGISTRAR OVERVIEW';
    if (role.includes('HR_MANAGER')) return 'HR MANAGER OVERVIEW';
    return 'DASHBOARD OVERVIEW';
  });

  primaryStats = computed<StatCard[]>(() => {
    const d = this.dashboardData;
    if (!d) return [];
    const role = this.roleCode();
    const icons = {
      users: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>',
      book: '<path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>',
      building: '<rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>',
      medal: '<path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/>',
      shield: '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>',
      clock: '<circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>',
      check: '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>',
      star: '<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>',
      home: '<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>',
      credit: '<line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>',
      alert: '<path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>',
      monitor: '<rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>',
    };

    if (role.includes('SUPER_ADMIN')) {
      return [
        { label: 'Total Users', value: d.totalUsers ?? 0, icon: icons.users, colorClass: 'card-purple', route: '/security/users' },
        { label: 'Total Roles', value: d.totalRoles ?? 0, icon: icons.shield, colorClass: 'card-teal', route: '/security/roles' },
        { label: 'Permissions', value: d.totalPermissions ?? 0, icon: icons.check, colorClass: 'card-green', route: '/security/permissions' },
        { label: 'Active Sessions', value: d.activeSessions ?? 0, icon: icons.clock, colorClass: 'card-amber', route: '/security/login-sessions' },
      ];
    }

    if (role.includes('UNIVERSITY_ADMIN') || role.includes('HALL_PROVOST') || role.includes('TRANSPORT_MANAGER') || role.includes('GENERAL_STAFF')) {
      return [
        { label: 'Total Faculties', value: d.totalFaculties ?? 0, icon: icons.users, colorClass: 'card-purple', route: '/academic/faculty' },
        { label: 'Total Departments', value: d.totalDepartments ?? 0, icon: icons.building, colorClass: 'card-teal', route: '/academic/departments' },
        { label: 'Total Students', value: d.totalStudents ?? 0, icon: icons.users, colorClass: 'card-green', route: '/students/list' },
        { label: 'Total Courses', value: d.totalCourses ?? 0, icon: icons.book, colorClass: 'card-amber', route: '/academic/courses' },
      ];
    }

    if (role.includes('FACULTY') || role.includes('ADVISOR')) {
      return [
        { label: 'Assigned Courses', value: d.assignedCourses ?? 0, icon: icons.book, colorClass: 'card-purple', route: '/academic/courses' },
        { label: 'Total Students', value: d.totalStudents ?? 0, icon: icons.users, colorClass: 'card-teal', route: '/students/list' },
        { label: 'Pending Assignments', value: d.pendingAssignments ?? 0, icon: icons.check, colorClass: 'card-green', route: '/lms/assignments' },
        { label: 'Upcoming Exams', value: d.upcomingExams ?? 0, icon: icons.clock, colorClass: 'card-amber', route: '/examination/exams' },
      ];
    }

    if (role.includes('DEPT_HEAD')) {
      return [
        { label: 'Total Courses', value: d.totalCourses ?? 0, icon: icons.book, colorClass: 'card-purple', route: '/academic/courses' },
        { label: 'Total Students', value: d.totalStudents ?? 0, icon: icons.users, colorClass: 'card-teal', route: '/students/list' },
        { label: 'Total Faculty', value: d.totalFaculty ?? 0, icon: icons.star, colorClass: 'card-green', route: '/hrm/employees' },
        { label: 'Pending Approvals', value: d.pendingApprovals ?? 0, icon: icons.clock, colorClass: 'card-amber', route: '/hrm/leave-requests' },
      ];
    }

    if (role.includes('STUDENT')) {
      return [
        { label: 'Total Students', value: d.totalStudents ?? 0, icon: icons.users, colorClass: 'card-purple', route: '/students/list' },
        { label: 'Active Enrollments', value: d.activeEnrollments ?? 0, icon: icons.book, colorClass: 'card-teal', route: '/students/enrollments' },
        { label: 'Registered Courses', value: d.registeredCourses ?? 0, icon: icons.medal, colorClass: 'card-green', route: '/academic/courses' },
        { label: 'Pending Fees', value: d.pendingFees ?? 0, icon: icons.credit, colorClass: 'card-amber', route: '/finance/payments' },
      ];
    }

    if (role.includes('ADMISSION_OFFICER')) {
      return [
        { label: 'Total Applications', value: d.totalApplications ?? 0, icon: icons.users, colorClass: 'card-purple', route: '/admissions/candidates' },
        { label: 'Pending Review', value: d.pendingReview ?? 0, icon: icons.clock, colorClass: 'card-amber', route: '/admissions/candidates' },
        { label: 'Approved', value: d.approved ?? 0, icon: icons.check, colorClass: 'card-green', route: '/admissions/candidates' },
        { label: 'Rejected', value: d.rejected ?? 0, icon: icons.alert, colorClass: 'card-red', route: '/admissions/candidates' },
      ];
    }

    if (role.includes('ACCOUNTS_OFFICER')) {
      return [
        { label: 'Total Invoices', value: d.totalInvoices ?? 0, icon: icons.credit, colorClass: 'card-purple', route: '/finance/payments' },
        { label: 'Paid', value: d.paid ?? 0, icon: icons.check, colorClass: 'card-green', route: '/finance/payments' },
        { label: 'Pending', value: d.pending ?? 0, icon: icons.clock, colorClass: 'card-amber', route: '/finance/payments' },
        { label: 'Overdue', value: d.overdue ?? 0, icon: icons.alert, colorClass: 'card-red', route: '/finance/payments' },
      ];
    }

    if (role.includes('LIBRARIAN')) {
      return [
        { label: 'Total Books', value: d.totalBooks ?? 0, icon: icons.book, colorClass: 'card-purple', route: '/library/books' },
        { label: 'Borrowed', value: d.borrowed ?? 0, icon: icons.users, colorClass: 'card-teal', route: '/library/books' },
        { label: 'Available', value: d.available ?? 0, icon: icons.check, colorClass: 'card-green', route: '/library/books' },
        { label: 'Overdue', value: d.overdue ?? 0, icon: icons.alert, colorClass: 'card-red', route: '/library/books' },
      ];
    }

    if (role.includes('REGISTRAR')) {
      return [
        { label: 'Total Students', value: d.totalStudents ?? 0, icon: icons.users, colorClass: 'card-purple', route: '/students/list' },
        { label: 'Total Courses', value: d.totalCourses ?? 0, icon: icons.book, colorClass: 'card-teal', route: '/academic/courses' },
        { label: 'Total Applications', value: d.totalApplications ?? 0, icon: icons.check, colorClass: 'card-green', route: '/admissions/applications' },
        { label: 'Total Departments', value: d.totalDepartments ?? 0, icon: icons.building, colorClass: 'card-amber', route: '/academic/departments' },
      ];
    }

    if (role.includes('HR_MANAGER')) {
      return [
        { label: 'Total Employees', value: d.totalEmployees ?? 0, icon: icons.users, colorClass: 'card-purple', route: '/hrm/employees' },
        { label: 'Leave Requests', value: d.pendingLeaveRequests ?? 0, icon: icons.clock, colorClass: 'card-teal', route: '/hrm/leave-requests' },
        { label: 'Payrolls', value: d.totalPayrolls ?? 0, icon: icons.credit, colorClass: 'card-green', route: '/hrm/payrolls' },
        { label: 'Pending Approvals', value: d.pendingApprovals ?? 0, icon: icons.star, colorClass: 'card-amber', route: '/hrm/leave-requests' },
      ];
    }

    return [
      { label: 'Total Students', value: d.totalStudents ?? 0, icon: icons.users, colorClass: 'card-purple' },
      { label: 'Total Courses', value: d.totalCourses ?? 0, icon: icons.book, colorClass: 'card-teal' },
      { label: 'Total Employees', value: d.totalEmployees ?? 0, icon: icons.star, colorClass: 'card-green' },
      { label: 'System Health', value: d.systemHealth ?? 'N/A', icon: icons.shield, colorClass: 'card-amber' },
    ];
  });

  secondaryStats = computed<StatCard[]>(() => {
    const d = this.dashboardData;
    if (!d) return [];
    const role = this.roleCode();
    const icons = {
      users: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>',
      book: '<path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>',
      check: '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>',
      clock: '<circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>',
    };

    if (role.includes('SUPER_ADMIN')) {
      return [
        { label: 'Recent Logins', value: d.recentLogins ?? 0, icon: icons.check, colorClass: 'card-blue' },
        { label: 'Security Alerts', value: d.securityAlerts ?? 0, icon: icons.clock, colorClass: 'card-pink' },
        { label: 'System Health', value: d.systemHealth ?? 'N/A', icon: icons.check, colorClass: 'card-emerald' },
      ];
    }

    if (role.includes('UNIVERSITY_ADMIN') || role.includes('HALL_PROVOST') || role.includes('TRANSPORT_MANAGER') || role.includes('GENERAL_STAFF')) {
      return [
        { label: 'Total Employees', value: d.totalEmployees ?? 0, icon: icons.users, colorClass: 'card-indigo', route: '/hrm/employees' },
        { label: 'Pending Approvals', value: d.pendingApprovals ?? 0, icon: icons.clock, colorClass: 'card-pink', route: '/hrm/leave-requests' },
        { label: 'Recent Activities', value: d.recentActivities ?? 0, icon: icons.check, colorClass: 'card-blue' },
      ];
    }

    if (role.includes('FACULTY') || role.includes('ADVISOR')) {
      return [
        { label: 'Recent Submissions', value: d.recentSubmissions ?? 0, icon: icons.check, colorClass: 'card-blue', route: '/lms/assignments' },
      ];
    }

    if (role.includes('STUDENT')) {
      return [
        { label: 'Total Alumni', value: d.totalAlumni ?? 0, icon: icons.users, colorClass: 'card-indigo' },
        { label: 'Pending Assignments', value: d.pendingAssignments ?? 0, icon: icons.clock, colorClass: 'card-pink', route: '/lms/assignments' },
      ];
    }

    return [];
  });

  quickActions = computed(() => {
    const role = this.roleCode();
    const iconPaths = {
      users: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>',
      book: '<path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>',
      check: '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>',
      bell: '<path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/>',
      calendar: '<rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>',
      credit: '<line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>',
      monitor: '<rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>',
      chart: '<line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>',
    };

    if (role.includes('SUPER_ADMIN')) {
      return [
        { label: 'Users', route: '/security/users', icon: iconPaths.users, color: '#6366f1' },
        { label: 'Roles', route: '/security/roles', icon: iconPaths.bell, color: '#3b82f6' },
        { label: 'Permissions', route: '/security/permissions', icon: iconPaths.check, color: '#22c55e' },
        { label: 'Reports', route: '/reports/generated', icon: iconPaths.chart, color: '#f59e0b' },
      ];
    }

    if (role.includes('UNIVERSITY_ADMIN') || role.includes('HALL_PROVOST') || role.includes('TRANSPORT_MANAGER') || role.includes('GENERAL_STAFF')) {
      return [
        { label: 'Faculties', route: '/academic/faculty', icon: iconPaths.users, color: '#6366f1' },
        { label: 'Departments', route: '/academic/departments', icon: iconPaths.book, color: '#3b82f6' },
        { label: 'Students', route: '/students/list', icon: iconPaths.users, color: '#22c55e' },
        { label: 'Courses', route: '/academic/courses', icon: iconPaths.book, color: '#f59e0b' },
        { label: 'Employees', route: '/hrm/employees', icon: iconPaths.users, color: '#ec4899' },
        { label: 'Reports', route: '/reports/generated', icon: iconPaths.chart, color: '#8b5cf6' },
      ];
    }

    if (role.includes('FACULTY') || role.includes('ADVISOR')) {
      return [
        { label: 'My Courses', route: '/academic/courses', icon: iconPaths.book, color: '#3b82f6' },
        { label: 'Attendance', route: '/students/attendance', icon: iconPaths.check, color: '#22c55e' },
        { label: 'Assignments', route: '/lms/assignments', icon: iconPaths.users, color: '#6366f1' },
        { label: 'Results', route: '/examination/results', icon: iconPaths.chart, color: '#f59e0b' },
        { label: 'Notices', route: '/communication/notices', icon: iconPaths.bell, color: '#ec4899' },
        { label: 'Schedule', route: '/academic/class-routines', icon: iconPaths.calendar, color: '#8b5cf6' },
      ];
    }

    if (role.includes('STUDENT')) {
      return [
        { label: 'My Courses', route: '/students/enrollments', icon: iconPaths.book, color: '#3b82f6' },
        { label: 'Schedule', route: '/academic/class-routines', icon: iconPaths.calendar, color: '#6366f1' },
        { label: 'Exams', route: '/examination/exams', icon: iconPaths.check, color: '#f59e0b' },
        { label: 'Library', route: '/library/books', icon: iconPaths.book, color: '#22c55e' },
        { label: 'Notices', route: '/communication/notices', icon: iconPaths.bell, color: '#ec4899' },
        { label: 'LMS', route: '/lms/assignments', icon: iconPaths.monitor, color: '#8b5cf6' },
      ];
    }

    if (role.includes('ADMISSION_OFFICER')) {
      return [
        { label: 'Applications', route: '/admissions/candidates', icon: iconPaths.users, color: '#6366f1' },
        { label: 'Merit Lists', route: '/admissions/merit-lists', icon: iconPaths.chart, color: '#f59e0b' },
        { label: 'Interviews', route: '/admissions/interviews', icon: iconPaths.check, color: '#22c55e' },
        { label: 'Offer Letters', route: '/admissions/offer-letters', icon: iconPaths.book, color: '#3b82f6' },
      ];
    }

    if (role.includes('ACCOUNTS_OFFICER')) {
      return [
        { label: 'Payments', route: '/finance/payments', icon: iconPaths.credit, color: '#22c55e' },
        { label: 'Invoices', route: '/finance/invoices', icon: iconPaths.book, color: '#3b82f6' },
        { label: 'Reports', route: '/reports/generated', icon: iconPaths.chart, color: '#8b5cf6' },
        { label: 'Students', route: '/students/list', icon: iconPaths.users, color: '#6366f1' },
      ];
    }

    if (role.includes('LIBRARIAN')) {
      return [
        { label: 'Books', route: '/library/books', icon: iconPaths.book, color: '#3b82f6' },
        { label: 'Issue/Return', route: '/library/issues', icon: iconPaths.check, color: '#22c55e' },
        { label: 'Categories', route: '/library/categories', icon: iconPaths.users, color: '#6366f1' },
         { label: 'Reports', route: '/reports/generated', icon: iconPaths.chart, color: '#8b5cf6' },
      ];
    }

    if (role.includes('REGISTRAR')) {
      return [
        { label: 'Students', route: '/students/list', icon: iconPaths.users, color: '#6366f1' },
        { label: 'Admissions', route: '/admissions/applications', icon: iconPaths.check, color: '#22c55e' },
        { label: 'Courses', route: '/academic/courses', icon: iconPaths.book, color: '#3b82f6' },
        { label: 'Exams', route: '/examination/exams', icon: iconPaths.calendar, color: '#f59e0b' },
        { label: 'Reports', route: '/reports/generated', icon: iconPaths.chart, color: '#8b5cf6' },
        { label: 'Notices', route: '/communication/notices', icon: iconPaths.bell, color: '#ec4899' },
      ];
    }

    if (role.includes('HR_MANAGER')) {
      return [
        { label: 'Employees', route: '/hrm/employees', icon: iconPaths.users, color: '#6366f1' },
        { label: 'Attendance', route: '/hrm/attendance', icon: iconPaths.check, color: '#22c55e' },
        { label: 'Leave Requests', route: '/hrm/leave-requests', icon: iconPaths.bell, color: '#3b82f6' },
        { label: 'Payrolls', route: '/hrm/payrolls', icon: iconPaths.credit, color: '#f59e0b' },
        { label: 'Reports', route: '/reports/generated', icon: iconPaths.chart, color: '#8b5cf6' },
        { label: 'Notices', route: '/communication/notices', icon: iconPaths.calendar, color: '#ec4899' },
      ];
    }

    return [
      { label: 'Students', route: '/students/list', icon: iconPaths.users, color: '#6366f1' },
      { label: 'Courses', route: '/academic/courses', icon: iconPaths.book, color: '#3b82f6' },
    ];
  });

  ngOnInit(): void {
    this.updateTime();
    interval(1000).pipe(takeUntil(this.destroy$)).subscribe(() => this.updateTime());
    this.loadDashboard();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDashboard(): void {
    this.loading = true;
    this.error = false;
    this.refreshing = false;

    this.dashboardService.getDashboard().pipe(takeUntil(this.destroy$)).subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.buildNotifications(data);
        this.loading = false;
        this.loadRecentActivities();
        this.loadNotifications();
      },
      error: () => {
        this.loading = false;
        this.error = true;
        this.toastService.error('Failed to load dashboard data');
      }
    });
  }

  refreshDashboard(): void {
    this.refreshing = true;
    this.loadDashboard();
  }

  private loadRecentActivities(): void {
    this.activityLogService.findRecent(5).pipe(takeUntil(this.destroy$)).subscribe({
      next: (logs) => {
        const colors = ['#6366f1', '#22c55e', '#f59e0b', '#3b82f6', '#ec4899'];
        this.recentActivities = logs.map((log, i) => ({
          title: log.action || 'Activity',
          description: log.description || `${log.module || 'System'} - ${log.entityType || ''}`,
          color: colors[i % colors.length],
          time: log.createdAt ? this.timeAgo(log.createdAt) : 'Just now',
        }));
      },
      error: () => {}
    });
  }

  private loadNotifications(): void {
    this.notificationService.getNotifications(0, 5).pipe(takeUntil(this.destroy$)).subscribe({
      next: (res) => {
        const items = res?.content || res || [];
        if (Array.isArray(items)) {
          this.notifications = items.map((n: any) => ({
            title: n.title || 'Notification',
            message: n.message || n.content || '',
            type: n.type || 'info',
          }));
        }
      },
      error: () => {}
    });
  }

  private timeAgo(dateStr: string): string {
    const now = new Date();
    const date = new Date(dateStr);
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    if (seconds < 60) return 'Just now';
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes}m ago`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    const days = Math.floor(hours / 24);
    return `${days}d ago`;
  }

  private buildNotifications(data: any): void {
    this.notifications = [];
    if (data.pendingApprovals && typeof data.pendingApprovals === 'number' && data.pendingApprovals > 0) {
      this.notifications.push({ title: 'Pending Approvals', message: `${data.pendingApprovals} items need your attention`, type: 'warning' });
    }
    if (data.securityAlerts && typeof data.securityAlerts === 'number' && data.securityAlerts > 0) {
      this.notifications.push({ title: 'Security Alert', message: `${data.securityAlerts} security alerts detected`, type: 'danger' });
    }
    if (data.systemHealth) {
      this.notifications.push({ title: 'System Status', message: `System health: ${data.systemHealth}`, type: data.systemHealth === 'UP' ? 'success' : 'warning' });
    }
  }

  getColorName(cardClass: string): string {
    const map: Record<string, string> = {
      'card-purple': 'purple', 'card-teal': 'teal', 'card-green': 'green',
      'card-amber': 'amber', 'card-blue': 'blue', 'card-pink': 'pink',
      'card-indigo': 'indigo', 'card-emerald': 'emerald', 'card-red': 'red',
    };
    return map[cardClass] || 'purple';
  }

  private updateTime(): void {
    const now = new Date();
    this.currentTime = now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  }
}
