import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PasswordService } from '../../../services/password.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h1>Forgot Password</h1>
          <p>Enter your email address and we'll send you a reset link</p>
        </div>

        @if (!emailSent) {
          <form (ngSubmit)="sendResetLink()" #forgotForm="ngForm">
            <div class="form-group">
              <label for="email">Email Address</label>
              <input type="email" id="email" [(ngModel)]="email" name="email" class="form-control"
                     placeholder="you@university.edu" required email>
            </div>
            @if (errorMessage) {
              <div class="error-message">{{ errorMessage }}</div>
            }
            <button type="submit" class="btn btn-primary btn-block" [disabled]="sending || !forgotForm.valid">
              @if (sending) {
                <span class="btn-spinner"></span> Sending...
              } @else {
                Send Reset Link
              }
            </button>
          </form>
        } @else {
          <div class="success-state">
            <div class="success-icon">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                <circle cx="24" cy="24" r="24" fill="#dcfce7"/>
                <path d="M16 24l5 5 11-11" stroke="#16a34a" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>Check Your Email</h3>
            <p>We've sent a password reset link to <strong>{{ email }}</strong></p>
            <p class="small">Didn't receive the email? Check your spam folder or try again.</p>
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
    .logo-mark { width: 48px; height: 48px; border-radius: 12px; object-fit: contain; }
    .auth-header h1 { margin: 0 0 0.5rem; font-size: 1.5rem; color: var(--text-primary); }
    .auth-header p { margin: 0; font-size: 0.875rem; color: var(--text-muted); }
    .form-group { margin-bottom: 1rem; }
    .form-group label { display: block; font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; margin-bottom: 4px; }
    .form-control { width: 100%; padding: 10px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; transition: border-color 0.2s; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
    .error-message { color: #ef4444; font-size: 0.8125rem; margin-bottom: 12px; }
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
    .success-state p { margin: 0 0 0.5rem; color: var(--text-muted); font-size: 0.875rem; }
    .success-state .small { font-size: 0.8125rem; color: var(--text-muted); }
    .auth-footer { text-align: center; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid var(--border-color); }
    .back-link { display: inline-flex; align-items: center; gap: 4px; color: var(--text-muted); text-decoration: none; font-size: 0.875rem; }
    .back-link:hover { color: var(--brand-color); }
  `]
})
export class ForgotPasswordComponent {
  email = '';
  sending = false;
  errorMessage = '';
  emailSent = false;

  constructor(
    private passwordService: PasswordService,
    private toastService: ToastService,
    private router: Router
  ) {}

  sendResetLink() {
    this.sending = true;
    this.errorMessage = '';
    this.passwordService.forgotPassword(this.email).subscribe({
      next: () => {
        this.emailSent = true;
        this.sending = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to send reset link. Please try again.';
        this.sending = false;
      }
    });
  }
}
