import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AdmissionConfirmationService {
  private apiUrl = `${environment.apiUrl}/admission-confirmations`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = '', status: string = '',
           documentsVerified: boolean | null = null, feePaid: boolean | null = null): Observable<PagedResponse<any>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (status) httpParams = httpParams.set('status', status);
    if (documentsVerified !== null) httpParams = httpParams.set('documentsVerified', documentsVerified.toString());
    if (feePaid !== null) httpParams = httpParams.set('feePaid', feePaid.toString());
    return this.http.get<PagedResponse<any>>(this.apiUrl, { params: httpParams });
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  getMyConfirmation(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my`);
  }

  initiateConfirmation(allocationId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/initiate/${allocationId}`, {});
  }

  submitDocuments(confirmationId: number, documents: { documentType: string; documentName: string; fileUrl?: string }[]): Observable<any[]> {
    return this.http.post<any[]>(`${this.apiUrl}/${confirmationId}/submit-documents`, documents);
  }

  verifyDocuments(confirmationId: number, verified: boolean, remarks: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${confirmationId}/verify-documents`, { verified, remarks });
  }

  payFee(confirmationId: number, amount: number, paymentMethod: string, transactionId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${confirmationId}/pay-fee`, { amount, paymentMethod, transactionId });
  }

  confirmAdmission(confirmationId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${confirmationId}/confirm`, {});
  }

  getDocuments(confirmationId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${confirmationId}/documents`);
  }

  getStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/stats`);
  }
}
