import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApplicantPortalService } from '../../../services/applicant-portal.service';

@Component({
  selector: 'app-applicant-test',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="test-page">
      @if (loading) {
        <div class="loading"><div class="spinner"></div><span>Loading test...</span></div>
      } @else if (submitted) {
        <div class="result-panel">
          <div class="result-icon">
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none"><circle cx="32" cy="32" r="32" fill="#d1fae5"/><path d="M20 32l8 8 16-16" stroke="#059669" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <h2>Test Submitted!</h2>
          <div class="result-stats">
            <div class="stat"><span class="stat-val">{{ result.score }}</span><span class="stat-lbl">Score</span></div>
            <div class="stat"><span class="stat-val">{{ result.percentage | number:'1.1-1' }}%</span><span class="stat-lbl">Percentage</span></div>
            <div class="stat"><span class="stat-val">{{ result.correctAnswers }}/{{ result.totalQuestions }}</span><span class="stat-lbl">Correct</span></div>
          </div>
          <p>You can close this window and check your results on the applicant dashboard.</p>
        </div>
      } @else if (!testInfo?.testId) {
        <div class="no-test">
          <h2>No Test Available</h2>
          <p>There is no admission test available for you at this time. Please check back later.</p>
        </div>
      } @else if (!attemptStarted) {
        <div class="start-panel">
          <h2>{{ testInfo.testName }}</h2>
          <div class="test-info">
            <div class="info-item"><span>Total Marks:</span><strong>{{ testInfo.totalMarks }}</strong></div>
            <div class="info-item"><span>Passing Marks:</span><strong>{{ testInfo.passingMarks }}</strong></div>
            <div class="info-item"><span>Test Date:</span><strong>{{ testInfo.testDate }}</strong></div>
            <div class="info-item"><span>Questions:</span><strong>{{ questions.length }}</strong></div>
          </div>
          <p class="test-desc">{{ testInfo.description }}</p>
          <div class="start-warning">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 1v10M8 14v1" stroke="#e6a817" stroke-width="2" stroke-linecap="round"/></svg>
            <span>Once you start, the timer cannot be paused. Make sure you have a stable internet connection.</span>
          </div>
          <button class="btn btn-primary btn-lg" (click)="startTest()">Start Test</button>
        </div>
      } @else {
        <div class="test-active">
          <div class="test-topbar">
            <div class="timer" [class.warning]="timeRemaining < 300">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><circle cx="8" cy="8" r="7" stroke="currentColor" stroke-width="1.5"/><path d="M8 4v4l3 2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              {{ formatTime(timeRemaining) }}
            </div>
            <div class="progress-text">{{ answeredCount }}/{{ questions.length }} answered</div>
            <button class="btn btn-success" (click)="confirmSubmit()">Submit Test</button>
          </div>

          <div class="test-body">
            <div class="question-nav">
              @for (q of questions; track q.id; let i = $index) {
                <button class="q-btn" [class.active]="currentQuestion === i" [class.answered]="answers[q.id]" (click)="currentQuestion = i">
                  {{ i + 1 }}
                </button>
              }
            </div>

            <div class="question-panel">
              @if (questions[currentQuestion]) {
                <div class="q-header">
                  <span class="q-num">Question {{ currentQuestion + 1 }} of {{ questions.length }}</span>
                  <span class="q-marks">{{ questions[currentQuestion].marks }} mark(s)</span>
                </div>
                <p class="q-text">{{ questions[currentQuestion].questionText }}</p>
                <div class="options">
                  @for (opt of ['A', 'B', 'C', 'D']; track opt) {
                    <button class="option" [class.selected]="answers[questions[currentQuestion].id] === opt" (click)="selectAnswer(questions[currentQuestion].id, opt)">
                      <span class="opt-letter">{{ opt }}</span>
                      <span class="opt-text">{{ getOptionText(questions[currentQuestion], opt) }}</span>
                    </button>
                  }
                </div>
                <div class="q-nav-buttons">
                  <button class="btn btn-outline" [disabled]="currentQuestion === 0" (click)="currentQuestion = currentQuestion - 1">Previous</button>
                  @if (currentQuestion < questions.length - 1) {
                    <button class="btn btn-primary" (click)="currentQuestion = currentQuestion + 1">Next</button>
                  }
                </div>
              }
            </div>
          </div>
        </div>
      }

      @if (showSubmitConfirm) {
        <div class="modal-overlay" (click)="showSubmitConfirm = false">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <h3>Submit Test?</h3>
            <p>You have answered {{ answeredCount }} out of {{ questions.length }} questions.</p>
            @if (answeredCount < questions.length) {
              <p class="warning">You have {{ questions.length - answeredCount }} unanswered questions.</p>
            }
            <div class="modal-actions">
              <button class="btn btn-outline" (click)="showSubmitConfirm = false">Cancel</button>
              <button class="btn btn-success" (click)="submitTest()">Submit</button>
            </div>
          </div>
        </div>
      }

      @if (error) {
        <div class="error-banner">{{ error }}</div>
      }
    </div>
  `,
  styles: [`
    .test-page { max-width: 900px; margin: 0 auto; padding: 1.5rem; }
    .loading, .no-test, .start-panel, .result-panel { text-align: center; padding: 3rem 1rem; }
    .spinner { width: 32px; height: 32px; border: 3px solid #e2e8f0; border-top-color: #0056b3; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 12px; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .test-info { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin: 1.5rem 0; }
    .info-item { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 12px; text-align: center; }
    .info-item span { display: block; font-size: 0.75rem; color: #64748b; margin-bottom: 4px; }
    .info-item strong { font-size: 1.25rem; color: #1e293b; }
    .test-desc { color: #64748b; font-size: 0.875rem; margin: 1rem 0; }
    .start-warning { background: #fef3c7; border: 1px solid #fcd34d; border-radius: 8px; padding: 10px 14px; display: flex; align-items: center; gap: 8px; margin: 1.5rem 0; font-size: 0.8125rem; color: #92400e; }
    .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 600; }
    .btn-primary { background: #0056b3; color: #fff; }
    .btn-success { background: #28a745; color: #fff; }
    .btn-outline { background: #fff; color: #374151; border: 1px solid #d1d5db; }
    .btn-lg { padding: 12px 28px; font-size: 1rem; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .test-topbar { display: flex; justify-content: space-between; align-items: center; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 12px 16px; margin-bottom: 1rem; position: sticky; top: 0; z-index: 10; }
    .timer { display: flex; align-items: center; gap: 6px; font-size: 1.125rem; font-weight: 700; color: #1e293b; font-variant-numeric: tabular-nums; }
    .timer.warning { color: #dc3545; }
    .progress-text { font-size: 0.875rem; color: #64748b; }
    .test-body { display: grid; grid-template-columns: 60px 1fr; gap: 1rem; }
    .question-nav { display: flex; flex-direction: column; gap: 4px; }
    .q-btn { width: 36px; height: 36px; border: 1px solid #e2e8f0; border-radius: 8px; background: #fff; cursor: pointer; font-size: 0.8125rem; font-weight: 600; color: #64748b; }
    .q-btn.active { border-color: #0056b3; background: #eff6ff; color: #0056b3; }
    .q-btn.answered { background: #0056b3; color: #fff; border-color: #0056b3; }
    .question-panel { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.5rem; }
    .q-header { display: flex; justify-content: space-between; margin-bottom: 12px; }
    .q-num { font-size: 0.8125rem; color: #64748b; font-weight: 500; }
    .q-marks { font-size: 0.8125rem; color: #0056b3; font-weight: 600; }
    .q-text { font-size: 1.0625rem; color: #1e293b; line-height: 1.6; margin-bottom: 1.5rem; }
    .options { display: flex; flex-direction: column; gap: 10px; }
    .option { display: flex; align-items: center; gap: 12px; padding: 14px 16px; border: 2px solid #e2e8f0; border-radius: 10px; background: #fff; cursor: pointer; text-align: left; transition: all 0.15s; }
    .option:hover { border-color: #93c5fd; background: #f0f9ff; }
    .option.selected { border-color: #0056b3; background: #eff6ff; }
    .opt-letter { width: 32px; height: 32px; border-radius: 50%; background: #f1f5f9; display: flex; align-items: center; justify-content: center; font-weight: 700; color: #475569; font-size: 0.875rem; flex-shrink: 0; }
    .option.selected .opt-letter { background: #0056b3; color: #fff; }
    .opt-text { font-size: 0.9375rem; color: #1e293b; }
    .q-nav-buttons { display: flex; justify-content: space-between; margin-top: 1.5rem; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
    .modal-content { background: #fff; border-radius: 16px; padding: 2rem; max-width: 400px; width: 100%; text-align: center; }
    .modal-content h3 { margin: 0 0 8px; color: #1e293b; }
    .modal-content p { margin: 4px 0; font-size: 0.875rem; color: #64748b; }
    .warning { color: #dc3545 !important; font-weight: 600; }
    .modal-actions { display: flex; gap: 10px; justify-content: center; margin-top: 1.5rem; }
    .result-panel h2 { margin: 1rem 0; color: #065f46; }
    .result-stats { display: flex; gap: 2rem; justify-content: center; margin: 2rem 0; }
    .stat { text-align: center; }
    .stat-val { display: block; font-size: 2rem; font-weight: 800; color: #1e293b; }
    .stat-lbl { font-size: 0.8125rem; color: #64748b; }
    .error-banner { background: #fee2e2; border: 1px solid #fca5a5; color: #991b1b; padding: 10px 16px; border-radius: 8px; margin-top: 1rem; font-size: 0.875rem; }
  `]
})
export class ApplicantTestComponent implements OnInit, OnDestroy {
  loading = true;
  testInfo: any = null;
  questions: any[] = [];
  answers: { [questionId: string]: string } = {};
  currentQuestion = 0;
  attemptStarted = false;
  attemptId: number | null = null;
  showSubmitConfirm = false;
  submitted = false;
  result: any = null;
  error = '';
  timeRemaining = 0;
  timerInterval: any = null;

  constructor(private applicantService: ApplicantPortalService) {}

  ngOnInit() {
    this.loadTestInfo();
  }

  ngOnDestroy() {
    if (this.timerInterval) { clearInterval(this.timerInterval); }
  }

  loadTestInfo() {
    this.applicantService.getMyTest().subscribe({
      next: (data) => {
        this.testInfo = data;
        if (data.testId) {
          this.loadQuestions(data.testId);
        } else {
          this.loading = false;
        }
      },
      error: () => { this.loading = false; this.error = 'Failed to load test info'; }
    });
  }

  loadQuestions(testId: number) {
    this.applicantService.getTestQuestions(testId).subscribe({
      next: (q) => { this.questions = q; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load questions'; }
    });
  }

  startTest() {
    this.applicantService.startTest(this.testInfo.testId).subscribe({
      next: (data) => {
        this.attemptId = data.attemptId;
        this.attemptStarted = true;
        this.timeRemaining = this.questions.length * 60;
        this.startTimer();
      },
      error: () => { this.error = 'Failed to start test'; }
    });
  }

  startTimer() {
    this.timerInterval = setInterval(() => {
      this.timeRemaining--;
      if (this.timeRemaining <= 0) {
        clearInterval(this.timerInterval);
        this.submitTest();
      }
    }, 1000);
  }

  formatTime(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  }

  getOptionText(question: any, opt: string): string {
    const map: { [key: string]: string } = { A: question.optionA, B: question.optionB, C: question.optionC, D: question.optionD };
    return map[opt] || '';
  }

  selectAnswer(questionId: string, option: string) {
    this.answers[questionId] = option;
  }

  get answeredCount(): number {
    return Object.keys(this.answers).length;
  }

  confirmSubmit() {
    this.showSubmitConfirm = true;
  }

  submitTest() {
    this.showSubmitConfirm = false;
    if (this.timerInterval) { clearInterval(this.timerInterval); }
    this.applicantService.submitTest(this.attemptId!, this.answers).subscribe({
      next: (data) => { this.submitted = true; this.result = data; },
      error: () => { this.error = 'Failed to submit test'; }
    });
  }
}
