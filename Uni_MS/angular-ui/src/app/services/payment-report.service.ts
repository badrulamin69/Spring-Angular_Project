import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PaymentReportService {
  private apiUrl = `${environment.apiUrl}/payment-reports`;

  constructor(private http: HttpClient) {}

  getDailyReport(date?: string): Observable<any> {
    let httpParams = new HttpParams();
    if (date) httpParams = httpParams.set('date', date);
    return this.http.get<any>(`${this.apiUrl}/daily`, { params: httpParams });
  }

  getMonthlyReport(month: number, year: number): Observable<any> {
    const httpParams = new HttpParams()
      .set('month', month.toString())
      .set('year', year.toString());
    return this.http.get<any>(`${this.apiUrl}/monthly`, { params: httpParams });
  }

  getYearlyReport(year: number): Observable<any> {
    const httpParams = new HttpParams().set('year', year.toString());
    return this.http.get<any>(`${this.apiUrl}/yearly`, { params: httpParams });
  }

  getAnalytics(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/analytics`);
  }
}
