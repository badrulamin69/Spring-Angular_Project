import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReportTemplate } from '../models/report-template';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReportTemplateService {
  private apiUrl = `${environment.apiUrl}/report-templates`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<ReportTemplate>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<ReportTemplate>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<ReportTemplate> {
    return this.http.get<ReportTemplate>(`${this.apiUrl}/${id}`);
  }

  save(reportTemplate: ReportTemplate): Observable<ReportTemplate> {
    return this.http.post<ReportTemplate>(this.apiUrl, reportTemplate);
  }

  update(id: number, reportTemplate: ReportTemplate): Observable<ReportTemplate> {
    return this.http.put<ReportTemplate>(`${this.apiUrl}/${id}`, reportTemplate);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
