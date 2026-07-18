import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ExamSchedule } from '../models/exam-schedule';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ExamScheduleService {
  private apiUrl = `${environment.apiUrl}/exam-schedules`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<ExamSchedule>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<ExamSchedule>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<ExamSchedule> {
    return this.http.get<ExamSchedule>(`${this.apiUrl}/${id}`);
  }

  save(examSchedule: ExamSchedule): Observable<ExamSchedule> {
    return this.http.post<ExamSchedule>(this.apiUrl, examSchedule);
  }

  update(id: number, examSchedule: ExamSchedule): Observable<ExamSchedule> {
    return this.http.put<ExamSchedule>(`${this.apiUrl}/${id}`, examSchedule);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
