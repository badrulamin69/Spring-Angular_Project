import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionTestResult } from '../models/admission-test-result';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AdmissionTestResultService {
  private apiUrl = `${environment.apiUrl}/admission-test-results`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdmissionTestResult>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdmissionTestResult>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionTestResult> {
    return this.http.get<AdmissionTestResult>(`${this.apiUrl}/${id}`);
  }

  save(data: AdmissionTestResult): Observable<AdmissionTestResult> {
    return this.http.post<AdmissionTestResult>(this.apiUrl, data);
  }

  saveBulk(data: AdmissionTestResult[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/bulk`, data);
  }

  update(id: number, data: AdmissionTestResult): Observable<AdmissionTestResult> {
    return this.http.put<AdmissionTestResult>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
