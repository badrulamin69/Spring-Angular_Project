import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PageParams } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class WorkflowService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/workflows`;

  findAll(params?: PageParams): Observable<any> {
    const p = params || { page: 0, size: 10 };
    return this.http.get<any>(`${this.apiUrl}?page=${p.page}&size=${p.size}`);
  }

  findById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  save(data: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }

  update(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  getSteps(workflowId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${workflowId}/steps`);
  }

  addStep(workflowId: number, step: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${workflowId}/steps`, step);
  }

  updateStep(stepId: number, step: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/steps/${stepId}`, step);
  }

  deleteStep(stepId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/steps/${stepId}`);
  }

  getApprovals(entityType: string, entityId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/approvals?entityType=${entityType}&entityId=${entityId}`);
  }

  approveLeaveRequest(id: number, comments?: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/leave-requests/${id}/approve`, { comments });
  }

  rejectLeaveRequest(id: number, reason?: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/leave-requests/${id}/reject`, { reason });
  }
}
