import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Invoice } from '../models/invoice';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private apiUrl = `${environment.apiUrl}/invoices`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Invoice>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<Invoice>>(this.apiUrl, { params: httpParams });
  }

  search(search: string, status: string, params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Invoice>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (status) httpParams = httpParams.set('status', status);
    return this.http.get<PagedResponse<Invoice>>(`${this.apiUrl}/search`, { params: httpParams });
  }

  findById(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.apiUrl}/${id}`);
  }

  findByStudentId(studentId: number, params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Invoice>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<Invoice>>(`${this.apiUrl}/student/${studentId}`, { params: httpParams });
  }

  generateInvoice(studentId: number, semesterId: number, academicYear: string): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.apiUrl}/generate`, { studentId, semesterId, academicYear });
  }

  updateStatus(id: number, status: string): Observable<Invoice> {
    return this.http.put<Invoice>(`${this.apiUrl}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
