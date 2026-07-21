import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../models/paged-response';

export interface TableColumn {
  key: string;
  label: string;
  sortable?: boolean;
  type?: 'text' | 'number' | 'select' | 'textarea' | 'date' | 'checkbox' | 'email' | 'password';
  options?: { label: string; value: any }[];
  required?: boolean;
  placeholder?: string;
  hidden?: boolean;
}

export interface RowAction {
  label: string;
  icon?: string;
  class?: string;
  title?: string;
  condition?: (item: any) => boolean;
  onClick: (item: any) => void;
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="table-wrapper">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <div class="search-box">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><circle cx="6.5" cy="6.5" r="5.5" stroke="currentColor" stroke-width="1.5"/><path d="M10.5 10.5L14.5 14.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
            <input type="text" placeholder="Search..." [(ngModel)]="searchTerm" (input)="onSearch()">
          </div>
          @if (selectedIds.size > 0) {
            <span class="selected-count">{{ selectedIds.size }} selected</span>
            <button class="btn btn-sm btn-danger" (click)="onBulkDelete()">Bulk Delete</button>
          }
        </div>
        <div class="toolbar-right">
          <button class="btn btn-sm btn-outline" (click)="exportCSV()">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v8M3 6l4 4 4-4M1 10v2h12v-2" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            Export CSV
          </button>
          <button class="btn btn-sm btn-outline" (click)="refresh.emit()">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M1.5 7a5.5 5.5 0 019.37-3.9M12.5 7a5.5 5.5 0 01-9.37 3.9" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/><path d="M11 1v2.5h-2.5M3 13v-2.5h2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            Refresh
          </button>
        </div>
      </div>

      @if (loading) {
        <div class="loading-state">
          <div class="spinner"></div>
          <span>Loading...</span>
        </div>
      } @else {
        <div class="table-scroll">
          <table>
            <thead>
              <tr>
                <th class="col-check"><input type="checkbox" [checked]="isAllSelected()" (change)="toggleAll()"></th>
                @for (col of visibleColumns; track col.key) {
                  <th [class.sortable]="col.sortable" (click)="col.sortable && onSort(col.key)">
                    {{ col.label }}
                    @if (col.sortable && params.sortBy === col.key) {
                      <span class="sort-icon">{{ params.sortDir === 'asc' ? 'Γû▓' : 'Γû╝' }}</span>
                    }
                  </th>
                }
                <th class="col-actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (item of data; track item.id) {
                <tr [class.selected]="selectedIds.has(item.id)">
                  <td class="col-check"><input type="checkbox" [checked]="selectedIds.has(item.id)" (change)="toggleRow(item.id)"></td>
                  @for (col of visibleColumns; track col.key) {
                    <td>
                      @if (col.type === 'checkbox') {
                        <span class="badge" [class.badge-success]="item[col.key]">{{ item[col.key] ? 'Yes' : 'No' }}</span>
                      } @else {
                        {{ item[col.key] }}
                      }
                    </td>
                  }
                  <td class="col-actions">
                    @if (rowActions.length > 0) {
                      @for (action of rowActions; track action.label) {
                        @if (!action.condition || action.condition(item)) {
                          <button class="btn-icon" [ngClass]="action.class || ''" [title]="action.title || action.label" (click)="action.onClick(item)">
                            {{ action.icon || '' }}
                          </button>
                        }
                      }
                    }
                    @if (showDefaultActions) {
                      <button class="btn-icon" (click)="onEdit.emit(item)" title="Edit">
                        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M8.5 2.5l3 3M1 13l.7-2.6L10 1.7l3 3L4.7 13.3 1 13z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                      </button>
                      <button class="btn-icon btn-icon-danger" (click)="onDelete.emit(item)" title="Delete">
                        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M2 4h10M5 4V2.5A.5.5 0 015.5 2h3a.5.5 0 01.5.5V4M11 4v7.5a1.5 1.5 0 01-1.5 1.5h-5A1.5 1.5 0 013 11.5V4" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                      </button>
                    }
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td [attr.colspan]="visibleColumns.length + 2" class="empty-state">
                    <div class="empty-icon">≡ƒôä</div>
                    <div>No records found</div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        @if (pagedData && pagedData.totalElements > 0) {
          <div class="pagination">
            <div class="pagination-info">
              Showing {{ pagedData.page * pagedData.size + 1 }}ΓÇô{{ min((pagedData.page + 1) * pagedData.size, pagedData.totalElements) }} of {{ pagedData.totalElements }}
            </div>
            <div class="pagination-controls">
              <button [disabled]="pagedData.first" (click)="goToPage(0)" title="First">
                <svg width="12" height="12" viewBox="0 0 12 12"><path d="M2.5 2L6.5 6L2.5 10M6.5 2L10.5 6L6.5 10" stroke="currentColor" stroke-width="1.2" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </button>
              <button [disabled]="pagedData.first" (click)="goToPage(pagedData.page - 1)" title="Previous">
                <svg width="12" height="12" viewBox="0 0 12 12"><path d="M8 2L4 6L8 10" stroke="currentColor" stroke-width="1.2" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </button>
              @for (pageNum of getPageNumbers(); track pageNum) {
                <button [class.active]="pageNum === pagedData.page" (click)="goToPage(pageNum)">{{ pageNum + 1 }}</button>
              }
              <button [disabled]="pagedData.last" (click)="goToPage(pagedData.page + 1)" title="Next">
                <svg width="12" height="12" viewBox="0 0 12 12"><path d="M4 2L8 6L4 10" stroke="currentColor" stroke-width="1.2" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </button>
              <button [disabled]="pagedData.last" (click)="goToPage(pagedData.totalPages - 1)" title="Last">
                <svg width="12" height="12" viewBox="0 0 12 12"><path d="M5.5 2L1.5 6L5.5 10M9.5 2L5.5 6L9.5 10" stroke="currentColor" stroke-width="1.2" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </button>
              <select [value]="params.size" (change)="onSizeChange($event)">
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
                <option value="100">100</option>
              </select>
            </div>
          </div>
        }
      }
    </div>
  `,
  styles: [`
    :host { display: block; width: 100%; max-width: 100%; min-width: 0; }
    .table-wrapper { background: var(--bg-secondary); border-radius: 12px; border: 1px solid var(--border-color); overflow: hidden; width: 100%; max-width: 100%; }
    .table-toolbar { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid var(--border-color); gap: 12px; flex-wrap: wrap; }
    .toolbar-left { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; flex: 1; min-width: 0; }
    .toolbar-right { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; flex-shrink: 0; }
    .search-box { display: flex; align-items: center; gap: 8px; background: var(--bg-primary); border: 1px solid var(--border-color); border-radius: 8px; padding: 6px 12px; min-width: 0; flex: 1; max-width: 320px; }
    .search-box input { border: none; background: transparent; color: var(--text-primary); font-size: 0.875rem; outline: none; width: 100%; min-width: 0; }
    .search-box input::placeholder { color: var(--text-muted); }
    .search-box svg { color: var(--text-muted); flex-shrink: 0; }
    .selected-count { font-size: 0.8125rem; color: var(--brand-color); font-weight: 500; }
    .btn { padding: 6px 12px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8125rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; white-space: nowrap; }
    .btn-sm { padding: 5px 10px; font-size: 0.8125rem; }
    .btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-secondary); }
    .btn-outline:hover { background: var(--bg-hover); color: var(--text-primary); }
    .btn-danger { background: #dc3545; color: #fff; }
    .btn-danger:hover { background: #bd2130; }
    .table-scroll { overflow-x: auto; -webkit-overflow-scrolling: touch; }
    .table-scroll::-webkit-scrollbar { height: 6px; }
    .table-scroll::-webkit-scrollbar-thumb { background: var(--border-color); border-radius: 3px; }
    table { width: 100%; border-collapse: collapse; min-width: 0; }
    th, td { padding: 10px 14px; text-align: left; border-bottom: 1px solid var(--border-color); color: var(--text-primary); }
    th { background: var(--bg-tertiary); font-weight: 600; color: var(--text-secondary); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; position: sticky; top: 0; z-index: 1; white-space: nowrap; }
    th.sortable { cursor: pointer; user-select: none; }
    th.sortable:hover { color: var(--text-primary); }
    .col-check { width: 40px; text-align: center; }
    .col-actions { min-width: 80px; text-align: center; }
    .sort-icon { font-size: 0.65rem; margin-left: 4px; }
    tr.selected { background: rgba(99, 102, 241, 0.08); }
    tr:hover { background: var(--bg-hover); }
    .col-actions { text-align: center; white-space: nowrap; }
    .btn-icon { background: none; border: none; cursor: pointer; padding: 4px 6px; border-radius: 4px; color: var(--text-muted); transition: all 0.15s; display: inline-flex; }
    .btn-icon:hover { background: var(--bg-hover); color: var(--text-primary); }
    .btn-icon-danger:hover { background: #fef2f2; color: #dc3545; }
    .empty-state { text-align: center; padding: 3rem 1rem !important; color: var(--text-muted); }
    .empty-icon { font-size: 2rem; margin-bottom: 8px; }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 3rem; gap: 12px; color: var(--text-muted); }
    .spinner { width: 24px; height: 24px; border: 3px solid var(--border-color); border-top-color: var(--brand-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
    .badge { padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #dcfce7; color: #166534; }
    .pagination { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px; border-top: 1px solid var(--border-color); flex-wrap: wrap; gap: 8px; }
    .pagination-info { font-size: 0.8125rem; color: var(--text-muted); }
    .pagination-controls { display: flex; gap: 3px; align-items: center; flex-wrap: wrap; }
    .pagination-controls button { padding: 4px 8px; border: 1px solid var(--border-color); background: var(--bg-secondary); color: var(--text-primary); border-radius: 4px; cursor: pointer; font-size: 0.8125rem; display: inline-flex; align-items: center; justify-content: center; min-width: 28px; transition: all 0.15s; }
    .pagination-controls button:hover:not(:disabled) { background: var(--bg-hover); }
    .pagination-controls button.active { background: var(--brand-color); color: #fff; border-color: var(--brand-color); }
    .pagination-controls button:disabled { opacity: 0.4; cursor: not-allowed; }
    .pagination-controls select { padding: 4px 8px; border: 1px solid var(--border-color); background: var(--bg-secondary); color: var(--text-primary); border-radius: 4px; font-size: 0.8125rem; }
    @keyframes spin { to { transform: rotate(360deg); } }

    @media (max-width: 768px) {
      .table-toolbar {
        flex-direction: column;
        align-items: stretch;
        gap: 8px;
      }
      .toolbar-left {
        flex-wrap: wrap;
      }
      .toolbar-right {
        justify-content: flex-start;
      }
      .search-box {
        max-width: 100%;
        flex: 1 1 100%;
      }
      .pagination {
        flex-direction: column;
        gap: 8px;
        align-items: center;
      }
      .pagination-controls {
        flex-wrap: wrap;
        justify-content: center;
      }
      th, td {
        padding: 8px 10px;
        font-size: 0.8125rem;
      }
    }

    @media (max-width: 480px) {
      .toolbar-right {
        flex-wrap: wrap;
      }
      .btn-sm {
        padding: 4px 8px;
        font-size: 0.75rem;
      }
      .pagination-controls button {
        min-width: 24px;
        padding: 3px 6px;
        font-size: 0.75rem;
      }
    }
  `]
})
export class DataTableComponent implements OnChanges {
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() pagedData: PagedResponse<any> | null = null;
  @Input() loading = false;
  @Input() params: PageParams = { ...DEFAULT_PAGE_PARAMS };
  @Input() rowActions: RowAction[] = [];
  @Input() showDefaultActions = true;

  @Output() pageChange = new EventEmitter<PageParams>();
  @Output() onEdit = new EventEmitter<any>();
  @Output() onDelete = new EventEmitter<any>();
  @Output() bulkDelete = new EventEmitter<any[]>();
  @Output() refresh = new EventEmitter<void>();
  @Output() search = new EventEmitter<string>();

  selectedIds = new Set<number>();
  searchTerm = '';
  min = Math.min;

  visibleColumns: TableColumn[] = [];

  ngOnChanges(changes: SimpleChanges) {
    if (changes['columns']) {
      this.visibleColumns = this.columns.filter(c => !c.hidden);
    }
  }

  onSearch() {
    this.search.emit(this.searchTerm);
  }

  toggleRow(id: number) {
    if (this.selectedIds.has(id)) {
      this.selectedIds.delete(id);
    } else {
      this.selectedIds.add(id);
    }
  }

  toggleAll() {
    if (this.isAllSelected()) {
      this.selectedIds.clear();
    } else {
      this.data.forEach(item => this.selectedIds.add(item.id));
    }
  }

  isAllSelected(): boolean {
    return this.data.length > 0 && this.data.every(item => this.selectedIds.has(item.id));
  }

  onBulkDelete() {
    const items = this.data.filter(item => this.selectedIds.has(item.id));
    this.bulkDelete.emit(items);
    this.selectedIds.clear();
  }

  exportCSV() {
    if (!this.data || this.data.length === 0) return;
    const headers = this.visibleColumns.map(c => c.label);
    const rows = this.data.map(item => this.visibleColumns.map(c => {
      const val = item[c.key];
      return typeof val === 'string' && val.includes(',') ? `"${val}"` : (val ?? '');
    }));
    const csv = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'export.csv';
    a.click();
    URL.revokeObjectURL(url);
  }

  onSort(column: string) {
    if (this.params.sortBy === column) {
      this.params.sortDir = this.params.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.params.sortBy = column;
      this.params.sortDir = 'asc';
    }
    this.pageChange.emit({ ...this.params });
  }

  goToPage(page: number) {
    this.pageChange.emit({ ...this.params, page });
  }

  onSizeChange(event: Event) {
    const size = parseInt((event.target as HTMLSelectElement).value, 10);
    this.pageChange.emit({ ...this.params, size, page: 0 });
  }

  getPageNumbers(): number[] {
    if (!this.pagedData) return [];
    const pages: number[] = [];
    const start = Math.max(0, this.pagedData.page - 2);
    const end = Math.min(this.pagedData.totalPages, start + 5);
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }
}
