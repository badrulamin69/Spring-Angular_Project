import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AdvisorApprovalRequest } from '../models/registration';

@Injectable({ providedIn: 'root' })
export class AdvisorApprovalService {
  private apiUrl = `${environment.apiUrl}/advisor-approvals`;

  constructor(private http: HttpClient) {}

  getPendingApprovals(semesterId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/pending/${semesterId}`);
  }

  processApproval(data: AdvisorApprovalRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/process`, data);
  }

  processBulkApproval(studentIds: number[], semesterId: number, action: string, comments?: string): Observable<any[]> {
    let params = new URLSearchParams();
    params.set('semesterId', semesterId.toString());
    params.set('action', action);
    if (comments) params.set('comments', comments);
    return this.http.post<any[]>(`${this.apiUrl}/bulk?${params.toString()}`, studentIds);
  }
}
