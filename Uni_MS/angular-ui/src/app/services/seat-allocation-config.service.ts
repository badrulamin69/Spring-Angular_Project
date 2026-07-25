import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams } from '../models/paged-response';
import { SeatAllocationConfig } from '../models/seat-allocation';

@Injectable({ providedIn: 'root' })
export class SeatAllocationConfigService {
  private apiUrl = `${environment.apiUrl}/seat-allocation-configs`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams, filters: any = {}): Observable<PagedResponse<SeatAllocationConfig>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (filters.search) httpParams = httpParams.set('search', filters.search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    if (filters.sessionId) httpParams = httpParams.set('sessionId', filters.sessionId);
    return this.http.get<PagedResponse<SeatAllocationConfig>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<SeatAllocationConfig> {
    return this.http.get<SeatAllocationConfig>(`${this.apiUrl}/${id}`);
  }

  save(data: SeatAllocationConfig): Observable<SeatAllocationConfig> {
    return this.http.post<SeatAllocationConfig>(this.apiUrl, data);
  }

  update(id: number, data: SeatAllocationConfig): Observable<SeatAllocationConfig> {
    return this.http.put<SeatAllocationConfig>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  activate(id: number): Observable<SeatAllocationConfig> {
    return this.http.put<SeatAllocationConfig>(`${this.apiUrl}/${id}/activate`, {});
  }

  close(id: number): Observable<SeatAllocationConfig> {
    return this.http.put<SeatAllocationConfig>(`${this.apiUrl}/${id}/close`, {});
  }
}
