import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentEnrollment } from '../models/student-enrollment';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class StudentEnrollmentService {
  private apiUrl = `${environment.apiUrl}/student-enrollments`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<StudentEnrollment>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<StudentEnrollment>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<StudentEnrollment> {
    return this.http.get<StudentEnrollment>(`${this.apiUrl}/${id}`);
  }

  save(studentEnrollment: StudentEnrollment): Observable<StudentEnrollment> {
    return this.http.post<StudentEnrollment>(this.apiUrl, studentEnrollment);
  }

  update(id: number, studentEnrollment: StudentEnrollment): Observable<StudentEnrollment> {
    return this.http.put<StudentEnrollment>(`${this.apiUrl}/${id}`, studentEnrollment);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
