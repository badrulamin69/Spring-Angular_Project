import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionTest } from '../models/admission-test';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AdmissionTestService {
  private apiUrl = `${environment.apiUrl}/admission-tests`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdmissionTest>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdmissionTest>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionTest> {
    return this.http.get<AdmissionTest>(`${this.apiUrl}/${id}`);
  }

  save(admissionTest: AdmissionTest): Observable<AdmissionTest> {
    return this.http.post<AdmissionTest>(this.apiUrl, admissionTest);
  }

  update(id: number, admissionTest: AdmissionTest): Observable<AdmissionTest> {
    return this.http.put<AdmissionTest>(`${this.apiUrl}/${id}`, admissionTest);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
