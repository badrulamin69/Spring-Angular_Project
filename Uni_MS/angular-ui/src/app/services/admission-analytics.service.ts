import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AdmissionAnalyticsService {
  private apiUrl = `${environment.apiUrl}/admission-applications`;
  constructor(private http: HttpClient) {}

  getStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/stats`);
  }

  getMonthlyTrend(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/analytics/monthly-trend`);
  }

  getProgramBreakdown(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/analytics/program-breakdown`);
  }
}
