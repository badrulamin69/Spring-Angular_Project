import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreAdmissionService } from '../../../services/pre-admission.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-pre-admissions',
  standalone: true,
  imports: [CommonModule, DataTableComponent, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Pre-Admission Registrations</h2>
        <p class="page-sub">Manage applicant pre-admission registrations</p>
      </div>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (onEdit)="viewDetail($event)"
      (onDelete)="confirmReject($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>

    @if (showDetail) {
      <div class="modal-overlay" (click)="showDetail = false">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Registration Detail</h3>
            <button class="close-btn" (click)="showDetail = false">&times;</button>
          </div>
          @if (selectedItem) {
            <div class="detail-grid">
              <div class="detail-item"><strong>Reg. No:</strong> {{ selectedItem.registrationNumber }}</div>
              <div class="detail-item"><strong>Name:</strong> {{ selectedItem.firstName }} {{ selectedItem.lastName }}</div>
              <div class="detail-item"><strong>Email:</strong> {{ selectedItem.email }}</div>
              <div class="detail-item"><strong>Phone:</strong> {{ selectedItem.phone }}</div>
              <div class="detail-item"><strong>DOB:</strong> {{ selectedItem.dateOfBirth }}</div>
              <div class="detail-item"><strong>Gender:</strong> {{ selectedItem.gender }}</div>
              <div class="detail-item"><strong>Father:</strong> {{ selectedItem.fatherName }}</div>
              <div class="detail-item"><strong>Mother:</strong> {{ selectedItem.motherName }}</div>
              <div class="detail-item"><strong>SSC GPA:</strong> {{ selectedItem.sscGpa }}</div>
              <div class="detail-item"><strong>HSC GPA:</strong> {{ selectedItem.hscGpa }}</div>
              <div class="detail-item"><strong>Preference 1:</strong> {{ selectedItem.programPreference1 }}</div>
              <div class="detail-item"><strong>Preference 2:</strong> {{ selectedItem.programPreference2 }}</div>
              <div class="detail-item"><strong>Preference 3:</strong> {{ selectedItem.programPreference3 }}</div>
              <div class="detail-item"><strong>Status:</strong> <span class="status-badge" [attr.data-status]="selectedItem.status">{{ selectedItem.status }}</span></div>
            </div>
            <div class="detail-actions">
              @if (selectedItem.status === 'SUBMITTED') {
                <button class="btn btn-success" (click)="approveItem(selectedItem)">Approve & Generate Admit Card</button>
                <button class="btn btn-danger" (click)="confirmReject(selectedItem)">Reject</button>
              }
              @if (selectedItem.status === 'ADMIT_CARD_GENERATED' || selectedItem.status === 'MERIT_PROCESSED' || selectedItem.status === 'ALLOCATED') {
                <button class="btn btn-primary" (click)="downloadAdmitCard(selectedItem)">
                  <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v8M3 6l4 4 4-4M1 10v2h12v-2" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  View Admit Card
                </button>
                <button class="btn btn-outline-primary" (click)="downloadAdmitCardPdf(selectedItem)">
                  <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v8M3 6l4 4 4-4M1 10v2h12v-2" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  Download PDF
                </button>
              }
            </div>
          }
        </div>
      </div>
    }

    <app-confirm-dialog
      [open]="showConfirm"
      [title]="confirmTitle"
      [message]="confirmMessage"
      confirmText="Reject"
      type="danger"
      (confirmed)="executeReject()"
      (cancelled)="showConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: #fff; border-radius: 12px; padding: 24px; max-width: 700px; width: 90%; max-height: 80vh; overflow-y: auto; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
    .modal-header h3 { margin: 0; font-size: 1.25rem; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; }
    .detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .detail-item { padding: 8px; background: #f8f9fa; border-radius: 6px; }
    .detail-item strong { color: #374151; }
    .detail-actions { margin-top: 16px; display: flex; gap: 8px; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; }
    .btn-success { background: #28a745; color: #fff; }
    .btn-danger { background: #dc3545; color: #fff; }
    .btn-outline-primary { background: #fff; color: #004080; border: 1px solid #004080; }
    .btn-outline-primary:hover { background: #eff6ff; }
    .status-badge { padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 600; }
    .status-badge[data-status="SUBMITTED"] { background: #dbeafe; color: #002d5f; }
    .status-badge[data-status="ADMIT_CARD_GENERATED"] { background: #d1fae5; color: #065f46; }
    .status-badge[data-status="REJECTED"] { background: #fee2e2; color: #991b1b; }
    .status-badge[data-status="ALLOCATED"] { background: #fef3c7; color: #92400e; }
  `]
})
export class PreAdmissionsComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  searchTerm = '';
  columns: TableColumn[] = [
    { key: 'registrationNumber', label: 'Reg. No', sortable: true },
    { key: 'firstName', label: 'First Name', sortable: true },
    { key: 'lastName', label: 'Last Name', sortable: true },
    { key: 'email', label: 'Email', sortable: true },
    { key: 'phone', label: 'Phone' },
    { key: 'programPreference1', label: 'Preference 1' },
    { key: 'status', label: 'Status', type: 'text' }
  ];
  showDetail = false;
  selectedItem: any = null;
  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  rejectTarget: any = null;

  constructor(private service: PreAdmissionService, private toastService: ToastService) {}

  ngOnInit() { this.loadData(); }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params, this.searchTerm).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load registrations'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.searchTerm = term; this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  viewDetail(item: any) { this.selectedItem = item; this.showDetail = true; }

  approveItem(item: any) {
    this.service.approve(item.id).subscribe({
      next: () => { this.showDetail = false; this.loadData(); this.toastService.success('Registration approved, admit card generated'); },
      error: () => this.toastService.error('Failed to approve')
    });
  }

  confirmReject(item: any) {
    this.rejectTarget = item;
    this.confirmTitle = 'Reject Registration';
    this.confirmMessage = `Are you sure you want to reject "${item.firstName} ${item.lastName}"?`;
    this.showConfirm = true;
  }

  executeReject() {
    this.showConfirm = false;
    if (this.rejectTarget) {
      this.service.reject(this.rejectTarget.id, 'Rejected by officer').subscribe({
        next: () => { this.loadData(); this.toastService.success('Registration rejected'); },
        error: () => this.toastService.error('Failed to reject')
      });
    }
  }

  downloadAdmitCard(item: any) {
    this.service.getAdmitCard(item.id).subscribe({
      next: (html) => {
        const blob = new Blob([html], { type: 'text/html' });
        const url = URL.createObjectURL(blob);
        const w = window.open(url, '_blank');
        if (w) {
          w.onload = () => { w.print(); };
        }
      },
      error: () => this.toastService.error('Failed to download admit card')
    });
  }

  downloadAdmitCardPdf(item: any) {
    this.service.getAdmitCardPdf(item.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `admit-card-${item.registrationNumber}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
        this.toastService.success('PDF downloaded successfully');
      },
      error: () => this.toastService.error('Failed to download PDF')
    });
  }
}
