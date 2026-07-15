import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GradeRule } from '../models/grade-rule';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GradeRuleService {
  private apiUrl = `${environment.apiUrl}/grade-rules`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<GradeRule>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<GradeRule>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<GradeRule> {
    return this.http.get<GradeRule>(`${this.apiUrl}/${id}`);
  }

  save(gradeRule: GradeRule): Observable<GradeRule> {
    return this.http.post<GradeRule>(this.apiUrl, gradeRule);
  }

  update(id: number, gradeRule: GradeRule): Observable<GradeRule> {
    return this.http.put<GradeRule>(`${this.apiUrl}/${id}`, gradeRule);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
