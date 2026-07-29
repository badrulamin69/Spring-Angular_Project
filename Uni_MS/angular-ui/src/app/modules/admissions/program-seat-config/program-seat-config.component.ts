import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ProgramSeatConfig, SeatAllocationConfig } from '../../../models/seat-allocation';
import { ProgramSeatConfigService } from '../../../services/program-seat-config.service';
import { SeatAllocationConfigService } from '../../../services/seat-allocation-config.service';
import { FacultyService } from '../../../services/faculty.service';
import { DepartmentService } from '../../../services/department.service';
import { ProgramService } from '../../../services/program.service';

@Component({
  selector: 'app-program-seat-config',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Program Seat Configuration</h2>
        <p class="subtitle">Define seat capacity per program per session</p>
      </div>
      <button class="btn btn-primary" (click)="openModal()">+ Add Program Seats</button>
    </div>

    <div class="filter-bar">
      <select [(ngModel)]="selectedConfigId" (change)="loadSeatConfigs()" class="form-control">
        <option value="">Select Allocation Config</option>
        @for (c of configs; track c.id) {
          <option [value]="c.id">{{ c.session?.name }} - {{ c.academicYear }} ({{ c.status }})</option>
        }
      </select>
    </div>

    @if (selectedConfigId) {
      <div class="summary-row">
        <div class="summary-card"><div class="summary-value">{{ seatSummary.totalSeats || 0 }}</div><div class="summary-label">Total Seats</div></div>
        <div class="summary-card"><div class="summary-value">{{ seatSummary.allocatedSeats || 0 }}</div><div class="summary-label">Allocated</div></div>
        <div class="summary-card"><div class="summary-value">{{ seatSummary.remainingSeats || 0 }}</div><div class="summary-label">Remaining</div></div>
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>Faculty</th><th>Department</th><th>Program</th><th>Shift</th>
              <th>Total</th><th>General</th><th>Quota</th><th>Reserved</th>
              <th>Allocated</th><th>Waiting</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (seat of seatConfigs; track seat.id) {
              <tr>
                <td>{{ seat.faculty?.name }}</td>
                <td>{{ seat.department?.name }}</td>
                <td>{{ seat.program?.name }}</td>
                <td>{{ seat.shift }}</td>
                <td>{{ seat.totalSeats }}</td>
                <td>{{ seat.generalSeats }}</td>
                <td>{{ seat.quotaSeats }}</td>
                <td>{{ seat.reservedSeats }}</td>
                <td>{{ seat.allocatedSeats }}</td>
                <td>{{ seat.waitingSeats }}</td>
                <td>
                  <button class="btn btn-sm btn-secondary" (click)="openModal(seat)">Edit</button>
                  <button class="btn btn-sm btn-danger" (click)="deleteSeat(seat)" [disabled]="(seat.allocatedSeats || 0) > 0">Delete</button>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="11" class="empty-state">No program seats configured</td></tr>
            }
          </tbody>
        </table>
      </div>
    }

    @if (showModal) {
      <div class="modal-overlay" (click)="showModal = false">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingItem ? 'Edit' : 'Add' }} Program Seats</h3>
            <button class="btn-close" (click)="showModal = false">&times;</button>
          </div>
          <form (ngSubmit)="saveSeat()">
            <div class="form-row-2">
              <div class="form-group">
                <label>Faculty *</label>
                <select [(ngModel)]="formData.facultyId" name="facultyId" class="form-control" required (change)="loadDepartments()">
                  <option value="">Select Faculty</option>
                  @for (f of faculties; track f.id) { <option [value]="f.id">{{ f.name }}</option> }
                </select>
              </div>
              <div class="form-group">
                <label>Department *</label>
                <select [(ngModel)]="formData.departmentId" name="departmentId" class="form-control" required (change)="loadPrograms()">
                  <option value="">Select Department</option>
                  @for (d of departments; track d.id) { <option [value]="d.id">{{ d.name }}</option> }
                </select>
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Program *</label>
                <select [(ngModel)]="formData.programId" name="programId" class="form-control" required>
                  <option value="">Select Program</option>
                  @for (p of programs; track p.id) { <option [value]="p.id">{{ p.name }}</option> }
                </select>
              </div>
              <div class="form-group">
                <label>Shift *</label>
                <select [(ngModel)]="formData.shift" name="shift" class="form-control" required>
                  <option value="DAY">Day</option>
                  <option value="EVENING">Evening</option>
                </select>
              </div>
            </div>
            <div class="form-row-2">
              <div class="form-group">
                <label>Total Seats *</label>
                <input type="number" [(ngModel)]="formData.totalSeats" name="totalSeats" class="form-control" required min="1" />
              </div>
              <div class="form-group">
                <label>Quota Seats</label>
                <input type="number" [(ngModel)]="formData.quotaSeats" name="quotaSeats" class="form-control" min="0" />
              </div>
            </div>
            <div class="form-group" style="margin-bottom:16px">
              <label>Reserved Seats</label>
              <input type="number" [(ngModel)]="formData.reservedSeats" name="reservedSeats" class="form-control" min="0" />
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="showModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving">{{ saving ? 'Saving...' : 'Save' }}</button>
            </div>
          </form>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .page-header h2 { margin: 0; font-size: 24px; }
    .subtitle { color: #6b7280; margin: 4px 0 0; }
    .filter-bar { margin-bottom: 20px; }
    .summary-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }
    .summary-card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); text-align: center; }
    .summary-value { font-size: 28px; font-weight: 700; color: #1e40af; }
    .summary-label { font-size: 13px; color: #6b7280; margin-top: 4px; }
    .table-container { overflow-x: auto; background: white; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
    .data-table th { background: #f3f4f6; padding: 12px; text-align: left; font-weight: 600; white-space: nowrap; }
    .data-table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; }
    .data-table tr:hover { background: #f9fafb; }
    .empty-state { text-align: center; padding: 40px; color: #6b7280; }
    .btn-sm { padding: 4px 10px; font-size: 12px; margin-right: 4px; }
    .btn-danger { background: #dc3545; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; border-radius: 12px; width: 90%; max-width: 600px; max-height: 90vh; overflow-y: auto; padding: 24px; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .modal-header h3 { margin: 0; }
    .btn-close { background: none; border: none; font-size: 24px; cursor: pointer; }
    .form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
    .form-group { display: flex; flex-direction: column; margin-bottom: 16px; }
    .form-group label { font-weight: 500; margin-bottom: 6px; font-size: 14px; }
    .form-control { padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; }
    .form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; padding-top: 16px; border-top: 1px solid #e5e7eb; }
    .btn { padding: 8px 16px; border-radius: 6px; border: none; cursor: pointer; font-size: 14px; font-weight: 500; }
    .btn-primary { background: #004080; color: white; }
    .btn-secondary { background: #e5e7eb; color: #374151; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
  `]
})
export class ProgramSeatConfigComponent implements OnInit {
  configs: SeatAllocationConfig[] = [];
  selectedConfigId = '';
  seatConfigs: ProgramSeatConfig[] = [];
  seatSummary: any = {};
  faculties: any[] = [];
  departments: any[] = [];
  programs: any[] = [];
  showModal = false;
  saving = false;
  editingItem: any = null;
  formData: any = this.getEmptyForm();

  constructor(
    private seatConfigService: ProgramSeatConfigService,
    private configService: SeatAllocationConfigService,
    private facultyService: FacultyService,
    private departmentService: DepartmentService,
    private programService: ProgramService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadConfigs();
    this.loadFaculties();
  }

  loadConfigs() {
    this.configService.findAll({ page: 0, size: 100, sortBy: 'id', sortDir: 'desc' }, {}).subscribe({
      next: (data) => { this.configs = data.content || []; }
    });
  }

  loadSeatConfigs() {
    if (!this.selectedConfigId) return;
    this.seatConfigService.findByConfigId(Number(this.selectedConfigId)).subscribe({
      next: (data) => { this.seatConfigs = data; }
    });
    this.seatConfigService.getSummary(Number(this.selectedConfigId)).subscribe({
      next: (data) => { this.seatSummary = data; }
    });
  }

  loadFaculties() {
    this.facultyService.getForDropdown().subscribe({
      next: (faculties) => { this.faculties = faculties; }
    });
  }

  loadDepartments() {
    if (!this.formData.facultyId) return;
    this.departmentService.findByFaculty(this.formData.facultyId).subscribe({
      next: (departments) => { this.departments = departments; }
    });
  }

  loadPrograms() {
    if (!this.formData.departmentId) return;
    this.programService.findByDepartment(this.formData.departmentId).subscribe({
      next: (programs) => { this.programs = programs; }
    });
  }

  getEmptyForm() {
    return { facultyId: '', departmentId: '', programId: '', shift: 'DAY', totalSeats: 0, quotaSeats: 0, reservedSeats: 0 };
  }

  openModal(item?: any) {
    this.editingItem = item ? { ...item } : null;
    this.formData = item ? {
      facultyId: item.facultyId || '', departmentId: item.departmentId || '', programId: item.programId || '',
      shift: item.shift || 'DAY', totalSeats: item.totalSeats || 0, quotaSeats: item.quotaSeats || 0, reservedSeats: item.reservedSeats || 0
    } : this.getEmptyForm();
    this.showModal = true;
  }

  saveSeat() {
    this.saving = true;
    const payload: any = {
      config: { id: Number(this.selectedConfigId) },
      faculty: { id: Number(this.formData.facultyId) },
      department: { id: Number(this.formData.departmentId) },
      program: { id: Number(this.formData.programId) },
      shift: this.formData.shift,
      totalSeats: this.formData.totalSeats,
      quotaSeats: this.formData.quotaSeats,
      reservedSeats: this.formData.reservedSeats
    };
    const req = this.editingItem?.id
      ? this.seatConfigService.update(this.editingItem.id, payload)
      : this.seatConfigService.save(payload);
    req.subscribe({
      next: () => {
        this.saving = false; this.showModal = false;
        this.toastService.success(this.editingItem ? 'Updated' : 'Created');
        this.loadSeatConfigs();
      },
      error: (err) => { this.saving = false; this.toastService.error(err.error?.message || 'Failed'); }
    });
  }

  deleteSeat(item: any) {
    if (confirm('Delete this seat configuration?')) {
      this.seatConfigService.delete(item.id).subscribe({
        next: () => { this.loadSeatConfigs(); this.toastService.success('Deleted'); },
        error: (err) => this.toastService.error(err.error?.message || 'Failed')
      });
    }
  }
}
