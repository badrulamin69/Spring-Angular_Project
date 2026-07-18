import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreditRule } from '../models/credit-rule';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class CreditRuleService {
  private apiUrl = `${environment.apiUrl}/credit-rules`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<CreditRule>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<CreditRule>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<CreditRule> {
    return this.http.get<CreditRule>(`${this.apiUrl}/${id}`);
  }

  save(creditRule: CreditRule): Observable<CreditRule> {
    return this.http.post<CreditRule>(this.apiUrl, creditRule);
  }

  update(id: number, creditRule: CreditRule): Observable<CreditRule> {
    return this.http.put<CreditRule>(`${this.apiUrl}/${id}`, creditRule);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
