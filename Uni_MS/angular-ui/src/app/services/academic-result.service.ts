import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AcademicResult } from '../models/academic-result';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AcademicResultService {
  private apiUrl = `${environment.apiUrl}/academic-results`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<AcademicResult>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<AcademicResult>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AcademicResult> {
    return this.http.get<AcademicResult>(`${this.apiUrl}/${id}`);
  }

  save(academicResult: AcademicResult): Observable<AcademicResult> {
    return this.http.post<AcademicResult>(this.apiUrl, academicResult);
  }

  update(id: number, academicResult: AcademicResult): Observable<AcademicResult> {
    return this.http.put<AcademicResult>(`${this.apiUrl}/${id}`, academicResult);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
