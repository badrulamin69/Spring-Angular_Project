import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PreAdmissionService } from '../../../services/pre-admission.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-merit-processing',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Merit Processing</h2>
        <p class="page-sub">Process merit scores and generate rankings</p>
      </div>
      <button class="btn btn-primary" (click)="processMerit()" [disabled]="processing">
        {{ processing ? 'Processing...' : 'Process Merit' }}
      </button>
    </div>

    <div class="info-card">
      <p>Click "Process Merit" to automatically calculate weighted scores, rank applicants, and allocate departments based on preferences and merit.</p>
    </div>

    <app-data-table
      [columns]="columns"
      [data]="pagedData?.content || []"
      [pagedData]="pagedData"
      [loading]="loading"
      [params]="params"
      (pageChange)="onPageChange($event)"
      (refresh)="loadData()"
      (search)="onSearch($event)"
    ></app-data-table>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; }
    .btn-primary { background: var(--brand-color); color: #fff; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .info-card { background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 8px; padding: 12px 16px; margin-bottom: 16px; }
    .info-card p { margin: 0; color: #1e40af; font-size: 0.875rem; }
  `]
})
export class MeritProcessingComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;
  pagedData: PagedResponse<any> | null = null;
  loading = true;
  processing = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  columns: TableColumn[] = [
    { key: 'registrationNumber', label: 'Reg. No', sortable: true },
    { key: 'firstName', label: 'First Name', sortable: true },
    { key: 'lastName', label: 'Last Name', sortable: true },
    { key: 'programPreference1', label: 'Preference 1' },
    { key: 'sscGpa', label: 'SSC GPA', sortable: true },
    { key: 'hscGpa', label: 'HSC GPA', sortable: true },
    { key: 'status', label: 'Status' }
  ];

  constructor(private service: PreAdmissionService, private toastService: ToastService) {}

  ngOnInit() { this.loadData(); }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load'); }
    });
  }

  onPageChange(params: PageParams) { this.params = params; this.loadData(); }
  onSearch(term: string) { this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  processMerit() {
    this.processing = true;
    this.service.processMerit().subscribe({
      next: (res) => {
        this.processing = false;
        this.loadData();
        this.toastService.success(res.message || 'Merit processing completed');
      },
      error: (err) => {
        this.processing = false;
        this.toastService.error(err.error?.message || 'Merit processing failed');
      }
    });
  }
}
