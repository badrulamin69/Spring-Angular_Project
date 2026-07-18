import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { AdmissionInterview } from '../models/admission-interview';

@Injectable({ providedIn: 'root' })
export class AdmissionInterviewService {
  private apiUrl = `${environment.apiUrl}/admission-interviews`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdmissionInterview>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdmissionInterview>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionInterview> {
    return this.http.get<AdmissionInterview>(`${this.apiUrl}/${id}`);
  }

  create(data: AdmissionInterview): Observable<AdmissionInterview> {
    return this.http.post<AdmissionInterview>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionInterview): Observable<AdmissionInterview> {
    return this.http.put<AdmissionInterview>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
