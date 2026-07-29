import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { SystemSettingService } from '../../../services/system-setting.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-account-lock-rules',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <app-confirm-dialog
      [open]="confirmOpen"
      [title]="confirmTitle"
      [message]="confirmMessage"
      confirmText="Unlock"
      type="warning"
      (confirmed)="onConfirmUnlock()"
      (cancelled)="confirmOpen = false"
    ></app-confirm-dialog>

    <div class="page-header">
      <div>
        <h2>Account Lock Rules</h2>
        <p class="page-sub">Configure account lockout policies, recovery options, and IP-based rules</p>
      </div>
    </div>

    @if (loading) {
      <div class="loading-state">
        <div class="spinner"></div>
        <span>Loading settings...</span>
      </div>
    } @else {
      <form (ngSubmit)="saveSettings()" #lockForm="ngForm">
        <div class="settings-grid">
          <div class="settings-card">
            <div class="card-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M10 2a4 4 0 00-4 4c0 1.1.45 2.1 1.17 2.83L10 12l2.83-3.17A4 4 0 0014 6a4 4 0 00-4-4z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><path d="M5 14h10M7 17h6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              <h3>Login Attempt Limits</h3>
            </div>
            <div class="card-body">
              <div class="form-group">
                <label for="max_login_attempts">Max Login Attempts</label>
                <input type="number" id="max_login_attempts" [(ngModel)]="settings.max_login_attempts" name="max_login_attempts" class="form-control" min="3" max="20">
                <span class="form-hint">Number of failed attempts before lockout (3G��20)</span>
              </div>
              <div class="form-group">
                <label for="lockout_duration_minutes">Lockout Duration (Minutes)</label>
                <input type="number" id="lockout_duration_minutes" [(ngModel)]="settings.lockout_duration_minutes" name="lockout_duration_minutes" class="form-control" min="5" max="1440">
                <span class="form-hint">How long the account stays locked (5G��1440 min)</span>
              </div>
              <div class="form-group">
                <label for="reset_attempts_after_minutes">Reset Attempts After (Minutes)</label>
                <input type="number" id="reset_attempts_after_minutes" [(ngModel)]="settings.reset_attempts_after_minutes" name="reset_attempts_after_minutes" class="form-control" min="1" max="1440">
                <span class="form-hint">Reset failed attempt counter after this period</span>
              </div>
            </div>
          </div>

          <div class="settings-card">
            <div class="card-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M10 2l2.09 4.24L17 7.27l-3.5 3.41.83 4.82L10 13.27l-4.33 2.23.83-4.82L3 7.27l4.91-.73L10 2z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <h3>Progressive Lockout</h3>
            </div>
            <div class="card-body">
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.enable_progressive_lockout" name="enable_progressive_lockout">
                  Enable Progressive Lockout
                </label>
                <span class="form-hint">Increase lockout duration with each consecutive lockout</span>
              </div>
              @if (settings.enable_progressive_lockout) {
                <div class="form-group">
                  <label for="lockout_multiplier">Lockout Multiplier</label>
                  <input type="number" id="lockout_multiplier" [(ngModel)]="settings.lockout_multiplier" name="lockout_multiplier" class="form-control" min="2" max="10">
                  <span class="form-hint">Multiplier for each subsequent lockout (default: 2x)</span>
                </div>
                <div class="form-group">
                  <label for="max_lockout_hours">Max Lockout Duration (Hours)</label>
                  <input type="number" id="max_lockout_hours" [(ngModel)]="settings.max_lockout_hours" name="max_lockout_hours" class="form-control" min="1" max="168">
                  <span class="form-hint">Maximum lockout duration cap in hours</span>
                </div>
              }
            </div>
          </div>

          <div class="settings-card">
            <div class="card-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M3 7l4 4-4 4M17 7l-4 4 4 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <h3>Account Recovery</h3>
            </div>
            <div class="card-body">
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.allow_self_unlock" name="allow_self_unlock">
                  Allow Self Unlock
                </label>
                <span class="form-hint">Users can unlock their own accounts after lockout expires</span>
              </div>
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.self_unlock_via_email" name="self_unlock_via_email">
                  Self Unlock via Email
                </label>
                <span class="form-hint">Send unlock link to user's email address</span>
              </div>
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.admin_unlock_required" name="admin_unlock_required">
                  Require Admin Unlock
                </label>
                <span class="form-hint">Only administrators can manually unlock accounts</span>
              </div>
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.notify_user_on_lockout" name="notify_user_on_lockout">
                  Notify User on Lockout
                </label>
                <span class="form-hint">Send notification email when account is locked</span>
              </div>
            </div>
          </div>

          <div class="settings-card">
            <div class="card-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="7" stroke="currentColor" stroke-width="1.5"/><path d="M10 6v4l3 2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <h3>IP-Based Rules</h3>
            </div>
            <div class="card-body">
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" [(ngModel)]="settings.enable_ip_lockout" name="enable_ip_lockout">
                  Enable IP-Based Lockout
                </label>
                <span class="form-hint">Track and limit login attempts per IP address</span>
              </div>
              @if (settings.enable_ip_lockout) {
                <div class="form-group">
                  <label for="max_attempts_per_ip">Max Attempts Per IP</label>
                  <input type="number" id="max_attempts_per_ip" [(ngModel)]="settings.max_attempts_per_ip" name="max_attempts_per_ip" class="form-control" min="5" max="100">
                  <span class="form-hint">Maximum login attempts from a single IP</span>
                </div>
                <div class="form-group">
                  <label class="checkbox-label">
                    <input type="checkbox" [(ngModel)]="settings.block_suspicious_ips" name="block_suspicious_ips">
                    Block Suspicious IPs
                  </label>
                  <span class="form-hint">Automatically block IPs with repeated failed attempts</span>
                </div>
              }
            </div>
          </div>
        </div>

        <div class="status-card settings-card full-width">
          <div class="card-header">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><rect x="3" y="3" width="14" height="14" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M7 7h6M7 10h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
            <h3>Current Lockout Status</h3>
          </div>
          <div class="card-body">
            @if (loadingUsers) {
              <div class="loading-state" style="padding: 1.5rem;">
                <div class="spinner"></div>
                <span>Loading locked accounts...</span>
              </div>
            } @else if (lockedUsers.length === 0) {
              <div class="empty-state">No accounts are currently locked.</div>
            } @else {
              <div class="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>Username</th>
                      <th>Email</th>
                      <th>Locked Until</th>
                      <th>Login Attempts</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (user of lockedUsers; track user.id) {
                      <tr>
                        <td>{{ user.username }}</td>
                        <td>{{ user.email }}</td>
                        <td>{{ user.lockedUntil | date:'medium' }}</td>
                        <td>{{ user.loginAttempts || 0 }}</td>
                        <td>
                          <button type="button" class="btn btn-danger btn-sm" (click)="confirmUnlock(user)">
                            <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v4M4.5 3.5h5a1 1 0 011 1v5a1 1 0 01-1 1h-5a1 1 0 01-1-1v-5a1 1 0 011-1z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                            Unlock
                          </button>
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          </div>
        </div>

        <div class="actions-bar">
          <button type="submit" class="btn btn-primary" [disabled]="saving">
            @if (saving) {
              <span class="btn-spinner"></span> Saving...
            } @else {
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M11 1H3a2 2 0 00-2 2v8a2 2 0 002 2h8a2 2 0 002-2V4l-2-3z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/><path d="M9 13V8H5v5M5 1v3h3" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              Save Settings
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
    .settings-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; margin-bottom: 1.5rem; }
    .settings-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; }
    .settings-card.full-width { grid-column: 1 / -1; margin-bottom: 1.5rem; }
    .card-header { display: flex; align-items: center; gap: 10px; padding: 16px 20px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); }
    .card-header h3 { margin: 0; font-size: 1rem; font-weight: 600; }
    .card-header svg { color: var(--brand-color); }
    .card-body { padding: 20px; display: flex; flex-direction: column; gap: 16px; }
    .form-group { display: flex; flex-direction: column; gap: 5px; }
    .form-group label { font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; transition: border-color 0.2s, box-shadow 0.2s; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
    .form-hint { font-size: 0.75rem; color: var(--text-muted); }
    .checkbox-label { display: flex; align-items: center; gap: 8px; font-size: 0.875rem; color: var(--text-primary); cursor: pointer; }
    .checkbox-label input { width: 16px; height: 16px; accent-color: var(--brand-color); }
    .actions-bar { display: flex; justify-content: flex-end; }
    .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-danger { background: #dc3545; color: #fff; }
    .btn-danger:hover { background: #bd2130; }
    .btn-sm { padding: 5px 10px; font-size: 0.8125rem; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 3rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    .empty-state { text-align: center; padding: 2rem; color: var(--text-muted); font-size: 0.875rem; }
    .table-wrapper { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 10px 14px; text-align: left; border-bottom: 1px solid var(--border-color); }
    th { background: var(--bg-tertiary); font-weight: 600; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; color: var(--text-secondary); }
    td { font-size: 0.875rem; color: var(--text-primary); }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class AccountLockRulesComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  loading = true;
  saving = false;
  loadingUsers = false;
  confirmOpen = false;
  confirmTitle = '';
  confirmMessage = '';
  selectedUserId: number | null = null;
  settingsMap: Map<number, any> = new Map();

  settings = {
    max_login_attempts: 5,
    lockout_duration_minutes: 15,
    reset_attempts_after_minutes: 30,
    enable_progressive_lockout: false,
    lockout_multiplier: 2,
    max_lockout_hours: 24,
    allow_self_unlock: true,
    self_unlock_via_email: true,
    admin_unlock_required: false,
    notify_user_on_lockout: true,
    enable_ip_lockout: false,
    max_attempts_per_ip: 20,
    block_suspicious_ips: false
  };

  lockedUsers: any[] = [];

  constructor(
    private service: SystemSettingService,
    private userService: UserService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadSettings();
    this.loadLockedUsers();
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
        this.toastService.error('Failed to load account lock settings');
      }
    });
  }

  loadLockedUsers() {
    this.loadingUsers = true;
    this.userService.findAll({ page: 0, size: 200, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => {
        const users = res.content || [];
        const now = new Date();
        this.lockedUsers = users.filter((u: any) => {
          if (!u.lockedUntil && !u.locked_until) return false;
          const lockedUntil = new Date(u.lockedUntil || u.locked_until);
          return lockedUntil > now;
        });
        this.loadingUsers = false;
      },
      error: () => {
        this.loadingUsers = false;
        this.lockedUsers = [];
      }
    });
  }

  confirmUnlock(user: any) {
    this.selectedUserId = user.id;
    this.confirmTitle = 'Unlock Account';
    this.confirmMessage = `Are you sure you want to unlock the account for "${user.username}"?`;
    this.confirmOpen = true;
  }

  onConfirmUnlock() {
    if (this.selectedUserId == null) return;
    const userId = this.selectedUserId;
    this.confirmOpen = false;

    this.userService.unlock(userId).subscribe({
      next: () => {
        this.toastService.success('Account unlocked successfully');
        this.loadLockedUsers();
      },
      error: () => {
        this.toastService.error('Failed to unlock account');
      }
    });

    this.selectedUserId = null;
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
        category: 'SECURITY',
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
    this.toastService.success('Account lock settings saved successfully');
    this.loadSettings();
  }

  private getDescription(key: string): string {
    const descriptions: Record<string, string> = {
      max_login_attempts: 'Maximum failed login attempts before account is locked',
      lockout_duration_minutes: 'Duration of account lockout in minutes',
      reset_attempts_after_minutes: 'Time in minutes to reset the failed attempts counter',
      enable_progressive_lockout: 'Enable increasing lockout duration on repeated lockouts',
      lockout_multiplier: 'Multiplier applied to lockout duration on each consecutive lockout',
      max_lockout_hours: 'Maximum lockout duration cap in hours for progressive lockout',
      allow_self_unlock: 'Allow users to unlock their own accounts after lockout expires',
      self_unlock_via_email: 'Send unlock link to user email for self-service unlock',
      admin_unlock_required: 'Require administrator intervention to unlock accounts',
      notify_user_on_lockout: 'Send notification email when account is locked',
      enable_ip_lockout: 'Track and limit login attempts per IP address',
      max_attempts_per_ip: 'Maximum login attempts allowed from a single IP address',
      block_suspicious_ips: 'Automatically block IPs with repeated failed login attempts'
    };
    return descriptions[key] || key;
  }
}
