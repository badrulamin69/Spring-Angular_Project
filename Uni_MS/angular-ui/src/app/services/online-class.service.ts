import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OnlineClass } from '../models/online-class';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OnlineClassService {
  private apiUrl = `${environment.apiUrl}/online-classes`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<OnlineClass>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<OnlineClass>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<OnlineClass> {
    return this.http.get<OnlineClass>(`${this.apiUrl}/${id}`);
  }

  save(onlineClass: OnlineClass): Observable<OnlineClass> {
    return this.http.post<OnlineClass>(this.apiUrl, onlineClass);
  }

  update(id: number, onlineClass: OnlineClass): Observable<OnlineClass> {
    return this.http.put<OnlineClass>(`${this.apiUrl}/${id}`, onlineClass);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
