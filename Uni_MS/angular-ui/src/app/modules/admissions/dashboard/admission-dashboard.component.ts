import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdmissionDashboardService } from '../../../services/admission-dashboard.service';

@Component({
  selector: 'app-admission-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="page-header">
      <div>
        <h2>Admission Dashboard</h2>
        <p class="page-sub">Overview of admission operations</p>
      </div>
    </div>

    @if (loading) {
      <div class="loading-state">Loading dashboard...</div>
    } @else {
      <div class="stats-grid">
        <div class="stat-card" routerLink="/admissions/candidates">
          <div class="stat-icon blue">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2"/><circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2"/><path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="2"/><path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalCandidates || 0 }}</span>
            <span class="stat-label">Total Candidates</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/admissions/applications">
          <div class="stat-icon green">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2"/><polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalApplications || 0 }}</span>
            <span class="stat-label">Total Applications</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/admissions/reviews">
          <div class="stat-icon orange">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.pendingReviews || 0 }}</span>
            <span class="stat-label">Pending Reviews</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/admissions/interviews">
          <div class="stat-icon purple">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M15 10l4.553-2.276A1 1 0 0 1 21 8.618v6.764a1 1 0 0 1-1.447.894L15 14v-4z" stroke="currentColor" stroke-width="2"/><rect x="3" y="6" width="12" height="12" rx="2" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.pendingInterviews || 0 }}</span>
            <span class="stat-label">Scheduled Interviews</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/admissions/campaigns">
          <div class="stat-icon cyan">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M22 12h-4l-3 9L9 3l-3 9H2" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.activeCampaigns || 0 }}</span>
            <span class="stat-label">Active Campaigns</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/admissions/merit-lists">
          <div class="stat-icon red">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 2L2 7l10 5 10-5-10-5z" stroke="currentColor" stroke-width="2"/><path d="M2 17l10 5 10-5" stroke="currentColor" stroke-width="2"/><path d="M2 12l10 5 10-5" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.meritLists || 0 }}</span>
            <span class="stat-label">Merit Lists</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/admissions/offer-letters">
          <div class="stat-icon teal">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><rect x="2" y="4" width="20" height="16" rx="2" stroke="currentColor" stroke-width="2"/><path d="M7 8h10M7 12h6" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.offerLettersIssued || 0 }}</span>
            <span class="stat-label">Offer Letters Issued</span>
          </div>
        </div>

        <div class="stat-card" routerLink="/admissions/enrollments">
          <div class="stat-icon indigo">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2"/><polyline points="22,4 12,14.01 9,11.01" stroke="currentColor" stroke-width="2"/></svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalEnrolled || 0 }}</span>
            <span class="stat-label">Total Enrolled</span>
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
    .stat-icon.blue { background: rgba(59,130,246,0.1); color: #3b82f6; }
    .stat-icon.green { background: rgba(16,185,129,0.1); color: #10b981; }
    .stat-icon.orange { background: rgba(245,158,11,0.1); color: #f59e0b; }
    .stat-icon.purple { background: rgba(139,92,246,0.1); color: #8b5cf6; }
    .stat-icon.cyan { background: rgba(6,182,212,0.1); color: #06b6d4; }
    .stat-icon.red { background: rgba(239,68,68,0.1); color: #ef4444; }
    .stat-icon.teal { background: rgba(20,184,166,0.1); color: #14b8a6; }
    .stat-icon.indigo { background: rgba(99,102,241,0.1); color: #6366f1; }
    .stat-info { display: flex; flex-direction: column; }
    .stat-value { font-size: 1.5rem; font-weight: 700; color: var(--text-primary); }
    .stat-label { font-size: 0.8rem; color: var(--text-muted); }
    @media (max-width: 1024px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
    @media (max-width: 640px) { .stats-grid { grid-template-columns: 1fr; } }
  `]
})
export class AdmissionDashboardComponent implements OnInit {
  stats: any = {};
  loading = true;

  constructor(private dashboardService: AdmissionDashboardService) {}

  ngOnInit() {
    this.dashboardService.getStats().subscribe({
      next: (data) => { this.stats = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }
}
