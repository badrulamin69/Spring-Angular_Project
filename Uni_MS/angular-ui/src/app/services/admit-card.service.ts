import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmitCard } from '../models/admit-card';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AdmitCardService {
  private apiUrl = `${environment.apiUrl}/admit-cards`;
  constructor(private http: HttpClient) {}
  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = '', testId?: number): Observable<PagedResponse<AdmitCard>> {
    let httpParams = new HttpParams().set('page', params.page.toString()).set('size', params.size.toString()).set('sortBy', params.sortBy).set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (testId) httpParams = httpParams.set('testId', testId.toString());
    return this.http.get<PagedResponse<AdmitCard>>(this.apiUrl, { params: httpParams });
  }
  findByTestId(testId: number): Observable<AdmitCard[]> { return this.http.get<AdmitCard[]>(`${this.apiUrl}/test/${testId}`); }
  findByRegistrationId(regId: number): Observable<AdmitCard> { return this.http.get<AdmitCard>(`${this.apiUrl}/registration/${regId}`); }
  findById(id: number): Observable<AdmitCard> { return this.http.get<AdmitCard>(`${this.apiUrl}/${id}`); }
  save(data: AdmitCard): Observable<AdmitCard> { return this.http.post<AdmitCard>(this.apiUrl, data); }
  update(id: number, data: AdmitCard): Observable<AdmitCard> { return this.http.put<AdmitCard>(`${this.apiUrl}/${id}`, data); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/${id}`); }
  generate(testId: number): Observable<any> { return this.http.post(`${this.apiUrl}/generate/${testId}`, {}); }
}
