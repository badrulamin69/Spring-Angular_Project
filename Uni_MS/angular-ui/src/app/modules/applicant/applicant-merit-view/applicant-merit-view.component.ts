import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApplicantPortalService } from '../../../services/applicant-portal.service';

@Component({
  selector: 'app-applicant-merit-view',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="merit-page">
      @if (loading) {
        <div class="loading"><div class="spinner"></div><span>Loading your merit position...</span></div>
      } @else if (!meritData?.found && !waitingData?.found) {
        <div class="empty-state">
          <div class="empty-icon">
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none"><circle cx="32" cy="32" r="32" fill="#f1f5f9"/><path d="M32 18v28M18 32h28" stroke="#94a3b8" stroke-width="3" stroke-linecap="round"/></svg>
          </div>
          <h2>No Merit Position Found</h2>
          <p>You have not been assigned a merit position yet. Please check back later or contact the admissions office.</p>
        </div>
      } @else {
        @if (meritData?.found) {
          <div class="result-card" [class.selected]="meritData.status === 'SELECTED'" [class.waiting]="meritData.status === 'WAITING'">
            <div class="result-header">
              <h2>Merit Result</h2>
              <span class="status-badge" [attr.data-status]="meritData.status">{{ meritData.status }}</span>
            </div>
            <div class="result-grid">
              <div class="result-item"><span class="label">Your Rank</span><span class="value rank">#{{ meritData.rank }}</span></div>
              <div class="result-item"><span class="label">Score</span><span class="value">{{ meritData.score | number:'1.1-1' }}%</span></div>
              <div class="result-item"><span class="label">Test Marks</span><span class="value">{{ meritData.testMarks | number:'1.1-1' }}</span></div>
              <div class="result-item"><span class="label">Roll Number</span><span class="value">{{ meritData.rollNumber }}</span></div>
              <div class="result-item"><span class="label">Program</span><span class="value">{{ meritData.programName }}</span></div>
              <div class="result-item"><span class="label">Total Applicants</span><span class="value">{{ meritData.totalApplicants }}</span></div>
            </div>
            <div class="result-meta">
              <span>List: {{ meritData.meritListName }}</span>
              <span>Status: {{ meritData.listStatus }}</span>
            </div>
          </div>
        }
        @if (waitingData?.found) {
          <div class="result-card waiting-card">
            <div class="result-header">
              <h2>Waiting List Position</h2>
              <span class="status-badge waiting">WAITING</span>
            </div>
            <div class="result-grid">
              <div class="result-item"><span class="label">Waiting Position</span><span class="value rank">#{{ waitingData.rank }}</span></div>
              <div class="result-item"><span class="label">Score</span><span class="value">{{ waitingData.score | number:'1.1-1' }}%</span></div>
              <div class="result-item"><span class="label">Roll Number</span><span class="value">{{ waitingData.rollNumber }}</span></div>
            </div>
            <div class="result-meta"><span>List: {{ waitingData.waitingListName }}</span></div>
          </div>
        }
      }
    </div>
  `,
  styles: [`
    .merit-page { max-width: 800px; margin: 0 auto; padding: 2rem 1rem; }
    .loading { text-align: center; padding: 3rem; }
    .spinner { width: 32px; height: 32px; border: 3px solid #e2e8f0; border-top-color: #4F46E5; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 12px; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .empty-state { text-align: center; padding: 3rem 1rem; }
    .empty-icon { margin-bottom: 1rem; }
    .empty-state h2 { color: #475569; margin: 0.5rem 0; }
    .empty-state p { color: #64748b; font-size: 0.9375rem; }
    .result-card { background: #fff; border: 2px solid #e2e8f0; border-radius: 16px; padding: 2rem; margin-bottom: 1.5rem; }
    .result-card.selected { border-color: #22c55e; }
    .result-card.waiting, .result-card.waiting-card { border-color: #f59e0b; }
    .result-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
    .result-header h2 { margin: 0; color: #1e293b; }
    .status-badge { display: inline-block; padding: 4px 12px; border-radius: 6px; font-size: 0.8125rem; font-weight: 600; }
    .status-badge[data-status="SELECTED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="WAITING"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="NOT_SELECTED"] { background: #fee2e2; color: #991b1b; }
    .result-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1.25rem; }
    .result-item { text-align: center; padding: 1rem; background: #f8fafc; border-radius: 10px; }
    .result-item .label { display: block; font-size: 0.75rem; color: #64748b; margin-bottom: 0.5rem; text-transform: uppercase; letter-spacing: 0.05em; }
    .result-item .value { font-size: 1.5rem; font-weight: 700; color: #1e293b; }
    .result-item .value.rank { color: #4F46E5; font-size: 2rem; }
    .result-meta { display: flex; justify-content: space-between; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #e2e8f0; font-size: 0.8125rem; color: #64748b; }
  `]
})
export class ApplicantMeritViewComponent implements OnInit {
  loading = true;
  meritData: any = null;
  waitingData: any = null;

  constructor(private applicantService: ApplicantPortalService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.applicantService.getMyMerit().subscribe({
      next: (data) => { this.meritData = data; this.loadWaiting(); },
      error: () => { this.loading = false; }
    });
  }

  loadWaiting() {
    this.applicantService.getMyWaitingPosition().subscribe({
      next: (data) => { this.waitingData = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }
}
