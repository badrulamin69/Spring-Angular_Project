import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AcademicSession } from '../models/academic-session';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AcademicSessionService {
  private apiUrl = `${environment.apiUrl}/academic-sessions`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<AcademicSession>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<AcademicSession>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AcademicSession> {
    return this.http.get<AcademicSession>(`${this.apiUrl}/${id}`);
  }

  save(academicSession: AcademicSession): Observable<AcademicSession> {
    return this.http.post<AcademicSession>(this.apiUrl, academicSession);
  }

  update(id: number, academicSession: AcademicSession): Observable<AcademicSession> {
    return this.http.put<AcademicSession>(`${this.apiUrl}/${id}`, academicSession);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
