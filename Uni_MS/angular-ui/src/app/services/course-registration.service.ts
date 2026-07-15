import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { CourseRegistration } from '../models/course-registration';

@Injectable({ providedIn: 'root' })
export class CourseRegistrationService {
  private apiUrl = `${environment.apiUrl}/course-registrations`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<CourseRegistration>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    return this.http.get<PagedResponse<CourseRegistration>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<CourseRegistration> {
    return this.http.get<CourseRegistration>(`${this.apiUrl}/${id}`);
  }

  create(data: CourseRegistration): Observable<CourseRegistration> {
    return this.http.post<CourseRegistration>(this.apiUrl, data);
  }

  update(id: number, data: CourseRegistration): Observable<CourseRegistration> {
    return this.http.put<CourseRegistration>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
