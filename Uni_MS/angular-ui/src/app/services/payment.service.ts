import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment } from '../models/payment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<Payment>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<Payment>>(this.apiUrl, { params: httpParams });
  }

  search(search: string, status: string, params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Payment>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (status) httpParams = httpParams.set('status', status);
    return this.http.get<PagedResponse<Payment>>(`${this.apiUrl}/search`, { params: httpParams });
  }

  findById(id: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/${id}`);
  }

  findByStudentId(studentId: number, params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<Payment>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<Payment>>(`${this.apiUrl}/student/${studentId}`, { params: httpParams });
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }

  initiatePayment(data: any): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/initiate`, data);
  }

  processOnlinePayment(id: number, data: any): Observable<Payment> {
    return this.http.put<Payment>(`${this.apiUrl}/${id}/process-online`, data);
  }

  processOfflinePayment(id: number): Observable<Payment> {
    return this.http.put<Payment>(`${this.apiUrl}/${id}/process-offline`, {});
  }

  approvePayment(id: number, approvedBy: string): Observable<Payment> {
    return this.http.put<Payment>(`${this.apiUrl}/${id}/approve`, { approvedBy });
  }

  rejectPayment(id: number): Observable<Payment> {
    return this.http.put<Payment>(`${this.apiUrl}/${id}/reject`, {});
  }

  refundPayment(id: number, data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/refund`, data);
  }
}
