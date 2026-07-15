import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuditLogService } from '../../../services/audit-log.service';
import { AuditLog } from '../../../models/audit-log';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../../../models/paged-response';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit-logs.component.html'
})
export class AuditLogsComponent implements OnInit {
  pagedData: PagedResponse<AuditLog> | null = null;
  loading = true;
  params: PageParams = { ...DEFAULT_PAGE_PARAMS, sortBy: 'id', sortDir: 'desc' };
  selectedLog: AuditLog | null = null;
  Math = Math;

  constructor(private service: AuditLogService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.service.findAll(this.params).subscribe({
      next: (data) => { this.pagedData = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  onPageChange(params: PageParams) {
    this.params = params;
    this.loadData();
  }

  viewDetails(log: AuditLog) {
    this.selectedLog = log;
  }

  closeDetails() {
    this.selectedLog = null;
  }

  getUserName(log: AuditLog): string {
    if (!log.user) return '-';
    return log.user.username || log.user.email || '-';
  }
}
