import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdmissionConfirmationService } from '../../../services/admission-confirmation.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-applicant-confirmation',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>My Admission Confirmation</h2>
        <p class="page-sub">Track your admission confirmation status</p>
      </div>
    </div>

    @if (loading) {
      <div class="loading-state"><div class="spinner"></div><span>Loading...</span></div>
    } @else if (confirmation) {
      <div class="confirmation-card">
        <div class="card-header">
          <h3>{{ confirmation.confirmationNumber }}</h3>
          <span class="status-badge" [attr.data-status]="confirmation.status">{{ formatStatus(confirmation.status) }}</span>
        </div>

        <div class="progress-steps">
          <div class="step" [class.active]="isStepActive(0)" [class.completed]="isStepCompleted(0)">
            <div class="step-icon">1</div>
            <div class="step-label">Allocation Confirmed</div>
          </div>
          <div class="step-line" [class.active]="isStepCompleted(0)"></div>
          <div class="step" [class.active]="isStepActive(1)" [class.completed]="isStepCompleted(1)">
            <div class="step-icon">2</div>
            <div class="step-label">Documents Submitted</div>
          </div>
          <div class="step-line" [class.active]="isStepCompleted(1)"></div>
          <div class="step" [class.active]="isStepActive(2)" [class.completed]="isStepCompleted(2)">
            <div class="step-icon">3</div>
            <div class="step-label">Documents Verified</div>
          </div>
          <div class="step-line" [class.active]="isStepCompleted(2)"></div>
          <div class="step" [class.active]="isStepActive(3)" [class.completed]="isStepCompleted(3)">
            <div class="step-icon">4</div>
            <div class="step-label">Fee Paid</div>
          </div>
          <div class="step-line" [class.active]="isStepCompleted(3)"></div>
          <div class="step" [class.active]="isStepActive(4)" [class.completed]="isStepCompleted(4)">
            <div class="step-icon">5</div>
            <div class="step-label">Enrolled</div>
          </div>
        </div>

        <div class="detail-grid">
          <div class="detail-item">
            <label>Program</label>
            <span>{{ confirmation.allocation?.allocatedProgram?.name || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>Department</label>
            <span>{{ confirmation.allocation?.allocatedDepartment?.name || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>Faculty</label>
            <span>{{ confirmation.allocation?.allocatedFaculty?.name || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>Shift</label>
            <span>{{ confirmation.allocation?.shift || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>Merit Rank</label>
            <span>#{{ confirmation.allocation?.meritRank || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>Status</label>
            <span class="status-badge" [attr.data-status]="confirmation.status">{{ formatStatus(confirmation.status) }}</span>
          </div>
        </div>

        @if (confirmation.documentRemarks) {
          <div class="remarks-box">
            <label>Remarks</label>
            <p>{{ confirmation.documentRemarks }}</p>
          </div>
        }

        @if (documents.length > 0) {
          <div class="section">
            <h4>Submitted Documents</h4>
            <div class="doc-list">
              @for (doc of documents; track doc.id) {
                <div class="doc-item">
                  <span class="doc-type">{{ doc.documentType }}</span>
                  <span class="doc-name">{{ doc.documentName }}</span>
                  <span class="status-badge" [attr.data-status]="doc.status">{{ doc.status }}</span>
                </div>
              }
            </div>
          </div>
        }

        @if (confirmation.status === 'PENDING' || confirmation.status === 'ALLOCATED') {
          <div class="section">
            <h4>Submit Documents</h4>
            <div class="doc-form">
              @for (doc of newDocs; track $index; let i = $index) {
                <div class="doc-row">
                  <select [(ngModel)]="doc.documentType" class="form-control">
                    <option value="SSC_CERTIFICATE">SSC Certificate</option>
                    <option value="HSC_CERTIFICATE">HSC Certificate</option>
                    <option value="TRANSCRIPT">Transcript</option>
                    <option value="ADMIT_CARD">Admit Card</option>
                    <option value="PHOTO">Photo</option>
                    <option value="SIGNATURE">Signature</option>
                    <option value="NATIONAL_ID">National ID</option>
                    <option value="BIRTH_CERTIFICATE">Birth Certificate</option>
                    <option value="OTHER">Other</option>
                  </select>
                  <input type="text" [(ngModel)]="doc.documentName" placeholder="Document name" class="form-control">
                  <input type="text" [(ngModel)]="doc.fileUrl" placeholder="File URL" class="form-control">
                  @if (newDocs.length > 1) {
                    <button class="btn btn-sm btn-danger" (click)="removeDoc(i)">Remove</button>
                  }
                </div>
              }
              <div class="doc-actions">
                <button class="btn btn-sm btn-outline" (click)="addDoc()">+ Add Document</button>
                <button class="btn btn-sm btn-primary" (click)="submitDocs()" [disabled]="submitting">
                  @if (submitting) {
                    <span class="spinner-sm"></span> Submitting...
                  } @else {
                    Submit Documents
                  }
                </button>
              </div>
            </div>
          </div>
        }

        @if (confirmation.status === 'DOCUMENTS_VERIFIED' && !confirmation.feePaid) {
          <div class="section">
            <h4>Pay Admission Fee</h4>
            <div class="fee-form">
              <div class="form-row">
                <div class="form-group">
                  <label>Amount</label>
                  <input type="number" [(ngModel)]="feeAmount" placeholder="Enter amount" class="form-control">
                </div>
                <div class="form-group">
                  <label>Payment Method</label>
                  <select [(ngModel)]="feePaymentMethod" class="form-control">
                    <option value="CASH">Cash</option>
                    <option value="BANK_TRANSFER">Bank Transfer</option>
                    <option value="CARD">Card</option>
                    <option value="MOBILE_BANKING">Mobile Banking</option>
                  </select>
                </div>
              </div>
              <button class="btn btn-sm btn-primary" (click)="payFee()" [disabled]="paying">
                @if (paying) {
                  <span class="spinner-sm"></span> Processing...
                } @else {
                  Pay Fee
                }
              </button>
            </div>
          </div>
        }
      </div>
    } @else {
      <div class="empty-state">
        <div class="empty-icon">&#9888;</div>
        <h3>No Confirmation Found</h3>
        <p>You don't have an active admission confirmation. Please wait for seat allocation.</p>
      </div>
    }
  `,
  styles: [`
    .page-header { margin-bottom: 24px; }
    .page-header h2 { font-size: 1.5rem; font-weight: 700; color: var(--text-primary); margin: 0; }
    .page-sub { font-size: 0.875rem; color: var(--text-muted); margin: 4px 0 0; }
    .loading-state { display: flex; align-items: center; justify-content: center; gap: 12px; padding: 60px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: #0056b3; border-radius: 50%; animation: spin 0.8s linear infinite; }
    .spinner-sm { width: 14px; height: 14px; border: 2px solid var(--border-color); border-top-color: #0056b3; border-radius: 50%; animation: spin 0.8s linear infinite; display: inline-block; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .confirmation-card { background: var(--bg-secondary); border-radius: 12px; border: 1px solid var(--border-color); overflow: hidden; }
    .card-header { display: flex; align-items: center; justify-content: space-between; padding: 20px; border-bottom: 1px solid var(--border-color); }
    .card-header h3 { margin: 0; font-size: 1.125rem; color: var(--text-primary); }
    .progress-steps { display: flex; align-items: center; justify-content: center; padding: 32px 20px; gap: 0; }
    .step { display: flex; flex-direction: column; align-items: center; gap: 8px; }
    .step-icon { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.875rem; font-weight: 600; background: var(--bg-tertiary); color: var(--text-muted); border: 2px solid var(--border-color); transition: all 0.3s; }
    .step.active .step-icon { background: #dbeafe; color: #004080; border-color: #0056b3; }
    .step.completed .step-icon { background: #dcfce7; color: #166534; border-color: #28a745; }
    .step-label { font-size: 0.7rem; color: var(--text-muted); text-align: center; max-width: 80px; }
    .step.active .step-label, .step.completed .step-label { color: var(--text-primary); }
    .step-line { width: 40px; height: 2px; background: var(--border-color); margin-bottom: 20px; }
    .step-line.active { background: #28a745; }
    .detail-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; padding: 20px; }
    .detail-item label { display: block; font-size: 0.75rem; color: var(--text-muted); margin-bottom: 4px; }
    .detail-item span { font-size: 0.875rem; color: var(--text-primary); }
    .remarks-box { margin: 0 20px 20px; padding: 12px; background: var(--bg-tertiary); border-radius: 8px; }
    .remarks-box label { display: block; font-size: 0.75rem; color: var(--text-muted); margin-bottom: 4px; }
    .remarks-box p { margin: 0; font-size: 0.875rem; color: var(--text-primary); }
    .section { padding: 20px; border-top: 1px solid var(--border-color); }
    .section h4 { margin: 0 0 12px; font-size: 1rem; color: var(--text-primary); }
    .doc-list { display: flex; flex-direction: column; gap: 8px; }
    .doc-item { display: flex; align-items: center; gap: 12px; padding: 10px; background: var(--bg-tertiary); border-radius: 8px; }
    .doc-type { font-weight: 600; font-size: 0.875rem; min-width: 120px; }
    .doc-name { flex: 1; font-size: 0.875rem; color: var(--text-muted); }
    .doc-form { display: flex; flex-direction: column; gap: 12px; }
    .doc-row { display: flex; gap: 8px; align-items: center; }
    .doc-actions { display: flex; gap: 8px; justify-content: flex-end; }
    .fee-form { display: flex; flex-direction: column; gap: 12px; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .form-group label { display: block; font-size: 0.75rem; color: var(--text-muted); margin-bottom: 4px; }
    .form-control { padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border-color); background: var(--bg-tertiary); color: var(--text-primary); font-size: 0.875rem; flex: 1; }
    .btn { padding: 8px 16px; border-radius: 8px; font-size: 0.875rem; font-weight: 500; cursor: pointer; border: none; transition: all 0.15s; }
    .btn-sm { padding: 6px 12px; font-size: 0.8rem; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-primary); }
    .btn-outline:hover { background: var(--bg-hover); }
    .btn-primary { background: #0056b3; color: #fff; }
    .btn-primary:hover { background: #004080; }
    .btn-danger { background: #dc3545; color: #fff; }
    .btn-danger:hover { background: #bd2130; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .status-badge { padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 500; white-space: nowrap; }
    .status-badge[data-status="PENDING"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="ALLOCATED"] { background: #dbeafe; color: #002d5f; }
    .status-badge[data-status="DOCUMENTS_SUBMITTED"] { background: #e0e7ff; color: #3730a3; }
    .status-badge[data-status="DOCUMENTS_VERIFIED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="DOCUMENTS_REJECTED"] { background: #fee2e2; color: #991b1b; }
    .status-badge[data-status="FEE_PAID"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="CONFIRMED"] { background: #dcfce7; color: #166534; }
    .status-badge[data-status="ENROLLED"] { background: #dcfce7; color: #166534; }
    .status-badge[data-status="SUBMITTED"] { background: #dbeafe; color: #002d5f; }
    .status-badge[data-status="VERIFIED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="REJECTED"] { background: #fee2e2; color: #991b1b; }
    .empty-state { text-align: center; padding: 60px 20px; background: var(--bg-secondary); border-radius: 12px; border: 1px solid var(--border-color); }
    .empty-icon { font-size: 3rem; margin-bottom: 16px; }
    .empty-state h3 { margin: 0 0 8px; color: var(--text-primary); }
    .empty-state p { margin: 0; color: var(--text-muted); font-size: 0.875rem; }
    @media (max-width: 768px) {
      .progress-steps { flex-wrap: wrap; gap: 4px; }
      .step-line { width: 20px; }
      .doc-row { flex-direction: column; }
      .form-row { grid-template-columns: 1fr; }
    }
  `]
})
export class ApplicantConfirmationComponent implements OnInit {
  confirmation: any = null;
  documents: any[] = [];
  loading = false;
  submitting = false;
  paying = false;
  feeAmount = 0;
  feePaymentMethod = 'CASH';
  newDocs = [{ documentType: 'SSC_CERTIFICATE', documentName: '', fileUrl: '' }];

  private statusOrder = ['PENDING', 'ALLOCATED', 'DOCUMENTS_SUBMITTED', 'DOCUMENTS_VERIFIED', 'FEE_PAID', 'CONFIRMED', 'ENROLLED'];

  constructor(
    private service: AdmissionConfirmationService,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.loadConfirmation();
  }

  loadConfirmation() {
    this.loading = true;
    this.service.getMyConfirmation().subscribe({
      next: (res) => {
        this.confirmation = res;
        this.feeAmount = res.feeAmount || 0;
        this.loading = false;
        this.loadDocuments();
      },
      error: () => { this.loading = false; }
    });
  }

  loadDocuments() {
    if (!this.confirmation) return;
    this.service.getDocuments(this.confirmation.id).subscribe({
      next: (docs) => this.documents = docs,
      error: () => this.toast.error('Operation failed. Please try again.')
    });
  }

  formatStatus(status: string): string {
    return status?.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase()) || '';
  }

  getStepIndex(status: string): number {
    const idx = this.statusOrder.indexOf(status);
    return idx >= 0 ? idx : 0;
  }

  isStepActive(i: number): boolean {
    return this.confirmation && this.getStepIndex(this.confirmation.status) === i;
  }

  isStepCompleted(i: number): boolean {
    return this.confirmation && this.getStepIndex(this.confirmation.status) > i;
  }

  addDoc() {
    this.newDocs.push({ documentType: 'OTHER', documentName: '', fileUrl: '' });
  }

  removeDoc(index: number) {
    this.newDocs.splice(index, 1);
  }

  submitDocs() {
    const validDocs = this.newDocs.filter(d => d.documentName);
    if (validDocs.length === 0) {
      this.toast.warning('Please fill in at least one document name');
      return;
    }
    this.submitting = true;
    this.service.submitDocuments(this.confirmation.id, validDocs).subscribe({
      next: () => {
        this.toast.success('Documents submitted successfully');
        this.submitting = false;
        this.loadConfirmation();
      },
      error: (err) => { this.toast.error(err.error?.message || 'Submission failed'); this.submitting = false; }
    });
  }

  payFee() {
    if (this.feeAmount <= 0) {
      this.toast.warning('Please enter a valid amount');
      return;
    }
    this.paying = true;
    this.service.payFee(this.confirmation.id, this.feeAmount, this.feePaymentMethod, 'TXN-' + Date.now()).subscribe({
      next: () => {
        this.toast.success('Fee payment successful');
        this.paying = false;
        this.loadConfirmation();
      },
      error: (err) => { this.toast.error(err.error?.message || 'Payment failed'); this.paying = false; }
    });
  }
}
