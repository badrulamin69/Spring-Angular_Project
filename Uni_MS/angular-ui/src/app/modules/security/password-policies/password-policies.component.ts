import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SystemSettingService } from '../../../services/system-setting.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-password-policies',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Password Policies</h2>
        <p class="page-sub">Configure password rules, expiry, reset policies, and account recovery</p>
      </div>
    </div>

    @if (loading) {
      <div class="loading-state">
        <div class="spinner"></div>
        <span>Loading password policies...</span>
      </div>
    } @else {
      <form (ngSubmit)="saveSettings()" #settingsForm="ngForm">
        <div class="settings-grid">
          <div class="settings-card">
            <div class="card-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M10 2a4 4 0 00-4 4c0 1.1.45 2.1 1.17 2.83L10 12l2.83-3.17A4 4 0 0014 6a4 4 0 00-4-4z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><path d="M5 14h10M7 17h6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              <h3>Password Strength Rules</h3>
            </div>
            <div class="card-body">
              <div class="form-row">
                <div class="form-group">
                  <label for="min_password_length">Minimum Password Length</label>
                  <input type="number" id="min_password_length" [(ngModel)]="settings.min_password_length" name="min_password_length" class="form-control" min="6" max="128">
                  <span class="form-hint">Minimum 6 characters required</span>
                </div>
                <div class="form-group">
                  <label for="max_password_length">Maximum Password Length</label>
                  <input type="number" id="max_password_length" [(ngModel)]="settings.max_password_length" name="max_password_length" class="form-control" min="6" max="128">
                  <span class="form-hint">Maximum 128 characters allowed</span>
                </div>
              </div>
              <div class="checkbox-grid">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.require_uppercase" name="require_uppercase">
                  Require Uppercase Letters
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.require_lowercase" name="require_lowercase">
                  Require Lowercase Letters
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.require_numbers" name="require_numbers">
                  Require Numbers
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.require_special_chars" name="require_special_chars">
                  Require Special Characters
                </label>
              </div>
            </div>
          </div>

          <div class="settings-card">
            <div class="card-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><rect x="3" y="4" width="14" height="12" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M7 2v4M13 2v4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              <h3>Password Expiry</h3>
            </div>
            <div class="card-body">
              <div class="form-row">
                <div class="form-group">
                  <label class="checkbox-label">
                    <input type="checkbox" [(ngModel)]="settings.password_expiry_enabled" name="password_expiry_enabled">
                    Enable Password Expiry
                  </label>
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label for="password_expiry_days">Expiry Period (Days)</label>
                  <input type="number" id="password_expiry_days" [(ngModel)]="settings.password_expiry_days" name="password_expiry_days" class="form-control" min="30" max="365" [disabled]="!settings.password_expiry_enabled">
                  <span class="form-hint">Between 30 and 365 days</span>
                </div>
                <div class="form-group">
                  <label for="password_history_count">Password History Count</label>
                  <input type="number" id="password_history_count" [(ngModel)]="settings.password_history_count" name="password_history_count" class="form-control" min="0" max="24">
                  <span class="form-hint">Prevent reuse of last N passwords</span>
                </div>
              </div>
            </div>
          </div>

          <div class="settings-card">
            <div class="card-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M3 7h14M5 3h10a2 2 0 012 2v10a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><path d="M10 11v4M8 13h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              <h3>Password Reset Policy</h3>
            </div>
            <div class="card-body">
              <div class="form-row">
                <div class="form-group">
                  <label for="password_reset_method">Reset Method</label>
                  <select id="password_reset_method" [(ngModel)]="settings.password_reset_method" name="password_reset_method" class="form-control">
                    <option value="Email Link">Email Link</option>
                    <option value="Admin Reset">Admin Reset</option>
                    <option value="Both">Both</option>
                  </select>
                </div>
                <div class="form-group">
                  <label for="reset_token_expiry_minutes">Reset Token Expiry (Minutes)</label>
                  <input type="number" id="reset_token_expiry_minutes" [(ngModel)]="settings.reset_token_expiry_minutes" name="reset_token_expiry_minutes" class="form-control" min="5" max="1440">
                  <span class="form-hint">How long the reset link remains valid</span>
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label for="max_reset_attempts_per_day">Max Reset Attempts Per Day</label>
                  <input type="number" id="max_reset_attempts_per_day" [(ngModel)]="settings.max_reset_attempts_per_day" name="max_reset_attempts_per_day" class="form-control" min="1" max="10">
                  <span class="form-hint">Limit password reset requests per day</span>
                </div>
              </div>
            </div>
          </div>

          <div class="settings-card">
            <div class="card-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M10 2l2.09 4.24L17 7.27l-3.5 3.41.83 4.82L10 13.27l-4.33 2.23.83-4.82L3 7.27l4.91-.73L10 2z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <h3>Account Recovery</h3>
            </div>
            <div class="card-body">
              <div class="form-row">
                <div class="form-group">
                  <label class="checkbox-label">
                    <input type="checkbox" [(ngModel)]="settings.enable_security_questions" name="enable_security_questions">
                    Enable Security Questions
                  </label>
                </div>
                <div class="form-group">
                  <label for="min_security_questions">Minimum Security Questions</label>
                  <input type="number" id="min_security_questions" [(ngModel)]="settings.min_security_questions" name="min_security_questions" class="form-control" min="1" max="10" [disabled]="!settings.enable_security_questions">
                  <span class="form-hint">Questions required for account recovery</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="actions-bar">
          <button type="submit" class="btn btn-primary" [disabled]="saving">
            @if (saving) {
              <span class="btn-spinner"></span> Saving...
            } @else {
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M11 1H3a2 2 0 00-2 2v8a2 2 0 002 2h8a2 2 0 002-2V4l-2-3z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/><path d="M9 13V8H5v5M5 1v3h3" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              Save Policies
            }
          </button>
        </div>
      </form>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .settings-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.5rem; }
    .settings-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; }
    .card-header { display: flex; align-items: center; gap: 10px; padding: 16px 20px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); }
    .card-header h3 { margin: 0; font-size: 1rem; font-weight: 600; }
    .card-header svg { color: var(--brand-color); }
    .card-body { padding: 20px; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
    .form-row:last-child { margin-bottom: 0; }
    .form-group { display: flex; flex-direction: column; gap: 5px; }
    .form-group label { font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; transition: border-color 0.2s, box-shadow 0.2s; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
    .form-control:disabled { opacity: 0.5; cursor: not-allowed; }
    select.form-control { cursor: pointer; appearance: auto; }
    .checkbox-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .checkbox-label { display: flex; align-items: center; gap: 8px; font-size: 0.875rem; color: var(--text-primary); cursor: pointer; }
    .checkbox-label input { width: 16px; height: 16px; accent-color: var(--brand-color); }
    .form-hint { font-size: 0.75rem; color: var(--text-muted); }
    .actions-bar { display: flex; justify-content: flex-end; }
    .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 3rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class PasswordPoliciesComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  loading = true;
  saving = false;
  settingsMap: Map<number, any> = new Map();

  settings = {
    min_password_length: 8,
    require_uppercase: true,
    require_lowercase: true,
    require_numbers: true,
    require_special_chars: true,
    max_password_length: 128,
    password_expiry_enabled: false,
    password_expiry_days: 90,
    password_history_count: 5,
    password_reset_method: 'Email Link',
    reset_token_expiry_minutes: 60,
    max_reset_attempts_per_day: 3,
    enable_security_questions: false,
    min_security_questions: 3
  };

  constructor(private service: SystemSettingService, private toastService: ToastService) {}

  ngOnInit() {
    this.loadSettings();
  }

  loadSettings() {
    this.loading = true;
    this.service.findAll().subscribe({
      next: (data: any[]) => {
        if (Array.isArray(data)) {
          data.forEach((s: any) => {
            this.settingsMap.set(s.id, s);
            const key = s.settingKey || s.setting_key || s.key;
            if (key && (key as string) in this.settings) {
              const val = s.settingValue || s.setting_value || s.value;
              if (typeof this.settings[key as keyof typeof this.settings] === 'boolean') {
                (this.settings as any)[key] = val === 'true' || val === true || val === '1' || val === 1;
              } else if (typeof this.settings[key as keyof typeof this.settings] === 'number') {
                (this.settings as any)[key] = parseInt(val, 10) || 0;
              } else {
                (this.settings as any)[key] = val;
              }
            }
          });
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load password policies');
      }
    });
  }

  saveSettings() {
    this.saving = true;
    let completed = 0;
    const total = Object.keys(this.settings).length;

    Object.entries(this.settings).forEach(([key, value]) => {
      const existing = Array.from(this.settingsMap.values()).find(
        (s: any) => (s.settingKey || s.setting_key || s.key) === key
      );

      const payload: any = {
        settingKey: key,
        settingValue: String(value),
        category: 'PASSWORD_POLICY',
        description: this.getDescription(key)
      };

      if (existing?.id) {
        this.service.update(existing.id, payload).subscribe({
          next: () => {
            completed++;
            if (completed === total) this.onSaveComplete();
          },
          error: () => {
            completed++;
            if (completed === total) this.onSaveComplete();
          }
        });
      } else {
        this.service.save(payload).subscribe({
          next: (saved: any) => {
            if (saved?.id) this.settingsMap.set(saved.id, saved);
            completed++;
            if (completed === total) this.onSaveComplete();
          },
          error: () => {
            completed++;
            if (completed === total) this.onSaveComplete();
          }
        });
      }
    });
  }

  private onSaveComplete() {
    this.saving = false;
    this.toastService.success('Password policies saved successfully');
    this.loadSettings();
  }

  private getDescription(key: string): string {
    const descriptions: Record<string, string> = {
      min_password_length: 'Minimum number of characters required for passwords',
      require_uppercase: 'Require at least one uppercase letter in passwords',
      require_lowercase: 'Require at least one lowercase letter in passwords',
      require_numbers: 'Require at least one number in passwords',
      require_special_chars: 'Require at least one special character in passwords',
      max_password_length: 'Maximum number of characters allowed in passwords',
      password_expiry_enabled: 'Enable or disable password expiry enforcement',
      password_expiry_days: 'Number of days before a password expires',
      password_history_count: 'Number of previous passwords to prevent reuse',
      password_reset_method: 'Method used for password reset (Email Link, Admin Reset, or Both)',
      reset_token_expiry_minutes: 'Duration in minutes before a reset token expires',
      max_reset_attempts_per_day: 'Maximum password reset attempts allowed per day',
      enable_security_questions: 'Enable security questions for account recovery',
      min_security_questions: 'Minimum number of security questions required'
    };
    return descriptions[key] || key;
  }
}
