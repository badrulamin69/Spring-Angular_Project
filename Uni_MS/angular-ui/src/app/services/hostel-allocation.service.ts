import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HostelAllocation } from '../models/hostel-allocation';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class HostelAllocationService {
  private apiUrl = `${environment.apiUrl}/hostel-allocations`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<HostelAllocation>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<HostelAllocation>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<HostelAllocation> {
    return this.http.get<HostelAllocation>(`${this.apiUrl}/${id}`);
  }

  save(hostelAllocation: HostelAllocation): Observable<HostelAllocation> {
    return this.http.post<HostelAllocation>(this.apiUrl, hostelAllocation);
  }

  update(id: number, hostelAllocation: HostelAllocation): Observable<HostelAllocation> {
    return this.http.put<HostelAllocation>(`${this.apiUrl}/${id}`, hostelAllocation);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
