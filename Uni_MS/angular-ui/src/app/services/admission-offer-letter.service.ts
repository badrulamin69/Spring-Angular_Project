import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { AdmissionOfferLetter } from '../models/admission-offer-letter';

@Injectable({ providedIn: 'root' })
export class AdmissionOfferLetterService {
  private apiUrl = `${environment.apiUrl}/admission-offer-letters`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdmissionOfferLetter>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdmissionOfferLetter>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionOfferLetter> {
    return this.http.get<AdmissionOfferLetter>(`${this.apiUrl}/${id}`);
  }

  create(data: AdmissionOfferLetter): Observable<AdmissionOfferLetter> {
    return this.http.post<AdmissionOfferLetter>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionOfferLetter): Observable<AdmissionOfferLetter> {
    return this.http.put<AdmissionOfferLetter>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  accept(id: number): Observable<AdmissionOfferLetter> {
    return this.http.put<AdmissionOfferLetter>(`${this.apiUrl}/${id}/accept`, {});
  }

  decline(id: number, reason: string): Observable<AdmissionOfferLetter> {
    return this.http.put<AdmissionOfferLetter>(`${this.apiUrl}/${id}/decline`, { reason });
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
