import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AcademicDashboardService } from '../../../services/academic-dashboard.service';
import { ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-container">
      <div class="page-header">
        <div>
          <h2>Academic Dashboard</h2>
          <p class="page-sub">Overview of academic operations</p>
        </div>
      </div>

      @if (loading) {
        <div class="loading-state">
          <div class="loader"></div>
          Loading dashboard...
        </div>
      } @else {
        <div class="section-group">
          <div class="section-header">
            <span class="section-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </span>
            <h3>ACADEMIC OVERVIEW</h3>
          </div>
          <div class="stats-grid">
            <div class="stat-card card-3d card-purple">
              <div class="card-inner">
                <div class="stat-icon-wrap icon-purple">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                </div>
                <div class="stat-content">
                  <div class="stat-top">
                    <span class="stat-value">{{ stats.totalFaculties || 0 }}</span>
                    <span class="live-badge">Live</span>
                  </div>
                  <span class="stat-label">Total Faculties</span>
                </div>
              </div>
              <div class="card-shine"></div>
            </div>

            <div class="stat-card card-3d card-teal">
              <div class="card-inner">
                <div class="stat-icon-wrap icon-teal">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
                </div>
                <div class="stat-content">
                  <div class="stat-top">
                    <span class="stat-value">{{ stats.totalDepartments || 0 }}</span>
                    <span class="live-badge">Live</span>
                  </div>
                  <span class="stat-label">Total Departments</span>
                </div>
              </div>
              <div class="card-shine"></div>
            </div>

            <div class="stat-card card-3d card-green">
              <div class="card-inner">
                <div class="stat-icon-wrap icon-green">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/></svg>
                </div>
                <div class="stat-content">
                  <div class="stat-top">
                    <span class="stat-value">{{ stats.totalPrograms || 0 }}</span>
                    <span class="live-badge">Live</span>
                  </div>
                  <span class="stat-label">Total Programs</span>
                </div>
              </div>
              <div class="card-shine"></div>
            </div>

            <div class="stat-card card-3d card-amber">
              <div class="card-inner">
                <div class="stat-icon-wrap icon-amber">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
                </div>
                <div class="stat-content">
                  <div class="stat-top">
                    <span class="stat-value">{{ stats.totalCourses || 0 }}</span>
                    <span class="live-badge">Live</span>
                  </div>
                  <span class="stat-label">Total Courses</span>
                </div>
              </div>
              <div class="card-shine"></div>
            </div>
          </div>
        </div>

        <div class="section-group">
          <div class="section-header">
            <span class="section-icon icon-ops">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
            </span>
            <h3>OPERATIONS</h3>
          </div>
          <div class="stats-grid">
            <div class="stat-card card-3d card-blue">
              <div class="card-inner">
                <div class="stat-icon-wrap icon-blue">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
                </div>
                <div class="stat-content">
                  <div class="stat-top">
                    <span class="stat-value">{{ stats.totalSubjects || 0 }}</span>
                    <span class="live-badge">Live</span>
                  </div>
                  <span class="stat-label">Total Subjects</span>
                </div>
              </div>
              <div class="card-shine"></div>
            </div>

            <div class="stat-card card-3d card-pink">
              <div class="card-inner">
                <div class="stat-icon-wrap icon-pink">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                </div>
                <div class="stat-content">
                  <div class="stat-top">
                    <span class="stat-value">{{ stats.activeSessions || 0 }}</span>
                    <span class="live-badge">Live</span>
                  </div>
                  <span class="stat-label">Active Sessions</span>
                </div>
              </div>
              <div class="card-shine"></div>
            </div>

            <div class="stat-card card-3d card-indigo">
              <div class="card-inner">
                <div class="stat-icon-wrap icon-indigo">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div class="stat-content">
                  <div class="stat-top">
                    <span class="stat-value">{{ stats.activeSemesters || 0 }}</span>
                    <span class="live-badge">Live</span>
                  </div>
                  <span class="stat-label">Active Semesters</span>
                </div>
              </div>
              <div class="card-shine"></div>
            </div>

            <div class="stat-card card-3d card-emerald">
              <div class="card-inner">
                <div class="stat-icon-wrap icon-emerald">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                </div>
                <div class="stat-content">
                  <div class="stat-top">
                    <span class="stat-value">{{ stats.activeCampuses || 0 }}</span>
                    <span class="live-badge">Live</span>
                  </div>
                  <span class="stat-label">Active Campuses</span>
                </div>
              </div>
              <div class="card-shine"></div>
            </div>
          </div>
        </div>

        <div class="bottom-section">
          <div class="events-panel">
            <h3>Recent Academic Events</h3>
            @if (recentEvents.length === 0) {
              <p class="empty-state">No recent events</p>
            } @else {
              <div class="events-table">
                <table>
                  <thead>
                    <tr>
                      <th>Title</th>
                      <th>Type</th>
                      <th>Start Date</th>
                      <th>End Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (event of recentEvents; track event.id) {
                      <tr>
                        <td>{{ event.title }}</td>
                        <td><span class="badge">{{ event.eventType }}</span></td>
                        <td>{{ event.startDate | date:'mediumDate' }}</td>
                        <td>{{ event.endDate | date:'mediumDate' }}</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          </div>

          <div class="quick-panel">
            <h3>Quick Actions</h3>
            <div class="quick-links">
              <a routerLink="/academic/faculty" class="quick-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Faculties
              </a>
              <a routerLink="/academic/departments" class="quick-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
                Departments
              </a>
              <a routerLink="/academic/courses" class="quick-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/></svg>
                Courses
              </a>
              <a routerLink="/academic/subjects" class="quick-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
                Subjects
              </a>
              <a routerLink="/academic/batches" class="quick-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Batches
              </a>
              <a routerLink="/academic/academic-calendar" class="quick-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                Calendar
              </a>
              <a routerLink="/academic/class-routines" class="quick-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                Routines
              </a>
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
    }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; letter-spacing: -0.02em; }
    .page-sub { margin: 4px 0 0; font-size: 0.875rem; color: var(--text-muted); }

    .loading-state {
      text-align: center;
      padding: 4rem;
      color: var(--text-muted);
      font-size: 0.875rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;
    }
    .loader {
      width: 36px;
      height: 36px;
      border: 3px solid var(--border-color);
      border-top-color: var(--brand-color);
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }

    .section-group { margin-bottom: 2rem; }
    .section-header {
      display: flex;
      align-items: center;
      gap: 0.625rem;
      margin-bottom: 1rem;
    }
    .section-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 32px;
      height: 32px;
      border-radius: 8px;
      background: rgba(99, 102, 241, 0.12);
      color: #002d5f;
    }
    .section-icon.icon-ops {
      background: rgba(139, 92, 246, 0.12);
      color: #5a3e8e;
    }
    .section-header h3 {
      font-size: 0.75rem;
      font-weight: 700;
      color: var(--text-muted);
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 1rem;
    }

    .stat-card.card-3d {
      position: relative;
      border-radius: 16px;
      overflow: hidden;
      cursor: default;
      transition: transform 0.25s ease, box-shadow 0.25s ease;
      background: var(--bg-secondary);
      border: 1px solid var(--border-color);
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
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 50%;
      background: linear-gradient(180deg, rgba(255,255,255,0.03) 0%, transparent 100%);
      pointer-events: none;
      border-radius: 16px 16px 0 0;
    }

    .card-inner {
      position: relative;
      z-index: 1;
      padding: 1.25rem;
      display: flex;
      align-items: flex-start;
      gap: 1rem;
    }

    .stat-icon-wrap {
      flex-shrink: 0;
      width: 48px;
      height: 48px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow:
        0 2px 6px rgba(0, 0, 0, 0.1),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
    }

    .icon-purple { background: linear-gradient(135deg, #002d5f, #004080); color: #fff; }
    .icon-teal { background: linear-gradient(135deg, #3388cc, #5b9bd5); color: #fff; }
    .icon-green { background: linear-gradient(135deg, #28a745, #4ade80); color: #fff; }
    .icon-amber { background: linear-gradient(135deg, #e6a817, #fbbf24); color: #fff; }
    .icon-blue { background: linear-gradient(135deg, #0056b3, #60a5fa); color: #fff; }
    .icon-pink { background: linear-gradient(135deg, #c8102e, #f472b6); color: #fff; }
    .icon-indigo { background: linear-gradient(135deg, #5a3e8e, #a78bfa); color: #fff; }
    .icon-emerald { background: linear-gradient(135deg, #28a745, #34d399); color: #fff; }

    .stat-content { flex: 1; min-width: 0; }
    .stat-top {
      display: flex;
      align-items: baseline;
      justify-content: space-between;
      gap: 0.5rem;
    }
    .stat-value {
      font-size: 1.75rem;
      font-weight: 800;
      color: var(--text-primary);
      line-height: 1.1;
      letter-spacing: -0.03em;
    }
    .live-badge {
      font-size: 0.625rem;
      font-weight: 600;
      color: #28a745;
      background: rgba(34, 197, 94, 0.1);
      padding: 2px 8px;
      border-radius: 20px;
      letter-spacing: 0.04em;
      text-transform: uppercase;
      flex-shrink: 0;
    }
    .stat-label {
      display: block;
      font-size: 0.8125rem;
      color: var(--text-muted);
      margin-top: 4px;
      font-weight: 500;
    }

    .bottom-section {
      display: grid;
      grid-template-columns: 1fr 320px;
      gap: 1.25rem;
    }

    .events-panel, .quick-panel {
      background: var(--bg-secondary);
      border: 1px solid var(--border-color);
      border-radius: 16px;
      padding: 1.25rem;
      box-shadow:
        0 1px 2px rgba(0, 0, 0, 0.04),
        0 4px 12px rgba(0, 0, 0, 0.06),
        inset 0 1px 0 rgba(255, 255, 255, 0.06);
    }
    .events-panel h3, .quick-panel h3 {
      font-size: 1rem;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 1rem;
    }

    .empty-state { color: var(--text-muted); font-size: 0.875rem; }

    .events-table { overflow-x: auto; }
    .events-table table { width: 100%; border-collapse: collapse; }
    .events-table th, .events-table td { padding: 0.625rem 0.75rem; text-align: left; font-size: 0.8125rem; }
    .events-table th {
      color: var(--text-muted);
      font-weight: 600;
      border-bottom: 1px solid var(--border-color);
      font-size: 0.75rem;
      text-transform: uppercase;
      letter-spacing: 0.04em;
    }
    .events-table td { color: var(--text-primary); border-bottom: 1px solid var(--border-color); }
    .events-table tr:last-child td { border-bottom: none; }
    .badge {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 6px;
      font-size: 0.6875rem;
      font-weight: 600;
      background: rgba(99, 102, 241, 0.1);
      color: #002d5f;
    }

    .quick-links {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }
    .quick-link {
      display: flex;
      align-items: center;
      gap: 0.625rem;
      padding: 0.625rem 0.875rem;
      background: var(--bg-tertiary);
      border: 1px solid var(--border-color);
      border-radius: 10px;
      color: var(--text-primary);
      text-decoration: none;
      font-size: 0.8125rem;
      font-weight: 500;
      transition: all 0.2s ease;
      cursor: pointer;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
    }
    .quick-link:hover {
      border-color: var(--brand-color);
      color: var(--brand-color);
      background: var(--bg-secondary);
      transform: translateX(4px);
      box-shadow: 0 2px 8px rgba(0, 45, 95, 0.1);
    }
    .quick-link svg { flex-shrink: 0; opacity: 0.6; }
    .quick-link:hover svg { opacity: 1; }

    @media (max-width: 1024px) {
      .stats-grid { grid-template-columns: repeat(2, 1fr); }
      .bottom-section { grid-template-columns: 1fr; }
    }
    @media (max-width: 640px) {
      .stats-grid { grid-template-columns: 1fr; }
      .page-header h2 { font-size: 1.25rem; }
      .events-table th, .events-table td { padding: 0.5rem 0.625rem; font-size: 0.75rem; }
      .stat-card .card-inner { padding: 1rem; }
      .stat-icon-wrap { width: 40px; height: 40px; }
      .stat-value { font-size: 1.5rem; }
    }
  `]
})
export class AcademicDashboardComponent implements OnInit {
  stats: any = {};
  recentEvents: any[] = [];
  loading = true;

  constructor(
    private dashboardService: AcademicDashboardService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load dashboard stats');
      }
    });

    this.dashboardService.getRecentEvents().subscribe({
      next: (data) => {
        this.recentEvents = Array.isArray(data) ? data.slice(0, 10) : (data?.content || []).slice(0, 10);
      },
      error: () => {}
    });
  }
}
