import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClassRoutineService } from '../../../services/class-routine.service';
import { Classroom, Building } from '../../../models/class-routine';
import { ToastComponent, ToastService } from '../../../shared/toast/toast.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-classroom',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ConfirmDialogComponent],
  template: `
    <app-toast></app-toast>
    <div class="page-header">
      <div>
        <h2>Classroom Management</h2>
        <p class="page-sub">Manage classrooms and rooms across buildings</p>
      </div>
      <button class="btn btn-primary" (click)="openForm()">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Add Classroom
      </button>
    </div>

    <div class="card">
      <div class="table-header">
        <div class="search-box">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input type="text" [(ngModel)]="searchTerm" placeholder="Search classrooms..." (input)="filterData()">
        </div>
        <select [(ngModel)]="filterBuilding" class="form-control filter-select" (change)="filterData()">
          <option value="">All Buildings</option>
          @for (b of buildings; track b.id) {
            <option [ngValue]="b.id">{{ b.name }}</option>
          }
        </select>
        <select [(ngModel)]="filterType" class="form-control filter-select" (change)="filterData()">
          <option value="">All Types</option>
          <option value="LECTURE_HALL">Lecture Hall</option>
          <option value="CLASSROOM">Classroom</option>
          <option value="LAB">Lab</option>
          <option value="SEMINAR">Seminar</option>
          <option value="AUDITORIUM">Auditorium</option>
        </select>
      </div>
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Building</th>
              <th>Room #</th>
              <th>Floor</th>
              <th>Capacity</th>
              <th>Type</th>
              <th>Lab</th>
              <th>Smart</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (room of filteredRooms; track room.id) {
              <tr>
                <td>{{ room.id }}</td>
                <td>{{ room.buildingName }}</td>
                <td>{{ room.roomNumber }}</td>
                <td>{{ room.floor }}</td>
                <td>{{ room.capacity }}</td>
                <td><span class="badge badge-info">{{ room.roomType }}</span></td>
                <td>
                  @if (room.isLab) {
                    <span class="badge badge-success">Yes</span>
                  } @else {
                    <span class="badge badge-secondary">No</span>
                  }
                </td>
                <td>
                  @if (room.isSmartClassroom) {
                    <span class="badge badge-success">Yes</span>
                  } @else {
                    <span class="badge badge-secondary">No</span>
                  }
                </td>
                <td>
                  @if (room.isActive) {
                    <span class="badge badge-success">Active</span>
                  } @else {
                    <span class="badge badge-secondary">Inactive</span>
                  }
                </td>
                <td>
                  <div class="actions">
                    <button class="btn-icon" (click)="openForm(room)" title="Edit">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                    </button>
                    <button class="btn-icon btn-danger" (click)="confirmDelete(room)" title="Delete">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="10" class="text-center text-muted">No classrooms found</td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>

    @if (showForm) {
      <div class="modal-overlay" (click)="closeForm()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingItem ? 'Edit' : 'New' }} Classroom</h3>
            <button class="btn-close" (click)="closeForm()">&times;</button>
          </div>
          <form (ngSubmit)="save()">
            <div class="form-grid">
              <div class="form-group">
                <label>Building *</label>
                <select [(ngModel)]="form.buildingId" name="buildingId" required class="form-control">
                  <option value="">Select Building</option>
                  @for (b of buildings; track b.id) {
                    <option [ngValue]="b.id">{{ b.name }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label>Room Number *</label>
                <input type="text" [(ngModel)]="form.roomNumber" name="roomNumber" required class="form-control" placeholder="e.g. 101">
              </div>
              <div class="form-group">
                <label>Floor *</label>
                <input type="number" [(ngModel)]="form.floor" name="floor" required class="form-control">
              </div>
              <div class="form-group">
                <label>Capacity *</label>
                <input type="number" [(ngModel)]="form.capacity" name="capacity" required class="form-control">
              </div>
              <div class="form-group">
                <label>Room Type *</label>
                <select [(ngModel)]="form.roomType" name="roomType" required class="form-control">
                  <option value="">Select Type</option>
                  <option value="LECTURE_HALL">Lecture Hall</option>
                  <option value="CLASSROOM">Classroom</option>
                  <option value="LAB">Lab</option>
                  <option value="SEMINAR">Seminar</option>
                  <option value="AUDITORIUM">Auditorium</option>
                </select>
              </div>
              <div class="form-group">
                <label>Equipment</label>
                <input type="text" [(ngModel)]="form.equipment" name="equipment" class="form-control" placeholder="e.g. Projector, Whiteboard">
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.isLab" name="isLab"> Is Lab</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.isSmartClassroom" name="isSmartClassroom"> Smart Classroom</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.hasProjector" name="hasProjector"> Has Projector</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.hasWhiteboard" name="hasWhiteboard"> Has Whiteboard</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.hasWifi" name="hasWifi"> Has WiFi</label>
              </div>
              <div class="form-group checkbox-group">
                <label><input type="checkbox" [(ngModel)]="form.isActive" name="isActive"> Active</label>
              </div>
              <div class="form-group full-width">
                <label>Remarks</label>
                <textarea [(ngModel)]="form.remarks" name="remarks" class="form-control" rows="2"></textarea>
              </div>
            </div>
            @if (formError) {
              <div class="error-message">{{ formError }}</div>
            }
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeForm()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="saving">
                {{ saving ? 'Saving...' : 'Save' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    }

    <app-confirm-dialog
      [open]="showForm === false && showConfirm"
      [title]="confirmTitle"
      [message]="confirmMessage"
      confirmText="Delete"
      type="danger"
      (confirmed)="executeDelete()"
      (cancelled)="showConfirm = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: #e2e8f0; font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: #94a3b8; }
    .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; font-size: 0.875rem; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn-primary { background: #3b82f6; color: #fff; }
    .btn-primary:hover { filter: brightness(0.9); }
    .btn-secondary { background: #334155; color: #e2e8f0; }
    .card { background: #1e293b; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; }
    .table-header { display: flex; gap: 12px; padding: 16px 20px; border-bottom: 1px solid #334155; align-items: center; }
    .search-box { display: flex; align-items: center; gap: 8px; background: #0f172a; border: 1px solid #334155; border-radius: 8px; padding: 8px 12px; flex: 1; }
    .search-box input { border: none; background: transparent; color: #e2e8f0; font-size: 0.875rem; outline: none; width: 100%; }
    .search-box svg { color: #64748b; flex-shrink: 0; }
    .filter-select { padding: 8px 12px; border: 1px solid #334155; border-radius: 8px; background: #0f172a; color: #e2e8f0; font-size: 0.875rem; min-width: 150px; }
    .table-responsive { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #334155; font-size: 0.875rem; }
    .data-table th { background: #0f172a; font-weight: 600; color: #94a3b8; }
    .data-table tr:hover { background: rgba(59,130,246,0.05); }
    .actions { display: flex; gap: 4px; }
    .btn-icon { width: 32px; height: 32px; border: none; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; background: transparent; color: #94a3b8; transition: all 0.15s; }
    .btn-icon:hover { background: #334155; color: #3b82f6; }
    .btn-danger:hover { color: #ef4444; }
    .badge { padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
    .badge-success { background: #064e3b; color: #6ee7b7; }
    .badge-secondary { background: #334155; color: #94a3b8; }
    .badge-info { background: #1e3a5f; color: #93c5fd; }
    .text-center { text-align: center; }
    .text-muted { color: #94a3b8; }
    .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: #1e293b; border-radius: 12px; width: 90%; max-width: 650px; max-height: 90vh; overflow-y: auto; border: 1px solid #334155; }
    .modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #334155; }
    .modal-header h3 { margin: 0; font-size: 1.125rem; color: #e2e8f0; }
    .btn-close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #94a3b8; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; padding: 20px; }
    .form-group { display: flex; flex-direction: column; gap: 4px; }
    .form-group.full-width { grid-column: 1 / -1; }
    .form-group label { font-size: 0.875rem; font-weight: 500; color: #94a3b8; }
    .form-control { padding: 8px 12px; border: 1px solid #334155; border-radius: 6px; font-size: 0.875rem; background: #0f172a; color: #e2e8f0; }
    .form-control:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59,130,246,0.1); }
    .checkbox-group { justify-content: center; }
    .checkbox-group label { flex-direction: row; align-items: center; gap: 8px; cursor: pointer; }
    .error-message { padding: 8px 12px; background: rgba(239,68,68,0.1); color: #fca5a5; border-radius: 6px; margin: 0 20px; font-size: 0.875rem; border: 1px solid rgba(239,68,68,0.2); }
    .modal-footer { display: flex; justify-content: flex-end; gap: 8px; padding: 16px 20px; border-top: 1px solid #334155; }
  `]
})
export class ClassroomComponent implements OnInit {
  rooms: Classroom[] = [];
  filteredRooms: Classroom[] = [];
  buildings: Building[] = [];
  loading = true;
  saving = false;
  showForm = false;
  editingItem: Classroom | null = null;
  formError = '';
  showConfirm = false;
  confirmTitle = '';
  confirmMessage = '';
  deleteTarget: Classroom | null = null;
  searchTerm = '';
  filterBuilding = '';
  filterType = '';

  form: Partial<Classroom> = {
    buildingId: 0, roomNumber: '', floor: 0, capacity: 40,
    roomType: '', isLab: false, isSmartClassroom: false,
    hasProjector: true, hasWhiteboard: true, hasWifi: true,
    equipment: '', isAvailable: true, isActive: true, remarks: ''
  };

  constructor(
    private service: ClassRoutineService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadBuildings();
    this.loadData();
  }

  loadBuildings() {
    this.service.getBuildings().subscribe({
      next: (data) => this.buildings = data,
      error: () => this.toastService.error('Operation failed. Please try again.')
    });
  }

  loadData() {
    this.loading = true;
    this.service.getClassrooms().subscribe({
      next: (data) => { this.rooms = data; this.filterData(); this.loading = false; },
      error: () => { this.loading = false; this.toastService.error('Failed to load classrooms'); }
    });
  }

  filterData() {
    this.filteredRooms = this.rooms.filter(r => {
      const matchSearch = !this.searchTerm || r.roomNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) || (r.buildingName || '').toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchBuilding = !this.filterBuilding || r.buildingId === Number(this.filterBuilding);
      const matchType = !this.filterType || r.roomType === this.filterType;
      return matchSearch && matchBuilding && matchType;
    });
  }

  openForm(item?: Classroom) {
    this.editingItem = item ? { ...item } : null;
    this.form = item ? { ...item } : {
      buildingId: 0, roomNumber: '', floor: 0, capacity: 40,
      roomType: '', isLab: false, isSmartClassroom: false,
      hasProjector: true, hasWhiteboard: true, hasWifi: true,
      equipment: '', isAvailable: true, isActive: true, remarks: ''
    };
    this.formError = '';
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editingItem = null;
    this.formError = '';
  }

  save() {
    this.saving = true;
    this.formError = '';
    const handleSuccess = (msg: string) => {
      this.saving = false;
      this.closeForm();
      this.loadData();
      this.toastService.success(msg);
    };
    const handleError = (err: any) => {
      this.saving = false;
      this.formError = err.error?.message || 'Save failed';
    };

    if (this.editingItem?.id) {
      this.service.updateClassroom(this.editingItem.id, this.form as Classroom).subscribe({ next: () => handleSuccess('Classroom updated'), error: handleError });
    } else {
      this.service.createClassroom(this.form as Classroom).subscribe({ next: () => handleSuccess('Classroom created'), error: handleError });
    }
  }

  confirmDelete(item: Classroom) {
    this.deleteTarget = item;
    this.confirmTitle = 'Delete Classroom';
    this.confirmMessage = `Are you sure you want to delete room "${item.roomNumber}"?`;
    this.showConfirm = true;
  }

  executeDelete() {
    this.showConfirm = false;
    if (this.deleteTarget?.id) {
      this.service.deleteClassroom(this.deleteTarget.id).subscribe({
        next: () => { this.loadData(); this.toastService.success('Classroom deleted'); },
        error: () => this.toastService.error('Failed to delete classroom')
      });
    }
    this.deleteTarget = null;
  }
}
