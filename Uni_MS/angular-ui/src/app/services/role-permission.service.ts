import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RolePermission } from '../models/role-permission';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class RolePermissionService {
  private apiUrl = `${environment.apiUrl}/role-permissions`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<RolePermission>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<RolePermission>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<RolePermission> {
    return this.http.get<RolePermission>(`${this.apiUrl}/${id}`);
  }

  save(rolePermission: RolePermission): Observable<RolePermission> {
    return this.http.post<RolePermission>(this.apiUrl, rolePermission);
  }

  update(id: number, rolePermission: RolePermission): Observable<RolePermission> {
    return this.http.put<RolePermission>(`${this.apiUrl}/${id}`, rolePermission);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
