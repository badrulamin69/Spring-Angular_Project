import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { DisciplinaryRecord } from '../models/disciplinary-record';

@Injectable({ providedIn: 'root' })
export class DisciplinaryRecordService {
  private apiUrl = `${environment.apiUrl}/disciplinary-records`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<DisciplinaryRecord>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    return this.http.get<PagedResponse<DisciplinaryRecord>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<DisciplinaryRecord> {
    return this.http.get<DisciplinaryRecord>(`${this.apiUrl}/${id}`);
  }

  create(data: DisciplinaryRecord): Observable<DisciplinaryRecord> {
    return this.http.post<DisciplinaryRecord>(this.apiUrl, data);
  }

  update(id: number, data: DisciplinaryRecord): Observable<DisciplinaryRecord> {
    return this.http.put<DisciplinaryRecord>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
