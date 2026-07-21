import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdmitCardService } from '../../../services/admit-card.service';
import { AdmissionTestService } from '../../../services/admission-test.service';
import { DataTableComponent, TableColumn } from '../../../shared/data-table/data-table.component';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';
import { ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-admit-cards',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent],
  template: `
    <div class="page-header">
      <div>
        <h2>Admit Cards</h2>
        <p class="page-sub">Generate and manage admit cards</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" (click)="generateCards()" [disabled]="!selectedTestId || generating">
          {{ generating ? 'Generating...' : 'Generate Admit Cards' }}
        </button>
      </div>
    </div>
    <div class="filter-bar">
      <label>Filter by Test:</label>
      <select [(ngModel)]="selectedTestId" (change)="loadData()">
        <option [ngValue]="null">All Tests</option>
        @for (t of tests; track t.id) { <option [ngValue]="t.id">{{ t.name }}</option> }
      </select>
    </div>
    <app-data-table [columns]="columns" [data]="pagedData?.content || []" [pagedData]="pagedData" [loading]="loading" [params]="params" (pageChange)="onPageChange($event)" (refresh)="loadData()" (search)="onSearch($event)"></app-data-table>
    @if (selectedCard) {
      <div class="pdf-actions-bar">
        <span>Selected: {{ selectedCard.admitCardNumber }}</span>
        <button class="btn btn-primary btn-sm" (click)="downloadPdf(selectedCard)">Download PDF</button>
        <button class="btn btn-secondary btn-sm" (click)="selectedCard = null">Close</button>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: #1e293b; }
    .page-sub { margin: 0.25rem 0 0; color: #64748b; font-size: 0.875rem; }
    .header-actions { display: flex; gap: 0.5rem; }
    .btn { padding: 0.5rem 1rem; border-radius: 6px; border: none; cursor: pointer; font-size: 0.875rem; font-weight: 500; }
    .btn-primary { background: #4F46E5; color: white; }
    .btn-secondary { background: #e2e8f0; color: #475569; }
    .btn-outline { background: white; color: #475569; border: 1px solid #d1d5db; }
    .btn-sm { padding: 0.375rem 0.75rem; font-size: 0.8125rem; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .filter-bar { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1rem; padding: 0.75rem 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; }
    .filter-bar label { font-weight: 500; color: #475569; font-size: 0.875rem; }
    .filter-bar select { padding: 0.375rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.875rem; background: white; }
    .pdf-actions-bar { display: flex; gap: 0.5rem; align-items: center; margin-top: 1rem; padding: 0.75rem 1rem; background: #f0f9ff; border-radius: 8px; border: 1px solid #bae6fd; }
  `]
})
export class AdmitCardsComponent implements OnInit {
  pagedData: PagedResponse<any> | null = null;
  loading = false;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  selectedTestId: number | null = null;
  tests: any[] = [];
  generating = false;
  selectedCard: any = null;

  columns: TableColumn[] = [
    { key: 'id', label: 'ID' },
    { key: 'admitCardNumber', label: 'Card No', sortable: true },
    { key: 'rollNumber', label: 'Roll No', sortable: true },
    { key: 'seatNumber', label: 'Seat' },
    { key: 'centerName', label: 'Center' },
    { key: 'buildingName', label: 'Building' },
    { key: 'roomName', label: 'Room' },
    { key: 'status', label: 'Status' },
    { key: 'issuedAt', label: 'Issued At', type: 'text' }
  ];

  constructor(private admitService: AdmitCardService, private testService: AdmissionTestService, private toast: ToastService) {}

  ngOnInit() { this.loadTests(); this.loadData(); }

  loadTests() {
    this.testService.getForDropdown().subscribe({
      next: (tests) => { this.tests = tests; },
      error: () => { this.tests = []; }
    });
  }

  loadData() {
    this.loading = true;
    this.admitService.findAll(this.params, '', this.selectedTestId || undefined).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; this.toast.error('Failed to load admit cards'); }
    });
  }

  onPageChange(p: PageParams) { this.params = p; this.loadData(); }
  onSearch(term: string) { this.params = { ...DEFAULT_PAGE_PARAMS }; this.loadData(); }

  downloadPdf(item: any) {
    const url = this.admitService.getPdfUrl(item.id);
    window.open(url, '_blank');
  }

  generateCards() {
    if (!this.selectedTestId) return;
    this.generating = true;
    this.admitService.generate(this.selectedTestId).subscribe({
      next: (res) => { this.generating = false; this.toast.success('Admit cards generated'); this.loadData(); },
      error: (err) => { this.generating = false; this.toast.error(err.error?.message || 'Failed to generate admit cards'); }
    });
  }
}
