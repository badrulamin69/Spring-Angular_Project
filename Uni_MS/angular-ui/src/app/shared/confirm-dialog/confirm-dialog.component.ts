import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (open) {
      <div class="confirm-overlay" (click)="onCancel()">
        <div class="confirm-box" [class.wide]="mode === 'workflow'" (click)="$event.stopPropagation()">
          <div class="confirm-icon" [class]="'icon-' + type">
            @switch (type) {
              @case ('danger') { ΓÜá }
              @case ('warning') { ! }
              @case ('success') { ✓ }
              @default { ? }
            }
          </div>
          <h3>{{ title }}</h3>
          <p>{{ message }}</p>

          @if (mode === 'workflow') {
            <div class="workflow-section">
              <div class="workflow-actions">
                <button class="btn btn-success" (click)="onApprove()" [disabled]="loading">
                  {{ approveText }}
                </button>
                <button class="btn btn-danger" (click)="onReject()" [disabled]="loading">
                  {{ rejectText }}
                </button>
              </div>
              @if (requireReason) {
                <div class="reason-field">
                  <label>{{ reasonLabel }}</label>
                  <textarea [(ngModel)]="reason" rows="3" [placeholder]="reasonPlaceholder"></textarea>
                </div>
              }
            </div>
          } @else {
            <div class="confirm-actions">
              <button class="btn btn-secondary" (click)="onCancel()">{{ cancelText }}</button>
              <button class="btn" [class]="'btn-' + type" (click)="onConfirm()" [disabled]="loading">
                {{ loading ? loadingText : confirmText }}
              </button>
            </div>
          }
        </div>
      </div>
    }
  `,
  styles: [`
    .confirm-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000; backdrop-filter: blur(4px); }
    .confirm-box { background: var(--bg-secondary); border-radius: 16px; padding: 28px; max-width: 380px; width: 90%; text-align: center; box-shadow: 0 20px 60px rgba(0,0,0,0.3); animation: popIn 0.2s ease-out; }
    .confirm-box.wide { max-width: 480px; }
    .confirm-icon { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; margin: 0 auto 16px; }
    .icon-danger { background: #f8d7da; color: #dc3545; }
    .icon-warning { background: #fff3cd; color: #e6a817; }
    .icon-success { background: #d4edda; color: #28a745; }
    .icon-info { background: #d1ecf1; color: #17a2b8; }
    .confirm-box h3 { margin: 0 0 8px; font-size: 1.125rem; color: var(--text-primary); }
    .confirm-box p { margin: 0 0 20px; font-size: 0.875rem; color: var(--text-muted); line-height: 1.5; }
    .confirm-actions { display: flex; gap: 10px; justify-content: center; }
    .workflow-section { display: flex; flex-direction: column; gap: 16px; }
    .workflow-actions { display: flex; gap: 10px; justify-content: center; }
    .reason-field { text-align: left; }
    .reason-field label { display: block; font-size: 0.8125rem; font-weight: 500; color: var(--text-primary); margin-bottom: 6px; }
    .reason-field textarea { width: 100%; padding: 10px; border: 1px solid var(--border-color); border-radius: 8px; font-size: 0.875rem; background: var(--bg-primary); color: var(--text-primary); resize: vertical; }
    .reason-field textarea:focus { outline: none; border-color: var(--accent); }
    .btn { padding: 8px 20px; border: none; border-radius: 8px; font-size: 0.875rem; font-weight: 500; cursor: pointer; transition: all 0.15s; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-secondary { background: var(--bg-tertiary); color: var(--text-primary); border: 1px solid var(--border-color); }
    .btn-secondary:hover { background: var(--bg-hover-strong); }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-danger:hover { background: #dc2626; }
    .btn-warning { background: #f59e0b; color: #fff; }
    .btn-warning:hover { background: #d97706; }
    .btn-success { background: #22c55e; color: #fff; }
    .btn-success:hover { background: #16a34a; }
    .btn-info { background: #3b82f6; color: #fff; }
    .btn-info:hover { background: #2563eb; }
    @keyframes popIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
    @media (max-width: 480px) {
      .confirm-box { padding: 20px; max-width: 95%; }
      .confirm-actions, .workflow-actions { flex-direction: column; }
      .confirm-actions .btn, .workflow-actions .btn { width: 100%; }
    }
  `]
})
export class ConfirmDialogComponent {
  @Input() open = false;
  @Input() title = 'Confirm Action';
  @Input() message = 'Are you sure?';
  @Input() confirmText = 'Confirm';
  @Input() type: 'danger' | 'warning' | 'info' | 'success' = 'danger';
  @Input() mode: 'confirm' | 'workflow' = 'confirm';
  @Input() approveText = 'Approve';
  @Input() rejectText = 'Reject';
  @Input() requireReason = false;
  @Input() reasonLabel = 'Reason (required for rejection)';
  @Input() reasonPlaceholder = 'Enter reason...';
  @Input() cancelText = 'Cancel';
  @Input() loading = false;
  @Input() loadingText = 'Processing...';

  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();
  @Output() approved = new EventEmitter<string>();
  @Output() rejected = new EventEmitter<string>();

  reason = '';

  onConfirm() { this.confirmed.emit(); }
  onCancel() { this.cancelled.emit(); this.reason = ''; }

  onApprove() {
    if (this.loading) return;
    this.approved.emit(this.reason);
    this.reason = '';
  }

  onReject() {
    if (this.loading) return;
    if (this.requireReason && !this.reason.trim()) return;
    this.rejected.emit(this.reason);
    this.reason = '';
  }
}
