import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookReturn } from '../models/book-return';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class BookReturnService {
  private apiUrl = `${environment.apiUrl}/book-returns`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<BookReturn>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<BookReturn>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<BookReturn> {
    return this.http.get<BookReturn>(`${this.apiUrl}/${id}`);
  }

  save(bookReturn: BookReturn): Observable<BookReturn> {
    return this.http.post<BookReturn>(this.apiUrl, bookReturn);
  }

  update(id: number, bookReturn: BookReturn): Observable<BookReturn> {
    return this.http.put<BookReturn>(`${this.apiUrl}/${id}`, bookReturn);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
