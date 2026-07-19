import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionTest } from '../models/admission-test';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AdmissionTestService {
  private apiUrl = `${environment.apiUrl}/admission-tests`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = '', filters: { status?: string; facultyId?: number; departmentId?: number } = {}): Observable<PagedResponse<AdmissionTest>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    if (filters.facultyId) httpParams = httpParams.set('facultyId', filters.facultyId.toString());
    if (filters.departmentId) httpParams = httpParams.set('departmentId', filters.departmentId.toString());
    return this.http.get<PagedResponse<AdmissionTest>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionTest> {
    return this.http.get<AdmissionTest>(`${this.apiUrl}/${id}`);
  }

  save(admissionTest: AdmissionTest): Observable<AdmissionTest> {
    return this.http.post<AdmissionTest>(this.apiUrl, admissionTest);
  }

  update(id: number, admissionTest: AdmissionTest): Observable<AdmissionTest> {
    return this.http.put<AdmissionTest>(`${this.apiUrl}/${id}`, admissionTest);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  publish(id: number): Observable<AdmissionTest> {
    return this.http.put<AdmissionTest>(`${this.apiUrl}/${id}/publish`, {});
  }

  close(id: number): Observable<AdmissionTest> {
    return this.http.put<AdmissionTest>(`${this.apiUrl}/${id}/close`, {});
  }

  getStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/stats`);
  }
}
