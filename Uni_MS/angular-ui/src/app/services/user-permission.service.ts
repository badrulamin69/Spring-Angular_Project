import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserPermission } from '../models/user-permission';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class UserPermissionService {
  private apiUrl = `${environment.apiUrl}/user-permissions`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<UserPermission>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<UserPermission>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<UserPermission> {
    return this.http.get<UserPermission>(`${this.apiUrl}/${id}`);
  }

  findByUserId(userId: number): Observable<UserPermission[]> {
    return this.http.get<UserPermission[]>(`${this.apiUrl}/user/${userId}`);
  }

  getEffectivePermissions(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/user/${userId}/effective`);
  }

  save(data: any): Observable<UserPermission> {
    return this.http.post<UserPermission>(this.apiUrl, data);
  }

  update(id: number, data: any): Observable<UserPermission> {
    return this.http.put<UserPermission>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  bulkSave(data: { userId: number; permissionIds: number[]; granted: boolean }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/bulk`, data);
  }
}
