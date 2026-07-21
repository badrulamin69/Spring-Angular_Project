import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { PreAdmissionService } from '../../../services/pre-admission.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-registration-success',
  standalone: true,
  imports: [CommonModule, RouterLink, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="public-page">
      <div class="success-card">

        <div class="success-header">
          <div class="success-icon">
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
              <circle cx="32" cy="32" r="32" fill="#d1fae5"/>
              <path d="M20 32l8 8 16-16" stroke="#059669" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h1>Registration Successful!</h1>
          <p class="success-subtitle">Your application has been submitted successfully. Please save your details below.</p>
        </div>

        <div class="reg-summary-grid">
          <div class="summary-card">
            <span class="summary-label">Applicant ID</span>
            <span class="summary-value">{{ data.id }}</span>
          </div>
          <div class="summary-card">
            <span class="summary-label">Registration Number</span>
            <span class="summary-value highlight">{{ data.registrationNumber }}</span>
          </div>
          <div class="summary-card">
            <span class="summary-label">Tracking Number</span>
            <span class="summary-value">{{ data.trackingNumber }}</span>
          </div>
          <div class="summary-card">
            <span class="summary-label">Payment Status</span>
            <span class="summary-value status-pending">Pending</span>
          </div>
        </div>

        <div class="qr-section" *ngIf="qrCode">
          <h3>Scan to Check Status</h3>
          <img [src]="'data:image/png;base64,' + qrCode" alt="QR Code" class="qr-image">
          <p class="qr-hint">Scan this QR code to check your application status anytime</p>
        </div>

        <div class="credentials-box">
          <h3>Your Login Credentials</h3>
          <p class="cred-note">Use these credentials to login to the student portal</p>
          <div class="cred-row">
            <span class="cred-label">Email:</span>
            <span class="cred-value">{{ data.loginEmail }}</span>
            <button class="copy-btn" (click)="copyToClipboard(data.loginEmail)">Copy</button>
          </div>
          <div class="cred-row">
            <span class="cred-label">Password:</span>
            <span class="cred-value password-val">{{ showPassword ? data.tempPassword : '••••••••••' }}</span>
            <button class="copy-btn" (click)="togglePassword()">{{ showPassword ? 'Hide' : 'Show' }}</button>
            <button class="copy-btn" (click)="copyToClipboard(data.tempPassword)">Copy</button>
          </div>
        </div>

        <div class="applicant-details">
          <h3>Applicant Details</h3>
          <div class="details-grid">
            <div class="detail-row">
              <span class="detail-label">Full Name</span>
              <span class="detail-value">{{ data.firstName }} {{ data.lastName }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">Email</span>
              <span class="detail-value">{{ data.email }}</span>
            </div>
            <div class="detail-row" *ngIf="data.phone">
              <span class="detail-label">Phone</span>
              <span class="detail-value">{{ data.phone }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">Application Status</span>
              <span class="detail-value status-badge">{{ data.status }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">Submitted</span>
              <span class="detail-value">{{ submissionTime }}</span>
            </div>
          </div>
        </div>

        <div class="action-buttons">
          <button class="btn btn-primary" (click)="downloadPdf()" [disabled]="downloadingPdf">
            @if (downloadingPdf) {
              <span class="spinner-sm"></span> Generating PDF...
            } @else {
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
              Download Registration Copy (PDF)
            }
          </button>
          <button class="btn btn-outline" (click)="printPage()">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 6 2 18 2 18 9"/><path d="M6 18H4a2 2 0 01-2-2v-5a2 2 0 012-2h16a2 2 0 012 2v5a2 2 0 01-2 2h-2"/><rect x="6" y="14" width="12" height="8"/></svg>
            Print
          </button>
        </div>

        <div class="timeline-section">
          <h3>Admission Timeline</h3>
          <div class="timeline">
            <div class="timeline-item active">
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <strong>Application Submitted</strong>
                <p>Your application has been received and is under review.</p>
              </div>
            </div>
            <div class="timeline-item">
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <strong>Application Review</strong>
                <p>Admissions team reviews your academic credentials.</p>
              </div>
            </div>
            <div class="timeline-item">
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <strong>Admit Card Generation</strong>
                <p>Once approved, your admit card will be generated.</p>
              </div>
            </div>
            <div class="timeline-item">
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <strong>Online Admission Test</strong>
                <p>You will take an online MCQ admission test from the portal.</p>
              </div>
            </div>
            <div class="timeline-item">
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <strong>Merit Processing & Allocation</strong>
                <p>Your test score and academic GPA will be combined for merit ranking.</p>
              </div>
            </div>
          </div>
        </div>

        <div class="warning-text">
          <strong>Important:</strong> Please save your registration number and tracking number. You will need them to check your application status and download your admit card after approval.
        </div>

        <div class="bottom-actions">
          <a routerLink="/" class="btn btn-outline">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            Back to Home
          </a>
          <a routerLink="/login" class="btn btn-primary">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 3h4a2 2 0 012 2v14a2 2 0 01-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" y1="12" x2="3" y2="12"/></svg>
            Login to Portal
          </a>
          <a routerLink="/pre-admission/register" class="btn btn-outline">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 5v14M5 12h14"/></svg>
            Apply Another Program
          </a>
        </div>

      </div>
    </div>
  `,
  styles: [`
    .public-page { min-height: 100vh; background: linear-gradient(135deg, #f0f4ff 0%, #e0e7ff 100%); display: flex; align-items: flex-start; justify-content: center; padding: 2rem 1rem; }
    .success-card { background: #fff; border-radius: 16px; padding: 2rem; max-width: 720px; width: 100%; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
    .success-header { text-align: center; margin-bottom: 1.5rem; }
    .success-icon { margin-bottom: 1rem; }
    .success-header h1 { margin: 0 0 6px; font-size: 1.75rem; color: #065f46; }
    .success-subtitle { margin: 0; color: #64748b; font-size: 0.9375rem; }
    .reg-summary-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; margin-bottom: 1.5rem; }
    .summary-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 14px 16px; text-align: center; }
    .summary-label { display: block; font-size: 0.75rem; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 4px; }
    .summary-value { display: block; font-size: 0.9375rem; font-weight: 700; color: #1e293b; }
    .summary-value.highlight { color: #004080; font-family: 'SF Mono', 'Consolas', monospace; font-size: 0.875rem; }
    .status-pending { color: #b38600; background: #fef3c7; padding: 2px 10px; border-radius: 12px; display: inline-block; font-size: 0.8125rem !important; }
    .qr-section { text-align: center; margin-bottom: 1.5rem; padding: 1.5rem; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; }
    .qr-section h3 { margin: 0 0 12px; font-size: 1rem; color: #1e293b; }
    .qr-image { width: 180px; height: 180px; border: 2px solid #e2e8f0; border-radius: 8px; }
    .qr-hint { margin: 8px 0 0; font-size: 0.8125rem; color: #64748b; }
    .credentials-box { background: #f0fdf4; border: 2px solid #86efac; border-radius: 12px; padding: 1.25rem; margin-bottom: 1.5rem; text-align: left; }
    .credentials-box h3 { margin: 0 0 4px; font-size: 1rem; color: #065f46; border: none; padding: 0; }
    .cred-note { font-size: 0.8125rem; color: #059669; margin: 0 0 12px; }
    .cred-row { display: flex; align-items: center; gap: 8px; padding: 8px 0; border-bottom: 1px solid #d1fae5; }
    .cred-row:last-child { border-bottom: none; }
    .cred-label { font-size: 0.875rem; color: #374151; font-weight: 500; min-width: 80px; }
    .cred-value { font-size: 0.875rem; color: #1e293b; font-family: 'SF Mono', monospace; flex: 1; }
    .password-val { letter-spacing: 2px; }
    .copy-btn { padding: 4px 10px; border: 1px solid #d1fae5; background: #fff; color: #059669; border-radius: 6px; cursor: pointer; font-size: 0.75rem; font-weight: 500; transition: background 0.15s; }
    .copy-btn:hover { background: #ecfdf5; }
    .applicant-details { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.25rem; margin-bottom: 1.5rem; text-align: left; }
    .applicant-details h3 { margin: 0 0 1rem; font-size: 1rem; color: #1e293b; border: none; padding: 0; }
    .details-grid { display: flex; flex-direction: column; gap: 0; }
    .detail-row { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #e2e8f0; }
    .detail-row:last-child { border-bottom: none; }
    .detail-label { font-size: 0.875rem; color: #64748b; }
    .detail-value { font-size: 0.875rem; color: #1e293b; font-weight: 600; }
    .status-badge { color: #002d5f; background: #dbeafe; padding: 2px 10px; border-radius: 12px; font-size: 0.8125rem !important; }
    .action-buttons { display: flex; gap: 12px; margin-bottom: 1.5rem; flex-wrap: wrap; }
    .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; transition: all 0.15s; text-decoration: none; display: inline-flex; align-items: center; gap: 6px; }
    .btn-primary { background: #059669; color: #fff; }
    .btn-primary:hover { background: #047857; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-outline { background: #fff; color: #374151; border: 1px solid #d1d5db; }
    .btn-outline:hover { background: #f9fafb; }
    .spinner-sm { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.8s linear infinite; display: inline-block; vertical-align: middle; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .timeline-section { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.25rem; margin-bottom: 1.5rem; text-align: left; }
    .timeline-section h3 { margin: 0 0 1rem; font-size: 1rem; color: #1e293b; border: none; padding: 0; }
    .timeline { display: flex; flex-direction: column; gap: 0; padding-left: 8px; }
    .timeline-item { display: flex; gap: 14px; align-items: flex-start; position: relative; padding-bottom: 16px; padding-left: 4px; }
    .timeline-item:last-child { padding-bottom: 0; }
    .timeline-item:not(:last-child)::before { content: ''; position: absolute; left: 11px; top: 20px; bottom: 0; width: 2px; background: #e2e8f0; }
    .timeline-item.active:not(:last-child)::before { background: #004080; }
    .timeline-dot { width: 24px; height: 24px; border-radius: 50%; background: #e2e8f0; border: 3px solid #fff; box-shadow: 0 0 0 2px #e2e8f0; flex-shrink: 0; margin-top: 2px; z-index: 1; }
    .timeline-item.active .timeline-dot { background: #004080; box-shadow: 0 0 0 2px #004080; }
    .timeline-content strong { font-size: 0.875rem; color: #1e293b; display: block; margin-bottom: 2px; }
    .timeline-content p { margin: 0; font-size: 0.8125rem; color: #64748b; line-height: 1.4; }
    .warning-text { font-size: 0.8125rem; color: #b45309; background: #fef3c7; border: 1px solid #fcd34d; border-radius: 8px; padding: 10px 14px; margin-bottom: 1.5rem; text-align: left; }
    .warning-text strong { color: #92400e; }
    .bottom-actions { display: flex; gap: 12px; justify-content: center; flex-wrap: wrap; }
    @media (max-width: 600px) {
      .reg-summary-grid { grid-template-columns: 1fr; }
      .action-buttons { flex-direction: column; }
      .bottom-actions { flex-direction: column; }
    }
  `]
})
export class RegistrationSuccessComponent implements OnInit {
  data: any = {};
  qrCode: string | null = null;
  showPassword = false;
  downloadingPdf = false;
  submissionTime = '';

  constructor(
    private route: ActivatedRoute,
    private service: PreAdmissionService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    const params = this.route.snapshot.queryParams;
    this.data = {
      id: params['id'] || '',
      registrationNumber: params['reg'] || '',
      trackingNumber: params['tracking'] || '',
      firstName: params['firstName'] || '',
      lastName: params['lastName'] || '',
      email: params['email'] || '',
      phone: params['phone'] || '',
      loginEmail: params['loginEmail'] || '',
      tempPassword: params['password'] || '',
      status: params['status'] || 'SUBMITTED',
    };

    this.submissionTime = new Date().toLocaleString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });

    if (this.data.registrationNumber) {
      this.service.getRegistrationQrCode(this.data.registrationNumber).subscribe({
        next: (blob) => {
          const reader = new FileReader();
          reader.onload = () => {
            const base64 = (reader.result as string).split(',')[1];
            this.qrCode = base64;
          };
          reader.readAsDataURL(blob);
        },
        error: () => this.toastService.error('Operation failed. Please try again.')
      });
    }
  }

  downloadPdf() {
    if (!this.data.registrationNumber) return;
    this.downloadingPdf = true;
    this.service.getRegistrationPdf(this.data.registrationNumber).subscribe({
      next: (blob) => {
        this.downloadingPdf = false;
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `registration-${this.data.registrationNumber}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => {
        this.downloadingPdf = false;
        this.toastService.error('Failed to generate PDF. Please try again.');
      }
    });
  }

  printPage() {
    window.print();
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      this.toastService.success('Copied to clipboard!');
    });
  }
}
