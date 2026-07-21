import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EnrollmentConfig } from '../models/semester-enrollment';

@Injectable({ providedIn: 'root' })
export class EnrollmentConfigService {
  private apiUrl = `${environment.apiUrl}/enrollment-configs`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<EnrollmentConfig[]> {
    return this.http.get<EnrollmentConfig[]>(this.apiUrl);
  }

  findActive(): Observable<EnrollmentConfig[]> {
    return this.http.get<EnrollmentConfig[]>(`${this.apiUrl}/active`);
  }

  findById(id: number): Observable<EnrollmentConfig> {
    return this.http.get<EnrollmentConfig>(`${this.apiUrl}/${id}`);
  }

  findBySemester(semesterId: number): Observable<EnrollmentConfig> {
    return this.http.get<EnrollmentConfig>(`${this.apiUrl}/semester/${semesterId}`);
  }

  isEnrollmentOpen(semesterId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check/${semesterId}`);
  }

  create(data: EnrollmentConfig): Observable<EnrollmentConfig> {
    return this.http.post<EnrollmentConfig>(this.apiUrl, data);
  }

  update(id: number, data: EnrollmentConfig): Observable<EnrollmentConfig> {
    return this.http.put<EnrollmentConfig>(`${this.apiUrl}/${id}`, data);
  }

  closeEnrollment(id: number): Observable<EnrollmentConfig> {
    return this.http.post<EnrollmentConfig>(`${this.apiUrl}/${id}/close`, {});
  }

  reopenEnrollment(id: number): Observable<EnrollmentConfig> {
    return this.http.post<EnrollmentConfig>(`${this.apiUrl}/${id}/reopen`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
