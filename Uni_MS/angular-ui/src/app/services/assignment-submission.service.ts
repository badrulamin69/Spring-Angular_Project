import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AssignmentSubmission } from '../models/assignment-submission';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AssignmentSubmissionService {
  private apiUrl = `${environment.apiUrl}/assignment-submissions`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AssignmentSubmission>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AssignmentSubmission>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AssignmentSubmission> {
    return this.http.get<AssignmentSubmission>(`${this.apiUrl}/${id}`);
  }

  save(assignmentSubmission: AssignmentSubmission): Observable<AssignmentSubmission> {
    return this.http.post<AssignmentSubmission>(this.apiUrl, assignmentSubmission);
  }

  update(id: number, assignmentSubmission: AssignmentSubmission): Observable<AssignmentSubmission> {
    return this.http.put<AssignmentSubmission>(`${this.apiUrl}/${id}`, assignmentSubmission);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
