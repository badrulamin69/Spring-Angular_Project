import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PasswordService } from '../../../services/password.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Change Password</h2>
        <p class="page-sub">Update your account password</p>
      </div>
    </div>

    <div class="change-password-card">
      <form (ngSubmit)="changePassword()" #passwordForm="ngForm">
        <div class="form-group">
          <label for="currentPassword">Current Password</label>
          <input type="password" id="currentPassword" [(ngModel)]="currentPassword" name="currentPassword" class="form-control" required>
        </div>
        <div class="form-group">
          <label for="newPassword">New Password</label>
          <input type="password" id="newPassword" [(ngModel)]="newPassword" name="newPassword" class="form-control" required minlength="6">
        </div>
        <div class="form-group">
          <label for="confirmPassword">Confirm New Password</label>
          <input type="password" id="confirmPassword" [(ngModel)]="confirmPassword" name="confirmPassword" class="form-control" required>
        </div>
        @if (errorMessage) {
          <div class="error-message">{{ errorMessage }}</div>
        }
        <div class="actions-bar">
          <button type="button" class="btn btn-secondary" (click)="goBack()">Cancel</button>
          <button type="submit" class="btn btn-primary" [disabled]="saving || !passwordForm.valid">
            @if (saving) {
              <span class="btn-spinner"></span> Updating...
            } @else {
              Update Password
            }
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .change-password-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 24px; max-width: 480px; }
    .form-group { display: flex; flex-direction: column; gap: 5px; margin-bottom: 16px; }
    .form-group label { font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; transition: border-color 0.2s, box-shadow 0.2s; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
    .error-message { color: #ef4444; font-size: 0.8125rem; margin-bottom: 12px; }
    .actions-bar { display: flex; justify-content: flex-end; gap: 10px; margin-top: 8px; }
    .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-secondary { background: var(--bg-primary); color: var(--text-primary); border: 1px solid var(--border-color); }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class ChangePasswordComponent {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  saving = false;
  errorMessage = '';

  constructor(
    private passwordService: PasswordService,
    private router: Router,
    private toastService: ToastService
  ) {}

  changePassword() {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'New passwords do not match';
      return;
    }
    this.saving = true;
    this.errorMessage = '';
    this.passwordService.changePassword(this.currentPassword, this.newPassword).subscribe({
      next: () => {
        this.toastService.success('Password changed successfully');
        this.saving = false;
        setTimeout(() => this.router.navigate(['/dashboard']), 1500);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to change password';
        this.saving = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
