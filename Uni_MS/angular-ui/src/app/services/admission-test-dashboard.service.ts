import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AdmissionTestDashboardService {
  private apiUrl = `${environment.apiUrl}/admission-test-dashboard`;
  constructor(private http: HttpClient) {}
  getStats(): Observable<any> { return this.http.get(`${this.apiUrl}/stats`); }
  getChartData(): Observable<any> { return this.http.get(`${this.apiUrl}/charts`); }
}
