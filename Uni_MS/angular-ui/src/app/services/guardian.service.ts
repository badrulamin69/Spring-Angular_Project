import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Guardian } from '../models/guardian';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GuardianService {
  private apiUrl = `${environment.apiUrl}/guardians`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<Guardian>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<Guardian>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Guardian> {
    return this.http.get<Guardian>(`${this.apiUrl}/${id}`);
  }

  save(guardian: Guardian): Observable<Guardian> {
    return this.http.post<Guardian>(this.apiUrl, guardian);
  }

  update(id: number, guardian: Guardian): Observable<Guardian> {
    return this.http.put<Guardian>(`${this.apiUrl}/${id}`, guardian);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
