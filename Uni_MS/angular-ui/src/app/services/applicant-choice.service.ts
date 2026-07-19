import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicantChoiceSubmission, ApplicantChoice, AvailableProgram } from '../models/choice-filling';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApplicantChoiceService {
  private apiUrl = `${environment.apiUrl}/applicant-choices`;
  constructor(private http: HttpClient) {}

  getAllSubmissions(params: PageParams = DEFAULT_PAGE_PARAMS, filters: any = {}): Observable<PagedResponse<ApplicantChoiceSubmission>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (filters.search) httpParams = httpParams.set('search', filters.search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    if (filters.configId) httpParams = httpParams.set('configId', filters.configId.toString());
    return this.http.get<PagedResponse<ApplicantChoiceSubmission>>(`${this.apiUrl}/admin/submissions`, { params: httpParams });
  }

  getSubmissionById(id: number): Observable<ApplicantChoiceSubmission> {
    return this.http.get<ApplicantChoiceSubmission>(`${this.apiUrl}/admin/submissions/${id}`);
  }

  getSubmissionChoices(submissionId: number): Observable<ApplicantChoice[]> {
    return this.http.get<ApplicantChoice[]>(`${this.apiUrl}/admin/submissions/${submissionId}/choices`);
  }

  lockSubmission(id: number): Observable<ApplicantChoiceSubmission> {
    return this.http.put<ApplicantChoiceSubmission>(`${this.apiUrl}/admin/submissions/${id}/lock`, {});
  }

  reopenSubmission(id: number): Observable<ApplicantChoiceSubmission> {
    return this.http.put<ApplicantChoiceSubmission>(`${this.apiUrl}/admin/submissions/${id}/reopen`, {});
  }

  getStats(configId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/admin/stats/${configId}`);
  }

  getAvailablePrograms(configId: number): Observable<AvailableProgram[]> {
    return this.http.get<AvailableProgram[]>(`${this.apiUrl}/admin/available-programs/${configId}`);
  }

  startSubmission(configId: number): Observable<ApplicantChoiceSubmission> {
    return this.http.post<ApplicantChoiceSubmission>(`${this.apiUrl}/start/${configId}`, {});
  }

  getMySubmission(configId: number): Observable<ApplicantChoiceSubmission> {
    return this.http.get<ApplicantChoiceSubmission>(`${this.apiUrl}/my-submission`, { params: { configId: configId.toString() } });
  }

  getMyChoices(submissionId: number): Observable<ApplicantChoice[]> {
    return this.http.get<ApplicantChoice[]>(`${this.apiUrl}/my-choices`, { params: { submissionId: submissionId.toString() } });
  }

  addChoice(submissionId: number, programId: number): Observable<ApplicantChoice> {
    return this.http.post<ApplicantChoice>(`${this.apiUrl}/add-choice/${submissionId}/${programId}`, {});
  }

  removeChoice(choiceId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/remove-choice/${choiceId}`);
  }

  moveChoice(choiceId: number, direction: string): Observable<ApplicantChoice> {
    return this.http.put<ApplicantChoice>(`${this.apiUrl}/move-choice/${choiceId}`, null, { params: { direction } });
  }

  submitChoices(submissionId: number): Observable<ApplicantChoiceSubmission> {
    return this.http.post<ApplicantChoiceSubmission>(`${this.apiUrl}/submit/${submissionId}`, {});
  }
}
