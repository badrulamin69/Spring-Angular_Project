import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionTestQuestion } from '../models/admission-test-question';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AdmissionTestQuestionService {
  private apiUrl = `${environment.apiUrl}/admission-test-questions`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, testId?: number): Observable<PagedResponse<AdmissionTestQuestion> | AdmissionTestQuestion[]> {
    if (testId) {
      return this.http.get<AdmissionTestQuestion[]>(this.apiUrl, { params: new HttpParams().set('testId', testId.toString()) });
    }
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<AdmissionTestQuestion>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionTestQuestion> {
    return this.http.get<AdmissionTestQuestion>(`${this.apiUrl}/${id}`);
  }

  save(data: AdmissionTestQuestion): Observable<AdmissionTestQuestion> {
    return this.http.post<AdmissionTestQuestion>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionTestQuestion): Observable<AdmissionTestQuestion> {
    return this.http.put<AdmissionTestQuestion>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  countByTestId(testId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/count`, { params: new HttpParams().set('testId', testId.toString()) });
  }
}
