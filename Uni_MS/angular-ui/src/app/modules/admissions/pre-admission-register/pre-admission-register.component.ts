import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PreAdmissionService } from '../../../services/pre-admission.service';

@Component({
  selector: 'app-pre-admission-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="public-page">
      <div class="register-card">
        <div class="card-header">
          <div class="uni-logo">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
              <rect width="48" height="48" rx="12" fill="#2563eb"/>
              <path d="M14 34V20l10-8 10 8v14H28V26h-8v8z" fill="#fff"/>
              <rect x="12" y="34" width="24" height="3" rx="1" fill="#fff"/>
            </svg>
          </div>
          <h1>University Pre-Admission Registration</h1>
          <p>Fill in the form below to register for admission</p>
        </div>

        @if (registrationResult) {
          <div class="success-panel">
            <div class="success-icon">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                <circle cx="24" cy="24" r="24" fill="#d1fae5"/>
                <path d="M15 24l6 6 12-12" stroke="#059669" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h2>Registration Successful!</h2>

            <div class="credentials-box">
              <h3>Your Login Credentials</h3>
              <p class="cred-note">Use these credentials to login to the student portal</p>
              <div class="cred-row">
                <span class="cred-label">Email:</span>
                <span class="cred-value">{{ registrationResult.loginEmail }}</span>
                <button class="copy-btn" (click)="copyToClipboard(registrationResult.loginEmail)">Copy</button>
              </div>
              <div class="cred-row">
                <span class="cred-label">Password:</span>
                <span class="cred-value password-val">{{ showPassword ? registrationResult.tempPassword : '••••••••••' }}</span>
                <button class="copy-btn" (click)="togglePassword()">{{ showPassword ? 'Hide' : 'Show' }}</button>
                <button class="copy-btn" (click)="copyToClipboard(registrationResult.tempPassword)">Copy</button>
              </div>
            </div>

            <div class="reg-info">
              <div class="info-row">
                <span>Registration No:</span>
                <strong>{{ registrationResult.registrationNumber }}</strong>
              </div>
              <div class="info-row">
                <span>Name:</span>
                <strong>{{ registrationResult.firstName }} {{ registrationResult.lastName }}</strong>
              </div>
              <div class="info-row">
                <span>Status:</span>
                <strong class="status-submitted">SUBMITTED</strong>
              </div>
            </div>

            <p class="warning-text">Please save your password. You will need it to login and download your admit card after approval.</p>

            <div class="success-actions">
              <a routerLink="/login" class="btn btn-primary btn-lg">Login to Portal</a>
              <a routerLink="/pre-admission/status" class="btn btn-outline">Check Status</a>
            </div>
          </div>
        }

        @if (errorMessage) {
          <div class="alert alert-error">
            <strong>Error:</strong> {{ errorMessage }}
          </div>
        }

        @if (!registrationResult) {
          <form (ngSubmit)="onSubmit()" #regForm="ngForm">
            <h3>Personal Information</h3>
            <div class="form-row">
              <div class="form-group">
                <label>First Name *</label>
                <input type="text" [(ngModel)]="formData.firstName" name="firstName" required maxlength="100" placeholder="Enter first name">
              </div>
              <div class="form-group">
                <label>Last Name *</label>
                <input type="text" [(ngModel)]="formData.lastName" name="lastName" required maxlength="100" placeholder="Enter last name">
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Email *</label>
                <input type="email" [(ngModel)]="formData.email" name="email" required email placeholder="your@email.com">
              </div>
              <div class="form-group">
                <label>Phone</label>
                <input type="text" [(ngModel)]="formData.phone" name="phone" maxlength="20" placeholder="+880XXXXXXXXXX">
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Date of Birth *</label>
                <input type="date" [(ngModel)]="formData.dateOfBirth" name="dateOfBirth" required>
              </div>
              <div class="form-group">
                <label>Gender</label>
                <select [(ngModel)]="formData.gender" name="gender">
                  <option value="">Select</option>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Father Name</label>
                <input type="text" [(ngModel)]="formData.fatherName" name="fatherName" maxlength="100" placeholder="Father's full name">
              </div>
              <div class="form-group">
                <label>Mother Name</label>
                <input type="text" [(ngModel)]="formData.motherName" name="motherName" maxlength="100" placeholder="Mother's full name">
              </div>
            </div>
            <div class="form-group">
              <label>Guardian Phone</label>
              <input type="text" [(ngModel)]="formData.guardianPhone" name="guardianPhone" maxlength="20" placeholder="Guardian's phone number">
            </div>
            <div class="form-group">
              <label>Address</label>
              <textarea [(ngModel)]="formData.address" name="address" rows="2" placeholder="Full address"></textarea>
            </div>

            <h3>Academic Information</h3>
            <div class="form-row">
              <div class="form-group">
                <label>SSC GPA *</label>
                <input type="number" [(ngModel)]="formData.sscGpa" name="sscGpa" required min="0" max="5" step="0.01" placeholder="0.00 - 5.00">
              </div>
              <div class="form-group">
                <label>SSC Year</label>
                <input type="number" [(ngModel)]="formData.sscYear" name="sscYear" min="2000" max="2030" placeholder="e.g. 2023">
              </div>
              <div class="form-group">
                <label>SSC Board</label>
                <input type="text" [(ngModel)]="formData.sscBoard" name="sscBoard" maxlength="100" placeholder="e.g. Dhaka Board">
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>HSC GPA *</label>
                <input type="number" [(ngModel)]="formData.hscGpa" name="hscGpa" required min="0" max="5" step="0.01" placeholder="0.00 - 5.00">
              </div>
              <div class="form-group">
                <label>HSC Year</label>
                <input type="number" [(ngModel)]="formData.hscYear" name="hscYear" min="2000" max="2030" placeholder="e.g. 2025">
              </div>
              <div class="form-group">
                <label>HSC Board</label>
                <input type="text" [(ngModel)]="formData.hscBoard" name="hscBoard" maxlength="100" placeholder="e.g. Dhaka Board">
              </div>
            </div>

            <h3>Program Preferences</h3>
            <div class="form-row">
              <div class="form-group">
                <label>Preference 1 *</label>
                <input type="text" [(ngModel)]="formData.programPreference1" name="programPreference1" required maxlength="100" placeholder="e.g. B.Sc. in CSE">
              </div>
              <div class="form-group">
                <label>Preference 2</label>
                <input type="text" [(ngModel)]="formData.programPreference2" name="programPreference2" maxlength="100" placeholder="e.g. B.Sc. in EEE">
              </div>
              <div class="form-group">
                <label>Preference 3</label>
                <input type="text" [(ngModel)]="formData.programPreference3" name="programPreference3" maxlength="100" placeholder="e.g. B.Sc. in BBA">
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary btn-lg" [disabled]="submitting || !regForm.valid">
                {{ submitting ? 'Registering...' : 'Submit Registration' }}
              </button>
              <a routerLink="/pre-admission/status" class="btn btn-link">Check Status Instead</a>
            </div>
          </form>
        }
      </div>
    </div>
  `,
  styles: [`
    .public-page { min-height: 100vh; background: linear-gradient(135deg, #f0f4ff 0%, #e0e7ff 100%); display: flex; align-items: flex-start; justify-content: center; padding: 2rem 1rem; }
    .register-card { background: #fff; border-radius: 16px; padding: 2rem; max-width: 800px; width: 100%; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
    .card-header { text-align: center; margin-bottom: 2rem; }
    .uni-logo { margin-bottom: 1rem; }
    .card-header h1 { margin: 0; font-size: 1.75rem; color: #1e293b; }
    .card-header p { margin: 4px 0 0; color: #64748b; }
    h3 { margin: 1.5rem 0 0.75rem; font-size: 1.1rem; color: #334155; border-bottom: 2px solid #e2e8f0; padding-bottom: 6px; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem; }
    .form-row:has(.form-group:nth-child(2)):not(:has(.form-group:nth-child(3))) { grid-template-columns: 1fr 1fr; }
    .form-group { margin-bottom: 1rem; }
    .form-group label { display: block; margin-bottom: 4px; font-size: 0.875rem; font-weight: 500; color: #374151; }
    .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 10px 12px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 0.875rem; transition: border-color 0.15s; box-sizing: border-box; }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { outline: none; border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,0.1); }
    .form-actions { margin-top: 1.5rem; display: flex; gap: 1rem; align-items: center; }
    .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; transition: all 0.15s; text-decoration: none; display: inline-flex; align-items: center; }
    .btn-primary { background: #2563eb; color: #fff; }
    .btn-primary:hover { background: #1d4ed8; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-lg { padding: 12px 28px; font-size: 1rem; }
    .btn-outline { background: #fff; color: #374151; border: 1px solid #d1d5db; }
    .btn-outline:hover { background: #f9fafb; }
    .btn-link { color: #2563eb; background: none; }
    .btn-link:hover { text-decoration: underline; }
    .alert { padding: 1rem; border-radius: 8px; margin-bottom: 1rem; }
    .alert-error { background: #fee2e2; border: 1px solid #fca5a5; color: #991b1b; }
    .alert strong { display: block; margin-bottom: 4px; }
    .alert p { margin: 2px 0; }
    .success-panel { text-align: center; }
    .success-icon { margin-bottom: 1rem; }
    .success-panel h2 { margin: 0 0 1.5rem; font-size: 1.5rem; color: #065f46; }
    .credentials-box { background: #f0fdf4; border: 2px solid #86efac; border-radius: 12px; padding: 1.25rem; margin-bottom: 1.5rem; text-align: left; }
    .credentials-box h3 { margin: 0 0 4px; font-size: 1rem; color: #065f46; border: none; padding: 0; }
    .cred-note { font-size: 0.8125rem; color: #059669; margin: 0 0 12px; }
    .cred-row { display: flex; align-items: center; gap: 8px; padding: 8px 0; border-bottom: 1px solid #d1fae5; }
    .cred-row:last-child { border-bottom: none; }
    .cred-label { font-size: 0.875rem; color: #374151; font-weight: 500; min-width: 80px; }
    .cred-value { font-size: 0.875rem; color: #1e293b; font-family: 'SF Mono', monospace; flex: 1; }
    .password-val { letter-spacing: 2px; }
    .copy-btn { padding: 4px 10px; border: 1px solid #d1fae5; background: #fff; color: #059669; border-radius: 6px; cursor: pointer; font-size: 0.75rem; font-weight: 500; }
    .copy-btn:hover { background: #ecfdf5; }
    .reg-info { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1rem; margin-bottom: 1rem; text-align: left; }
    .info-row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 0.875rem; }
    .info-row span { color: #64748b; }
    .status-submitted { color: #1d4ed8; background: #dbeafe; padding: 2px 8px; border-radius: 4px; }
    .warning-text { font-size: 0.8125rem; color: #b45309; background: #fef3c7; border: 1px solid #fcd34d; border-radius: 8px; padding: 10px 14px; margin-bottom: 1.5rem; text-align: left; }
    .success-actions { display: flex; gap: 12px; justify-content: center; }
    @media (max-width: 640px) { .form-row { grid-template-columns: 1fr; } }
  `]
})
export class PreAdmissionRegisterComponent {
  formData: any = {
    firstName: '', lastName: '', email: '', phone: '', dateOfBirth: '',
    gender: '', address: '', fatherName: '', motherName: '', guardianPhone: '',
    sscGpa: null, sscYear: null, sscBoard: '',
    hscGpa: null, hscYear: null, hscBoard: '',
    programPreference1: '', programPreference2: '', programPreference3: ''
  };
  submitting = false;
  registrationResult: any = null;
  errorMessage = '';
  showPassword = false;

  constructor(private service: PreAdmissionService) {}

  onSubmit() {
    this.submitting = true;
    this.errorMessage = '';
    this.service.register(this.formData).subscribe({
      next: (res) => {
        this.submitting = false;
        this.registrationResult = res;
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = err.error?.message || err.error?.error || err.message || 'Registration failed. Please try again.';
      }
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      alert('Copied to clipboard!');
    });
  }
}
