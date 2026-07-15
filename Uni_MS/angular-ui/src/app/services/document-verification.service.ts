import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentVerification } from '../models/document-verification';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DocumentVerificationService {
  private apiUrl = `${environment.apiUrl}/document-verifications`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<DocumentVerification>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<DocumentVerification>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<DocumentVerification> {
    return this.http.get<DocumentVerification>(`${this.apiUrl}/${id}`);
  }

  save(documentVerification: DocumentVerification): Observable<DocumentVerification> {
    return this.http.post<DocumentVerification>(this.apiUrl, documentVerification);
  }

  update(id: number, documentVerification: DocumentVerification): Observable<DocumentVerification> {
    return this.http.put<DocumentVerification>(`${this.apiUrl}/${id}`, documentVerification);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
