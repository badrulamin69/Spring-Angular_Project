import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionAttendance } from '../models/admission-attendance';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AdmissionAttendanceService {
  private apiUrl = `${environment.apiUrl}/admission-attendance`;
  constructor(private http: HttpClient) {}
  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = '', testId?: number): Observable<PagedResponse<AdmissionAttendance>> {
    let httpParams = new HttpParams().set('page', params.page.toString()).set('size', params.size.toString()).set('sortBy', params.sortBy).set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (testId) httpParams = httpParams.set('testId', testId.toString());
    return this.http.get<PagedResponse<AdmissionAttendance>>(this.apiUrl, { params: httpParams });
  }
  findByTestId(testId: number): Observable<AdmissionAttendance[]> { return this.http.get<AdmissionAttendance[]>(`${this.apiUrl}/test/${testId}`); }
  getStats(testId: number): Observable<any> { return this.http.get(`${this.apiUrl}/stats/${testId}`); }
  markAttendance(data: any): Observable<any> { return this.http.post(`${this.apiUrl}/mark`, data); }
  update(id: number, data: AdmissionAttendance): Observable<AdmissionAttendance> { return this.http.put<AdmissionAttendance>(`${this.apiUrl}/${id}`, data); }
}
