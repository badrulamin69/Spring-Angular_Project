import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdvisorApprovalService } from '../../../services/advisor-approval.service';
import { SemesterService } from '../../../services/semester.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-advisor-approval',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Advisor Approval</h2>
        <p class="page-sub">Review and approve student course registrations</p>
      </div>
    </div>

    <div class="filter-bar card">
      <div class="form-row">
        <div class="form-group">
          <label>Semester *</label>
          <select [(ngModel)]="semesterId" class="form-control" (change)="loadPending()">
            <option value="">Select Semester</option>
            @for (sem of semesters; track sem.id) {
              <option [ngValue]="sem.id">{{ sem.name }}</option>
            }
          </select>
        </div>
        <div class="form-group" style="justify-content: flex-end;">
          <button class="btn btn-success" (click)="approveAll()" [disabled]="!selectedIds.length">
            Approve Selected ({{ selectedIds.length }})
          </button>
          <button class="btn btn-danger" (click)="rejectAll()" [disabled]="!selectedIds.length">
            Reject Selected ({{ selectedIds.length }})
          </button>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th><input type="checkbox" (change)="toggleAll($event)"></th>
              <th>Student</th>
              <th>Code</th>
              <th>Course</th>
              <th>Credits</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (reg of pendingApprovals; track reg.id) {
              <tr>
                <td><input type="checkbox" [checked]="selectedIds.includes(reg.id)" (change)="toggleSelect(reg.id)"></td>
                <td>{{ reg.student?.firstName }} {{ reg.student?.lastName }}</td>
                <td>{{ reg.student?.studentCode }}</td>
                <td>{{ reg.course?.name }}</td>
                <td>{{ reg.creditHours }}</td>
                <td><span class="badge badge-warning">{{ reg.advisorStatus }}</span></td>
                <td>
                  <div class="actions">
                    <button class="btn btn-success btn-sm" (click)="approve(reg.id)">Approve</button>
                    <button class="btn btn-danger btn-sm" (click)="reject(reg.id)">Reject</button>
                  </div>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="7" class="text-center text-muted">
                @if (semesterId) {
                  No pending approvals for this semester
                } @else {
                  Select a semester to view pending approvals
                }
              </td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>

    @if (showCommentModal) {
      <div class="modal-overlay" (click)="closeCommentModal()">
        <div class="modal-content modal-sm" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ commentAction === 'APPROVE' ? 'Approve' : 'Reject' }} Registration</h3>
            <button class="btn-close" (click)="closeCommentModal()">&times;</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Comments (optional)</label>
              <textarea [(ngModel)]="commentText" class="form-control" rows="3" placeholder="Add comments..."></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" (click)="closeCommentModal()">Cancel</button>
            <button class="btn {{ commentAction === 'APPROVE' ? 'btn-success' : 'btn-danger' }}" (click)="submitComment()">
              {{ commentAction === 'APPROVE' ? 'Approve' : 'Reject' }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-success { background: #10b981; color: #fff; }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .btn-sm { padding: 4px 10px; font-size: 0.75rem; }
    .card { background: var(--card-bg); border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; margin-bottom: 1rem; }
    .filter-bar { padding: 16px; }
    .form-row { display: flex; gap: 16px; align-items: flex-end; }
    .form-group { display: flex; flex-direction: column; gap: 4px; flex: 1; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: var(--text-secondary); }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 0.875rem; background: var(--card-bg); color: var(--text-primary); }
    .form-control:focus { outline: none; border-color: var(--brand-color); }
    .table-responsive { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid var(--border-color); font-size: 0.875rem; }
    .data-table th { background: var(--bg-secondary); font-weight: 600; color: var(--text-secondary); }
    .data-table tr:hover { background: var(--bg-secondary); }
    .actions { display: flex; gap: 4px; }
    .badge { padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #d1fae5; color: #065f46; }
    .badge-warning { background: #fef3c7; color: #92400e; }
    .text-center { text-align: center; }
    .text-muted { color: var(--text-muted); }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: var(--card-bg); border-radius: 12px; width: 90%; max-width: 400px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid var(--border-color); }
    .modal-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .btn-close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--text-muted); }
    .modal-body { padding: 20px; }
    .modal-footer { display: flex; justify-content: flex-end; gap: 8px; padding: 16px 20px; border-top: 1px solid var(--border-color); }
  `]
})
export class AdvisorApprovalComponent implements OnInit {
  semesters: any[] = [];
  semesterId: number | null = null;
  pendingApprovals: any[] = [];
  selectedIds: number[] = [];
  showCommentModal = false;
  commentAction = '';
  commentText = '';
  commentTargetId: number | null = null;

  constructor(
    private advisorService: AdvisorApprovalService,
    private semesterService: SemesterService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.semesterService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }).subscribe({
      next: (data) => { this.semesters = data.content || []; },
      error: () => {}
    });
  }

  loadPending() {
    if (!this.semesterId) return;
    this.advisorService.getPendingApprovals(this.semesterId).subscribe({
      next: (data) => { this.pendingApprovals = data; this.selectedIds = []; },
      error: () => this.toastService.error('Failed to load pending approvals')
    });
  }

  toggleAll(event: any) {
    if (event.target.checked) {
      this.selectedIds = this.pendingApprovals.map(r => r.id);
    } else {
      this.selectedIds = [];
    }
  }

  toggleSelect(id: number) {
    if (this.selectedIds.includes(id)) {
      this.selectedIds = this.selectedIds.filter(i => i !== id);
    } else {
      this.selectedIds.push(id);
    }
  }

  approve(id: number) {
    this.commentAction = 'APPROVE';
    this.commentTargetId = id;
    this.commentText = '';
    this.showCommentModal = true;
  }

  reject(id: number) {
    this.commentAction = 'REJECT';
    this.commentTargetId = id;
    this.commentText = '';
    this.showCommentModal = true;
  }

  approveAll() {
    this.commentAction = 'APPROVE';
    this.commentTargetId = null;
    this.commentText = '';
    this.showCommentModal = true;
  }

  rejectAll() {
    this.commentAction = 'REJECT';
    this.commentTargetId = null;
    this.commentText = '';
    this.showCommentModal = true;
  }

  closeCommentModal() {
    this.showCommentModal = false;
    this.commentTargetId = null;
  }

  submitComment() {
    const ids = this.commentTargetId ? [this.commentTargetId] : this.selectedIds;
    this.advisorService.processApproval({
      registrationIds: ids,
      action: this.commentAction,
      comments: this.commentText
    }).subscribe({
      next: () => {
        this.closeCommentModal();
        this.loadPending();
        this.toastService.success(`Registration ${this.commentAction.toLowerCase()}d`);
      },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to process')
    });
  }
}
