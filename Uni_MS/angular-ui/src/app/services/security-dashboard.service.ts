import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SecurityDashboardService {
  private apiUrl = `${environment.apiUrl}/security/dashboard`;

  constructor(private http: HttpClient) {}

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }

  getRecentActivities(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/recent-activities`);
  }

  getLoginStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/login-stats`);
  }
}
