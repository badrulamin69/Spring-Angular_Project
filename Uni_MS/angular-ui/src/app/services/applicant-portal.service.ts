import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApplicantPortalService {
  private apiUrl = `${environment.apiUrl}/applicant`;

  constructor(private http: HttpClient) {}

  getMyRegistration(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my-registration`);
  }

  getMyTest(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my-test`);
  }

  getTestQuestions(testId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my-test/${testId}/questions`);
  }

  startTest(testId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/test/${testId}/start`, {});
  }

  submitTest(attemptId: number, answers: { [questionId: string]: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/test/submit`, { attemptId, answers });
  }

  getMyResults(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my-results`);
  }

  getMyAllocation(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my-allocation`);
  }

  confirmAllocation(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/my-allocation/${id}/confirm`, {});
  }

  declineAllocation(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/my-allocation/${id}/decline`, {});
  }

  enrollSelf(): Observable<any> {
    return this.http.post(`${this.apiUrl}/my-enroll`, {});
  }
}
