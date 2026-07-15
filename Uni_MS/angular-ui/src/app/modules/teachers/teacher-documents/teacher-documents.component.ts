import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeacherService } from '../../../services/teacher.service';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-teacher-documents',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Teacher Documents</h2>
        <p class="page-sub">Manage teacher documents and certificates</p>
      </div>
    </div>
    <div class="panel">
      <div class="panel-body">
        <div class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          <p>Select a teacher to view their documents</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .panel { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 16px; }
    .panel-body { padding: 2rem; }
    .empty-state { text-align: center; padding: 3rem; color: var(--text-muted); display: flex; flex-direction: column; align-items: center; gap: 1rem; }
  `]
})
export class TeacherDocumentsComponent implements OnInit {
  constructor(private teacherService: TeacherService, private toastService: ToastService) {}
  ngOnInit() {}
}
