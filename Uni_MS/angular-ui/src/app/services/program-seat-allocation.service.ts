import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams } from '../models/paged-response';
import { ProgramSeatAllocation, AllocationStats, AllocationResult } from '../models/seat-allocation';

@Injectable({ providedIn: 'root' })
export class ProgramSeatAllocationService {
  private apiUrl = `${environment.apiUrl}/program-seat-allocations`;

  constructor(private http: HttpClient) {}

  findAll(configId: number, params: PageParams, filters: any = {}): Observable<PagedResponse<ProgramSeatAllocation>> {
    let httpParams = new HttpParams()
      .set('configId', configId.toString())
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (filters.search) httpParams = httpParams.set('search', filters.search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    if (filters.programId) httpParams = httpParams.set('programId', filters.programId);
    if (filters.facultyId) httpParams = httpParams.set('facultyId', filters.facultyId);
    if (filters.isWaiting !== null && filters.isWaiting !== undefined) httpParams = httpParams.set('isWaiting', filters.isWaiting);
    return this.http.get<PagedResponse<ProgramSeatAllocation>>(`${this.apiUrl}/admin/allocations`, { params: httpParams });
  }

  findById(id: number): Observable<ProgramSeatAllocation> {
    return this.http.get<ProgramSeatAllocation>(`${this.apiUrl}/admin/allocations/${id}`);
  }

  getStats(configId: number): Observable<AllocationStats> {
    return this.http.get<AllocationStats>(`${this.apiUrl}/admin/stats/${configId}`);
  }

  runAutoAllocation(configId: number): Observable<AllocationResult> {
    return this.http.post<AllocationResult>(`${this.apiUrl}/admin/auto-allocate/${configId}`, {});
  }

  manualAllocate(registrationId: number, programId: number, configId: number, shift: string, remarks?: string): Observable<ProgramSeatAllocation> {
    let params = new HttpParams()
      .set('registrationId', registrationId.toString())
      .set('programId', programId.toString())
      .set('configId', configId.toString())
      .set('shift', shift);
    if (remarks) params = params.set('remarks', remarks);
    return this.http.post<ProgramSeatAllocation>(`${this.apiUrl}/admin/manual-allocate`, null, { params });
  }

  changeAllocation(id: number, newProgramId: number, shift: string, remarks?: string): Observable<ProgramSeatAllocation> {
    let params = new HttpParams()
      .set('newProgramId', newProgramId.toString())
      .set('shift', shift);
    if (remarks) params = params.set('remarks', remarks);
    return this.http.put<ProgramSeatAllocation>(`${this.apiUrl}/admin/change-allocation/${id}`, null, { params });
  }

  cancelAllocation(id: number, remarks?: string): Observable<ProgramSeatAllocation> {
    let params = new HttpParams();
    if (remarks) params = params.set('remarks', remarks);
    return this.http.put<ProgramSeatAllocation>(`${this.apiUrl}/admin/cancel/${id}`, null, { params });
  }

  runReallocation(configId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/admin/reallocate/${configId}`, {});
  }

  expireOverdue(configId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/admin/expire-overdue/${configId}`, {});
  }

  getDemandReport(configId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/demand-report/${configId}`);
  }

  getMyAllocation(configId: number): Observable<ProgramSeatAllocation> {
    return this.http.get<ProgramSeatAllocation>(`${this.apiUrl}/my-allocation`, { params: { configId: configId.toString() } });
  }

  acceptAllocation(allocationId: number): Observable<ProgramSeatAllocation> {
    return this.http.post<ProgramSeatAllocation>(`${this.apiUrl}/accept/${allocationId}`, {});
  }

  declineAllocation(allocationId: number, remarks?: string): Observable<ProgramSeatAllocation> {
    let params = new HttpParams();
    if (remarks) params = params.set('remarks', remarks);
    return this.http.post<ProgramSeatAllocation>(`${this.apiUrl}/decline/${allocationId}`, null, { params });
  }
}
