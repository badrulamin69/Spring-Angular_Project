import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { ApplicationReview } from '../models/application-review';

@Injectable({ providedIn: 'root' })
export class ApplicationReviewService {
  private apiUrl = `${environment.apiUrl}/application-reviews`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<ApplicationReview>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<ApplicationReview>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<ApplicationReview> {
    return this.http.get<ApplicationReview>(`${this.apiUrl}/${id}`);
  }

  create(data: ApplicationReview): Observable<ApplicationReview> {
    return this.http.post<ApplicationReview>(this.apiUrl, data);
  }

  update(id: number, data: ApplicationReview): Observable<ApplicationReview> {
    return this.http.put<ApplicationReview>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
