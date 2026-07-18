import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DepartmentAllocation } from '../models/department-allocation';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class DepartmentAllocationService {
  private apiUrl = `${environment.apiUrl}/department-allocations`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<DepartmentAllocation>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<DepartmentAllocation>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<DepartmentAllocation> {
    return this.http.get<DepartmentAllocation>(`${this.apiUrl}/${id}`);
  }

  save(data: DepartmentAllocation): Observable<DepartmentAllocation> {
    return this.http.post<DepartmentAllocation>(this.apiUrl, data);
  }

  update(id: number, data: DepartmentAllocation): Observable<DepartmentAllocation> {
    return this.http.put<DepartmentAllocation>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  confirm(id: number): Observable<DepartmentAllocation> {
    return this.http.put<DepartmentAllocation>(`${this.apiUrl}/${id}/confirm`, {});
  }

  cancel(id: number): Observable<DepartmentAllocation> {
    return this.http.put<DepartmentAllocation>(`${this.apiUrl}/${id}/cancel`, {});
  }
}
