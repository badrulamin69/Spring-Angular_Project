import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmployeeAttendance } from '../models/employee-attendance';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EmployeeAttendanceService {
  private apiUrl = `${environment.apiUrl}/employee-attendance`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<EmployeeAttendance>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<EmployeeAttendance>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<EmployeeAttendance> {
    return this.http.get<EmployeeAttendance>(`${this.apiUrl}/${id}`);
  }

  save(employeeAttendance: EmployeeAttendance): Observable<EmployeeAttendance> {
    return this.http.post<EmployeeAttendance>(this.apiUrl, employeeAttendance);
  }

  update(id: number, employeeAttendance: EmployeeAttendance): Observable<EmployeeAttendance> {
    return this.http.put<EmployeeAttendance>(`${this.apiUrl}/${id}`, employeeAttendance);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
