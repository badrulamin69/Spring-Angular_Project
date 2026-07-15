import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AcademicDashboardService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/academic-dashboard/stats`);
  }

  getRecentEvents(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/academic-calendars`);
  }
}
