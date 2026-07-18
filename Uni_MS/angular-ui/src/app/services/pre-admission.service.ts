import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PreAdmissionRegistration } from '../models/pre-admission-registration';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class PreAdmissionService {
  private apiUrl = `${environment.apiUrl}/pre-admissions`;
  private publicUrl = `${environment.apiUrl}/pre-admission`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<PreAdmissionRegistration>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<PreAdmissionRegistration>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<PreAdmissionRegistration> {
    return this.http.get<PreAdmissionRegistration>(`${this.apiUrl}/${id}`);
  }

  register(data: PreAdmissionRegistration): Observable<any> {
    return this.http.post<any>(`${this.publicUrl}/register`, data);
  }

  checkStatus(registrationNumber: string): Observable<any> {
    return this.http.get(`${this.publicUrl}/status/${registrationNumber}`);
  }

  update(id: number, data: PreAdmissionRegistration): Observable<PreAdmissionRegistration> {
    return this.http.put<PreAdmissionRegistration>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  approve(id: number): Observable<PreAdmissionRegistration> {
    return this.http.put<PreAdmissionRegistration>(`${this.apiUrl}/${id}/approve`, {});
  }

  reject(id: number, remarks: string): Observable<PreAdmissionRegistration> {
    return this.http.put<PreAdmissionRegistration>(`${this.apiUrl}/${id}/reject`, { remarks });
  }

  processMerit(): Observable<any> {
    return this.http.post(`${this.apiUrl}/process-merit`, {});
  }

  getMeritPreview(): Observable<any> {
    return this.http.get(`${this.apiUrl}/merit-preview`);
  }

  getAdmitCard(id: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/${id}/admit-card`, { responseType: 'text' });
  }

  getAdmitCardPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/admit-card/pdf`, { responseType: 'blob' });
  }

  getRegistrationPdf(registrationNumber: string): Observable<Blob> {
    return this.http.get(`${this.publicUrl}/register/${registrationNumber}/pdf`, { responseType: 'blob' });
  }

  getRegistrationQrCode(registrationNumber: string): Observable<Blob> {
    return this.http.get(`${this.publicUrl}/register/${registrationNumber}/qr-code`, { responseType: 'blob' });
  }
}
