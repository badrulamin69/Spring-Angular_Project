import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentFee } from '../models/student-fee';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class StudentFeeService {
  private apiUrl = `${environment.apiUrl}/student-fees`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<StudentFee>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<StudentFee>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<StudentFee> {
    return this.http.get<StudentFee>(`${this.apiUrl}/${id}`);
  }

  save(studentFee: StudentFee): Observable<StudentFee> {
    return this.http.post<StudentFee>(this.apiUrl, studentFee);
  }

  update(id: number, studentFee: StudentFee): Observable<StudentFee> {
    return this.http.put<StudentFee>(`${this.apiUrl}/${id}`, studentFee);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
