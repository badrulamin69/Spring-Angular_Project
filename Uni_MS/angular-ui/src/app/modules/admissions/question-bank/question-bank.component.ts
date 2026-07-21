import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdmissionTestQuestionService } from '../../../services/admission-test-question.service';
import { AdmissionTestService } from '../../../services/admission-test.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';
import { AdmissionTestQuestion } from '../../../models/admission-test-question';

@Component({
  selector: 'app-question-bank',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Question Bank</h2>
        <p class="page-sub">Manage admission test MCQ questions</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">+ Add Question</button>
    </div>

    <div class="filter-bar">
      <label>Filter by Test:</label>
      <select [(ngModel)]="selectedTestId" (change)="loadData()">
        <option [ngValue]="null">All Tests</option>
        @for (t of tests; track t.id) {
          <option [ngValue]="t.id">{{ t.name }}</option>
        }
      </select>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (onEdit)="editQuestion($event)"
      (onDelete)="confirmDelete($event)"
      (refresh)="loadData()"
    ></app-data-table>

    @if (showForm) {
      <div class="modal-overlay" (click)="showForm = false">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editMode ? 'Edit Question' : 'Add Question' }}</h3>
            <button class="close-btn" (click)="showForm = false">&times;</button>
          </div>
          <form (ngSubmit)="saveQuestion()">
            <div class="form-group">
              <label>Test *</label>
              <select [(ngModel)]="formData.testId" name="testId" required>
                <option [ngValue]="null" disabled>Select Test</option>
                @for (t of tests; track t.id) {
                  <option [ngValue]="t.id">{{ t.name }}</option>
                }
              </select>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Subject</label>
                <input type="text" [(ngModel)]="formData.subject" name="subject" placeholder="e.g. Mathematics">
              </div>
              <div class="form-group">
                <label>Difficulty</label>
                <select [(ngModel)]="formData.difficulty" name="difficulty">
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label>Question Text *</label>
              <textarea [(ngModel)]="formData.questionText" name="questionText" required rows="3" placeholder="Enter the question"></textarea>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Option A *</label>
                <input type="text" [(ngModel)]="formData.optionA" name="optionA" required>
              </div>
              <div class="form-group">
                <label>Option B *</label>
                <input type="text" [(ngModel)]="formData.optionB" name="optionB" required>
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Option C *</label>
                <input type="text" [(ngModel)]="formData.optionC" name="optionC" required>
              </div>
              <div class="form-group">
                <label>Option D *</label>
                <input type="text" [(ngModel)]="formData.optionD" name="optionD" required>
              </div>
            </div>
            <div class="form-group">
              <label>Option E (Optional)</label>
              <input type="text" [(ngModel)]="formData.optionE" name="optionE" placeholder="Optional 5th option">
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Correct Option *</label>
                <select [(ngModel)]="formData.correctOption" name="correctOption" required>
                  <option value="">Select</option>
                  <option value="A">A</option>
                  <option value="B">B</option>
                  <option value="C">C</option>
                  <option value="D">D</option>
                  <option value="E">E</option>
                </select>
              </div>
              <div class="form-group">
                <label>Marks *</label>
                <input type="number" [(ngModel)]="formData.marks" name="marks" required min="1" step="1">
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Negative Marks</label>
                <input type="number" [(ngModel)]="formData.negativeMarks" name="negativeMarks" min="0" step="0.25" placeholder="0">
              </div>
              <div class="form-group">
                <label>Question Type</label>
                <select [(ngModel)]="formData.questionType" name="questionType">
                  <option value="MCQ">MCQ</option>
                  <option value="TRUE_FALSE">True/False</option>
                  <option value="SHORT_ANSWER">Short Answer</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label>Explanation</label>
              <textarea [(ngModel)]="formData.explanation" name="explanation" rows="2" placeholder="Answer explanation (optional)"></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="showForm = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving">{{ saving ? 'Saving...' : 'Save' }}</button>
            </div>
          </form>
        </div>
      </div>
    }

    @if (showDeleteConfirm) {
      <app-confirm-dialog
        title="Delete Question"
        message="Are you sure you want to delete this question? This action cannot be undone."
        (confirm)="deleteQuestion()"
        (cancel)="showDeleteConfirm = false"
      ></app-confirm-dialog>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: #1e293b; }
    .page-sub { margin: 0.25rem 0 0; color: #64748b; font-size: 0.875rem; }
    .btn { padding: 0.5rem 1rem; border-radius: 6px; border: none; cursor: pointer; font-size: 0.875rem; font-weight: 500; }
    .btn-primary { background: #4F46E5; color: white; }
    .btn-primary:hover { background: #4338CA; }
    .btn-secondary { background: #e2e8f0; color: #475569; }
    .btn-danger { background: #dc3545; color: white; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar label { font-weight: 500; color: #475569; font-size: 0.875rem; }
    .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; padding: 1.5rem; width: 90%; max-width: 600px; max-height: 90vh; overflow-y: auto; }
    .modal-lg { max-width: 700px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; color: #1e293b; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b; }
    .form-group { margin-bottom: 1rem; }
    .form-group label { display: block; margin-bottom: 0.25rem; font-weight: 500; color: #374151; font-size: 0.875rem; }
    .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.875rem; box-sizing: border-box; }
    .form-group textarea { resize: vertical; }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { outline: none; border-color: #4F46E5; box-shadow: 0 0 0 2px rgba(79,70,229,0.1); }
    .form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #e2e8f0; }
  `]
})
export class QuestionBankComponent implements OnInit {
  @ViewChild(DataTableComponent) dataTable!: DataTableComponent;

  pagedData: PagedResponse<AdmissionTestQuestion> | null = null;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  loading = false;
  showForm = false;
  showDeleteConfirm = false;
  editMode = false;
  saving = false;
  selectedTestId: number | null = null;
  selectedItem: AdmissionTestQuestion | null = null;
  tests: any[] = [];

  formData: any = {
    questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', optionE: '',
    correctOption: '', marks: 1, negativeMarks: 0, testId: null,
    subject: '', difficulty: 'MEDIUM', explanation: '', questionType: 'MCQ'
  };

  columns: TableColumn[] = [
    { key: 'id', label: 'ID' },
    { key: 'questionText', label: 'Question' },
    { key: 'subject', label: 'Subject' },
    { key: 'difficulty', label: 'Difficulty' },
    { key: 'correctOption', label: 'Answer' },
    { key: 'marks', label: 'Marks' },
    { key: 'negativeMarks', label: 'Neg. Marks' }
  ];

  constructor(
    private questionService: AdmissionTestQuestionService,
    private testService: AdmissionTestService,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.loadTests();
    this.loadData();
  }

  loadTests() {
    this.testService.getForDropdown().subscribe({
      next: (tests) => { this.tests = tests; },
      error: () => { this.tests = []; }
    });
  }

  loadData() {
    this.loading = true;
    const req = this.questionService.findAll(this.params, this.selectedTestId || undefined, '');
    req.subscribe({
      next: (res: any) => {
        this.pagedData = res.content ? res : { content: res, pageNumber: 0, pageSize: 20, totalElements: res.length, totalPages: 1, first: true, last: true };
        this.loading = false;
      },
      error: () => { this.loading = false; this.toast.error('Failed to load questions'); }
    });
  }

  onPageChange(pageParams: PageParams) {
    this.params = pageParams;
    this.loadData();
  }

  openForm() {
    this.editMode = false;
    this.formData = {
      questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', optionE: '',
      correctOption: '', marks: 1, negativeMarks: 0, testId: this.selectedTestId,
      subject: '', difficulty: 'MEDIUM', explanation: '', questionType: 'MCQ'
    };
    this.showForm = true;
  }

  editQuestion(q: AdmissionTestQuestion) {
    this.editMode = true;
    this.selectedItem = q;
    this.formData = {
      questionText: q.questionText, optionA: q.optionA, optionB: q.optionB,
      optionC: q.optionC, optionD: q.optionD, optionE: q.optionE || '',
      correctOption: q.correctOption, marks: q.marks, negativeMarks: q.negativeMarks || 0,
      testId: q.testId || q.test?.id || null,
      subject: q.subject || '', difficulty: q.difficulty || 'MEDIUM',
      explanation: q.explanation || '', questionType: q.questionType || 'MCQ'
    };
    this.showForm = true;
  }

  saveQuestion() {
    this.saving = true;
    const payload: any = { ...this.formData };
    if (this.formData.testId) {
      payload.test = { id: this.formData.testId };
    }
    const req = this.editMode && this.selectedItem?.id
      ? this.questionService.update(this.selectedItem.id, payload)
      : this.questionService.save(payload);
    req.subscribe({
      next: () => {
        this.toast.success(this.editMode ? 'Question updated' : 'Question created');
        this.showForm = false;
        this.saving = false;
        this.loadData();
      },
      error: () => { this.saving = false; this.toast.error('Failed to save question'); }
    });
  }

  confirmDelete(q: AdmissionTestQuestion) {
    this.selectedItem = q;
    this.showDeleteConfirm = true;
  }

  deleteQuestion() {
    if (!this.selectedItem?.id) return;
    this.questionService.delete(this.selectedItem.id).subscribe({
      next: () => {
        this.toast.success('Question deleted');
        this.showDeleteConfirm = false;
        this.loadData();
      },
      error: () => { this.toast.error('Failed to delete question'); }
    });
  }
}
