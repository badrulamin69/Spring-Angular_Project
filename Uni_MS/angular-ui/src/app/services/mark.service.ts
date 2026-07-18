import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mark } from '../models/mark';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MarkService {
  private apiUrl = `${environment.apiUrl}/marks`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<Mark>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<Mark>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Mark> {
    return this.http.get<Mark>(`${this.apiUrl}/${id}`);
  }

  save(mark: Mark): Observable<Mark> {
    return this.http.post<Mark>(this.apiUrl, mark);
  }

  update(id: number, mark: Mark): Observable<Mark> {
    return this.http.put<Mark>(`${this.apiUrl}/${id}`, mark);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
