import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Semester } from '../models/semester';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class SemesterService {
  private apiUrl = `${environment.apiUrl}/semesters`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Semester>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<Semester>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Semester> {
    return this.http.get<Semester>(`${this.apiUrl}/${id}`);
  }

  save(semester: Semester): Observable<Semester> {
    return this.http.post<Semester>(this.apiUrl, semester);
  }

  update(id: number, semester: Semester): Observable<Semester> {
    return this.http.put<Semester>(`${this.apiUrl}/${id}`, semester);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
