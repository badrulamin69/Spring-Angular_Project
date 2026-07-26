import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import {
  SemesterEnrollment,
  EnrollmentEligibility,
  EnrollmentApproval,
  EnrollmentDashboard,
  EnrollmentHistory
} from '../models/semester-enrollment';

@Injectable({ providedIn: 'root' })
export class SemesterEnrollmentService {
  private apiUrl = `${environment.apiUrl}/semester-enrollments`;

  constructor(private http: HttpClient) {}

  private normalizePageResponse(raw: any): PagedResponse<any> {
    return {
      content: raw.content || [],
      page: raw.currentPage ?? raw.page ?? 0,
      size: raw.size ?? 10,
      totalElements: raw.totalElements ?? 0,
      totalPages: raw.totalPages ?? 0,
      first: raw.first ?? (raw.currentPage === 0),
      last: raw.last ?? (raw.currentPage === raw.totalPages - 1),
      empty: raw.empty ?? (raw.content?.length === 0)
    };
  }

  checkEligibility(studentId: number, semesterId: number): Observable<EnrollmentEligibility> {
    return this.http.get<EnrollmentEligibility>(`${this.apiUrl}/eligibility/${studentId}/semester/${semesterId}`);
  }

  enroll(data: SemesterEnrollment): Observable<SemesterEnrollment> {
    return this.http.post<SemesterEnrollment>(`${this.apiUrl}/enroll`, data);
  }

  forceEnroll(data: SemesterEnrollment): Observable<SemesterEnrollment> {
    return this.http.post<SemesterEnrollment>(`${this.apiUrl}/force-enroll`, data);
  }

  getEnrollmentById(id: number): Observable<SemesterEnrollment> {
    return this.http.get<SemesterEnrollment>(`${this.apiUrl}/${id}`);
  }

  getStudentEnrollments(studentId: number): Observable<SemesterEnrollment[]> {
    return this.http.get<SemesterEnrollment[]>(`${this.apiUrl}/student/${studentId}`);
  }

  getStudentEnrollmentForSemester(studentId: number, semesterId: number): Observable<SemesterEnrollment> {
    return this.http.get<SemesterEnrollment>(`${this.apiUrl}/student/${studentId}/semester/${semesterId}`);
  }

  getPendingApprovals(semesterId: number): Observable<SemesterEnrollment[]> {
    return this.http.get<SemesterEnrollment[]>(`${this.apiUrl}/pending/semester/${semesterId}`);
  }

  getPendingApprovalsForAdvisor(advisorId: number, semesterId: number): Observable<SemesterEnrollment[]> {
    return this.http.get<SemesterEnrollment[]>(`${this.apiUrl}/pending/advisor/${advisorId}/semester/${semesterId}`);
  }

  processApproval(data: EnrollmentApproval): Observable<EnrollmentApproval> {
    return this.http.post<EnrollmentApproval>(`${this.apiUrl}/approval`, data);
  }

  cancelEnrollment(enrollmentId: number, reason?: string): Observable<SemesterEnrollment> {
    return this.http.post<SemesterEnrollment>(`${this.apiUrl}/${enrollmentId}/cancel`, { enrollmentId, reason });
  }

  reopenEnrollment(enrollmentId: number): Observable<SemesterEnrollment> {
    return this.http.post<SemesterEnrollment>(`${this.apiUrl}/${enrollmentId}/reopen`, {});
  }

  finalizeEnrollment(enrollmentId: number): Observable<SemesterEnrollment> {
    return this.http.post<SemesterEnrollment>(`${this.apiUrl}/${enrollmentId}/finalize`, {});
  }

  getDashboardStats(semesterId: number): Observable<EnrollmentDashboard> {
    return this.http.get<EnrollmentDashboard>(`${this.apiUrl}/dashboard/${semesterId}`);
  }

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, filters?: {
    semesterId?: number;
    departmentId?: number;
    facultyId?: number;
    programId?: number;
    status?: string;
  }): Observable<PagedResponse<any>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    if (filters) {
      if (filters.semesterId) httpParams = httpParams.set('semesterId', filters.semesterId.toString());
      if (filters.departmentId) httpParams = httpParams.set('departmentId', filters.departmentId.toString());
      if (filters.facultyId) httpParams = httpParams.set('facultyId', filters.facultyId.toString());
      if (filters.programId) httpParams = httpParams.set('programId', filters.programId.toString());
      if (filters.status) httpParams = httpParams.set('status', filters.status);
    }
    return this.http.get<any>(this.apiUrl, { params: httpParams })
      .pipe(map(raw => this.normalizePageResponse(raw)));
  }

  getHistoryByStudent(studentId: number, page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/history/student/${studentId}`, {
      params: new HttpParams().set('page', page.toString()).set('size', size.toString())
    });
  }

  getHistoryBySemester(semesterId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/history/semester/${semesterId}`);
  }

  getHistoryByEnrollment(enrollmentId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/history/enrollment/${enrollmentId}`);
  }
}
