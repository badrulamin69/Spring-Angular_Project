import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApplicantPortalService } from '../../../services/applicant-portal.service';
import { PreAdmissionService } from '../../../services/pre-admission.service';

@Component({
  selector: 'app-applicant-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="applicant-page">
      <div class="applicant-header">
        <div class="welcome">
          <h1>Welcome, {{ registration?.firstName }} {{ registration?.lastName }}</h1>
          <p>Registration: <strong>{{ registration?.registrationNumber }}</strong></p>
        </div>
      </div>

      <div class="timeline-section">
        <h2>Application Progress</h2>
        <div class="timeline">
          @for (step of steps; track step.key) {
            <div class="timeline-item" [class.active]="step.key === currentStep" [class.completed]="step.completed">
              <div class="timeline-dot">
                @if (step.completed) {
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M3 8.5l3.5 3.5 6.5-7" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                } @else {
                  {{ step.num }}
                }
              </div>
              <div class="timeline-content">
                <h3>{{ step.title }}</h3>
                <p>{{ step.desc }}</p>
                @if (step.key === currentStep && step.actionLabel) {
                  <button class="btn btn-sm" [class]="step.actionClass || 'btn-primary'" (click)="step.actionFn()">
                    {{ step.actionLabel }}
                  </button>
                }
              </div>
            </div>
          }
        </div>
      </div>

      <div class="info-cards">
        @if (allocation) {
          <div class="info-card">
            <h3>Department Allocation</h3>
            <div class="info-row"><span>Program:</span><strong>{{ allocation.allocatedProgram?.name || 'Pending' }}</strong></div>
            <div class="info-row"><span>Department:</span><strong>{{ allocation.allocatedDepartment?.name || 'Pending' }}</strong></div>
            <div class="info-row"><span>Score:</span><strong>{{ allocation.totalScore }}</strong></div>
            <div class="info-row"><span>Rank:</span><strong>#{{ allocation.meritRank }}</strong></div>
            <div class="info-row"><span>Status:</span><strong class="status-badge" [attr.data-status]="allocation.status">{{ allocation.status }}</strong></div>
            @if (allocation.status === 'ALLOCATED') {
              <div class="alloc-actions">
                <button class="btn btn-success" (click)="confirmAlloc()">Confirm</button>
                <button class="btn btn-danger" (click)="declineAlloc()">Decline</button>
              </div>
            }
          </div>
        }

        @if (testResult) {
          <div class="info-card">
            <h3>Test Results</h3>
            @if (testResult.attempts && testResult.attempts.length > 0) {
              @for (attempt of testResult.attempts; track attempt.id) {
                <div class="info-row"><span>Score:</span><strong>{{ attempt.score }} / {{ attempt.maxScore }}</strong></div>
                <div class="info-row"><span>Percentage:</span><strong>{{ attempt.percentage | number:'1.1-1' }}%</strong></div>
                <div class="info-row"><span>Correct:</span><strong>{{ attempt.correctAnswers }} / {{ attempt.totalQuestions }}</strong></div>
                <div class="info-row"><span>Status:</span><strong class="status-badge" [attr.data-status]="attempt.status">{{ attempt.status }}</strong></div>
              }
            } @else {
              <p class="no-data">No test attempt yet</p>
            }
          </div>
        }
      </div>

      <div class="error-banner" *ngIf="error">{{ error }}</div>
    </div>
  `,
  styles: [`
    .applicant-page { max-width: 900px; margin: 0 auto; padding: 1.5rem; }
    .applicant-header { background: linear-gradient(135deg, #1e40af, #3b82f6); color: #fff; border-radius: 16px; padding: 2rem; margin-bottom: 2rem; }
    .welcome h1 { margin: 0 0 4px; font-size: 1.5rem; }
    .welcome p { margin: 0; opacity: 0.85; font-size: 0.875rem; }
    .timeline-section { margin-bottom: 2rem; }
    .timeline-section h2 { font-size: 1.25rem; color: #1e293b; margin-bottom: 1.25rem; }
    .timeline { position: relative; padding-left: 32px; }
    .timeline::before { content: ''; position: absolute; left: 15px; top: 0; bottom: 0; width: 2px; background: #e2e8f0; }
    .timeline-item { position: relative; padding-bottom: 1.5rem; }
    .timeline-dot { position: absolute; left: -32px; top: 0; width: 32px; height: 32px; border-radius: 50%; background: #e2e8f0; color: #94a3b8; display: flex; align-items: center; justify-content: center; font-size: 0.75rem; font-weight: 700; z-index: 1; }
    .timeline-item.active .timeline-dot { background: #3b82f6; color: #fff; box-shadow: 0 0 0 4px rgba(59,130,246,0.2); }
    .timeline-item.completed .timeline-dot { background: #22c55e; color: #fff; }
    .timeline-content h3 { margin: 0 0 4px; font-size: 0.9375rem; color: #1e293b; }
    .timeline-content p { margin: 0 0 8px; font-size: 0.8125rem; color: #64748b; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.8125rem; font-weight: 600; }
    .btn-sm { padding: 6px 14px; font-size: 0.8125rem; }
    .btn-primary { background: #3b82f6; color: #fff; }
    .btn-primary:hover { background: #2563eb; }
    .btn-success { background: #22c55e; color: #fff; }
    .btn-success:hover { background: #16a34a; }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-danger:hover { background: #dc2626; }
    .info-cards { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .info-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.25rem; }
    .info-card h3 { margin: 0 0 12px; font-size: 1rem; color: #1e293b; border-bottom: 1px solid #e2e8f0; padding-bottom: 8px; }
    .info-row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 0.875rem; }
    .info-row span { color: #64748b; }
    .info-row strong { color: #1e293b; }
    .status-badge { padding: 2px 8px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
    .status-badge[data-status="ALLOCATED"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="CONFIRMED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="GRADED"] { background: #dbeafe; color: #1d4ed8; }
    .status-badge[data-status="IN_PROGRESS"] { background: #e0e7ff; color: #3730a3; }
    .alloc-actions { display: flex; gap: 8px; margin-top: 12px; }
    .no-data { color: #94a3b8; font-size: 0.875rem; font-style: italic; }
    .error-banner { background: #fee2e2; border: 1px solid #fca5a5; color: #991b1b; padding: 10px 16px; border-radius: 8px; margin-top: 1rem; font-size: 0.875rem; }
    @media (max-width: 640px) { .info-cards { grid-template-columns: 1fr; } }
  `]
})
export class ApplicantDashboardComponent implements OnInit {
  registration: any = null;
  testResult: any = null;
  allocation: any = null;
  error = '';
  currentStep = '';
  steps: any[] = [];

  constructor(
    private applicantService: ApplicantPortalService,
    private preAdmissionService: PreAdmissionService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.applicantService.getMyRegistration().subscribe({
      next: (reg) => {
        this.registration = reg;
        this.buildSteps();
        if (['ADMIT_CARD_GENERATED', 'TEST_COMPLETED', 'MERIT_PROCESSED', 'ALLOCATED'].includes(reg.status)) {
          this.loadTestResults();
        }
        if (['MERIT_PROCESSED', 'ALLOCATED'].includes(reg.status)) {
          this.loadAllocation();
        }
      },
      error: (err) => { this.error = 'Failed to load registration data'; }
    });
  }

  loadTestResults() {
    this.applicantService.getMyResults().subscribe({
      next: (data) => { this.testResult = data; }
    });
  }

  loadAllocation() {
    this.applicantService.getMyAllocation().subscribe({
      next: (data) => {
        if (data.allocationId) {
          this.allocation = data;
        }
      }
    });
  }

  buildSteps() {
    const s = this.registration.status;
    this.steps = [
      { num: '1', key: 'SUBMITTED', title: 'Registration Submitted', desc: 'Your application has been received', completed: this.isCompleted('SUBMITTED') },
      { num: '2', key: 'ADMIT_CARD_GENERATED', title: 'Admit Card Generated', desc: 'Download your admit card for the admission test', completed: this.isCompleted('ADMIT_CARD_GENERATED'), actionLabel: s === 'ADMIT_CARD_GENERATED' ? 'Download Admit Card' : null, actionClass: 'btn-primary', actionFn: () => this.downloadAdmitCard() },
      { num: '3', key: 'TEST_COMPLETED', title: 'Admission Test', desc: 'Take the online MCQ admission test', completed: this.isCompleted('TEST_COMPLETED'), actionLabel: s === 'ADMIT_CARD_GENERATED' ? 'Take Test' : null, actionClass: 'btn-primary', actionFn: () => window.open('/applicant/test', '_blank') },
      { num: '4', key: 'MERIT_PROCESSED', title: 'Merit Processing', desc: 'Your results are being processed', completed: this.isCompleted('MERIT_PROCESSED') },
      { num: '5', key: 'ALLOCATED', title: 'Department Allocation', desc: 'Your department allocation is ready', completed: this.isCompleted('ALLOCATED'), actionLabel: s === 'ALLOCATED' && this.allocation?.status === 'ALLOCATED' ? 'Review Allocation' : null, actionClass: 'btn-success', actionFn: () => {} },
    ];
    const order = ['DRAFT','SUBMITTED','ADMIT_CARD_GENERATED','TEST_COMPLETED','MERIT_PROCESSED','ALLOCATED','ENROLLED','REJECTED'];
    this.currentStep = this.steps.find(t => t.key === s)?.key || s;
  }

  isCompleted(step: string): boolean {
    const order = ['DRAFT','SUBMITTED','ADMIT_CARD_GENERATED','TEST_COMPLETED','MERIT_PROCESSED','ALLOCATED','ENROLLED'];
    const currentIdx = order.indexOf(this.registration.status);
    const stepIdx = order.indexOf(step);
    return currentIdx > stepIdx;
  }

  downloadAdmitCard() {
    this.preAdmissionService.getAdmitCard(this.registration.id).subscribe({
      next: (html) => {
        const blob = new Blob([html], { type: 'text/html' });
        const url = URL.createObjectURL(blob);
        const w = window.open(url, '_blank');
        if (w) { w.onload = () => w.print(); }
      },
      error: () => { this.error = 'Failed to download admit card'; }
    });
  }

  confirmAlloc() {
    if (this.allocation) {
      this.applicantService.confirmAllocation(this.allocation.allocationId).subscribe({
        next: () => { this.allocation.status = 'CONFIRMED'; this.loadData(); },
        error: () => { this.error = 'Failed to confirm allocation'; }
      });
    }
  }

  declineAlloc() {
    if (this.allocation && confirm('Are you sure you want to decline this allocation?')) {
      this.applicantService.declineAllocation(this.allocation.allocationId).subscribe({
        next: () => { this.allocation.status = 'CANCELLED'; },
        error: () => { this.error = 'Failed to decline allocation'; }
      });
    }
  }
}
