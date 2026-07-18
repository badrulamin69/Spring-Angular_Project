import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Prerequisite } from '../models/prerequisite';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class PrerequisiteService {
  private apiUrl = `${environment.apiUrl}/prerequisites`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<Prerequisite>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<Prerequisite>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Prerequisite> {
    return this.http.get<Prerequisite>(`${this.apiUrl}/${id}`);
  }

  save(prerequisite: Prerequisite): Observable<Prerequisite> {
    return this.http.post<Prerequisite>(this.apiUrl, prerequisite);
  }

  update(id: number, prerequisite: Prerequisite): Observable<Prerequisite> {
    return this.http.put<Prerequisite>(`${this.apiUrl}/${id}`, prerequisite);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
