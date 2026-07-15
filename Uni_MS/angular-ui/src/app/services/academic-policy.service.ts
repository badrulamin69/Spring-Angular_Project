import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AcademicPolicy } from '../models/academic-policy';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AcademicPolicyService {
  private apiUrl = `${environment.apiUrl}/academic-policies`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<AcademicPolicy>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<AcademicPolicy>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AcademicPolicy> {
    return this.http.get<AcademicPolicy>(`${this.apiUrl}/${id}`);
  }

  save(academicPolicy: AcademicPolicy): Observable<AcademicPolicy> {
    return this.http.post<AcademicPolicy>(this.apiUrl, academicPolicy);
  }

  update(id: number, academicPolicy: AcademicPolicy): Observable<AcademicPolicy> {
    return this.http.put<AcademicPolicy>(`${this.apiUrl}/${id}`, academicPolicy);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
