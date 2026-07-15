import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Curriculum } from '../models/curriculum';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class CurriculumService {
  private apiUrl = `${environment.apiUrl}/curricula`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Curriculum>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<Curriculum>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Curriculum> {
    return this.http.get<Curriculum>(`${this.apiUrl}/${id}`);
  }

  save(curriculum: Curriculum): Observable<Curriculum> {
    return this.http.post<Curriculum>(this.apiUrl, curriculum);
  }

  update(id: number, curriculum: Curriculum): Observable<Curriculum> {
    return this.http.put<Curriculum>(`${this.apiUrl}/${id}`, curriculum);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
