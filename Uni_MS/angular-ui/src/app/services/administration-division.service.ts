import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdministrationDivision } from '../models/administration-division';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AdministrationDivisionService {
  private apiUrl = `${environment.apiUrl}/administration-divisions`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdministrationDivision>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdministrationDivision>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdministrationDivision> {
    return this.http.get<AdministrationDivision>(`${this.apiUrl}/${id}`);
  }

  save(AdministrationDivision: AdministrationDivision): Observable<AdministrationDivision> {
    return this.http.post<AdministrationDivision>(this.apiUrl, AdministrationDivision);
  }

  update(id: number, AdministrationDivision: AdministrationDivision): Observable<AdministrationDivision> {
    return this.http.put<AdministrationDivision>(`${this.apiUrl}/${id}`, AdministrationDivision);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
