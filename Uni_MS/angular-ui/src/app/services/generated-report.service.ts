import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GeneratedReport } from '../models/generated-report';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GeneratedReportService {
  private apiUrl = `${environment.apiUrl}/generated-reports`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<GeneratedReport>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<GeneratedReport>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<GeneratedReport> {
    return this.http.get<GeneratedReport>(`${this.apiUrl}/${id}`);
  }

  save(generatedReport: GeneratedReport): Observable<GeneratedReport> {
    return this.http.post<GeneratedReport>(this.apiUrl, generatedReport);
  }

  update(id: number, generatedReport: GeneratedReport): Observable<GeneratedReport> {
    return this.http.put<GeneratedReport>(`${this.apiUrl}/${id}`, generatedReport);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
