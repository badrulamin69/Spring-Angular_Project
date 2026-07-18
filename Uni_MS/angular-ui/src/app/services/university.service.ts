import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { University } from '../models/university';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class UniversityService {
  private apiUrl = `${environment.apiUrl}/universities`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<University>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
      if (search) {
        httpParams = httpParams.set('search', search);
      }
    return this.http.get<PagedResponse<University>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<University> {
    return this.http.get<University>(`${this.apiUrl}/${id}`);
  }

  save(university: University): Observable<University> {
    return this.http.post<University>(this.apiUrl, university);
  }

  update(id: number, university: University): Observable<University> {
    return this.http.put<University>(`${this.apiUrl}/${id}`, university);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
