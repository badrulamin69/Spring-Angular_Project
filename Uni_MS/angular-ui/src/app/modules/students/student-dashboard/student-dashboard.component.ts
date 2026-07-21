import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StudentDashboardService } from '../../../services/student-dashboard.service';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="page-header">
      <div>
        <h2>Student Dashboard</h2>
        <p class="page-sub">Overview of student operations</p>
      </div>
    </div>

    @if (loading) {
      <div class="loading-state">Loading dashboard...</div>
    } @else {
      <div class="stats-grid">
        <div class="stat-card" routerLink="/students/list">
          <div class="stat-icon blue">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2"/><circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2"/><path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="2"/><path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalStudents || 0 }}</span>
            <span class="stat-label">Total Students</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/students/attendance">
          <div class="stat-icon green">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2"/><polyline points="22,4 12,14.01 9,11.01" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.todayAttendance || 0 }}</span>
            <span class="stat-label">Today's Attendance</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/students/course-registration">
          <div class="stat-icon orange">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2"/><polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.activeRegistrations || 0 }}</span>
            <span class="stat-label">Active Registrations</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/students/semester-registration">
          <div class="stat-icon purple">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/><path d="M16 2v4M8 2v4M3 10h18" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.semesterRegistrations || 0 }}</span>
            <span class="stat-label">Semester Registrations</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/students/result">
          <div class="stat-icon cyan">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 20V10M18 20V4M6 20v-4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalResults || 0 }}</span>
            <span class="stat-label">Results Published</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/students/transcript">
          <div class="stat-icon red">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2"/><path d="M12 18v-6M9 15l3 3 3-3" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.transcriptsIssued || 0 }}</span>
            <span class="stat-label">Transcripts Issued</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/students/certificates">
          <div class="stat-icon teal">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="7" stroke="currentColor" stroke-width="2"/><path d="M8.21 13.89L7 23l5-3 5 3-1.21-9.12" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.certificatesIssued || 0 }}</span>
            <span class="stat-label">Certificates Issued</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/students/alumni">
          <div class="stat-icon indigo">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 2L2 7l10 5 10-5-10-5z" stroke="currentColor" stroke-width="2"/><path d="M2 17l10 5 10-5" stroke="currentColor" stroke-width="2"/><path d="M2 12l10 5 10-5" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalAlumni || 0 }}</span>
            <span class="stat-label">Total Alumni</span>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .loading-state { text-align: center; padding: 3rem; color: var(--text-muted); }
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; }
    .stat-card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.25rem; display: flex; align-items: center; gap: 1rem; cursor: pointer; transition: all 0.15s; }
    .stat-card:hover { border-color: var(--brand-color); box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    .stat-icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .stat-icon.blue { background: rgba(59,130,246,0.1); color: #0056b3; }
    .stat-icon.green { background: rgba(16,185,129,0.1); color: #28a745; }
    .stat-icon.orange { background: rgba(245,158,11,0.1); color: #e6a817; }
    .stat-icon.purple { background: rgba(139,92,246,0.1); color: #5a3e8e; }
    .stat-icon.cyan { background: rgba(6,182,212,0.1); color: #17a2b8; }
    .stat-icon.red { background: rgba(239,68,68,0.1); color: #dc3545; }
    .stat-icon.teal { background: rgba(20,184,166,0.1); color: #3388cc; }
    .stat-icon.indigo { background: rgba(99,102,241,0.1); color: #002d5f; }
    .stat-info { display: flex; flex-direction: column; }
    .stat-value { font-size: 1.5rem; font-weight: 700; color: var(--text-primary); }
    .stat-label { font-size: 0.8rem; color: var(--text-muted); }
    @media (max-width: 1024px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
    @media (max-width: 640px) { .stats-grid { grid-template-columns: 1fr; } }
  `]
})
export class StudentDashboardComponent implements OnInit {
  stats: any = {};
  loading = true;

  constructor(private dashboardService: StudentDashboardService) {}

  ngOnInit() {
    this.dashboardService.getStats().subscribe({
      next: (data) => { this.stats = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }
}
