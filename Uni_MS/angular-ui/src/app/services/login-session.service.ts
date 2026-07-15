import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginSession } from '../models/login-session';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class LoginSessionService {
  private apiUrl = `${environment.apiUrl}/login-sessions`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<LoginSession>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<LoginSession>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<LoginSession> {
    return this.http.get<LoginSession>(`${this.apiUrl}/${id}`);
  }

  findActiveSessions(): Observable<LoginSession[]> {
    return this.http.get<LoginSession[]>(`${this.apiUrl}/active`);
  }

  getActiveSessionCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/active/count`);
  }

  findByUserId(userId: number): Observable<LoginSession[]> {
    return this.http.get<LoginSession[]>(`${this.apiUrl}/user/${userId}`);
  }

  terminateSession(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/terminate`, {});
  }

  terminateAllUserSessions(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/user/${userId}/terminate-all`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
