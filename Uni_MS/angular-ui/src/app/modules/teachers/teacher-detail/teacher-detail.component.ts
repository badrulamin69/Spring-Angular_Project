import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TeacherService } from '../../../services/teacher.service';
import { Teacher } from '../../../models/teacher';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';

@Component({
  selector: 'app-teacher-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="detail-container">
      @if (teacher) {
        <div class="page-header">
          <div>
            <h2>{{ teacher.firstName }} {{ teacher.lastName }}</h2>
            <p class="page-sub">{{ teacher.teacherCode }} &middot; {{ teacher.designation || 'Teacher' }}</p>
          </div>
          <a routerLink="/teachers/list" class="btn btn-secondary">Back to List</a>
        </div>

        <div class="info-grid">
          <div class="info-card">
            <h3>Personal Information</h3>
            <div class="info-row"><span class="label">Teacher ID</span><span>{{ teacher.teacherCode }}</span></div>
            <div class="info-row"><span class="label">Gender</span><span>{{ teacher.gender || '-' }}</span></div>
            <div class="info-row"><span class="label">Date of Birth</span><span>{{ teacher.dateOfBirth || '-' }}</span></div>
            <div class="info-row"><span class="label">Blood Group</span><span>{{ teacher.bloodGroup || '-' }}</span></div>
            <div class="info-row"><span class="label">Nationality</span><span>{{ teacher.nationality || '-' }}</span></div>
          </div>
          <div class="info-card">
            <h3>Contact</h3>
            <div class="info-row"><span class="label">Email</span><span>{{ teacher.email }}</span></div>
            <div class="info-row"><span class="label">Phone</span><span>{{ teacher.phone || '-' }}</span></div>
            <div class="info-row"><span class="label">Emergency Contact</span><span>{{ teacher.emergencyContact || '-' }}</span></div>
            <div class="info-row"><span class="label">Present Address</span><span>{{ teacher.presentAddress || '-' }}</span></div>
          </div>
          <div class="info-card">
            <h3>Employment</h3>
            <div class="info-row"><span class="label">Designation</span><span>{{ teacher.designation || '-' }}</span></div>
            <div class="info-row"><span class="label">Employment Type</span><span>{{ teacher.employmentType || '-' }}</span></div>
            <div class="info-row"><span class="label">Joining Date</span><span>{{ teacher.joiningDate || '-' }}</span></div>
            <div class="info-row"><span class="label">Status</span><span [class]="'badge badge-' + (teacher.status === 'ACTIVE' ? 'success' : 'danger')">{{ teacher.status || '-' }}</span></div>
          </div>
          <div class="info-card">
            <h3>Academic</h3>
            <div class="info-row"><span class="label">Highest Degree</span><span>{{ teacher.highestDegree || '-' }}</span></div>
            <div class="info-row"><span class="label">University</span><span>{{ teacher.university || '-' }}</span></div>
            <div class="info-row"><span class="label">Specialization</span><span>{{ teacher.specialization || '-' }}</span></div>
            <div class="info-row"><span class="label">Experience</span><span>{{ teacher.experience || '-' }}</span></div>
          </div>
        </div>
      } @else {
        <div class="loading">Loading teacher details...</div>
      }
    </div>
  `,
  styles: [`
    .detail-container { padding: 0.25rem; }
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.75rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 4px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; text-decoration: none; display: inline-flex; align-items: center; }
    .btn-secondary { background: var(--bg-tertiary, #f3f4f6); color: var(--text-primary); border: 1px solid var(--border-color); }
    .info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem; }
    .info-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 16px; padding: 1.25rem; }
    .info-card h3 { margin: 0 0 1rem; font-size: 0.9375rem; font-weight: 600; color: var(--text-primary); }
    .info-row { display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px solid var(--border-color); }
    .info-row:last-child { border-bottom: none; }
    .label { font-size: 0.8125rem; color: var(--text-muted); font-weight: 500; }
    .badge { padding: 2px 8px; border-radius: 10px; font-size: 0.6875rem; font-weight: 600; }
    .badge-success { background: rgba(34,197,94,0.1); color: #28a745; }
    .badge-danger { background: rgba(239,68,68,0.1); color: #dc3545; }
    .loading { text-align: center; padding: 4rem; color: var(--text-muted); }
    @media (max-width: 768px) { .info-grid { grid-template-columns: 1fr; } }
  `]
})
export class TeacherDetailComponent implements OnInit {
  teacher: Teacher | null = null;

  constructor(private route: ActivatedRoute, private teacherService: TeacherService, private toastService: ToastService) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.teacherService.findById(id).subscribe({
        next: (data) => this.teacher = data,
        error: () => this.toastService.error('Operation failed. Please try again.')
      });
    }
  }
}
