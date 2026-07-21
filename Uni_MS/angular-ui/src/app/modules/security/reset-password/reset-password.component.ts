import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PasswordService } from '../../../services/password.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h1>Reset Password</h1>
          <p>Enter your new password below</p>
        </div>

        @if (!resetSuccess) {
          <form (ngSubmit)="resetPassword()" #resetForm="ngForm">
            <div class="form-group">
              <label for="newPassword">New Password</label>
              <input type="password" id="newPassword" [(ngModel)]="newPassword" name="newPassword"
                     class="form-control" placeholder="Enter new password" required minlength="6">
            </div>
            <div class="form-group">
              <label for="confirmPassword">Confirm Password</label>
              <input type="password" id="confirmPassword" [(ngModel)]="confirmPassword" name="confirmPassword"
                     class="form-control" placeholder="Confirm new password" required>
            </div>
            @if (errorMessage) {
              <div class="error-message">{{ errorMessage }}</div>
            }
            <button type="submit" class="btn btn-primary btn-block" [disabled]="resetting || !resetForm.valid">
              @if (resetting) {
                <span class="btn-spinner"></span> Resetting...
              } @else {
                Reset Password
              }
            </button>
          </form>
        } @else {
          <div class="success-state">
            <div class="success-icon">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                <circle cx="24" cy="24" r="24" fill="#dcfce7"/>
                <path d="M16 24l5 5 11-11" stroke="#1e7e34" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>Password Reset Successfully</h3>
            <p>Your password has been updated. You can now log in with your new password.</p>
          </div>
        }

        <div class="auth-footer">
          <a routerLink="/login" class="back-link">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M10 12L6 8l4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
            Back to Login
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-container { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: var(--bg-primary); padding: 1rem; }
    .auth-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 16px; padding: 2rem; width: 100%; max-width: 420px; }
    .auth-header { text-align: center; margin-bottom: 1.5rem; }
    .auth-logo { display: flex; justify-content: center; margin-bottom: 1rem; }
    .auth-logo-img { width: 64px; height: 64px; object-fit: contain; }
    .auth-header h1 { margin: 0 0 0.5rem; font-size: 1.5rem; color: var(--text-primary); }
    .auth-header p { margin: 0; font-size: 0.875rem; color: var(--text-muted); }
    .form-group { margin-bottom: 1rem; }
    .form-group label { display: block; font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; margin-bottom: 4px; }
    .form-control { width: 100%; padding: 10px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; transition: border-color 0.2s; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
    .error-message { color: #dc3545; font-size: 0.8125rem; margin-bottom: 12px; }
    .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; justify-content: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-block { width: 100%; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .success-state { text-align: center; padding: 1rem 0; }
    .success-icon { margin-bottom: 1rem; }
    .success-state h3 { margin: 0 0 0.5rem; color: var(--text-primary); }
    .success-state p { margin: 0; color: var(--text-muted); font-size: 0.875rem; }
    .auth-footer { text-align: center; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid var(--border-color); }
    .back-link { display: inline-flex; align-items: center; gap: 4px; color: var(--text-muted); text-decoration: none; font-size: 0.875rem; }
    .back-link:hover { color: var(--brand-color); }
  `]
})
export class ResetPasswordComponent implements OnInit {
  token = '';
  newPassword = '';
  confirmPassword = '';
  resetting = false;
  errorMessage = '';
  resetSuccess = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private passwordService: PasswordService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      this.errorMessage = 'Invalid or missing reset token.';
    }
  }

  resetPassword() {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }
    this.resetting = true;
    this.errorMessage = '';
    this.passwordService.resetPassword(this.token, this.newPassword).subscribe({
      next: () => {
        this.resetSuccess = true;
        this.resetting = false;
        this.toastService.success('Password reset successfully');
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to reset password. The link may have expired.';
        this.resetting = false;
      }
    });
  }
}
