import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { Alumni } from '../models/alumni';

@Injectable({ providedIn: 'root' })
export class AlumniService {
  private apiUrl = `${environment.apiUrl}/alumni`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Alumni>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    return this.http.get<PagedResponse<Alumni>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Alumni> {
    return this.http.get<Alumni>(`${this.apiUrl}/${id}`);
  }

  create(data: Alumni): Observable<Alumni> {
    return this.http.post<Alumni>(this.apiUrl, data);
  }

  update(id: number, data: Alumni): Observable<Alumni> {
    return this.http.put<Alumni>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
