import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SeatAllocation } from '../models/seat-allocation';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class SeatAllocationService {
  private apiUrl = `${environment.apiUrl}/seat-allocations`;
  constructor(private http: HttpClient) {}
  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = '', testId?: number): Observable<PagedResponse<SeatAllocation>> {
    let httpParams = new HttpParams().set('page', params.page.toString()).set('size', params.size.toString()).set('sortBy', params.sortBy).set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (testId) httpParams = httpParams.set('testId', testId.toString());
    return this.http.get<PagedResponse<SeatAllocation>>(this.apiUrl, { params: httpParams });
  }
  findByTestId(testId: number): Observable<SeatAllocation[]> { return this.http.get<SeatAllocation[]>(`${this.apiUrl}/test/${testId}`); }
  findById(id: number): Observable<SeatAllocation> { return this.http.get<SeatAllocation>(`${this.apiUrl}/${id}`); }
  save(data: SeatAllocation): Observable<SeatAllocation> { return this.http.post<SeatAllocation>(this.apiUrl, data); }
  update(id: number, data: SeatAllocation): Observable<SeatAllocation> { return this.http.put<SeatAllocation>(`${this.apiUrl}/${id}`, data); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/${id}`); }
  autoGenerate(testId: number): Observable<any> { return this.http.post(`${this.apiUrl}/auto-generate/${testId}`, {}); }
}
