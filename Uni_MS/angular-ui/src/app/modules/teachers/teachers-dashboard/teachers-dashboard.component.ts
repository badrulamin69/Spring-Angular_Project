import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TeacherService } from '../../../services/teacher.service';

@Component({
  selector: 'app-teachers-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-container">
      <div class="page-header">
        <div>
          <h2>Teachers Dashboard</h2>
          <p class="page-sub">Overview of teacher management</p>
        </div>
      </div>

      @if (loading) {
        <div class="loading-state">
          <div class="loader"></div>
          Loading dashboard...
        </div>
      } @else {
        <div class="stats-grid">
          <div class="stat-card card-purple">
            <div class="card-inner">
              <div class="stat-icon-wrap icon-purple">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              </div>
              <div class="stat-content">
                <span class="stat-value">{{ stats.totalTeachers }}</span>
                <span class="stat-label">Total Teachers</span>
              </div>
            </div>
          </div>
          <div class="stat-card card-teal">
            <div class="card-inner">
              <div class="stat-icon-wrap icon-teal">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
              </div>
              <div class="stat-content">
                <span class="stat-value">{{ stats.activeTeachers }}</span>
                <span class="stat-label">Active Teachers</span>
              </div>
            </div>
          </div>
          <div class="stat-card card-amber">
            <div class="card-inner">
              <div class="stat-icon-wrap icon-amber">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              </div>
              <div class="stat-content">
                <span class="stat-value">{{ stats.onLeave }}</span>
                <span class="stat-label">On Leave</span>
              </div>
            </div>
          </div>
          <div class="stat-card card-red">
            <div class="card-inner">
              <div class="stat-icon-wrap icon-red">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
              </div>
              <div class="stat-content">
                <span class="stat-value">{{ stats.inactiveTeachers }}</span>
                <span class="stat-label">Inactive</span>
              </div>
            </div>
          </div>
        </div>

        <div class="bottom-section">
          <div class="panel">
            <div class="panel-header"><h3>Quick Actions</h3></div>
            <div class="quick-links">
              <a routerLink="/teachers/list" class="quick-link">
                <span class="ql-icon" style="color:#002d5f">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                </span>
                Teachers List
              </a>
              <a routerLink="/teachers/documents" class="quick-link">
                <span class="ql-icon" style="color:#28a745">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                </span>
                Documents
              </a>
              <a routerLink="/teachers/departments" class="quick-link">
                <span class="ql-icon" style="color:#e6a817">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
                </span>
                Departments
              </a>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .dashboard-container { padding: 0.25rem; }
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.75rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 4px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .loading-state { text-align: center; padding: 4rem; color: var(--text-muted); display: flex; flex-direction: column; align-items: center; gap: 1rem; }
    .loader { width: 36px; height: 36px; border: 3px solid var(--border-color); border-top-color: var(--brand-color, #002d5f); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 2rem; }
    .stat-card { position: relative; border-radius: 16px; overflow: hidden; background: var(--bg-secondary); border: 1px solid var(--border-color); box-shadow: 0 1px 2px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.06); }
    .card-inner { position: relative; z-index: 1; padding: 1.25rem; display: flex; align-items: flex-start; gap: 1rem; }
    .stat-icon-wrap { flex-shrink: 0; width: 48px; height: 48px; border-radius: 14px; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 6px rgba(0,0,0,0.1), inset 0 1px 0 rgba(255,255,255,0.2); }
    .icon-purple { background: linear-gradient(135deg, #002d5f, #004080); color: #fff; }
    .icon-teal { background: linear-gradient(135deg, #3388cc, #5b9bd5); color: #fff; }
    .icon-amber { background: linear-gradient(135deg, #e6a817, #fbbf24); color: #fff; }
    .icon-red { background: linear-gradient(135deg, #dc3545, #f87171); color: #fff; }
    .stat-content { flex: 1; }
    .stat-value { font-size: 1.75rem; font-weight: 800; color: var(--text-primary); display: block; }
    .stat-label { font-size: 0.8125rem; color: var(--text-muted); font-weight: 500; }
    .bottom-section { margin-bottom: 2rem; }
    .panel { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 16px; overflow: hidden; }
    .panel-header { padding: 1rem 1.25rem; border-bottom: 1px solid var(--border-color); }
    .panel-header h3 { margin: 0; font-size: 0.9375rem; font-weight: 600; color: var(--text-primary); }
    .quick-links { display: flex; flex-direction: column; gap: 0.5rem; padding: 0.75rem 0; }
    .quick-link { display: flex; align-items: center; gap: 0.625rem; padding: 0.625rem 0.875rem; background: var(--bg-tertiary, #f9fafb); border: 1px solid var(--border-color); border-radius: 10px; color: var(--text-primary); text-decoration: none; font-size: 0.8125rem; font-weight: 500; transition: all 0.2s ease; cursor: pointer; }
    .quick-link:hover { border-color: var(--brand-color, #002d5f); color: var(--brand-color, #002d5f); background: var(--bg-secondary); }
    .ql-icon { display: flex; flex-shrink: 0; }
    @media (max-width: 1200px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
    @media (max-width: 768px) { .stats-grid { grid-template-columns: 1fr; } }
  `]
})
export class TeachersDashboardComponent implements OnInit {
  stats = { totalTeachers: 0, activeTeachers: 0, onLeave: 0, inactiveTeachers: 0 };
  loading = true;

  constructor(private teacherService: TeacherService) {}

  ngOnInit() {
    this.teacherService.getDashboard().subscribe({
      next: (data) => { this.stats = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }
}
