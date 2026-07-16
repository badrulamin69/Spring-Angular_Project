import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AdmissionTestAttemptService {
  private apiUrl = `${environment.apiUrl}/admission-test-attempts`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<any>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString());
    return this.http.get<PagedResponse<any>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
