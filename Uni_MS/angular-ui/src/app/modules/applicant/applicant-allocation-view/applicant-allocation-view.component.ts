import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ProgramSeatAllocation } from '../../../models/seat-allocation';
import { ProgramSeatAllocationService } from '../../../services/program-seat-allocation.service';
import { SeatAllocationConfigService } from '../../../services/seat-allocation-config.service';
import { map } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-applicant-allocation-view',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>My Seat Allocation</h2>
        <p class="subtitle">View your allocated program and seat status</p>
      </div>
    </div>

    <div class="filter-bar">
      <select [(ngModel)]="selectedConfigId" (change)="loadAllocation()" class="form-control" style="max-width:300px">
        <option value="">Select Session</option>
        @for (c of configs; track c.id) {
          <option [value]="c.id">{{ c.session?.name }} - {{ c.academicYear }}</option>
        }
      </select>
    </div>

    @if (allocation) {
      <div class="allocation-card" [class.allocated]="allocation.status === 'ALLOCATED' && !allocation.isWaiting"
           [class.confirmed]="allocation.status === 'CONFIRMED'"
           [class.waiting]="allocation.isWaiting"
           [class.not-allocated]="allocation.status === 'NOT_ALLOCATED'"
           [class.declined]="allocation.status === 'DECLINED'"
           [class.expired]="allocation.status === 'EXPIRED'">
        <div class="allocation-header">
          <h3>{{ allocation.allocationNumber }}</h3>
          <span class="status-badge" [attr.data-status]="allocation.isWaiting ? 'WAITING' : allocation.status">
            {{ allocation.isWaiting ? 'WAITING LIST #' + allocation.waitingRank : allocation.status }}
          </span>
        </div>

        <div class="allocation-details">
          <div class="detail-group">
            <label>Allocated Faculty</label>
            <div class="detail-value">{{ allocation.allocatedFaculty?.name || 'N/A' }}</div>
          </div>
          <div class="detail-group">
            <label>Allocated Department</label>
            <div class="detail-value">{{ allocation.allocatedDepartment?.name || 'N/A' }}</div>
          </div>
          <div class="detail-group">
            <label>Allocated Program</label>
            <div class="detail-value highlight">{{ allocation.allocatedProgram?.name || 'N/A' }}</div>
          </div>
          <div class="detail-group">
            <label>Shift</label>
            <div class="detail-value">{{ allocation.shift || 'N/A' }}</div>
          </div>
          <div class="detail-group">
            <label>Allocation Round</label>
            <div class="detail-value">{{ allocation.allocationRound }}</div>
          </div>
          <div class="detail-group">
            <label>Merit Rank</label>
            <div class="detail-value">{{ allocation.meritRank || 'N/A' }}</div>
          </div>
          <div class="detail-group">
            <label>Choice Number</label>
            <div class="detail-value">{{ allocation.choiceNumber ? '#' + allocation.choiceNumber : 'N/A' }}</div>
          </div>
          <div class="detail-group">
            <label>Deadline</label>
            <div class="detail-value" [class.urgent]="isUrgent()">{{ allocation.deadline | date:'medium' }}</div>
          </div>
          @if (allocation.allocatedAt) {
            <div class="detail-group">
              <label>Allocated At</label>
              <div class="detail-value">{{ allocation.allocatedAt | date:'medium' }}</div>
            </div>
          }
          @if (allocation.acceptedAt) {
            <div class="detail-group">
              <label>Accepted At</label>
              <div class="detail-value">{{ allocation.acceptedAt | date:'medium' }}</div>
            </div>
          }
          @if (allocation.declinedAt) {
            <div class="detail-group">
              <label>Declined At</label>
              <div class="detail-value">{{ allocation.declinedAt | date:'medium' }}</div>
            </div>
          }
        </div>

        @if (allocation.status === 'ALLOCATED' && !allocation.isWaiting) {
          <div class="action-buttons">
            <button class="btn btn-success btn-lg" (click)="acceptSeat()">Accept Seat</button>
            <button class="btn btn-danger btn-lg" (click)="showDeclineModal = true">Decline Seat</button>
          </div>
        }

        @if (allocation.status === 'ALLOCATED' && allocation.isWaiting) {
          <div class="waiting-info">
            <p>You are on the waiting list. Your position: <strong>#{{ allocation.waitingRank }}</strong></p>
            <p>You will be promoted if a seat becomes available before the deadline.</p>
          </div>
        }

        @if (allocation.status === 'CONFIRMED') {
          <div class="confirmed-info">
            <p>You have <strong>accepted</strong> this seat. Please follow the admission instructions.</p>
          </div>
        }

        @if (allocation.status === 'NOT_ALLOCATED') {
          <div class="not-allocated-info">
            <p>No seat could be allocated based on your choices and available capacity.</p>
            <p>You may be allocated in a future round if seats become available.</p>
          </div>
        }
      </div>
    } @else if (selectedConfigId && !loading) {
      <div class="empty-state">
        <p>No allocation found for this session.</p>
      </div>
    }

    @if (showDeclineModal) {
      <div class="modal-overlay" (click)="showDeclineModal = false">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Decline Seat</h3>
            <button class="btn-close" (click)="showDeclineModal = false">&times;</button>
          </div>
          <div class="decline-warning">
            <p>Are you sure you want to decline this seat? This action cannot be undone.</p>
          </div>
          <div class="form-group">
            <label>Reason (optional)</label>
            <input type="text" [(ngModel)]="declineRemarks" class="form-control" placeholder="Reason for declining" />
          </div>
          <div class="form-actions">
            <button class="btn btn-secondary" (click)="showDeclineModal = false">Cancel</button>
            <button class="btn btn-danger" (click)="confirmDecline()" [disabled]="saving">{{ saving ? 'Declining...' : 'Confirm Decline' }}</button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { margin-bottom: 24px; }
    .page-header h2 { margin: 0; font-size: 24px; }
    .subtitle { color: #6b7280; margin: 4px 0 0; }
    .filter-bar { margin-bottom: 24px; }
    .allocation-card { background: white; border-radius: 12px; padding: 32px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border-left: 6px solid #d1d5db; }
    .allocation-card.allocated { border-left-color: #004080; }
    .allocation-card.confirmed { border-left-color: #28a745; }
    .allocation-card.waiting { border-left-color: #e6a817; }
    .allocation-card.not-allocated { border-left-color: #dc3545; }
    .allocation-card.declined { border-left-color: #6b7280; }
    .allocation-card.expired { border-left-color: #9ca3af; }
    .allocation-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .allocation-header h3 { margin: 0; font-size: 20px; }
    .status-badge { padding: 6px 14px; border-radius: 20px; font-size: 13px; font-weight: 600; background: #f3f4f6; color: #374151; }
    .status-badge[data-status="ALLOCATED"] { background: #dbeafe; color: #1e40af; }
    .status-badge[data-status="CONFIRMED"] { background: #dcfce7; color: #166534; }
    .status-badge[data-status="WAITING"] { background: #fef3c7; color: #92400e; }
    .status-badge[data-status="NOT_ALLOCATED"] { background: #fee2e2; color: #991b1b; }
    .status-badge[data-status="DECLINED"] { background: #f3f4f6; color: #6b7280; }
    .status-badge[data-status="EXPIRED"] { background: #f3f4f6; color: #9ca3af; }
    .allocation-details { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 24px; }
    .detail-group label { display: block; font-size: 12px; color: #6b7280; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 4px; }
    .detail-value { font-size: 16px; font-weight: 500; color: #1f2937; }
    .detail-value.highlight { font-size: 20px; color: #004080; font-weight: 700; }
    .detail-value.urgent { color: #dc3545; font-weight: 600; }
    .action-buttons { display: flex; gap: 16px; padding-top: 20px; border-top: 1px solid #e5e7eb; }
    .btn { padding: 10px 24px; border-radius: 8px; border: none; cursor: pointer; font-size: 15px; font-weight: 600; }
    .btn-lg { padding: 14px 32px; font-size: 16px; }
    .btn-success { background: #28a745; color: white; }
    .btn-danger { background: #dc3545; color: white; }
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .waiting-info { background: #fffbeb; border: 1px solid #fde68a; border-radius: 8px; padding: 16px; margin-top: 16px; }
    .waiting-info p { margin: 4px 0; color: #92400e; }
    .confirmed-info { background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 8px; padding: 16px; margin-top: 16px; }
    .confirmed-info p { margin: 4px 0; color: #166534; }
    .not-allocated-info { background: #fef2f2; border: 1px solid #fecaca; border-radius: 8px; padding: 16px; margin-top: 16px; }
    .not-allocated-info p { margin: 4px 0; color: #991b1b; }
    .empty-state { text-align: center; padding: 60px; background: white; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; width: 90%; max-width: 450px; padding: 24px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .modal-header h3 { margin: 0; }
    .btn-close { background: none; border: none; font-size: 24px; cursor: pointer; }
    .decline-warning { background: #fef2f2; border: 1px solid #fecaca; border-radius: 8px; padding: 12px; margin-bottom: 16px; }
    .decline-warning p { margin: 0; color: #991b1b; }
    .form-group { margin-bottom: 16px; }
    .form-group label { display: block; font-weight: 500; margin-bottom: 6px; }
    .form-control { width: 100%; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; box-sizing: border-box; }
    .form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
    @media (max-width: 768px) {
      .allocation-details { grid-template-columns: 1fr; }
    }
  `]
})
export class ApplicantAllocationViewComponent implements OnInit {
  configs: any[] = [];
  selectedConfigId = '';
  allocation: ProgramSeatAllocation | null = null;
  loading = false;
  showDeclineModal = false;
  declineRemarks = '';
  saving = false;

  constructor(
    private allocationService: ProgramSeatAllocationService,
    private seatAllocationConfigService: SeatAllocationConfigService,
    private toastService: ToastService,
    public router: Router
  ) {}

  ngOnInit() {
    this.loadConfigs();
  }

  loadConfigs() {
    this.seatAllocationConfigService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'asc' }, { status: 'ACTIVE' }).pipe(
      map(res => res.content || [])
    ).subscribe({
      next: (configs) => { this.configs = configs; }
    });
  }

  loadAllocation() {
    if (!this.selectedConfigId) { this.allocation = null; return; }
    this.loading = true;
    this.allocationService.getMyAllocation(Number(this.selectedConfigId)).subscribe({
      next: (data) => { this.allocation = data; this.loading = false; },
      error: () => { this.allocation = null; this.loading = false; }
    });
  }

  isUrgent(): boolean {
    if (!this.allocation?.deadline) return false;
    const deadline = new Date(this.allocation.deadline);
    const now = new Date();
    const hoursLeft = (deadline.getTime() - now.getTime()) / (1000 * 60 * 60);
    return hoursLeft > 0 && hoursLeft < 24;
  }

  acceptSeat() {
    if (!this.allocation?.id) return;
    if (confirm('Accept this seat? You will be enrolled in the allocated program.')) {
      this.allocationService.acceptAllocation(this.allocation.id).subscribe({
        next: (data) => { this.allocation = data; this.toastService.success('Seat accepted successfully!'); },
        error: (err) => this.toastService.error(err.error?.message || 'Failed to accept seat')
      });
    }
  }

  confirmDecline() {
    if (!this.allocation?.id) return;
    this.saving = true;
    this.allocationService.declineAllocation(this.allocation.id, this.declineRemarks).subscribe({
      next: (data) => {
        this.allocation = data; this.saving = false; this.showDeclineModal = false; this.declineRemarks = '';
        this.toastService.success('Seat declined');
      },
      error: (err) => { this.saving = false; this.toastService.error(err.error?.message || 'Failed'); }
    });
  }
}
