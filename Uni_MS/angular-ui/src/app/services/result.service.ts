import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Result } from '../models/result';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ResultService {
  private apiUrl = `${environment.apiUrl}/results`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Result>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<Result>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Result> {
    return this.http.get<Result>(`${this.apiUrl}/${id}`);
  }

  save(result: Result): Observable<Result> {
    return this.http.post<Result>(this.apiUrl, result);
  }

  update(id: number, result: Result): Observable<Result> {
    return this.http.put<Result>(`${this.apiUrl}/${id}`, result);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
