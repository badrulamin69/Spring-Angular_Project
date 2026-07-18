import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionCandidate } from '../models/admission-candidate';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AdmissionCandidateService {
  private apiUrl = `${environment.apiUrl}/admission-candidates`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdmissionCandidate>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdmissionCandidate>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionCandidate> {
    return this.http.get<AdmissionCandidate>(`${this.apiUrl}/${id}`);
  }

  save(admissionCandidate: AdmissionCandidate): Observable<AdmissionCandidate> {
    return this.http.post<AdmissionCandidate>(this.apiUrl, admissionCandidate);
  }

  update(id: number, admissionCandidate: AdmissionCandidate): Observable<AdmissionCandidate> {
    return this.http.put<AdmissionCandidate>(`${this.apiUrl}/${id}`, admissionCandidate);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
