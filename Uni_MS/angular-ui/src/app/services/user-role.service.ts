import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserRole } from '../models/user-role';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class UserRoleService {
  private apiUrl = `${environment.apiUrl}/user-roles`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<UserRole>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<UserRole>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<UserRole> {
    return this.http.get<UserRole>(`${this.apiUrl}/${id}`);
  }

  save(userRole: UserRole): Observable<UserRole> {
    return this.http.post<UserRole>(this.apiUrl, userRole);
  }

  update(id: number, userRole: UserRole): Observable<UserRole> {
    return this.http.put<UserRole>(`${this.apiUrl}/${id}`, userRole);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
