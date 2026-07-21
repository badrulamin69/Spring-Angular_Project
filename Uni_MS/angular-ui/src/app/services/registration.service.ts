import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import {
  CourseRegistrationRequest,
  AdvisorApprovalRequest,
  PaymentValidationRequest,
  RegistrationSummary,
  EligibilityCheck,
  RegistrationDashboard,
  RegistrationHistory
} from '../models/registration';

@Injectable({ providedIn: 'root' })
export class RegistrationService {
  private apiUrl = `${environment.apiUrl}/registrations`;

  constructor(private http: HttpClient) {}

  getStudentRegistrations(studentId: number, semesterId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/student/${studentId}/semester/${semesterId}`);
  }

  getRegistrationSummary(studentId: number, semesterId: number): Observable<RegistrationSummary> {
    return this.http.get<RegistrationSummary>(`${this.apiUrl}/summary/${studentId}/semester/${semesterId}`);
  }

  selectCourse(data: CourseRegistrationRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/select`, data);
  }

  dropCourse(registrationId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/drop/${registrationId}`, {});
  }

  finalizeRegistration(registrationId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/finalize/${registrationId}`, {});
  }

  processPayment(data: PaymentValidationRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/payment`, data);
  }

  getDashboardStats(semesterId: number): Observable<RegistrationDashboard> {
    return this.http.get<RegistrationDashboard>(`${this.apiUrl}/dashboard/${semesterId}`);
  }

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, status?: string, semesterId?: number): Observable<PagedResponse<any>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    if (status) httpParams = httpParams.set('status', status);
    if (semesterId) httpParams = httpParams.set('semesterId', semesterId.toString());
    return this.http.get<PagedResponse<any>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  checkEligibility(studentId: number, semesterId: number): Observable<EligibilityCheck> {
    return this.http.get<EligibilityCheck>(`${this.apiUrl}/eligibility/${studentId}/semester/${semesterId}`);
  }

  validateRegistration(studentId: number, subjectId: number, semesterId: number, batchId?: number): Observable<any> {
    let httpParams = new HttpParams();
    if (batchId) httpParams = httpParams.set('batchId', batchId.toString());
    return this.http.get<any>(`${this.apiUrl}/validate/${studentId}/subject/${subjectId}/semester/${semesterId}`, { params: httpParams });
  }

  getHistoryByStudent(studentId: number, page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/history/student/${studentId}`, {
      params: new HttpParams().set('page', page.toString()).set('size', size.toString())
    });
  }

  getHistoryBySemester(semesterId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/history/semester/${semesterId}`);
  }
}
