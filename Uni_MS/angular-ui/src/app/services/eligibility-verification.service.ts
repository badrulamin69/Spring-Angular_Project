import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EligibilityVerification } from '../models/eligibility-verification';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class EligibilityVerificationService {
  private apiUrl = `${environment.apiUrl}/eligibility-verifications`;
  constructor(private http: HttpClient) {}
  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = '', testId?: number): Observable<PagedResponse<EligibilityVerification>> {
    let httpParams = new HttpParams().set('page', params.page.toString()).set('size', params.size.toString()).set('sortBy', params.sortBy).set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (testId) httpParams = httpParams.set('testId', testId.toString());
    return this.http.get<PagedResponse<EligibilityVerification>>(this.apiUrl, { params: httpParams });
  }
  findByTestId(testId: number): Observable<EligibilityVerification[]> { return this.http.get<EligibilityVerification[]>(`${this.apiUrl}/test/${testId}`); }
  getStats(testId: number): Observable<any> { return this.http.get(`${this.apiUrl}/stats/${testId}`); }
  verify(data: any): Observable<EligibilityVerification> { return this.http.post<EligibilityVerification>(`${this.apiUrl}/verify`, data); }
  autoVerifyAll(testId: number): Observable<EligibilityVerification[]> { return this.http.post<EligibilityVerification[]>(`${this.apiUrl}/auto-verify/${testId}`, {}); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/${id}`); }
}
