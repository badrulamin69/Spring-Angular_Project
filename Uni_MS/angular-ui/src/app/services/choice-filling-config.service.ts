import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChoiceFillingConfig } from '../models/choice-filling';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ChoiceFillingConfigService {
  private apiUrl = `${environment.apiUrl}/choice-filling-configs`;
  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, filters: any = {}): Observable<PagedResponse<ChoiceFillingConfig>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (filters.search) httpParams = httpParams.set('search', filters.search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    if (filters.sessionId) httpParams = httpParams.set('sessionId', filters.sessionId.toString());
    return this.http.get<PagedResponse<ChoiceFillingConfig>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<ChoiceFillingConfig> {
    return this.http.get<ChoiceFillingConfig>(`${this.apiUrl}/${id}`);
  }

  save(data: ChoiceFillingConfig): Observable<ChoiceFillingConfig> {
    return this.http.post<ChoiceFillingConfig>(this.apiUrl, data);
  }

  update(id: number, data: ChoiceFillingConfig): Observable<ChoiceFillingConfig> {
    return this.http.put<ChoiceFillingConfig>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  activate(id: number): Observable<ChoiceFillingConfig> {
    return this.http.put<ChoiceFillingConfig>(`${this.apiUrl}/${id}/activate`, {});
  }

  close(id: number): Observable<ChoiceFillingConfig> {
    return this.http.put<ChoiceFillingConfig>(`${this.apiUrl}/${id}/close`, {});
  }

  getStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/stats`);
  }

  getActiveConfig(): Observable<ChoiceFillingConfig> {
    return this.http.get<ChoiceFillingConfig>(`${this.apiUrl}/active`);
  }
}
