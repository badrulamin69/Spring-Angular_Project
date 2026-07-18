import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionResult } from '../models/admission-result';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AdmissionResultService {
  private apiUrl = `${environment.apiUrl}/admission-results`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdmissionResult>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdmissionResult>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionResult> {
    return this.http.get<AdmissionResult>(`${this.apiUrl}/${id}`);
  }

  save(admissionResult: AdmissionResult): Observable<AdmissionResult> {
    return this.http.post<AdmissionResult>(this.apiUrl, admissionResult);
  }

  update(id: number, admissionResult: AdmissionResult): Observable<AdmissionResult> {
    return this.http.put<AdmissionResult>(`${this.apiUrl}/${id}`, admissionResult);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
