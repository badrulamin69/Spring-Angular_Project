import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fine } from '../models/payment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FineService {
  private apiUrl = `${environment.apiUrl}/fines`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Fine>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<Fine>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Fine> {
    return this.http.get<Fine>(`${this.apiUrl}/${id}`);
  }

  save(data: Fine): Observable<Fine> {
    return this.http.post<Fine>(this.apiUrl, data);
  }

  update(id: number, data: Fine): Observable<Fine> {
    return this.http.put<Fine>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  findByStudentId(studentId: number): Observable<Fine[]> {
    return this.http.get<Fine[]>(`${this.apiUrl}/student/${studentId}`);
  }

  waiveFine(id: number): Observable<Fine> {
    return this.http.put<Fine>(`${this.apiUrl}/${id}/waive`, {});
  }
}
