import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { StudentAttendance } from '../models/student-attendance';

@Injectable({ providedIn: 'root' })
export class StudentAttendanceService {
  private apiUrl = `${environment.apiUrl}/student-attendance`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<StudentAttendance>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    return this.http.get<PagedResponse<StudentAttendance>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<StudentAttendance> {
    return this.http.get<StudentAttendance>(`${this.apiUrl}/${id}`);
  }

  create(data: StudentAttendance): Observable<StudentAttendance> {
    return this.http.post<StudentAttendance>(this.apiUrl, data);
  }

  update(id: number, data: StudentAttendance): Observable<StudentAttendance> {
    return this.http.put<StudentAttendance>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
