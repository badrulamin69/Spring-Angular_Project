import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApplicantChoiceService } from '../../../services/applicant-choice.service';
import { ChoiceFillingConfigService } from '../../../services/choice-filling-config.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ApplicantChoiceSubmission, ApplicantChoice, AvailableProgram } from '../../../models/choice-filling';

@Component({
  selector: 'app-applicant-choice-filling',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="choice-page">
      @if (loading) {
        <div class="loading"><div class="spinner"></div><span>Loading...</span></div>
      } @else if (!activeConfig) {
        <div class="empty-state">
          <div class="empty-icon">
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none"><circle cx="32" cy="32" r="32" fill="#f1f5f9"/><path d="M32 18v28M18 32h28" stroke="#94a3b8" stroke-width="3" stroke-linecap="round"/></svg>
          </div>
          <h2>No Active Choice Filling</h2>
          <p>There is no active choice filling window at this time. Please check back later.</p>
        </div>
      } @else if (submission && submission.status === 'LOCKED') {
        <div class="submitted-view">
          <div class="success-header">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none"><circle cx="24" cy="24" r="24" fill="#d1fae5"/><path d="M16 24l6 6 10-12" stroke="#065f46" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>
            <div>
              <h2>Choices Submitted & Locked</h2>
              <p>Your choices have been locked by the administrator. Contact the admissions office for any changes.</p>
            </div>
          </div>
          <div class="submission-info">
            <div class="info-row"><span>Submission ID:</span><strong>{{ submission.submissionId }}</strong></div>
            <div class="info-row"><span>Status:</span><strong class="status-badge locked">LOCKED</strong></div>
            <div class="info-row"><span>Submitted:</span><strong>{{ submission.submittedAt | date:'medium' }}</strong></div>
          </div>
          @if (choices.length > 0) {
            <div class="choices-section">
              <h3>Your Choices</h3>
              @for (choice of choices; track choice.id) {
                <div class="choice-item readonly">
                  <span class="choice-rank">#{{ choice.priority }}</span>
                  <div class="choice-details">
                    <strong>{{ choice.programName }}</strong>
                    <span>{{ choice.departmentName }} - {{ choice.facultyName }}</span>
                  </div>
                </div>
              }
            </div>
          }
        </div>
      } @else if (submission && submission.status === 'SUBMITTED') {
        <div class="submitted-view">
          <div class="success-header">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none"><circle cx="24" cy="24" r="24" fill="#dbeafe"/><path d="M16 24l6 6 10-12" stroke="#002d5f" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>
            <div>
              <h2>Choices Submitted</h2>
              <p>Your choices have been submitted. You can still edit until the deadline.</p>
            </div>
          </div>
          <div class="submission-info">
            <div class="info-row"><span>Submission ID:</span><strong>{{ submission.submissionId }}</strong></div>
            <div class="info-row"><span>Status:</span><strong class="status-badge submitted">SUBMITTED</strong></div>
            <div class="info-row"><span>Submitted:</span><strong>{{ submission.submittedAt | date:'medium' }}</strong></div>
          </div>
          @if (choices.length > 0) {
            <div class="choices-section">
              <h3>Your Choices ({{ choices.length }}/{{ activeConfig.maxChoices }})</h3>
              @for (choice of choices; track choice.id) {
                <div class="choice-item">
                  <span class="choice-rank">#{{ choice.priority }}</span>
                  <div class="choice-details">
                    <strong>{{ choice.programName }}</strong>
                    <span>{{ choice.departmentName }} - {{ choice.facultyName }}</span>
                  </div>
                  <div class="choice-actions">
                    <button class="btn-icon-sm" (click)="moveChoice(choice.id!, 'up')" [disabled]="choice.priority === 1" title="Move Up">▲</button>
                    <button class="btn-icon-sm" (click)="moveChoice(choice.id!, 'down')" [disabled]="choice.priority === choices.length" title="Move Down">▼</button>
                    <button class="btn-icon-sm btn-remove" (click)="removeChoice(choice.id!)" title="Remove">✕</button>
                  </div>
                </div>
              }
            </div>
          }
          <div class="action-bar">
            <button class="btn btn-primary" (click)="submitChoices()" [disabled]="choices.length < activeConfig.minChoices">
              Final Submit ({{ choices.length }}/{{ activeConfig.minChoices }} min)
            </button>
          </div>
        </div>
      } @else {
        <div class="choice-page-inner">
          <div class="choice-header">
            <div>
              <h2>Fill Your Program Choices</h2>
              <p>Select and prioritize your preferred programs ({{ activeConfig.minChoices }}-{{ activeConfig.maxChoices }} choices)</p>
            </div>
            @if (choices.length >= activeConfig.minChoices) {
              <button class="btn btn-primary" (click)="submitChoices()">
                Submit Choices ({{ choices.length }}/{{ activeConfig.maxChoices }})
              </button>
            }
          </div>

          <div class="two-col">
            <div class="available-programs">
              <h3>Available Programs</h3>
              <div class="search-filter">
                <input type="text" placeholder="Search programs..." [(ngModel)]="programSearch">
                <select [(ngModel)]="facultyFilter">
                  <option value="">All Faculties</option>
                  @for (f of faculties; track f) { <option [value]="f">{{ f }}</option> }
                </select>
              </div>
              <div class="program-list">
                @for (prog of filteredPrograms; track prog.programId) {
                  <div class="program-card" [class.already-added]="isAlreadyAdded(prog.programId!)">
                    <div class="program-info">
                      <strong>{{ prog.programName }}</strong>
                      <span class="program-meta">{{ prog.departmentName }} | {{ prog.facultyName }}</span>
                      <span class="program-meta">{{ prog.programType }} | {{ prog.durationYears }} Years</span>
                    </div>
                    <button class="btn btn-sm btn-add"
                      [disabled]="isAlreadyAdded(prog.programId!) || choices.length >= activeConfig.maxChoices"
                      (click)="addChoice(prog.programId!)">
                      {{ isAlreadyAdded(prog.programId!) ? 'Added' : '+ Add' }}
                    </button>
                  </div>
                }
                @if (filteredPrograms.length === 0) {
                  <p class="no-data">No programs found</p>
                }
              </div>
            </div>

            <div class="my-choices">
              <h3>My Choices ({{ choices.length }}/{{ activeConfig.maxChoices }})</h3>
              @if (choices.length === 0) {
                <div class="empty-choices">
                  <p>No choices added yet. Browse available programs and click "+ Add" to add them.</p>
                </div>
              } @else {
                <div class="choices-list">
                  @for (choice of choices; track choice.id) {
                    <div class="choice-item">
                      <span class="choice-rank">#{{ choice.priority }}</span>
                      <div class="choice-details">
                        <strong>{{ choice.programName }}</strong>
                        <span>{{ choice.departmentName }} - {{ choice.facultyName }}</span>
                      </div>
                      <div class="choice-actions">
                        <button class="btn-icon-sm" (click)="moveChoice(choice.id!, 'up')" [disabled]="choice.priority === 1" title="Move Up">▲</button>
                        <button class="btn-icon-sm" (click)="moveChoice(choice.id!, 'down')" [disabled]="choice.priority === choices.length" title="Move Down">▼</button>
                        <button class="btn-icon-sm btn-remove" (click)="removeChoice(choice.id!)" title="Remove">✕</button>
                      </div>
                    </div>
                  }
                </div>
              }
              @if (choices.length > 0 && choices.length < activeConfig.minChoices) {
                <div class="validation-msg">Minimum {{ activeConfig.minChoices }} choices required ({{ activeConfig.minChoices - choices.length }} more needed)</div>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .choice-page { max-width: 1200px; margin: 0 auto; padding: 1.5rem; }
    .loading { text-align: center; padding: 3rem; }
    .spinner { width: 32px; height: 32px; border: 3px solid #e2e8f0; border-top-color: #4F46E5; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 12px; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .empty-state { text-align: center; padding: 3rem 1rem; }
    .empty-icon { margin-bottom: 1rem; }
    .empty-state h2 { color: #475569; margin: 0.5rem 0; }
    .empty-state p { color: #64748b; font-size: 0.9375rem; }
    .choice-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
    .choice-header h2 { margin: 0; font-size: 1.5rem; color: #1e293b; }
    .choice-header p { margin: 4px 0 0; color: #64748b; font-size: 0.875rem; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: #4F46E5; color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-sm { padding: 4px 10px; font-size: 0.8125rem; }
    .btn-add { background: #28a745; color: #fff; }
    .btn-add:disabled { background: #e2e8f0; color: #94a3b8; cursor: not-allowed; }
    .two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }
    .available-programs, .my-choices { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1rem; }
    .available-programs h3, .my-choices h3 { margin: 0 0 1rem; font-size: 1rem; color: #1e293b; }
    .search-filter { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
    .search-filter input, .search-filter select { padding: 0.375rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.8125rem; }
    .search-filter input { flex: 1; }
    .program-list { max-height: 500px; overflow-y: auto; }
    .program-card { display: flex; justify-content: space-between; align-items: center; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 8px; margin-bottom: 0.5rem; transition: all 0.15s; }
    .program-card:hover { border-color: #4F46E5; background: #f8fafc; }
    .program-card.already-added { opacity: 0.5; background: #f8fafc; }
    .program-info strong { display: block; color: #1e293b; margin-bottom: 2px; }
    .program-meta { font-size: 0.75rem; color: #64748b; display: block; }
    .choices-list { display: flex; flex-direction: column; gap: 0.5rem; }
    .choice-item { display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 8px; background: #fff; }
    .choice-item.readonly { background: #f8fafc; }
    .choice-rank { background: #4F46E5; color: #fff; width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.8125rem; flex-shrink: 0; }
    .choice-details { flex: 1; }
    .choice-details strong { display: block; color: #1e293b; font-size: 0.875rem; }
    .choice-details span { font-size: 0.75rem; color: #64748b; }
    .choice-actions { display: flex; gap: 4px; }
    .btn-icon-sm { width: 28px; height: 28px; border: 1px solid #d1d5db; border-radius: 4px; background: #fff; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.75rem; color: #475569; }
    .btn-icon-sm:hover:not(:disabled) { background: #f1f5f9; }
    .btn-icon-sm:disabled { opacity: 0.3; cursor: not-allowed; }
    .btn-remove { color: #dc3545; border-color: #fca5a5; }
    .btn-remove:hover { background: #fef2f2; }
    .empty-choices { text-align: center; padding: 2rem; color: #94a3b8; font-style: italic; }
    .validation-msg { background: #fef3c7; color: #92400e; padding: 0.5rem 0.75rem; border-radius: 6px; font-size: 0.8125rem; margin-top: 0.75rem; }
    .submitted-view { max-width: 800px; margin: 0 auto; }
    .success-header { display: flex; align-items: center; gap: 1rem; margin-bottom: 1.5rem; }
    .success-header h2 { margin: 0; color: #1e293b; }
    .success-header p { margin: 4px 0 0; color: #64748b; font-size: 0.875rem; }
    .submission-info { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1rem; margin-bottom: 1.5rem; }
    .info-row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 0.875rem; }
    .info-row span { color: #64748b; }
    .info-row strong { color: #1e293b; }
    .status-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .status-badge.locked { background: #d1fae5; color: #065f46; }
    .status-badge.submitted { background: #dbeafe; color: #002d5f; }
    .choices-section { margin-bottom: 1.5rem; }
    .choices-section h3 { margin: 0 0 1rem; color: #1e293b; }
    .action-bar { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1rem; padding-top: 1rem; border-top: 1px solid #e2e8f0; }
    .no-data { color: #94a3b8; font-style: italic; text-align: center; padding: 1rem; }
    @media (max-width: 768px) { .two-col { grid-template-columns: 1fr; } }
  `]
})
export class ApplicantChoiceFillingComponent implements OnInit {
  loading = true;
  activeConfig: any = null;
  submission: ApplicantChoiceSubmission | null = null;
  choices: ApplicantChoice[] = [];
  availablePrograms: AvailableProgram[] = [];
  programSearch = '';
  facultyFilter = '';
  faculties: string[] = [];

  constructor(
    private choiceService: ApplicantChoiceService,
    private configService: ChoiceFillingConfigService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadActiveConfig();
  }

  loadActiveConfig() {
    this.configService.getActiveConfig().subscribe({
      next: (config) => {
        this.activeConfig = config;
        this.loadSubmission();
      },
      error: () => { this.loading = false; }
    });
  }

  loadSubmission() {
    if (!this.activeConfig) { this.loading = false; return; }
    this.choiceService.startSubmission(this.activeConfig.id).subscribe({
      next: (sub) => {
        this.submission = sub;
        this.loadChoices();
        this.loadPrograms();
      },
      error: (err) => { this.loading = false; this.toastService.error(err.error?.message || 'Failed to initialize'); }
    });
  }

  loadChoices() {
    if (!this.submission) return;
    this.choiceService.getMyChoices(this.submission.id!).subscribe({
      next: (choices) => { this.choices = choices; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  loadPrograms() {
    if (!this.activeConfig) return;
    this.choiceService.getAvailablePrograms(this.activeConfig.id).subscribe({
      next: (programs) => {
        this.availablePrograms = programs;
        this.faculties = [...new Set(programs.map(p => p.facultyName || '').filter(f => f.length > 0))];
      },
      error: () => this.toastService.error('Operation failed. Please try again.')
    });
  }

  get filteredPrograms(): AvailableProgram[] {
    return this.availablePrograms.filter(p => {
      const matchesSearch = !this.programSearch ||
        p.programName?.toLowerCase().includes(this.programSearch.toLowerCase()) ||
        p.departmentName?.toLowerCase().includes(this.programSearch.toLowerCase());
      const matchesFaculty = !this.facultyFilter || p.facultyName === this.facultyFilter;
      return matchesSearch && matchesFaculty;
    });
  }

  isAlreadyAdded(programId: number): boolean {
    return this.choices.some(c => c.programId === programId);
  }

  addChoice(programId: number) {
    if (!this.submission?.id) return;
    this.choiceService.addChoice(this.submission.id, programId).subscribe({
      next: () => { this.loadChoices(); this.toastService.success('Choice added'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to add choice')
    });
  }

  removeChoice(choiceId: number) {
    this.choiceService.removeChoice(choiceId).subscribe({
      next: () => { this.loadChoices(); this.toastService.success('Choice removed'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to remove choice')
    });
  }

  moveChoice(choiceId: number, direction: string) {
    this.choiceService.moveChoice(choiceId, direction).subscribe({
      next: () => { this.loadChoices(); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to move choice')
    });
  }

  submitChoices() {
    if (!this.submission) return;
    if (this.choices.length < this.activeConfig.minChoices) {
      this.toastService.error(`Minimum ${this.activeConfig.minChoices} choices required`);
      return;
    }
    if (!confirm('Submit your choices? You may not be able to edit after submission.')) return;
    this.choiceService.submitChoices(this.submission.id!).subscribe({
      next: (sub) => { this.submission = sub; this.toastService.success('Choices submitted successfully'); },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to submit')
    });
  }
}
