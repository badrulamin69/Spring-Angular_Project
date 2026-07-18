import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransportAllocation } from '../models/transport-allocation';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class TransportAllocationService {
  private apiUrl = `${environment.apiUrl}/transport-allocations`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<TransportAllocation>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
      if (search) {
        httpParams = httpParams.set('search', search);
      }
    return this.http.get<PagedResponse<TransportAllocation>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<TransportAllocation> {
    return this.http.get<TransportAllocation>(`${this.apiUrl}/${id}`);
  }

  save(transportAllocation: TransportAllocation): Observable<TransportAllocation> {
    return this.http.post<TransportAllocation>(this.apiUrl, transportAllocation);
  }

  update(id: number, transportAllocation: TransportAllocation): Observable<TransportAllocation> {
    return this.http.put<TransportAllocation>(`${this.apiUrl}/${id}`, transportAllocation);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
