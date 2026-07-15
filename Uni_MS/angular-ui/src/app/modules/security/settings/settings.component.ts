import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SystemSettingService } from '../../../services/system-setting.service';
import { CurrentUserService } from '../../../services/current-user.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

interface SettingItem {
  id?: number;
  settingKey: string;
  settingValue: string;
  settingType: string;
  module: string;
  description: string;
  isPublic?: boolean;
}

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>

    <div class="page-header">
      <div>
        <h2>System Settings</h2>
        <p class="page-sub">Configure and manage all system-wide settings</p>
      </div>
      <div class="header-actions">
        @if (canManage) {
          <button class="btn btn-outline" (click)="seedDefaults()" [disabled]="seeding">
            @if (seeding) { <span class="btn-spinner"></span> Seeding... } @else { Seed Defaults }
          </button>
          <button class="btn btn-outline" (click)="clearCache()" [disabled]="clearingCache">
            @if (clearingCache) { <span class="btn-spinner"></span> Clearing... } @else { Clear Cache }
          </button>
        }
      </div>
    </div>

    <div class="tabs-wrapper">
      <div class="tabs-scroll">
        @for (tab of tabs; track tab.module) {
          <button class="tab-btn" [class.active]="activeTab === tab.module" (click)="switchTab(tab.module)">
            <span class="tab-icon" [innerHTML]="tab.icon"></span>
            {{ tab.label }}
          </button>
        }
      </div>
    </div>

    @if (loading) {
      <div class="loading-state">
        <div class="spinner"></div>
        <span>Loading settings...</span>
      </div>
    } @else {
      <div class="settings-content">
        @if (activeTab === 'SYSTEM_INFO') {
          <div class="info-grid">
            <div class="info-card">
              <div class="info-header">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M10 6v4l3 3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                <h3>System Information</h3>
              </div>
              <div class="info-body">
                @for (item of systemInfoItems; track item.label) {
                  <div class="info-row">
                    <span class="info-label">{{ item.label }}</span>
                    <span class="info-value">{{ item.value }}</span>
                  </div>
                }
              </div>
            </div>
          </div>
        } @else {
          <div class="module-header">
            <h3>{{ getActiveTabLabel() }}</h3>
            @if (canManage) {
              <button class="btn btn-outline btn-sm" (click)="resetModule()" [disabled]="resetting">
                @if (resetting) { <span class="btn-spinner"></span> Resetting... } @else { Reset to Defaults }
              </button>
            }
          </div>

          <div class="settings-grid">
            @for (group of getGroupedSettings(); track group.label) {
              <div class="settings-card" [class.full-width]="group.fullWidth">
                <div class="card-header">
                  <span class="card-icon" [innerHTML]="group.icon"></span>
                  <h4>{{ group.label }}</h4>
                </div>
                <div class="card-body">
                  @for (field of group.fields; track field.settingKey) {
                    <div class="form-group">
                      <label [for]="field.settingKey">{{ field.description || field.settingKey }}</label>

                      @switch (field.settingType) {
                        @case ('BOOLEAN') {
                          <div class="toggle-wrapper">
                            <label class="toggle">
                              <input type="checkbox" [id]="field.settingKey"
                                [checked]="field.settingValue === 'true'"
                                (change)="toggleBoolean(field)"
                                [disabled]="!canManage">
                              <span class="toggle-slider"></span>
                            </label>
                            <span class="toggle-label">{{ field.settingValue === 'true' ? 'Enabled' : 'Disabled' }}</span>
                          </div>
                        }
                        @case ('NUMBER') {
                          <input type="number" [id]="field.settingKey" class="form-control"
                            [(ngModel)]="field.settingValue"
                            [disabled]="!canManage">
                        }
                        @case ('COLOR') {
                          <div class="color-input-group">
                            <input type="color" [id]="field.settingKey" class="form-control color-picker"
                              [(ngModel)]="field.settingValue"
                              [disabled]="!canManage">
                            <input type="text" class="form-control color-text"
                              [(ngModel)]="field.settingValue"
                              [disabled]="!canManage">
                          </div>
                        }
                        @case ('FILE') {
                          <div class="file-input-group">
                            <input type="text" class="form-control"
                              [(ngModel)]="field.settingValue"
                              placeholder="Enter file URL"
                              [disabled]="!canManage">
                            @if (field.settingValue) {
                              <span class="file-preview">{{ field.settingValue }}</span>
                            }
                          </div>
                        }
                        @default {
                          @if (getSelectOptions(field.settingKey); as options) {
                            <select [id]="field.settingKey" class="form-control"
                              [(ngModel)]="field.settingValue"
                              [disabled]="!canManage">
                              @for (opt of options; track opt) {
                                <option [value]="opt">{{ opt }}</option>
                              }
                            </select>
                          } @else {
                            <input type="text" [id]="field.settingKey" class="form-control"
                              [(ngModel)]="field.settingValue"
                              [disabled]="!canManage">
                          }
                        }
                      }
                    </div>
                  }
                </div>
              </div>
            }
          </div>

          @if (canManage) {
            <div class="actions-bar">
              <button class="btn btn-primary" (click)="saveModuleSettings()" [disabled]="saving">
                @if (saving) { <span class="btn-spinner"></span> Saving... } @else {
                  <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M11 1H3a2 2 0 00-2 2v8a2 2 0 002 2h8a2 2 0 002-2V4l-2-3z" stroke="currentColor" stroke-width="1.2"/><path d="M9 13V8H5v5M5 1v3h3" stroke="currentColor" stroke-width="1.2"/></svg>
                  Save {{ getActiveTabLabel() }} Settings
                }
              </button>
            </div>
          }
        }
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .header-actions { display: flex; gap: 8px; }

    .tabs-wrapper { margin-bottom: 1.5rem; }
    .tabs-scroll { display: flex; gap: 4px; overflow-x: auto; padding-bottom: 4px; scrollbar-width: thin; }
    .tabs-scroll::-webkit-scrollbar { height: 4px; }
    .tabs-scroll::-webkit-scrollbar-thumb { background: var(--border-color); border-radius: 4px; }
    .tab-btn { display: flex; align-items: center; gap: 6px; padding: 8px 14px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-secondary); color: var(--text-secondary); cursor: pointer; font-size: 0.8125rem; font-weight: 500; white-space: nowrap; transition: all 0.15s; }
    .tab-btn:hover { border-color: var(--brand-color); color: var(--brand-color); }
    .tab-btn.active { background: var(--brand-color); color: #fff; border-color: var(--brand-color); }
    .tab-icon { display: flex; align-items: center; }
    .tab-icon :deep(svg) { width: 14px; height: 14px; }

    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 3rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }

    .settings-content { animation: fadeIn 0.2s ease; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(4px); } to { opacity: 1; transform: translateY(0); } }

    .module-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .module-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); font-weight: 600; }

    .settings-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; margin-bottom: 1.5rem; }
    .settings-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; }
    .settings-card.full-width { grid-column: 1 / -1; }
    .card-header { display: flex; align-items: center; gap: 10px; padding: 14px 20px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); }
    .card-header h4 { margin: 0; font-size: 0.9375rem; font-weight: 600; }
    .card-icon { display: flex; align-items: center; color: var(--brand-color); }
    .card-icon :deep(svg) { width: 18px; height: 18px; }
    .card-body { padding: 20px; display: flex; flex-direction: column; gap: 16px; }

    .form-group { display: flex; flex-direction: column; gap: 5px; }
    .form-group label { font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; }
    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-primary); color: var(--text-primary); font-size: 0.875rem; transition: border-color 0.2s, box-shadow 0.2s; }
    .form-control:focus { outline: none; border-color: var(--brand-color); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
    .form-control:disabled { opacity: 0.6; cursor: not-allowed; }
    select.form-control { cursor: pointer; appearance: auto; }

    .toggle-wrapper { display: flex; align-items: center; gap: 12px; }
    .toggle { position: relative; display: inline-block; width: 44px; height: 24px; cursor: pointer; }
    .toggle input { opacity: 0; width: 0; height: 0; }
    .toggle-slider { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: var(--border-color); border-radius: 24px; transition: 0.2s; }
    .toggle-slider::before { content: ''; position: absolute; width: 18px; height: 18px; left: 3px; bottom: 3px; background: #fff; border-radius: 50%; transition: 0.2s; }
    .toggle input:checked + .toggle-slider { background: var(--brand-color); }
    .toggle input:checked + .toggle-slider::before { transform: translateX(20px); }
    .toggle input:disabled + .toggle-slider { opacity: 0.5; cursor: not-allowed; }
    .toggle-label { font-size: 0.8125rem; color: var(--text-secondary); }

    .color-input-group { display: flex; gap: 8px; align-items: center; }
    .color-picker { width: 48px; height: 38px; padding: 2px; cursor: pointer; border-radius: 8px; }
    .color-text { flex: 1; }

    .file-input-group { display: flex; flex-direction: column; gap: 6px; }
    .file-preview { font-size: 0.75rem; color: var(--text-muted); word-break: break-all; }

    .info-grid { display: grid; grid-template-columns: 1fr; gap: 1.25rem; }
    .info-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; }
    .info-header { display: flex; align-items: center; gap: 10px; padding: 14px 20px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); }
    .info-header h3 { margin: 0; font-size: 0.9375rem; font-weight: 600; }
    .info-header svg { color: var(--brand-color); }
    .info-body { padding: 20px; }
    .info-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid var(--border-color); }
    .info-row:last-child { border-bottom: none; }
    .info-label { font-size: 0.875rem; color: var(--text-secondary); font-weight: 500; }
    .info-value { font-size: 0.875rem; color: var(--text-primary); font-weight: 600; }

    .actions-bar { display: flex; justify-content: flex-end; padding-top: 0.5rem; }

    .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { border-color: var(--brand-color); color: var(--brand-color); }
    .btn-sm { padding: 6px 12px; font-size: 0.8125rem; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }

    @media (max-width: 1024px) {
      .settings-grid { grid-template-columns: 1fr; }
    }

    @media (max-width: 640px) {
      .page-header { flex-direction: column; align-items: flex-start; gap: 10px; }
      .header-actions { width: 100%; }
      .module-header { flex-direction: column; align-items: flex-start; gap: 8px; }
    }
  `]
})
export class SettingsComponent implements OnInit {
  private service = inject(SystemSettingService);
  private currentUserService = inject(CurrentUserService);
  private toastService = inject(ToastService);

  loading = true;
  saving = false;
  seeding = false;
  resetting = false;
  clearingCache = false;
  activeTab = 'GENERAL';
  allSettings: SettingItem[] = [];
  systemInfoItems: { label: string; value: string }[] = [];

  get canManage(): boolean {
    return this.currentUserService.hasPermission('SETTINGS_MANAGE');
  }

  tabs = [
    { module: 'GENERAL', label: 'General', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M10 6v4l3 3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>' },
    { module: 'BRANDING', label: 'Branding', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 4h12v12H4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M4 10h12M10 4v12" stroke="currentColor" stroke-width="1.5"/></svg>' },
    { module: 'ACADEMIC', label: 'Academic', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M2 7l8-5 8 5-8 5-8-5z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M4 10v4c0 1 3 3 6 3s6-2 6-3v-4" stroke="currentColor" stroke-width="1.5"/></svg>' },
    { module: 'AUTHENTICATION', label: 'Authentication', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="5" y="9" width="10" height="8" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M7 9V6a3 3 0 016 0v3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>' },
    { module: 'EMAIL', label: 'Email', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="2" y="4" width="16" height="12" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M2 6l8 5 8-5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>' },
    { module: 'NOTIFICATION', label: 'Notification', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M10 2a5 5 0 00-5 5v3l-1 2h12l-1-2V7a5 5 0 00-5-5z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M8 16a2 2 0 004 0" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>' },
    { module: 'PAYMENT', label: 'Payment', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="2" y="5" width="16" height="10" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M2 8h16" stroke="currentColor" stroke-width="1.5"/></svg>' },
    { module: 'SECURITY', label: 'Security', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M10 2l6 3v5c0 4-2.5 6.5-6 8-3.5-1.5-6-4-6-8V5l6-3z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>' },
    { module: 'BACKUP', label: 'Backup', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 14a6 6 0 0112 0" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><rect x="6" y="8" width="8" height="6" rx="1" stroke="currentColor" stroke-width="1.5"/><path d="M10 4v4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>' },
    { module: 'FILE_STORAGE', label: 'File Storage', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 4h5l2 2h5v10H4V4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>' },
    { module: 'AUDIT', label: 'Audit', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 4h12v12H4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M7 8h6M7 11h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>' },
    { module: 'APPEARANCE', label: 'Appearance', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M10 2a8 8 0 010 16V2z" fill="currentColor" opacity="0.2"/></svg>' },
    { module: 'LOCALIZATION', label: 'Localization', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M2 10h16M10 2c2.5 2.5 3.5 5 3.5 8s-1 5.5-3.5 8M10 2c-2.5 2.5-3.5 5-3.5 8s1 5.5 3.5 8" stroke="currentColor" stroke-width="1.5"/></svg>' },
    { module: 'CACHE', label: 'Cache', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="3" y="3" width="14" height="14" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M3 10h14M10 3v14" stroke="currentColor" stroke-width="1.5" opacity="0.3"/><path d="M7 7h6v6H7z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>' },
    { module: 'MAINTENANCE', label: 'Maintenance', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M11 2l-1 4-4 1 4 1 1 4 1-4 4-1-4-1-1-4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M16 14l-1 2H5l-1-2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>' },
    { module: 'SYSTEM_INFO', label: 'System Info', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M10 6v1M10 9v5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>' },
  ];

  private fieldGroups: Record<string, { label: string; icon: string; fields: string[]; fullWidth?: boolean }[]> = {
    GENERAL: [
      { label: 'University Info', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M2 7l8-5 8 5-8 5-8-5z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M4 10v4c0 1 3 3 6 3s6-2 6-3v-4" stroke="currentColor" stroke-width="1.5"/></svg>', fields: ['general.university_name', 'general.short_name', 'general.university_code', 'general.established_year'] },
      { label: 'Contact', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="2" y="4" width="16" height="12" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M2 6l8 5 8-5" stroke="currentColor" stroke-width="1.5"/></svg>', fields: ['general.website', 'general.email', 'general.phone', 'general.mobile', 'general.address', 'general.city', 'general.state', 'general.country', 'general.postal_code'] },
      { label: 'Academic Defaults', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 4h12v12H4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M7 8h6M7 11h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['general.academic_year', 'general.semester_system', 'general.credit_system'] },
      { label: 'Regional', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M2 10h16M10 2c2.5 2.5 3.5 5 3.5 8s-1 5.5-3.5 8M10 2c-2.5 2.5-3.5 5-3.5 8s1 5.5 3.5 8" stroke="currentColor" stroke-width="1.5"/></svg>', fields: ['general.timezone', 'general.language', 'general.currency', 'general.date_format', 'general.time_format'] },
      { label: 'System', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M10 6v4l3 3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['general.maintenance_mode'], fullWidth: true },
    ],
    BRANDING: [
      { label: 'Logo & Images', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="3" y="3" width="14" height="14" rx="2" stroke="currentColor" stroke-width="1.5"/><circle cx="7" cy="8" r="2" stroke="currentColor" stroke-width="1.5"/><path d="M3 14l4-4 3 3 3-3 4 4" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>', fields: ['branding.logo_url', 'branding.dark_logo_url', 'branding.favicon_url', 'branding.login_background_url', 'branding.dashboard_banner_url'] },
      { label: 'Colors & Theme', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="8" cy="8" r="5" stroke="currentColor" stroke-width="1.5"/><circle cx="13" cy="11" r="5" stroke="currentColor" stroke-width="1.5"/></svg>', fields: ['branding.primary_color', 'branding.secondary_color', 'branding.accent_color', 'branding.theme_mode'] },
      { label: 'Layout', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="3" y="3" width="14" height="14" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M3 7h14M7 7v10" stroke="currentColor" stroke-width="1.5"/></svg>', fields: ['branding.sidebar_style', 'branding.header_style'] },
    ],
    ACADEMIC: [
      { label: 'Defaults', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 4h12v12H4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M7 8h6M7 11h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['academic.default_faculty', 'academic.default_department', 'academic.default_semester', 'academic.default_session'] },
      { label: 'Grading', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M10 2l2.09 4.24L17 7.27l-3.5 3.41.83 4.82L10 13.27l-4.33 2.23.83-4.82L3 7.27l4.91-.73L10 2z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>', fields: ['academic.credit_rules', 'academic.gpa_scale', 'academic.cgpa_scale', 'academic.passing_marks', 'academic.attendance_percentage', 'academic.result_calculation'] },
    ],
    AUTHENTICATION: [
      { label: 'Session', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="3" y="4" width="14" height="12" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M7 2v4M13 2v4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['auth.session_timeout_minutes', 'auth.remember_me_days'] },
      { label: 'Login Methods', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="5" y="9" width="10" height="8" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M7 9V6a3 3 0 016 0v3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['auth.email_login_enabled', 'auth.username_login_enabled', 'auth.single_device_login', 'auth.multi_device_login'] },
    ],
    EMAIL: [
      { label: 'SMTP Configuration', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="2" y="4" width="16" height="12" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M2 6l8 5 8-5" stroke="currentColor" stroke-width="1.5"/></svg>', fields: ['email.smtp_host', 'email.smtp_port', 'email.smtp_username', 'email.smtp_password', 'email.encryption', 'email.sender_name', 'email.sender_email'] },
    ],
    NOTIFICATION: [
      { label: 'Channels', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M10 2a5 5 0 00-5 5v3l-1 2h12l-1-2V7a5 5 0 00-5-5z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M8 16a2 2 0 004 0" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['notification.enable_email', 'notification.enable_sms', 'notification.enable_push', 'notification.enable_in_app'] },
    ],
    PAYMENT: [
      { label: 'Payment Gateways', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="2" y="5" width="16" height="10" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M2 8h16" stroke="currentColor" stroke-width="1.5"/></svg>', fields: ['payment.sslcommerz_enabled', 'payment.sslcommerz_sandbox', 'payment.bkash_enabled', 'payment.nagad_enabled', 'payment.rocket_enabled', 'payment.bank_transfer_enabled', 'payment.cash_enabled'] },
    ],
    SECURITY: [
      { label: 'Token & Encryption', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M10 2l6 3v5c0 4-2.5 6.5-6 8-3.5-1.5-6-4-6-8V5l6-3z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>', fields: ['security.jwt_expiration', 'security.refresh_token_expiration', 'security.password_encryption'] },
      { label: 'Network & Access', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M10 6v4l3 3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['security.csrf_enabled', 'security.cors_allowed_origins', 'security.audit_logging', 'security.ip_restriction', 'security.api_rate_limit'] },
    ],
    BACKUP: [
      { label: 'Backup Settings', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 14a6 6 0 0112 0" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><rect x="6" y="8" width="8" height="6" rx="1" stroke="currentColor" stroke-width="1.5"/><path d="M10 4v4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['backup.auto_backup_enabled', 'backup.backup_schedule', 'backup.backup_path'] },
    ],
    FILE_STORAGE: [
      { label: 'Upload Limits', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 4h5l2 2h5v10H4V4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>', fields: ['file.max_upload_size', 'file.allowed_types', 'file.image_max_size', 'file.document_max_size', 'file.video_max_size', 'file.storage_path'] },
    ],
    AUDIT: [
      { label: 'Audit Settings', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M4 4h12v12H4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M7 8h6M7 11h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>', fields: ['audit.enable_audit_log', 'audit.enable_activity_log', 'audit.retention_days'] },
    ],
    APPEARANCE: [
      { label: 'Theme', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M10 2a8 8 0 010 16V2z" fill="currentColor" opacity="0.2"/></svg>', fields: ['appearance.dark_mode', 'appearance.sidebar_collapsed', 'appearance.compact_mode', 'appearance.animations_enabled', 'appearance.font_size'] },
    ],
    LOCALIZATION: [
      { label: 'Regional Settings', icon: '<svg viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="1.5"/><path d="M2 10h16M10 2c2.5 2.5 3.5 5 3.5 8s-1 5.5-3.5 8M10 2c-2.5 2.5-3.5 5-3.5 8s1 5.5 3.5 8" stroke="currentColor" stroke-width="1.5"/></svg>', fields: ['localization.language', 'localization.currency', 'localization.timezone', 'localization.date_format', 'localization.number_format'] },
    ],
    CACHE: [
      { label: 'Cache Settings', icon: '<svg viewBox="0 0 20 20" fill="none"><rect x="3" y="3" width="14" height="14" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M7 7h6v6H7z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>', fields: ['cache.ttl_seconds', 'cache.max_size'] },
    ],
    MAINTENANCE: [
      { label: 'Maintenance Mode', icon: '<svg viewBox="0 0 20 20" fill="none"><path d="M11 2l-1 4-4 1 4 1 1 4 1-4 4-1-4-1-1-4z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>', fields: ['maintenance.enabled', 'maintenance.message', 'maintenance.allowed_admin_ips', 'maintenance.emergency_access'] },
    ],
  };

  private selectOptionsMap: Record<string, string[]> = {
    'general.timezone': ['Asia/Dhaka', 'Asia/Kolkata', 'Asia/Karachi', 'Asia/Colombo', 'UTC', 'America/New_York', 'America/Los_Angeles', 'Europe/London'],
    'general.language': ['en', 'bn', 'hi', 'ur', 'ar'],
    'general.currency': ['BDT', 'INR', 'PKR', 'LKR', 'USD', 'EUR', 'GBP'],
    'general.date_format': ['dd/MM/yyyy', 'MM/dd/yyyy', 'yyyy-MM-dd', 'dd MMM yyyy'],
    'general.time_format': ['hh:mm a', 'HH:mm', 'hh:mm:ss a'],
    'general.semester_system': ['Semester', 'Trimester', 'Quarter', 'Annual'],
    'general.credit_system': ['4.0', '5.0', '100'],
    'academic.gpa_scale': ['4.0', '5.0', '10.0'],
    'academic.cgpa_scale': ['4.0', '5.0', '10.0'],
    'academic.result_calculation': ['CGPA', 'GPA', 'Percentage', 'Division'],
    'email.encryption': ['TLS', 'SSL', 'NONE'],
    'backup.backup_schedule': ['DAILY', 'WEEKLY', 'MONTHLY'],
    'security.password_encryption': ['BCRYPT', 'SCRYPT', 'ARGON2'],
    'branding.theme_mode': ['dark', 'light', 'system'],
    'branding.sidebar_style': ['expanded', 'collapsed', 'floating'],
    'branding.header_style': ['fixed', 'static', 'sticky'],
    'localization.language': ['en', 'bn', 'hi', 'ur', 'ar'],
    'localization.currency': ['BDT', 'INR', 'PKR', 'LKR', 'USD', 'EUR'],
    'localization.timezone': ['Asia/Dhaka', 'Asia/Kolkata', 'Asia/Karachi', 'UTC'],
    'localization.date_format': ['dd/MM/yyyy', 'MM/dd/yyyy', 'yyyy-MM-dd'],
    'localization.number_format': ['#,##0.00', '#,##0', '0.00', '0,00'],
  };

  ngOnInit() {
    this.loadAllSettings();
    this.loadSystemInfo();
  }

  loadAllSettings() {
    this.loading = true;
    this.service.findAll().subscribe({
      next: (res: any) => {
        const data = res?.data || res;
        this.allSettings = Array.isArray(data) ? data : [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.error('Failed to load settings');
      }
    });
  }

  loadSystemInfo() {
    this.service.getSystemInfo().subscribe({
      next: (res: any) => {
        const info = res?.data || res;
        this.systemInfoItems = [
          { label: 'Application Version', value: info?.appVersion || '1.0.0' },
          { label: 'Angular Version', value: info?.angularVersion || '21' },
          { label: 'Spring Boot Version', value: info?.springBootVersion || '3.4.1' },
          { label: 'Java Version', value: info?.javaVersion || 'N/A' },
          { label: 'Build Number', value: info?.buildNumber || 'N/A' },
          { label: 'Environment', value: info?.environment || 'production' },
          { label: 'Server Time', value: info?.serverTime || new Date().toLocaleString() },
        ];
      }
    });
  }

  switchTab(module: string) {
    this.activeTab = module;
  }

  getActiveTabLabel(): string {
    return this.tabs.find(t => t.module === this.activeTab)?.label || this.activeTab;
  }

  getGroupedSettings(): { label: string; icon: string; fields: SettingItem[]; fullWidth?: boolean }[] {
    const groups = this.fieldGroups[this.activeTab] || [];
    return groups.map(g => ({
      label: g.label,
      icon: g.icon,
      fullWidth: g.fullWidth,
      fields: g.fields.map(key => {
        const existing = this.allSettings.find(s => s.settingKey === key);
        return existing || {
          settingKey: key,
          settingValue: '',
          settingType: this.inferType(key),
          module: this.activeTab,
          description: this.keyToLabel(key),
        };
      }),
    }));
  }

  private inferType(key: string): string {
    if (key.includes('_enabled') || key.includes('_mode') || key === 'general.maintenance_mode') return 'BOOLEAN';
    if (key.includes('color') || key.includes('Color')) return 'COLOR';
    if (key.includes('url') || key.includes('path') || key.includes('Url')) return 'FILE';
    if (key.includes('port') || key.includes('size') || key.includes('days') || key.includes('minutes') || key.includes('hours') || key.includes('limit') || key.includes('max') || key.includes('ttl') || key.includes('expiration') || key.includes('retention') || key.includes('year') || key.includes('marks') || key.includes('percentage') || key.includes('credit')) return 'NUMBER';
    return 'STRING';
  }

  private keyToLabel(key: string): string {
    return key.split('.').pop()!.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
  }

  getSelectOptions(key: string): string[] | null {
    return this.selectOptionsMap[key] || null;
  }

  toggleBoolean(field: SettingItem) {
    field.settingValue = field.settingValue === 'true' ? 'false' : 'true';
  }

  saveModuleSettings() {
    this.saving = true;
    const groups = this.getGroupedSettings();
    const allFields = groups.flatMap(g => g.fields);
    const payload = allFields.map(f => ({
      settingKey: f.settingKey,
      settingValue: f.settingValue,
      settingType: f.settingType,
      module: f.module,
      description: f.description,
      isPublic: false,
    }));

    this.service.batchUpdate(payload).subscribe({
      next: (res: any) => {
        const saved = res?.data || res;
        if (Array.isArray(saved)) {
          saved.forEach((s: SettingItem) => {
            const idx = this.allSettings.findIndex(x => x.settingKey === s.settingKey);
            if (idx >= 0) this.allSettings[idx] = s;
            else this.allSettings.push(s);
          });
        }
        this.saving = false;
        this.toastService.success(`${this.getActiveTabLabel()} settings saved successfully`);
      },
      error: () => {
        this.saving = false;
        this.toastService.error('Failed to save settings');
      }
    });
  }

  resetModule() {
    if (!confirm(`Reset all ${this.getActiveTabLabel()} settings to defaults?`)) return;
    this.resetting = true;
    this.service.resetModule(this.activeTab).subscribe({
      next: (res: any) => {
        const saved = res?.data || res;
        if (Array.isArray(saved)) {
          saved.forEach((s: SettingItem) => {
            const idx = this.allSettings.findIndex(x => x.settingKey === s.settingKey);
            if (idx >= 0) this.allSettings[idx] = s;
            else this.allSettings.push(s);
          });
        }
        this.resetting = false;
        this.toastService.success(`${this.getActiveTabLabel()} settings reset to defaults`);
      },
      error: () => {
        this.resetting = false;
        this.toastService.error('Failed to reset settings');
      }
    });
  }

  seedDefaults() {
    this.seeding = true;
    this.service.seedDefaults().subscribe({
      next: () => {
        this.seeding = false;
        this.toastService.success('Default settings seeded successfully');
        this.loadAllSettings();
      },
      error: () => {
        this.seeding = false;
        this.toastService.error('Failed to seed defaults');
      }
    });
  }

  clearCache() {
    this.clearingCache = true;
    this.service.clearCache().subscribe({
      next: () => {
        this.clearingCache = false;
        this.toastService.success('Cache cleared successfully');
      },
      error: () => {
        this.clearingCache = false;
        this.toastService.error('Failed to clear cache');
      }
    });
  }
}
