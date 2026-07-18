import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentProfile } from '../models/student-profile';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class StudentProfileService {
  private apiUrl = `${environment.apiUrl}/student-profiles`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<StudentProfile>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
      if (search) {
        httpParams = httpParams.set('search', search);
      }
    return this.http.get<PagedResponse<StudentProfile>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<StudentProfile> {
    return this.http.get<StudentProfile>(`${this.apiUrl}/${id}`);
  }

  save(studentProfile: StudentProfile): Observable<StudentProfile> {
    return this.http.post<StudentProfile>(this.apiUrl, studentProfile);
  }

  update(id: number, studentProfile: StudentProfile): Observable<StudentProfile> {
    return this.http.put<StudentProfile>(`${this.apiUrl}/${id}`, studentProfile);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
