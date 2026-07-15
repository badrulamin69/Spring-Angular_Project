import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AcademicCalendar } from '../models/academic-calendar';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AcademicCalendarService {
  private apiUrl = `${environment.apiUrl}/academic-calendars`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<AcademicCalendar>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<AcademicCalendar>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AcademicCalendar> {
    return this.http.get<AcademicCalendar>(`${this.apiUrl}/${id}`);
  }

  save(academicCalendar: AcademicCalendar): Observable<AcademicCalendar> {
    return this.http.post<AcademicCalendar>(this.apiUrl, academicCalendar);
  }

  update(id: number, academicCalendar: AcademicCalendar): Observable<AcademicCalendar> {
    return this.http.put<AcademicCalendar>(`${this.apiUrl}/${id}`, academicCalendar);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
