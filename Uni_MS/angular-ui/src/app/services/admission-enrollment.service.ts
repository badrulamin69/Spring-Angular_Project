import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { AdmissionEnrollment } from '../models/admission-enrollment';

@Injectable({ providedIn: 'root' })
export class AdmissionEnrollmentService {
  private apiUrl = `${environment.apiUrl}/admission-enrollments`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<AdmissionEnrollment>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    return this.http.get<PagedResponse<AdmissionEnrollment>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionEnrollment> {
    return this.http.get<AdmissionEnrollment>(`${this.apiUrl}/${id}`);
  }

  create(data: AdmissionEnrollment): Observable<AdmissionEnrollment> {
    return this.http.post<AdmissionEnrollment>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionEnrollment): Observable<AdmissionEnrollment> {
    return this.http.put<AdmissionEnrollment>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
