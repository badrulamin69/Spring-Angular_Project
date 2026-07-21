import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (open) {
      <div class="confirm-overlay" (click)="onCancel()">
        <div class="confirm-box" (click)="$event.stopPropagation()">
          <div class="confirm-icon" [class]="'icon-' + type">
            @switch (type) {
              @case ('danger') { ΓÜá }
              @case ('warning') { ! }
              @default { ? }
            }
          </div>
          <h3>{{ title }}</h3>
          <p>{{ message }}</p>
          <div class="confirm-actions">
            <button class="btn btn-secondary" (click)="onCancel()">Cancel</button>
            <button class="btn" [class]="'btn-' + type" (click)="onConfirm()">
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .confirm-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000; backdrop-filter: blur(4px); }
    .confirm-box { background: var(--bg-secondary); border-radius: 16px; padding: 28px; max-width: 380px; width: 90%; text-align: center; box-shadow: 0 20px 60px rgba(0,0,0,0.3); animation: popIn 0.2s ease-out; }
    .confirm-icon { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; margin: 0 auto 16px; }
    .icon-danger { background: #f8d7da; color: #dc3545; }
    .icon-warning { background: #fff3cd; color: #e6a817; }
    .icon-info { background: #d1ecf1; color: #17a2b8; }
    .confirm-box h3 { margin: 0 0 8px; font-size: 1.125rem; color: var(--text-primary); }
    .confirm-box p { margin: 0 0 20px; font-size: 0.875rem; color: var(--text-muted); line-height: 1.5; }
    .confirm-actions { display: flex; gap: 10px; justify-content: center; }
    .btn { padding: 8px 20px; border: none; border-radius: 8px; font-size: 0.875rem; font-weight: 500; cursor: pointer; transition: all 0.15s; }
    .btn-secondary { background: var(--bg-tertiary); color: var(--text-primary); border: 1px solid var(--border-color); }
    .btn-secondary:hover { background: var(--bg-hover-strong); }
    .btn-danger { background: #ef4444; color: #fff; }
    .btn-danger:hover { background: #dc2626; }
    .btn-warning { background: #f59e0b; color: #fff; }
    .btn-warning:hover { background: #d97706; }
    .btn-info { background: #3b82f6; color: #fff; }
    .btn-info:hover { background: #2563eb; }
    @keyframes popIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }

    @media (max-width: 480px) {
      .confirm-box {
        padding: 20px;
        max-width: 95%;
      }
      .confirm-actions {
        flex-direction: column;
      }
      .confirm-actions .btn {
        width: 100%;
      }
    }
  `]
})
export class ConfirmDialogComponent {
  @Input() open = false;
  @Input() title = 'Confirm Action';
  @Input() message = 'Are you sure?';
  @Input() confirmText = 'Confirm';
  @Input() type: 'danger' | 'warning' | 'info' = 'danger';
  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  onConfirm() { this.confirmed.emit(); }
  onCancel() { this.cancelled.emit(); }
}
