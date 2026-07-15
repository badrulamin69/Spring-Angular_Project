import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CourseAssignment } from '../models/course-assignment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CourseAssignmentService {
  private apiUrl = `${environment.apiUrl}/course-assignments`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<CourseAssignment>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<CourseAssignment>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<CourseAssignment> {
    return this.http.get<CourseAssignment>(`${this.apiUrl}/${id}`);
  }

  save(courseAssignment: CourseAssignment): Observable<CourseAssignment> {
    return this.http.post<CourseAssignment>(this.apiUrl, courseAssignment);
  }

  update(id: number, courseAssignment: CourseAssignment): Observable<CourseAssignment> {
    return this.http.put<CourseAssignment>(`${this.apiUrl}/${id}`, courseAssignment);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
