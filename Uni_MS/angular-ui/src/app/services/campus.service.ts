import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Campus } from '../models/campus';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class CampusService {
  private apiUrl = `${environment.apiUrl}/campuses`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<Campus>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<Campus>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Campus> {
    return this.http.get<Campus>(`${this.apiUrl}/${id}`);
  }

  save(campus: Campus): Observable<Campus> {
    return this.http.post<Campus>(this.apiUrl, campus);
  }

  update(id: number, campus: Campus): Observable<Campus> {
    return this.http.put<Campus>(`${this.apiUrl}/${id}`, campus);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
