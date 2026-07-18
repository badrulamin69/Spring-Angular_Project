import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookIssue } from '../models/book-issue';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class BookIssueService {
  private apiUrl = `${environment.apiUrl}/book-issues`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<BookIssue>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<BookIssue>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<BookIssue> {
    return this.http.get<BookIssue>(`${this.apiUrl}/${id}`);
  }

  save(bookIssue: BookIssue): Observable<BookIssue> {
    return this.http.post<BookIssue>(this.apiUrl, bookIssue);
  }

  update(id: number, bookIssue: BookIssue): Observable<BookIssue> {
    return this.http.put<BookIssue>(`${this.apiUrl}/${id}`, bookIssue);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
