import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Administration } from '../models/administration';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AdministrationService {
  private apiUrl = `${environment.apiUrl}/administrations`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<Administration>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<Administration>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Administration> {
    return this.http.get<Administration>(`${this.apiUrl}/${id}`);
  }

  save(administration: Administration): Observable<Administration> {
    return this.http.post<Administration>(this.apiUrl, administration);
  }

  update(id: number, administration: Administration): Observable<Administration> {
    return this.http.put<Administration>(`${this.apiUrl}/${id}`, administration);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
