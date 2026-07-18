import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
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
            <p>Redirecting to your registration details...</p>
            <div class="spinner-section">
              <div class="spinner-lg"></div>
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
                <label>Full Name *</label>
                <input type="text" [(ngModel)]="formData.fullName" name="fullName" required maxlength="200" placeholder="Enter your full name">
              </div>
              <div class="form-group">
                <label>Email *</label>
                <input type="email" [(ngModel)]="formData.email" name="email" required email placeholder="your@email.com">
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Phone</label>
                <input type="text" [(ngModel)]="formData.phone" name="phone" maxlength="20" placeholder="+880XXXXXXXXXX">
              </div>
              <div class="form-group">
                <label>Date of Birth *</label>
                <input type="date" [(ngModel)]="formData.dateOfBirth" name="dateOfBirth" required>
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Gender</label>
                <select [(ngModel)]="formData.gender" name="gender">
                  <option value="">Select</option>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
              <div class="form-group">
                <label>Blood Group</label>
                <select [(ngModel)]="formData.bloodGroup" name="bloodGroup">
                  <option value="">Select</option>
                  <option value="A+">A+</option>
                  <option value="A-">A-</option>
                  <option value="B+">B+</option>
                  <option value="B-">B-</option>
                  <option value="AB+">AB+</option>
                  <option value="AB-">AB-</option>
                  <option value="O+">O+</option>
                  <option value="O-">O-</option>
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
                <label>SSC Passing Year *</label>
                <div class="searchable-select">
                  <input type="text" class="searchable-input" [value]="sscYearSearch" (input)="filterSscYear($event)" (focus)="openDropdown('sscYear')" (blur)="closeDropdown('sscYear')" placeholder="Select SSC Passing Year" autocomplete="off" required name="sscYearSearch">
                  <div class="dropdown-list" *ngIf="dropdowns.sscYear">
                    <div class="dropdown-item" *ngFor="let y of filteredSscYears" (mousedown)="selectSscYear(y)">{{ y }}</div>
                    <div class="dropdown-item no-results" *ngIf="filteredSscYears.length === 0">No results found</div>
                  </div>
                </div>
                <input type="hidden" [(ngModel)]="formData.sscYear" name="sscYear">
              </div>
              <div class="form-group">
                <label>SSC Board *</label>
                <div class="searchable-select">
                  <input type="text" class="searchable-input" [value]="sscBoardSearch" (input)="filterSscBoard($event)" (focus)="openDropdown('sscBoard')" (blur)="closeDropdown('sscBoard')" placeholder="Select Education Board" autocomplete="off" required name="sscBoardSearch">
                  <div class="dropdown-list" *ngIf="dropdowns.sscBoard">
                    <div class="dropdown-item" *ngFor="let b of filteredSscBoards" (mousedown)="selectSscBoard(b)">{{ b }}</div>
                    <div class="dropdown-item no-results" *ngIf="filteredSscBoards.length === 0">No results found</div>
                  </div>
                </div>
                <input type="hidden" [(ngModel)]="formData.sscBoard" name="sscBoard">
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>HSC GPA *</label>
                <input type="number" [(ngModel)]="formData.hscGpa" name="hscGpa" required min="0" max="5" step="0.01" placeholder="0.00 - 5.00">
              </div>
              <div class="form-group">
                <label>HSC Passing Year *</label>
                <div class="searchable-select">
                  <input type="text" class="searchable-input" [value]="hscYearSearch" (input)="filterHscYear($event)" (focus)="openDropdown('hscYear')" (blur)="closeDropdown('hscYear')" placeholder="Select HSC Passing Year" autocomplete="off" required name="hscYearSearch">
                  <div class="dropdown-list" *ngIf="dropdowns.hscYear">
                    <div class="dropdown-item" *ngFor="let y of filteredHscYears" (mousedown)="selectHscYear(y)">{{ y }}</div>
                    <div class="dropdown-item no-results" *ngIf="filteredHscYears.length === 0">No results found</div>
                  </div>
                </div>
                <input type="hidden" [(ngModel)]="formData.hscYear" name="hscYear">
              </div>
              <div class="form-group">
                <label>HSC Board *</label>
                <div class="searchable-select">
                  <input type="text" class="searchable-input" [value]="hscBoardSearch" (input)="filterHscBoard($event)" (focus)="openDropdown('hscBoard')" (blur)="closeDropdown('hscBoard')" placeholder="Select Education Board" autocomplete="off" required name="hscBoardSearch">
                  <div class="dropdown-list" *ngIf="dropdowns.hscBoard">
                    <div class="dropdown-item" *ngFor="let b of filteredHscBoards" (mousedown)="selectHscBoard(b)">{{ b }}</div>
                    <div class="dropdown-item no-results" *ngIf="filteredHscBoards.length === 0">No results found</div>
                  </div>
                </div>
                <input type="hidden" [(ngModel)]="formData.hscBoard" name="hscBoard">
              </div>
            </div>

            <h3>Photo & Signature</h3>
            <div class="upload-row">
              <div class="upload-col">
                <label>Photo *</label>
                <div class="photo-preview" *ngIf="photoPreview">
                  <img [src]="photoPreview" alt="Photo preview">
                  <button type="button" class="photo-remove" (click)="removePhoto()">×</button>
                </div>
                <div class="photo-dropzone compact" *ngIf="!photoPreview" (click)="photoInput.click()" (dragover)="$event.preventDefault()" (drop)="onPhotoDrop($event)">
                  <svg width="28" height="28" viewBox="0 0 40 40" fill="none"><rect width="40" height="40" rx="8" fill="#f1f5f9"/><path d="M20 12v16M12 20h16" stroke="#94a3b8" stroke-width="2" stroke-linecap="round"/></svg>
                  <p>Click or drag photo</p>
                  <span>JPG, PNG — max 2MB</span>
                </div>
                <input type="file" #photoInput accept="image/jpeg,image/png" (change)="onPhotoSelect($event)" style="display:none">
              </div>
              <div class="upload-col">
                <label>Signature *</label>
                <div class="signature-preview" *ngIf="signaturePreview">
                  <img [src]="signaturePreview" alt="Signature preview">
                  <button type="button" class="photo-remove" (click)="removeSignature()">×</button>
                </div>
                <div class="photo-dropzone compact signature-dropzone" *ngIf="!signaturePreview" (click)="signatureInput.click()" (dragover)="$event.preventDefault()" (drop)="onSignatureDrop($event)">
                  <svg width="28" height="28" viewBox="0 0 40 40" fill="none"><rect width="40" height="40" rx="8" fill="#f1f5f9"/><path d="M10 28c3-8 6-12 10-12s4 6 7 6 3-4 5-4" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" fill="none"/></svg>
                  <p>Click or drag signature</p>
                  <span>JPG, PNG — max 1MB</span>
                </div>
                <input type="file" #signatureInput accept="image/jpeg,image/png" (change)="onSignatureSelect($event)" style="display:none">
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
    .searchable-select { position: relative; }
    .searchable-input { width: 100%; padding: 10px 12px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 0.875rem; transition: border-color 0.15s; box-sizing: border-box; background: #fff; }
    .searchable-input:focus { outline: none; border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,0.1); }
    .dropdown-list { position: absolute; top: 100%; left: 0; right: 0; max-height: 200px; overflow-y: auto; background: #fff; border: 1px solid #d1d5db; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); z-index: 1000; margin-top: 4px; }
    .dropdown-item { padding: 8px 12px; font-size: 0.875rem; cursor: pointer; color: #374151; }
    .dropdown-item:hover { background: #f0f4ff; color: #2563eb; }
    .dropdown-item.no-results { color: #9ca3af; cursor: default; font-style: italic; }
    .dropdown-item.no-results:hover { background: transparent; color: #9ca3af; }
    .upload-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 0.5rem; }
    .upload-col label { display: block; margin-bottom: 6px; font-size: 0.875rem; font-weight: 500; color: #374151; }
    .photo-preview { position: relative; width: 100%; height: 100px; border-radius: 8px; overflow: hidden; border: 2px solid #e2e8f0; background: #fff; }
    .photo-preview img { width: 100%; height: 100%; object-fit: cover; }
    .photo-remove { position: absolute; top: 4px; right: 4px; width: 22px; height: 22px; border-radius: 50%; background: #ef4444; color: #fff; border: none; cursor: pointer; font-size: 0.875rem; display: flex; align-items: center; justify-content: center; line-height: 1; }
    .photo-dropzone.compact { border: 2px dashed #d1d5db; border-radius: 8px; padding: 1rem; text-align: center; cursor: pointer; transition: border-color 0.15s, background 0.15s; min-height: 100px; display: flex; flex-direction: column; align-items: center; justify-content: center; }
    .photo-dropzone.compact:hover { border-color: #2563eb; background: #f0f4ff; }
    .photo-dropzone.compact p { margin: 0.25rem 0 2px; font-size: 0.8125rem; color: #374151; font-weight: 500; }
    .photo-dropzone.compact span { font-size: 0.6875rem; color: #94a3af; }
    .signature-preview { position: relative; width: 100%; height: 100px; border-radius: 8px; overflow: hidden; border: 2px solid #e2e8f0; background: #fff; }
    .signature-preview img { width: 100%; height: 100%; object-fit: contain; }
    .signature-dropzone { min-height: 100px; }
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
    .success-panel h2 { margin: 0 0 8px; font-size: 1.5rem; color: #065f46; }
    .success-panel p { margin: 0; color: #64748b; font-size: 0.9375rem; }
    .spinner-section { margin: 1.5rem 0; }
    .spinner-lg { width: 32px; height: 32px; border: 3px solid #e2e8f0; border-top-color: #059669; border-radius: 50%; animation: spin 0.8s linear infinite; display: inline-block; }
    .spinner-sm { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.8s linear infinite; display: inline-block; vertical-align: middle; }
    @keyframes spin { to { transform: rotate(360deg); } }
    @media (max-width: 640px) { .form-row { grid-template-columns: 1fr; } .upload-row { grid-template-columns: 1fr; } }
  `]
})
export class PreAdmissionRegisterComponent {
  formData: any = {
    fullName: '', email: '', phone: '', dateOfBirth: '',
    gender: '', bloodGroup: '', address: '', fatherName: '', motherName: '', guardianPhone: '',
    sscGpa: null, sscYear: null, sscBoard: '',
    hscGpa: null, hscYear: null, hscBoard: '',
    photoUrl: '',
    signatureUrl: ''
  };
  submitting = false;
  registrationResult: any = null;
  errorMessage = '';
  showPassword = false;
  downloadingPdf = false;
  photoPreview: string | null = null;
  signaturePreview: string | null = null;

  currentYear = new Date().getFullYear();
  sscYears: number[] = [];
  hscYears: number[] = [];
  filteredSscYears: number[] = [];
  filteredHscYears: number[] = [];
  boards = [
    'Dhaka', 'Rajshahi', 'Cumilla', 'Jashore', 'Chattogram',
    'Barishal', 'Sylhet', 'Dinajpur', 'Mymensingh',
    'Bangladesh Madrasah Education Board', 'Bangladesh Technical Education Board'
  ];
  filteredSscBoards: string[] = [...this.boards];
  filteredHscBoards: string[] = [...this.boards];
  sscYearSearch = '';
  sscBoardSearch = '';
  hscYearSearch = '';
  hscBoardSearch = '';
  dropdowns: any = { sscYear: false, sscBoard: false, hscYear: false, hscBoard: false };

  constructor(private service: PreAdmissionService, private router: Router) {
    this.sscYears = [];
    for (let y = 2020; y <= this.currentYear; y++) this.sscYears.push(y);
    this.filteredSscYears = [...this.sscYears];
    this.hscYears = [];
    for (let y = 2022; y <= this.currentYear + 2; y++) this.hscYears.push(y);
    this.filteredHscYears = [...this.hscYears];
  }

  openDropdown(name: string) { this.dropdowns[name] = true; }
  closeDropdown(name: string) { setTimeout(() => { this.dropdowns[name] = false; }, 150); }

  filterSscYear(e: Event) {
    const v = (e.target as HTMLInputElement).value.toLowerCase();
    this.filteredSscYears = this.sscYears.filter(y => y.toString().includes(v));
  }
  filterHscYear(e: Event) {
    const v = (e.target as HTMLInputElement).value.toLowerCase();
    this.filteredHscYears = this.hscYears.filter(y => y.toString().includes(v));
  }
  filterSscBoard(e: Event) {
    const v = (e.target as HTMLInputElement).value.toLowerCase();
    this.filteredSscBoards = this.boards.filter(b => b.toLowerCase().includes(v));
  }
  filterHscBoard(e: Event) {
    const v = (e.target as HTMLInputElement).value.toLowerCase();
    this.filteredHscBoards = this.boards.filter(b => b.toLowerCase().includes(v));
  }

  selectSscYear(y: number) { this.formData.sscYear = y; this.sscYearSearch = y.toString(); }
  selectHscYear(y: number) { this.formData.hscYear = y; this.hscYearSearch = y.toString(); }
  selectSscBoard(b: string) { this.formData.sscBoard = b; this.sscBoardSearch = b; }
  selectHscBoard(b: string) { this.formData.hscBoard = b; this.hscBoardSearch = b; }

  onSubmit() {
    this.submitting = true;
    this.errorMessage = '';
    const parts = this.formData.fullName.trim().split(/\s+/);
    const payload = {
      ...this.formData,
      firstName: parts[0] || '',
      lastName: parts.slice(1).join(' ') || parts[0] || ''
    };
    delete payload.fullName;
    this.service.register(payload).subscribe({
      next: (res) => {
        this.submitting = false;
        this.registrationResult = res;
        this.router.navigate(['/pre-admission/registration-success'], {
          queryParams: {
            id: res.id,
            reg: res.registrationNumber,
            tracking: res.trackingNumber,
            firstName: res.firstName,
            lastName: res.lastName,
            email: res.email,
            phone: res.phone || '',
            loginEmail: res.loginEmail,
            password: res.tempPassword,
            status: res.status || 'SUBMITTED'
          }
        });
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = this.parseError(err);
      }
    });
  }

  private parseError(err: any): string {
    if (err.status === 0 || !err.status) {
      return 'Network error. Please check your connection and try again.';
    }
    if (err.status === 503 || err.status === 502) {
      return 'Server unavailable. Please try again later.';
    }
    if (err.status === 500) {
      const msg = (err.error?.message || '').toLowerCase();
      if (msg.includes('database') || msg.includes('connection') || msg.includes('datasource')) {
        return 'Database connection failed. Please try again later.';
      }
      return 'Server error. Please try again later.';
    }
    if (err.status === 403) {
      const msg = (err.error?.message || '').toLowerCase();
      if (msg.includes('feature') || msg.includes('disabled') || msg.includes('closed')) {
        return 'Application is currently closed. Please try again later.';
      }
      return err.error?.message || 'Access denied.';
    }
    if (err.status === 404) {
      return err.error?.message || 'Resource not found.';
    }
    const body = err.error;
    if (body?.errors && typeof body.errors === 'object') {
      const fieldErrors: string[] = [];
      const fieldLabels: Record<string, string> = {
        firstName: 'First name', lastName: 'Last name', email: 'Email',
        phone: 'Phone', dateOfBirth: 'Date of birth', gender: 'Gender',
        photoUrl: 'Photo', signatureUrl: 'Signature'
      };
      for (const [field, msg] of Object.entries(body.errors)) {
        const label = fieldLabels[field] || field;
        fieldErrors.push(`${label}: ${msg}`);
      }
      return fieldErrors.length > 0 ? fieldErrors.join('. ') + '.' : 'Validation failed. Please check your input.';
    }
    const msg = (body?.message || '').toLowerCase();
    if (msg.includes('email already exists') || msg.includes('application with this email')) {
      return 'An application with this email already exists. Please use a different email or check your existing application status.';
    }
    if (msg.includes('user account with this email')) {
      return 'A user account with this email already exists. Please use a different email.';
    }
    if (msg.includes('user account with this username')) {
      return 'A user account with this username already exists. Please use a different email.';
    }
    if (msg.includes('phone') && msg.includes('exist')) {
      return 'An application with this phone number already exists.';
    }
    if (msg.includes('validation')) {
      return 'Validation failed. Please check all required fields.';
    }
    if (body?.message) {
      return body.message;
    }
    if (body?.error) {
      return body.error;
    }
    if (err.message) {
      if (err.message.includes('HttpErrorResponse')) {
        return 'Network error. Please check your connection and try again.';
      }
      if (err.message.includes('timeout')) {
        return 'Request timed out. Please try again.';
      }
      return err.message;
    }
    return 'Registration failed. Please try again.';
  }

  onPhotoSelect(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) this.processPhoto(file);
  }

  onPhotoDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files?.[0];
    if (file) this.processPhoto(file);
  }

  processPhoto(file: File) {
    if (file.size > 2 * 1024 * 1024) {
      this.errorMessage = 'Photo must be less than 2MB';
      return;
    }
    if (!['image/jpeg', 'image/png'].includes(file.type)) {
      this.errorMessage = 'Only JPG and PNG files are allowed';
      return;
    }
    const reader = new FileReader();
    reader.onload = () => {
      this.photoPreview = reader.result as string;
      this.formData.photoUrl = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  removePhoto() {
    this.photoPreview = null;
    this.formData.photoUrl = '';
  }

  onSignatureSelect(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) this.processSignature(file);
  }

  onSignatureDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files?.[0];
    if (file) this.processSignature(file);
  }

  processSignature(file: File) {
    if (file.size > 1 * 1024 * 1024) {
      this.errorMessage = 'Signature must be less than 1MB';
      return;
    }
    if (!['image/jpeg', 'image/png'].includes(file.type)) {
      this.errorMessage = 'Only JPG and PNG files are allowed';
      return;
    }
    const reader = new FileReader();
    reader.onload = () => {
      this.signaturePreview = reader.result as string;
      this.formData.signatureUrl = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  removeSignature() {
    this.signaturePreview = null;
    this.formData.signatureUrl = '';
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      alert('Copied to clipboard!');
    });
  }

  downloadRegistrationPdf() {
    if (!this.registrationResult?.registrationNumber) return;
    this.downloadingPdf = true;
    this.service.getRegistrationPdf(this.registrationResult.registrationNumber).subscribe({
      next: (blob) => {
        this.downloadingPdf = false;
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `registration-${this.registrationResult.registrationNumber}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => {
        this.downloadingPdf = false;
        this.errorMessage = 'Failed to generate PDF. Please try again.';
      }
    });
  }
}
