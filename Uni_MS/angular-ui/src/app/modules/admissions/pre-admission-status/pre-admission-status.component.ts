import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PreAdmissionService } from '../../../services/pre-admission.service';

@Component({
  selector: 'app-pre-admission-status',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="public-page">
      <div class="status-card">
        <div class="card-header">
          <div class="uni-logo">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
              <rect width="48" height="48" rx="12" fill="#2563eb"/>
              <path d="M14 34V20l10-8 10 8v14H28V26h-8v8z" fill="#fff"/>
              <rect x="12" y="34" width="24" height="3" rx="1" fill="#fff"/>
            </svg>
          </div>
          <h1>Check Registration Status</h1>
          <p>Enter your registration number to check your admission status</p>
        </div>

        <form (ngSubmit)="checkStatus()">
          <div class="search-row">
            <input type="text" [(ngModel)]="registrationNumber" name="regNo" placeholder="Enter registration number" required>
            <button type="submit" class="btn btn-primary" [disabled]="loading || !registrationNumber">
              {{ loading ? 'Checking...' : 'Check Status' }}
            </button>
          </div>
        </form>

        @if (errorMessage) {
          <div class="alert alert-error">{{ errorMessage }}</div>
        }

        @if (statusResult) {
          <div class="result-box">
            <div class="result-row">
              <span class="label">Registration No</span>
              <span class="value">{{ statusResult.registrationNumber }}</span>
            </div>
            <div class="result-row">
              <span class="label">Name</span>
              <span class="value">{{ statusResult.firstName }} {{ statusResult.lastName }}</span>
            </div>
            <div class="result-row">
              <span class="label">Status</span>
              <span class="value status-badge" [attr.data-status]="statusResult.status">{{ formatStatus(statusResult.status) }}</span>
            </div>
          </div>
        }

        <div class="footer-links">
          <a routerLink="/pre-admission/register">Register New Application</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .public-page { min-height: 100vh; background: linear-gradient(135deg, #f0f4ff 0%, #e0e7ff 100%); display: flex; align-items: center; justify-content: center; padding: 2rem 1rem; }
    .status-card { background: #fff; border-radius: 16px; padding: 2rem; max-width: 560px; width: 100%; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
    .card-header { text-align: center; margin-bottom: 2rem; }
    .uni-logo { margin-bottom: 1rem; }
    .card-header h1 { margin: 0; font-size: 1.5rem; color: #1e293b; }
    .card-header p { margin: 4px 0 0; color: #64748b; }
    .search-row { display: flex; gap: 8px; }
    .search-row input { flex: 1; padding: 12px 16px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 1rem; }
    .search-row input:focus { outline: none; border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,0.1); }
    .btn { padding: 12px 24px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; transition: all 0.15s; }
    .btn-primary { background: #2563eb; color: #fff; }
    .btn-primary:hover { background: #1d4ed8; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert { padding: 1rem; border-radius: 8px; margin-top: 1rem; }
    .alert-error { background: #fee2e2; border: 1px solid #fca5a5; color: #991b1b; }
    .result-box { margin-top: 1.5rem; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.25rem; }
    .result-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #e2e8f0; }
    .result-row:last-child { border-bottom: none; }
    .result-row .label { color: #64748b; font-size: 0.875rem; }
    .result-row .value { font-weight: 600; color: #1e293b; }
    .status-badge { padding: 2px 10px; border-radius: 12px; font-size: 0.8rem; font-weight: 600; }
    .status-badge[data-status="DRAFT"] { background: #f1f5f9; color: #64748b; }
    .status-badge[data-status="SUBMITTED"] { background: #dbeafe; color: #1d4ed8; }
    .status-badge[data-status="ADMIT_CARD_GENERATED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="TEST_COMPLETED"] { background: #e0e7ff; color: #3730a3; }
    .status-badge[data-status="MERIT_PROCESSED"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="ALLOCATED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="ENROLLED"] { background: #a7f3d0; color: #065f46; }
    .status-badge[data-status="REJECTED"] { background: #fee2e2; color: #991b1b; }
    .footer-links { margin-top: 1.5rem; text-align: center; }
    .footer-links a { color: #2563eb; text-decoration: none; font-size: 0.875rem; }
    .footer-links a:hover { text-decoration: underline; }
  `]
})
export class PreAdmissionStatusComponent {
  registrationNumber = '';
  loading = false;
  statusResult: any = null;
  errorMessage = '';

  constructor(private service: PreAdmissionService) {}

  checkStatus() {
    this.loading = true;
    this.errorMessage = '';
    this.statusResult = null;
    this.service.checkStatus(this.registrationNumber).subscribe({
      next: (data) => { this.statusResult = data; this.loading = false; },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.status === 404
          ? 'Registration number not found. Please check and try again.'
          : 'Failed to check status. Please try again later.';
      }
    });
  }

  formatStatus(status: string): string {
    return status?.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase()) || '';
  }
}
