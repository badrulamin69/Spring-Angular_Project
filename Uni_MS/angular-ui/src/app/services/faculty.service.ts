import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Faculty } from '../models/faculty';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class FacultyService {
  private apiUrl = `${environment.apiUrl}/faculties`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Faculty>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<Faculty>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Faculty> {
    return this.http.get<Faculty>(`${this.apiUrl}/${id}`);
  }

  save(faculty: Faculty): Observable<Faculty> {
    return this.http.post<Faculty>(this.apiUrl, faculty);
  }

  update(id: number, faculty: Faculty): Observable<Faculty> {
    return this.http.put<Faculty>(`${this.apiUrl}/${id}`, faculty);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
